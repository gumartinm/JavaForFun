package de.javapos.example.hardware;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jpos.JposConst;
import jpos.JposException;
import org.apache.log4j.Logger;
import de.javapos.example.annotation.GuardedBy;
import de.javapos.example.queue.JposEventListener;

/**
 * Not thread-safe (WORKING ON IT)
 * 
 * @author
 *
 */
public class KeyBoardDeviceLinux implements BaseKeyBoardDriver {
	private static final Logger logger = Logger.getLogger(KeyBoardDeviceLinux.class);
	//value EV_KEY from include/linux/input.h
	private static final int EV_KEY = 1;
	private static final String javaposKeyBoardLock = "/tmp/javaposKeyBoardLock";
	//No me gusta nada esto, simplemente es para evitar mirar si thread es null la primera vez en el metodo enable...
	private Thread thread = new Thread ("KeyBoardDeviceLinux-Thread");
	private String deviceName;
	@GuardedBy("claimLock") private FileChannel fileChannelLock;
	@GuardedBy("this") private DataInputStream device;
	@GuardedBy("claimLock") private volatile boolean isClaimed;
	@GuardedBy("this") private boolean isEnabled;
	//Usando volatile porque cumplo las condiciones de uso. Ver Java Concurrency in Practice PAG????
	private volatile boolean autoDisable;
	private JposEventListener eventListener;
	@GuardedBy("claimLock") private FileLock lock;
	private Lock claimLock = new ReentrantLock(true);
	
	@Override
	public boolean isOpened() {
		return false;
	}

	@Override
	public synchronized void close() throws JposException {
		this.disable();
		this.release();
	}

	/**
	 * Claim device.
	 *
	 * <p>
	 * <b>Thread-safe</b>
	 * </p>
	 */
	@Override
	public void claim(int time) throws JposException {

		//Using explicit Locks. Java Concurrency in Practice§13
		//I want to attempt to acquire this lock without waiting for it forever when the timeout is not -1.
		if (time == -1) {
			try {
				this.claimLock.lockInterruptibly();
				//Exclusive access code.
				try {
					this.claimImplementation(time);
				} finally {
					this.claimLock.unlock();
				}
			} catch (InterruptedException e) {
				//restore interrupt status.
				Thread.currentThread().interrupt();
				throw new JposException(JposConst.JPOS_E_CLAIMED, "Interrupt exception detected.", e);
			}
		}
		else {
			try {
				if (this.claimLock.tryLock(time, TimeUnit.MILLISECONDS)) {
					//Exclusive access code.
					try {
						this.claimImplementation(time);
					} finally {
						this.claimLock.unlock();
					}
				}
				else {
					throw new JposException(JposConst.JPOS_E_TIMEOUT, "Timeout while trying to claim device.");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new JposException(JposConst.JPOS_E_CLAIMED, "Interrupt exception detected.", e);
			}
			try {
				this.wait(250);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new JposException(JposConst.JPOS_E_CLAIMED, "Interrupt exception detected.", e);
			}
		}
	}

	private void claimImplementation(int time) throws JposException {
		FileLock lock = null;
		FileChannel fileChannelLock = null;


		if (this.isClaimed) {
			return;
		}

		try {
			fileChannelLock = new FileOutputStream(javaposKeyBoardLock).getChannel();
		} catch (FileNotFoundException e) {
			throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "File not found.",e);
		}

		if (time == -1) {
			try {
				//This method is not aware interrupt. :(
				lock = fileChannelLock.lock();
			//I do not like catching RunTimeExceptions but I have no choice...
			} catch (OverlappingFileLockException e) {
				closeFileLock(fileChannelLock);
				throw new JposException(JposConst.JPOS_E_CLAIMED, "More than one instances of the " +
										"KeyBoardDeviceLinux JavaPOS driver.",e); 
			} catch (IOException e) {
				closeFileLock(fileChannelLock);
				throw new JposException(JposConst.JPOS_E_CLAIMED, "Error while trying to claim device.",e);
			}
		}
		else {
			long lastTime = System.nanoTime() + ((time + 250) * 1000000);
			//Espera activa. ¿Alguna idea de cómo hacer esto sin espera activa?  :(
			do {
				try {
					if ((lock = fileChannelLock.tryLock()) != null) {
						break;
					}
					this.wait(250);
				//I do not like catching RunTimeExceptions but I have no choice...
				} catch (OverlappingFileLockException e) {
					closeFileLock(fileChannelLock);
					throw new JposException(JposConst.JPOS_E_CLAIMED, "More than one instances of the " +
															"KeyBoardDeviceLinux JavaPOS driver.",e); 
				} catch (IOException e) {
					closeFileLock(fileChannelLock);
					throw new JposException(JposConst.JPOS_E_CLAIMED, "Error while trying to claim device.",e);    
                } catch(InterruptedException e) {
                    closeFileLock(fileChannelLock);
                    //restore interrupt status.
                	Thread.currentThread().interrupt();
                    throw new JposException(JposConst.JPOSERR, "Interrupt exception detected.", e);
                }
			} while(System.nanoTime() <= lastTime);
			
			if (lock == null) {
				//Time out
				closeFileLock(fileChannelLock);
				throw new JposException(JposConst.JPOS_E_TIMEOUT, "Timeout while trying to claim device.");
			}
		}
		
		this.lock = lock;
		this.fileChannelLock = fileChannelLock;
		this.isClaimed = true;
	}

