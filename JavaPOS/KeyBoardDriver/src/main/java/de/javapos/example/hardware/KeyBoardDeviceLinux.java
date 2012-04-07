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
import de.javapos.example.ThreadSafe;
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
	private DataInputStream device;
	private FileChannel fileChannelLock;
	private boolean isClaimed;
	private boolean autoDisable;
	private boolean isEnabled;
	private JposEventListener eventListener;
	private FileLock lock;
	
	@Override
	public boolean isOpened() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() throws JposException {
		// TODO Auto-generated method stub

	}

	@Override
	public void claim() throws JposException {
		this.claim(0);
	}

	/**
	 * Not thread-safe.
	 * This method just works in the following cases:
	 * 1. Several processes trying to claim the device. Just one will own the device and the another 
	 * receive a nice exception.
	 * 2. Several threads in the same Java process trying to claim the device. If the class loader is the same for
	 * both threads for sure one will lock and the another one receive an exception.
	 * This method probably is not going to work in this case:
	 * 1. Several threads in the same Java process with different class loaders. The java.nio classes must be in the
	 * same class loader, otherwise (I have not tested it) the threads are not going to realize the file was previously locked
	 * because the FileLock implementation is using static fields in order to find out if the file was locked before.
	 * See: SharedFileLockTable in FileChannelImpl where the static classes are being used. By the way, since Java 5.0 
	 * it is system-wide but in 1.4 it was not. The doubt is with different class loaders for each thread... Is this "system-wide" 
	 * implementation going to work? I do not think so.
	 */
	@Override
	public void claim(int paramInt) throws JposException {
		FileLock lock = null;
		
		try {
			this.fileChannelLock = new FileOutputStream(javaposKeyBoardLock).getChannel();
		} catch (FileNotFoundException e) {
			throw new JposException(JposConst.JPOS_E_NOTCLAIMED, "File not found.",e);
		}
		
		if (paramInt == -1) {
			try {
				lock = this.fileChannelLock.lock();
			//I do not like catching RunTimeExceptions but I have no choice...
			} catch (OverlappingFileLockException e) {
				throw new JposException(JposConst.JPOS_E_CLAIMED, "Some thread from this process already claimed this device.",e);
			} catch (IOException e) {
				throw new JposException(JposConst.JPOS_E_CLAIMED, "Error while trying to claim device.",e);
			}
		}
		else {
			long initTime = System.nanoTime();
			do {
				try {
					lock = this.fileChannelLock.tryLock();
				//I do not like catching RunTimeExceptions but I have no choice...
				} catch (OverlappingFileLockException e) {
					throw new JposException(JposConst.JPOS_E_CLAIMED, "Some thread from this process already claimed this device.",e);
				} catch (IOException e) {
					throw new JposException(JposConst.JPOS_E_CLAIMED, "Error while trying to claim device.",e);
				}
			} while ((((System.nanoTime() - initTime)/ 1000000) < paramInt) && (lock == null));
			
			if (lock == null) {
				throw new JposException(JposConst.JPOS_E_TIMEOUT, "Timeout while trying to claim device.");
			}
			else {
				this.lock = lock;
			}
		}
	}

	@Override
	public void release() throws JposException {
		try {
			this.lock.release();
		} catch (IOException e) {
			throw new JposException(JposConst.JPOSERR, "Device not found.",e);
		}
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

		if (this.device == null) {
			throw new JposException(JposConst.JPOSERR, "There is not an assigned device", 
								new NullPointerException("The device field has null value"));
		}

		if (this.isEnabled) {
			throw new JposException(JposConst.JPOSERR, "The device was not disabled.");
		}
		else {
			if (this.thread.isAlive()) {
				throw new JposException(JposConst.JPOSERR, "The device was disabled but the hardware " +
										"thread is still running. Overloaded system?");
			}
			else {
				this.thread = new Thread (new HardwareLoop(this.device, this.eventListener), "KeyBoardDeviceLinux-Thread");
				this.thread.setUncaughtExceptionHandler(new DriverHWUncaughtExceptionHandler());
				this.thread.start();
				this.isEnabled = true;
			}
		}
	}

	@Override
	public void disable() throws JposException {
		this.thread.interrupt();
		this.isEnabled = false;
	}

	@Override
	public boolean isEnabled() {
		return this.isEnabled;
	}

	@Override
	public void autoDisable(boolean autoDisable) {
		this.autoDisable = autoDisable;
	}

	@Override
	public void addEventListener(JposEventListener jposEventListener)
			throws JposException {
		this.eventListener = jposEventListener;
	}

	@Override
	public void removeEventListener(JposEventListener jposEventListener) {
		this.eventListener = null;
	}

	@Override
	public boolean write(byte[] paramArrayOfByte, int paramInt1, int paramInt2,
			int paramInt3) throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2,
			int paramInt3) throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int writeRead(byte[] paramArrayOfByte1, int paramInt1,
			int paramInt2, byte[] paramArrayOfByte2, int paramInt3,
			int paramInt4, int paramInt5) throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDescription(int paramInt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void flush(int paramInt) throws JposException {
		// TODO Auto-generated method stub

	}
	
	
	/**
	 * 
	 * NO OLVIDAR try/finally PARA DEJAR EL DISPOSITIVO CORRECTAMENTE
	 * @throws JposException
	 */
	@Override
	public void device(String device) throws JposException {
		try {
			this.device = new DataInputStream(Channels.newInputStream(new FileInputStream(device).getChannel()));
		} catch (FileNotFoundException e) {
			throw new JposException(JposConst.JPOS_E_NOHARDWARE, "Device not found.",e);
		}
	}
	
	@ThreadSafe
	private class HardwareLoop implements Runnable {
		private final DataInputStream device;
		private final JposEventListener eventListener;
		
		private HardwareLoop (DataInputStream device, JposEventListener eventListener) {
			this.device = device;
			this.eventListener = eventListener;
		}
		
		@Override
		public void run() {
			//OS 64 bits
			byte []buffer = new byte[24];
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
					
					ch1 = buffer[19];
			        ch2 = buffer[18];
			        code = (short)((ch1 << 8) + (ch2 << 0));
			        
			        ch1 = buffer[17];
			        ch2 = buffer[16];
			        type = (short)((ch1 << 8) + (ch2 << 0));
			        
			        
			        ch1 = buffer[23];
			        ch2 = buffer[22];;
			        ch3 = buffer[21];
			        ch4 = buffer[20];
			        value = ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
			        
			        if (type == KeyBoardDeviceLinux.EV_KEY) {
			        	//this.eventListener.inputAvailable(code);
			        	logger.debug("Captured key " + "type: " + type + " code: " + code + " value: " + value);
			        }
				}
			} catch (EOFException e) {
				logger.error("Something went really bad. Impossible end of file while reading keyboard device!!!!", e);
			} catch (IOException e) {
				//We reach this code because the thread was interrupted (disable) or because there was an I/O error
				//logger.debug or logger.error if it was an error I am not going to log it... :/
				logger.debug("Finished KeyBoardDeviceLinux Thread", e);
			} finally {
				try {
					this.device.close();
					//SI EL HILO MUERE ¿DEBERÍA DEJAR EL ESTADO DEL DISPOSITIVO LOGICO JAVAPOS
					//EN UN MODO CONSISTENTE?
					//liberar el LOCK SI EL HILO MUERE DE FORMA INESPERADA????
					//¿como moriria este hilo de forma inesperada?? por ejemplo por RunTimeException
					//¿Como hago saber a la aplicacion que este hilo murio de forma inesperada por RunTimeException y el teclado
					//ha dejado de funcionar... Si el teclado deja de funcionar no sería mejor parar toda la aplicacion y ver
					//que pasa...
				} catch (IOException e1) {
					logger.warn("Something went wrong while closing the channel",e1);
				}
			}
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
		String device ="/dev/input/event6";
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
