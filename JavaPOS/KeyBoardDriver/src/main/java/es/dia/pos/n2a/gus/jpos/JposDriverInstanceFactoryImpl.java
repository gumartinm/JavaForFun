package es.dia.pos.n2a.gus.jpos;


import java.lang.reflect.Constructor;

import jpos.JposConst;
import jpos.JposException;
import jpos.config.JposEntry;

/**
 * Retrieve the device HW driver using this instance factory.
 * @author 
 */
public class JposDriverInstanceFactoryImpl implements JposDriverInstanceFactory
{	
	
    /**
     * @input_parameters: 
     */ 
	@Override
	public <E> E createInstance(String logicalName, JposEntry entry, Class<E> typeClass) throws JposException 
	{
    	if (entry.getPropertyValue("driverClass") == null)
        {
            throw new JposException(JposConst.JPOS_E_NOSERVICE, "Missing driverClass JposEntry");
        }    	
    	E driverInstance = null;
        try
        {
            String serviceClassName = (String)entry.getPropertyValue("driverClass");
            Class<?> serviceClass = Class.forName(serviceClassName);
            Class<?>[] params = new Class<?>[0];
            Constructor<?> ctor = serviceClass.getConstructor(params);
            if (typeClass.isInstance(ctor.newInstance((Object[])params)) )
            {
            	//This cast is correct (IMHO) because we are checking the right type
            	//with the method isInstance.
            	//Why must I declare this local variable with SuppressWarnings? This is weird...
            	@SuppressWarnings("unchecked") E aux = (E)ctor.newInstance((Object[])params);
            	driverInstance = aux;
            }
        }
        catch(Exception e)
        {
        	throw new JposException(JposConst.JPOS_E_NOSERVICE, "Could not create " +
            		"the driver instance for device with logicalName= " + logicalName, e);
        }   
        return driverInstance;
	}
}
