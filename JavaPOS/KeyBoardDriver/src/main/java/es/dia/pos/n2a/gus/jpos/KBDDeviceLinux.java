/**
 * 
 */
package es.dia.pos.n2a.gus.jpos;

import jpos.JposException;

/**
 * @author
 *
 */
public class KBDDeviceLinux implements BaseKeyBoardDriver {

	/* (non-Javadoc)
	 * @see es.dia.pos.n2a.gus.jpos.KeyBoardDriver#isOpened()
	 */
	@Override
	public boolean isOpened() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see es.dia.pos.n2a.gus.jpos.KeyBoardDriver#close()
	 */
	@Override
	public void close() throws JposException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see es.dia.pos.n2a.gus.jpos.KeyBoardDriver#claim()
	 */
	@Override
	public void claim() throws JposException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see es.dia.pos.n2a.gus.jpos.KeyBoardDriver#claim(int)
	 */
	@Override
	public void claim(int paramInt) throws JposException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see es.dia.pos.n2a.gus.jpos.KeyBoardDriver#release()
	 */
	@Override
	public void release() throws JposException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see es.dia.pos.n2a.gus.jpos.KeyBoardDriver#isClaimed()
	 */
	@Override
	public boolean isClaimed() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see es.dia.pos.n2a.gus.jpos.KeyBoardDriver#enable()
	 */
	@Override
	public void enable() throws JposException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see es.dia.pos.n2a.gus.jpos.KeyBoardDriver#disable()
	 */
	@Override
	public void disable() throws JposException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see es.dia.pos.n2a.gus.jpos.KeyBoardDriver#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see es.dia.pos.n2a.gus.jpos.KeyBoardDriver#write(byte[], int, int, int)
	 */
	@Override
	public boolean write(byte[] paramArrayOfByte, int paramInt1, int paramInt2,
			int paramInt3) throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see es.dia.pos.n2a.gus.jpos.KeyBoardDriver#read(byte[], int, int, int)
	 */
	@Override
	public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2,
			int paramInt3) throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see es.dia.pos.n2a.gus.jpos.KeyBoardDriver#writeRead(byte[], int, int, byte[], int, int, int)
	 */
	@Override
	public int writeRead(byte[] paramArrayOfByte1, int paramInt1,
			int paramInt2, byte[] paramArrayOfByte2, int paramInt3,
			int paramInt4, int paramInt5) throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see es.dia.pos.n2a.gus.jpos.KeyBoardDriver#getDescription(int)
	 */
	@Override
	public String getDescription(int paramInt) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see es.dia.pos.n2a.gus.jpos.KeyBoardDriver#flush(int)
	 */
	@Override
	public void flush(int paramInt) throws JposException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addEventListener(DCALEventListener paramDCALEventListener)
			throws JposException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeEventListener(DCALEventListener paramDCALEventListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
