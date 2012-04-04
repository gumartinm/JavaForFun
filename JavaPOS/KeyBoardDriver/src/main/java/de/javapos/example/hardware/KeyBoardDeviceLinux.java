package de.javapos.example.hardware;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import jpos.JposConst;
import jpos.JposException;
import org.apache.log4j.Logger;
import de.javapos.example.ThreadSafe;
import de.javapos.example.queue.JposEventListener;

public class KeyBoardDeviceLinux implements BaseKeyBoardDriver {
	private static final Logger logger = Logger.getLogger(KeyBoardDeviceLinux.class);
	//value EV_KEY from include/linux/input.h
	private static final int EV_KEY = 1;
	private Semaphore mutex = new Semaphore(1, true);
	//No me gusta nada esto, simplemente es para evitar mirar si thread es null la primera vez en el metodo enable...
	private Thread thread = new Thread ("KeyBoardDeviceLinux-Thread");
	private InputStream device;
	private FileChannel fileChannel;
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

	@Override
	public void claim(int paramInt) throws JposException {
		if (paramInt == -1) {
			try {
				this.lock = this.fileChannel.lock();
			} catch (IOException e) {
				throw new JposException(JposConst.JPOS_E_CLAIMED, "Device not found.",e);
			}
		}
		else {
			long initTime = System.currentTimeMillis();
			do {
				try {
					this.lock = this.fileChannel.tryLock();
				} catch (IOException e) {
					throw new JposException(JposConst.JPOS_E_CLAIMED, "Error while trying to claim device.",e);
				}
			} while (((System.currentTimeMillis() - initTime) < paramInt) && (this.lock == null));
			if (this.lock == null) {
				throw new JposException(JposConst.JPOS_E_TIMEOUT, "Timeout while trying to claim device.");
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
										"thread is still running. Is your CPU/CPUs overloaded?");
			}
			else {
				this.thread = new Thread (new HardwareLoop(this.device), "KeyBoardDeviceLinux-Thread");
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
			this.fileChannel = new FileInputStream(device).getChannel();
			this.device = Channels.newInputStream(this.fileChannel);
		} catch (FileNotFoundException e) {
			throw new JposException(JposConst.JPOS_E_NOHARDWARE, "Device not found.",e);
		}
	}
	
	@ThreadSafe
	private class HardwareLoop implements Runnable {
		private final InputStream device;
		
		private HardwareLoop (InputStream device) {
			this.device = device;
		}
		
		@Override
		public void run() {
			//Para 32 bits
			//En 64 bits me parece que struct timeval tiene un valor diferente. Averigüar valor de
			//struct timeval en 64 bits :(
			byte []buffer = new byte[32];
			short code = 0;
			short type = 0;
			int value = 0;
			int ch1 = 0;
			int ch2 = 0;
			int ch3 = 0;
			int ch4 = 0;
			
			try {
				while (!Thread.currentThread().isInterrupted()) {
					//using command: evtest /dev/input/event4
					this.device.read(buffer);
					ch1 = buffer[11];
			        ch2 = buffer[10];
			        code = (short)((ch1 << 8) + (ch2 << 0));
			        
			        ch1 = buffer[9];
			        ch2 = buffer[8];
			        type = (short)((ch1 << 8) + (ch2 << 0));
			        
			        
			        ch1 = buffer[15];
			        ch2 = buffer[14];;
			        ch3 = buffer[13];
			        ch4 = buffer[12];
			        value = ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
			        
			        
			        if (type == KeyBoardDeviceLinux.EV_KEY) {
			        	System.out.println("type: " + type + " code: " + code + " value: " + value);
			        }
				}
				System.out.println("KeyBoardDeviceLinux: Hardware's thread finished.");
				//logger.debug("KeyBoardDeviceLinux: Hardware's thread finished.");
			} catch (IOException e) {
				//logger.error("KeyBoardDeviceLinux:", e);
				e.printStackTrace();
			} finally {
				try {
					this.device.close();
					//SI EL HILO MUERE ¿DEBERÍA DEJAR EL ESTADO DEL DISPOSITIVO LOGICO JAVAPOS
					//EN UN MODO CONSISTENTE?
				} catch (IOException e1) {
					e1.printStackTrace();
					//logger.warn("KeyBoardDeviceLinux:", e1);
				}
			}
		}
	}

	private class DriverHWUncaughtExceptionHandler implements UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			System.out.println("Thread " + t.getName() + " " + e);
			//logger.warn("Exception not expected while running thread " + t.getName() , e);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] param) throws InterruptedException
	{
		//Because I do not have a POS I am going to use the keyboard of my computer.
		//see: /dev/input/by-path/
		//And the Keyborad scancode is for USB devices.
		String device ="/dev/input/event4";
		
		KeyBoardDeviceLinux driver = new KeyBoardDeviceLinux();
		
		System.out.println("Main test of KeyBoardDeviceLinux class");
		try {
			driver.device(device);
			driver.claim();
			driver.enable();
			Thread.sleep(5000);
			driver.disable();
			System.out.println("End test of KeyBoardDeviceLinux class");
		} catch (JposException e) {
			e.getOrigException().printStackTrace();
			e.printStackTrace();
		}
	}
}
