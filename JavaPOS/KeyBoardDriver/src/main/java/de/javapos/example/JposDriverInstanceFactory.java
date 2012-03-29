/**
 * 
 */
package de.javapos.example;

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