	private void closeFileLock(FileChannel fileChannelLock ) {
		try {
			fileChannelLock.close();
		} catch (IOException e) {
			logger.warn("Error while closing the file lock", e);
		}
	}

	/**
	 * Release device.
	 *
	 * <p>
	 * Thread-safe.
	 * </p>
	 */
	@Override
	public void release() throws JposException {
		try {
			this.claimLock.lockInterruptibly();
			try {
				//Exclusive access code.
				this.releaseImplementation();
			} finally {
				this.claimLock.unlock();
			}
		} catch (InterruptedException e) {
			//restore interrupt status.
			Thread.currentThread().interrupt();
			throw new JposException(JposConst.JPOS_E_CLAIMED, "Interrupt exception detected.", e);
		}
	}

	private void releaseImplementation() throws JposException {

		if (!this.isClaimed) {
			return;
		}
		
		try {
			this.lock.release();
			this.fileChannelLock.close();
		} catch (IOException e) {
			throw new JposException(JposConst.JPOSERR, "Error when closing the keyboard file lock", e);
		}

		this.isClaimed = false;
	}

	@Override
	public boolean isClaimed() {
		return this.isClaimed;
	}

	/**
	 * Not thread-safe.!!!!!
	 * 
	 * NO OLVIDAR try/finally PARA DEJAR EL DISPOSITIVO CORRECTAMENTE
	 * @throws JposException
	 * @throws RejectedExecutionException if this task cannot be
     * accepted for execution. CUIDADO RUNTIME NO OLVIDAR try/finally PARA DEJAR BIEN EL DISPOSITIVO
	 */
	@Override
	public synchronized void enable() throws JposException {
		if (this.isEnabled) {
			return;
		}
		if (this.deviceName == null) {
			throw new JposException(JposConst.JPOSERR, "There is not an assigned device", 
										new NullPointerException("The deviceName field has null value"));
		}

		//Esto mola porque me ahorro una variable indicando si el dispositivo fue habilitado o no
		//el problema es que puede darse el caso que se ejecute esto:
		//open();
		//claim();<--- El teclado no es un dispostivo compartido (var la especificacion JavaPOS)
		//enable;
		//disable();
		//enable();
		//Y en segundo enable (aqui) se vea el hilo todavía vivo (temas del planificador del SO) y lanze excepcion de que 
		//el dispositivo no ha sido deshabilitado cuando realmente sí lo ha sido.  :(
		//Ese no puede ser el comportamiento esperado... Luego esto estaba gracioso porque me evitaba tener
		//que usar una variable compartida llamada isEnable pero no es válido. :(
		//Reahacer esto y usar unicamente una variable compartida llamada isEnable para saber si el
		//dispostivo fue o no deshabilitado y dejar de fijarme en el estado del hilo.
		//Espera!!! ¿Y si en disable me espero a que termine el hilo? Entonces esto funcionaría guay.
		//Por narices el hilo va a terminar tal y como lo he hecho así que no habria problema :)
		//Esa es la solucion adecuada!!! Aunque el disable ahora tarde un poco mas hace mi aplicacion más robusta
		//¿Hay algún inconveniente mas a parte de hacer que el disable sea algo más lento? tardara el tiempo
		//que tarde en morir el hilo.
		//PROBLEMA OTRA VEZ, ¿y si el hilo murio por alguna cosa inesperada como por ejemplo que el archivo del 
		//dispositivo del SO se borro o cualquier otra cosa? Aqui veria el hilo muerto pero en realidad el
		//dispositivo no fue deshabilitado si no que el hilo murio de forma inesperada Y NO SE CERRO this.device!!!... 
		//Si soluciono este ultimo escollo ¿sí me podre basar en si el hilo está o no muerto?
		if (!this.thread.isAlive()) {
			try {
				this.device = new DataInputStream(Channels.newInputStream(new FileInputStream(this.deviceName).getChannel()));
			} catch (FileNotFoundException e) {
				throw new JposException(JposConst.JPOS_E_NOHARDWARE, "Device not found.",e);
			}

			Runnable task = new Runnable () {

				@Override
				public void run() {
					//Hidden pointer: KeyBoardDeviceLinux.this.runBatchTask();
					runBatchTask();
				}

			};
			this.thread = new Thread (task, "KeyBoardDeviceLinux-Thread");
			this.thread.setUncaughtExceptionHandler(new DriverHWUncaughtExceptionHandler());
			this.thread.start();
			this.isEnabled = true;
		}
	}

