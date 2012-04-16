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
import jpos.JposConst;
import jpos.JposException;
import org.apache.log4j.Logger;
import de.javapos.example.queue.JposEventListener;

/**
 * Not thread-safe
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
	private FileChannel fileChannelLock;
	private DataInputStream device;
	private boolean isClaimed;
	//Usando volatile porque cumplo las condiciones de uso. Ver Java Concurrency in Practice PAG????
	private volatile boolean autoDisable;
	private JposEventListener eventListener;
	private FileLock lock;
	
	@Override
	public boolean isOpened() {
		return false;
	}

	@Override
	public void close() throws JposException {
		this.disable();
		this.release();
	}

	@Override
	public void claim() throws JposException {
		this.claim(0);
	}

	/**
	 * Not thread-safe.!!!!!
	 */
	@Override
	public void claim(int time) throws JposException {
		FileLock lock = null;
		
		if (this.isClaimed) {
			return;
		}

		try {
			this.fileChannelLock = new FileOutputStream(javaposKeyBoardLock).getChannel();
		} catch (FileNotFoundException e) {
			throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "File not found.",e);
		}

		if (time == -1) {
			try {
				//This method is not aware interrupt. :(
				lock = this.fileChannelLock.lock();
			//I do not like catching RunTimeExceptions but I have no choice...
			} catch (OverlappingFileLockException e) {
				logger.warn("Concurrent access in claim method not supported without synchronization or you have" +
							"more than one instances of the KeyBoardDeviceLinux class", e);
			} catch (IOException e) {
				throw new JposException(JposConst.JPOS_E_CLAIMED, "Error while trying to claim device.",e);
			}
		}
		else {
			long lastTime = System.nanoTime() + ((time + 250) * 1000000);
			//Espera activa. ¿Alguna idea de cómo hacer esto sin espera activa?  :(
			while((System.nanoTime() <= lastTime) && (lock == null)) {
				try {
					if ((lock = this.fileChannelLock.tryLock()) != null) {
						break;
					}
				//I do not like catching RunTimeExceptions but I have no choice...
				} catch (OverlappingFileLockException e) {
					logger.warn("Concurrent access in claim method not supported without synchronization or you have" +
								"more than one instances of the KeyBoardDeviceLinux class", e);
				} catch (IOException e) {
					throw new JposException(JposConst.JPOS_E_CLAIMED, "Error while trying to claim device.",e);
				}
				try {
                    this.wait(250);
                } catch(InterruptedException e) {
                	//restore interrupt status.
                	Thread.currentThread().interrupt();
                }	
			} 
			
			if (lock == null) {
				throw new JposException(JposConst.JPOS_E_TIMEOUT, "Timeout while trying to claim device.", null);
			}
		}
		
		if (lock != null) {
			//Just when concurrent access without synchronization (this method is not tread-safe) you could see
			//null value. claim does not support concurrent access in itself anyway I take counter measures in case
			//of someone forgets it.
			this.lock = lock;
			this.isClaimed = true;
		}
	}

	/**
	 * Not thread-safe.!!!!!
	 */
	@Override
	public void release() throws JposException {
		if (!this.isClaimed) {
			return;
		}
		
		if (this.lock != null) {
			try {
				//SI LLAMO A ESTO PERO FALLO AL INTENTAR LOCK NO CIERRO NI LIBERO EL LOCK REALIZADO
				//DESDE OTRO HILO :)
				this.lock.release();
			} catch (IOException e) {
				throw new JposException(JposConst.JPOSERR, "Error when releasing the keyboard file lock",e);
			}
		}

		if (this.fileChannelLock != null) {
			try {
				this.fileChannelLock.close();
				this.fileChannelLock = null;
			} catch (IOException e) {
				throw new JposException(JposConst.JPOSERR, "Error when closing the keyboard file lock", e);
			}
		}
		
		this.isClaimed = false;
	}

	@Override
	public boolean isClaimed() {
		return this.isClaimed;
	}

	/**
	 * 
	 * NO OLVIDAR try/finally PARA DEJAR EL DISPOSITIVO CORRECTAMENTE
	 * @throws JposException
	 * @throws RejectedExecutionException if this task cannot be
     * accepted for execution. CUIDADO RUNTIME NO OLVIDAR try/finally PARA DEJAR BIEN EL DISPOSITIVO
	 */
	@Override
	public void enable() throws JposException {
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
		if (this.thread.isAlive()) {
			throw new JposException(JposConst.JPOSERR, "The device was not disabled.", null);
		}
		else {
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
		}
	}

	@Override
	public void disable() throws JposException {
		//This method releases the Java NIO channel. It is thread safety. :) see: Interruptible
		this.thread.interrupt();
		try {
			this.thread.join();
		} catch (InterruptedException e) {
			//restore interrupt status.
			//QUE PASA SIN LLAMAN A INTERRUPT EN JOIN Y EL HILO NUNCA MURIERA
			//TAL Y COMO ESTA HECHO EL HILO NO TARDARA MUCHO EN MORIR (espero) ASI QUE 
			//AUNQUE ALGO ME INTERRUMPA NO DEBERIA PASAR GRAN COSA, LO UNICO QUE SI OTRO HILO
			//JUSTO AQUI INTENTA HACER ENABLE PUEDE QUE VEA EL HILO TODAVIA VIVO AUNQUE YA SE HABIA
			//PASADO POR EL DISABLE, ¿¿¿PERO TAN POCO ES UN GRAN PROBLEMA ESO??? LUEGO PARA MÍ QUE ¿ESTO ESTA OK?
			Thread.currentThread().interrupt();	
		}
	}

	@Override
	public boolean isEnabled() {
		return this.thread.isAlive();
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
		//OS 64 bits timeval 8 bytes  -> struct input_event 16 bytes
		//OS 32 bits timeval 16 bytes -> struct input_event 24 bytes
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
                    eventListener.inputAvailable(code);
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
			//This method releases the Java NIO channels. It is thread safety :) see: Interruptible 
			this.thread.interrupt();
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
