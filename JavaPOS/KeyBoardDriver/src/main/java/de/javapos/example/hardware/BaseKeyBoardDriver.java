/**
 * 
 */
package de.javapos.example.hardware;

import de.javapos.example.queue.JposEventListener;
import jpos.JposException;

/**
 * @author
 *
 */
public interface BaseKeyBoardDriver {
	
	public boolean isOpened();

	public void close() throws JposException;
	
	/**
	 * Claim device.
	 *
	 * @param  paramInt -1 wait forever
	 * @throws JposException in case of any error while trying to claim the device.
	 */
	public void claim(int paramInt) throws JposException;

	/**
	 * Release device.
	 *
	 * @throws JposException in case of any error while trying to release the device.
	 */
	public void release() throws JposException;
	
	public boolean isClaimed();
	
	public void enable() throws JposException;
	
	public void disable() throws JposException;
	
	public boolean isEnabled();
	
	public void addEventListener(JposEventListener jposEventListener) throws JposException;
	
	public void removeEventListener(JposEventListener jposEventListener);
	
	public boolean write(byte[] paramArrayOfByte, int paramInt1, int paramInt2, 
			int paramInt3) throws JposException;
	
	public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2, 
		int paramInt3) throws JposException;
	
	public int writeRead(byte[] paramArrayOfByte1, int paramInt1, 
			int paramInt2, byte[] paramArrayOfByte2, int paramInt3, int paramInt4, 
			int paramInt5) throws JposException;
	
	public String getDescription(int paramInt);
		
	public void flush(int paramInt) throws JposException;
	
	public void device(String device) throws JposException;

	public void autoDisable (boolean autoDisable);
}
