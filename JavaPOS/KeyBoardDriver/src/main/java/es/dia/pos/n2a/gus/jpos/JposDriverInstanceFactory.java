/**
 * 
 */
package es.dia.pos.n2a.gus.jpos;

import jpos.JposException;
import jpos.config.JposEntry;

/**
 * @author
 *
 */
public interface JposDriverInstanceFactory {

	/**
	 * 
	 * @param logicalName 
	 * @param jposEntry
	 * @param entry
	 * @return
	 * @throws JposException
	 */
	public <E> E createInstance(String logicalName, JposEntry entry, Class<E> pruebaClass) throws JposException;
}
