package de.javapos.example.hardware;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import jpos.JposConst;
import jpos.JposException;
import org.apache.log4j.Logger;
import de.javapos.example.ThreadSafe;
import de.javapos.example.queue.JposEventQueue;

public class KeyBoardDeviceLinux implements BaseKeyBoardDriver {
	private static final Logger logger = Logger.getLogger(KeyBoardDeviceLinux.class);
	//value EV_KEY from include/linux/input.h
	private static final int EV_KEY = 1;
	private Semaphore mutex = new Semaphore(1, true);
	private JposEventQueue eventQueue;
	private final ExecutorService exec = Executors.newSingleThreadExecutor();
	private DataInputStream device;
	private boolean isClaimed;
	private boolean autoDisable;
	
	@Override
	public boolean isOpened() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() throws JposException {
		// TODO Auto-generated method stub

	}

	
	/**
	 * 
	 * @throws JposException
	 */
	@Override
	public void claim() throws JposException {
		this.claim(0);
	}

	@Override
	public void claim(int paramInt) throws JposException {


	}

	@Override
	public void release() throws JposException {
		// TODO Auto-generated method stub

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
		//Mirar en capitulo 8? como hacer que no se encolen mas tareas
		//(por si se llama varias veces a enable ¿sin querer?)
		//me da a mí que en este caso es una chorrada usar Executor... :(
		//En el Executor hay que cambiar la Policy (si se puede) y usar: {@link ThreadPoolExecutor.DiscardPolicy}
		//Por defecto usa una que lanza excepcion RunTime si no pueden añadirse nuevas tareas :(
		this.exec.execute(new HardwareLoop(this.device));
	}

	@Override
	public void disable() throws JposException {
		this.exec.shutdownNow();
	}

	@Override
	public boolean isEnabled() {
		return this.exec.isTerminated();
	}

	@Override
	public void addEventListener(JposEventQueue jposEventQueue)
			throws JposException {
		this.eventQueue = jposEventQueue;

	}

	@Override
	public void removeEventListener(JposEventQueue jposEventQueue) {
		// TODO Auto-generated method stub

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
			this.device = new DataInputStream(new FileInputStream(device));
		} catch (FileNotFoundException e) {
			throw new JposException(JposConst.JPOS_E_NOHARDWARE, "Device not found.",e);
		}
	}
	
	@ThreadSafe
	private class HardwareLoop implements Runnable {
		private final DataInputStream device;
		
		private HardwareLoop (DataInputStream device) {
			this.device = device;
		}
		
		@Override
		public void run() {
			byte []buffer = new byte[16];
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
					this.device.readFully(buffer);
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
			} catch (IOException e) {
				logger.error("KeyBoardDeviceLinux:", e);
			} finally {
				try {
					this.device.close();
					//SI EL HILO MUERE ¿DEBERÍA DEJAR EL ESTADO DEL DISPOSITIVO LOGICO JAVAPOS
					//EN UN MODO CONSISTENTE?
				} catch (IOException e1) {
					logger.warn("KeyBoardDeviceLinux:", e1);
				}
			}
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