	@Override
	public synchronized void disable() throws JposException {

		if (!this.isEnabled) {
			return;
		}

		//This method releases the Java NIO channel. It is thread safety. :) see: Interruptible
		this.thread.interrupt();
		try {
			this.thread.join();
		} catch (InterruptedException e) {
			//restore interrupt status.
			Thread.currentThread().interrupt();
			throw new JposException(JposConst.JPOSERR, "Interrupt exception detected.", e);
		}

		this.isEnabled = false;
	}

	@Override
	public synchronized boolean isEnabled() {
		return this.isEnabled;
	}

	@Override
	public void autoDisable(boolean autoDisable) {
		this.autoDisable = autoDisable;
	}


	@Override
	public void addEventListener(JposEventListener jposEventListener) throws JposException {
		this.eventListener = jposEventListener;
	}

	@Override
	public void removeEventListener(JposEventListener jposEventListener) {
		this.eventListener = null;
	}

	@Override
	public boolean write(byte[] paramArrayOfByte, int paramInt1, int paramInt2,
			int paramInt3) throws JposException {
		return false;
	}

	@Override
	public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2,
			int paramInt3) throws JposException {
		return 0;
	}

	@Override
	public int writeRead(byte[] paramArrayOfByte1, int paramInt1,
			int paramInt2, byte[] paramArrayOfByte2, int paramInt3,
			int paramInt4, int paramInt5) throws JposException {
		return 0;
	}

	@Override
	public String getDescription(int paramInt) {
		return null;
	}

	@Override
	public void flush(int paramInt) throws JposException {

	}
	
	
	/**
	 * 
	 * NO OLVIDAR try/finally PARA DEJAR EL DISPOSITIVO CORRECTAMENTE
	 */
	@Override
	public void device(String device) {
		this.deviceName = device;
	}


	private void runBatchTask() {
		//OS 64 bits timeval 16 bytes  -> struct input_event 24 bytes
		//OS 32 bits timeval 8 bytes -> struct input_event 16 bytes
		byte []buffer = new byte[16];
		//byte []buffer = new byte[24];
		short code = 0;
		short type = 0;
		int value = 0;
		int ch1 = 0;
		int ch2 = 0;
		int ch3 = 0;
		int ch4 = 0;
		
		try {
			while (true) {
				//using command: evtest /dev/input/event4
				//It is not clear hear but I am using sun.nio.ch.FileChannelImpl.read(ByteBuffer ) method which throws
				//exception (java.nio.channels.ClosedByInterruptException) when the thread is interrupted
				//see: Thread.interrupt(), sun.nio.ch.FileChannelImpl.read(ByteBuffer) and 
				//java.nio.channels.spi.AbstractInterruptibleChannel.begin() methods
				//Basically before getting in an I/O operation that might block indefinitely the begin method implements
				//the Thread.blocker field. This field is used when running the method Thread.interrupt(). The blocker
				//implementation closes the file descriptor, so the I/0 operation throws an IOException (closed file)
				//In the finally block of the sun.nio.ch.FileChannelImpl.read(ByteBuffer) method we can find the
				//java.nio.channels.spi.AbstractInterruptibleChannel.end(boolean) method which throws the java.nio.channels.ClosedByInterruptException
				//We are losing the IOException (because the file was closed by the blocker implementation) but indeed
				//the reality is that we are getting out of the I/O blocking operation because we interrupted the running thread. 
				this.device.readFully(buffer);
				
				ch1 = buffer[11];
		        ch2 = buffer[10];
		        //ch1 = buffer[19];
		        //ch2 = buffer[18];
		        code = (short)((ch1 << 8) + (ch2 << 0));
		        
		        ch1 = buffer[9];
		        ch2 = buffer[8];
		        //ch1 = buffer[17];
		        //ch2 = buffer[16];
		        type = (short)((ch1 << 8) + (ch2 << 0));
		        
		        
		        ch1 = buffer[15];
		        ch2 = buffer[14];
		        ch3 = buffer[13];
		        ch4 = buffer[12];
		        //ch1 = buffer[23];
		        //ch2 = buffer[22];
		        //ch3 = buffer[21];
		        //ch4 = buffer[20];
		        value = ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
		        
		        if (type == KeyBoardDeviceLinux.EV_KEY) {
                    //Problema aqui con eventListener y que vea != null y luego justo dentro del if 
                    //otro hilo me lo quite...
                    if (eventListener != null) {
                        eventListener.inputAvailable(code);
                    }

		        	logger.debug("Captured key " + "type: " + type + " code: " + code + " value: " + value);
                    if (autoDisable) {
                        break;
		        	}
		        }
			}
		} catch (EOFException e) {
			logger.error("Something went really bad. Impossible end of file while reading keyboard device!!!!", e);
		} catch (IOException e) {
			//We reach this code because the thread was interrupted (disable) or because there was an I/O error
			//logger.debug or logger.error if it was an error I am not going to log it... :/
			logger.debug("Finished KeyBoardDeviceLinux Thread", e);
		} finally {
			//This method releases the Java NIO channels (this.device). It is thread safety :) see: Interruptible 
			this.thread.interrupt();
			this.device = null;   //¿Realmente esto es necesario? (no lo creo  :/ )
				//SI EL HILO MUERE ¿ESTOY DEJANDO EL ESTADO DEL DISPOSITIVO LOGICO JAVAPOS
				//EN UN MODO CONSISTENTE?
				//liberar el LOCK SI EL HILO MUERE DE FORMA INESPERADA???? NO, JavaPOS no dice nada.
				//¿como moriria este hilo de forma inesperada?? por ejemplo por RunTimeException
				//¿Como hago saber a la aplicacion que este hilo murio de forma inesperada por RunTimeException y el teclado
				//ha dejado de funcionar... Si el teclado deja de funcionar no sería mejor parar toda la aplicacion y ver
				//que pasa...

		}
	}

	private class DriverHWUncaughtExceptionHandler implements UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			logger.warn("Exception not expected while running thread " + t.getName() , e);
		}
	}
	
	public static void main(String[] param) throws InterruptedException
	{
		
		//Because I do not have a POS I am going to use the keyboard of my computer.
		//see: /dev/input/by-path/
		//And the Keyborad scancode is for USB devices.
		String device ="/dev/input/event4";
		KeyBoardDeviceLinux driver = new KeyBoardDeviceLinux();
		
		logger.info("Main test of KeyBoardDeviceLinux class");
		try {
			driver.device(device);
			driver.claim(10000);
			driver.enable();
			Thread.sleep(5000);
			driver.disable();
			driver.release();
			logger.info("End test of KeyBoardDeviceLinux class");
		} catch (JposException e) {
			if (e.getOrigException() != null) {
				logger.info("Origen JposException", e.getOrigException());
			}
			logger.info("JposException", e);
		}
	}
}
