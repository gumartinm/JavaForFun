/**
 * 
 */
package de.javapos.example.hardware;

import de.javapos.example.queue.JposEventQueue;
import jpos.JposException;

/**
 * @author
 *
 */
public interface BaseKeyBoardDriver {
	
	public boolean isOpened();

	public void close() throws JposException;
    
	public void claim() throws JposException;
	
	public void claim(int paramInt) throws JposException;

	public void release() throws JposException;
	
	public boolean isClaimed();
	
	public void enable() throws JposException;
	
	public void disable() throws JposException;
	
	public boolean isEnabled();
	
	public void addEventListener(JposEventQueue jposEventQueue) throws JposException;
	
	public void removeEventListener(JposEventQueue jposEventQueue);
	
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
}
