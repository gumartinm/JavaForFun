package de.javapos.example;

import java.lang.reflect.Constructor;
import jpos.JposConst;
import jpos.JposException;
import jpos.config.JposEntry;
import jpos.loader.JposServiceInstance;
import jpos.loader.JposServiceInstanceFactory;

/**
 * Instance factory to get the JPOS device services
 * @author 
 */
public class JposServiceInstanceFactoryImpl implements JposServiceInstanceFactory
{    
    /**
     * @input_parameters: 
     * entry: (for jpos.xml and jpos.properties must be serviceClass)
     * logicalName: device's name (in jpos.xml or jpos.properties file, jpos.xml file by default)
     */
    public JposServiceInstance createInstance(String logicalName, JposEntry entry) throws JposException
    {
        if(!entry.hasPropertyWithName(JposEntry.SERVICE_CLASS_PROP_NAME))
        {
            throw new JposException(JposConst.JPOS_E_NOSERVICE, "The JposEntry does not contain the 'serviceClass' property");
        }
        JposServiceInstance serviceInstance = null;
        try
        {
            String serviceClassName = (String)entry.getPropertyValue(JposEntry.SERVICE_CLASS_PROP_NAME);
            Class<?> serviceClass = Class.forName(serviceClassName);
            Class<?>[] params = new Class<?>[0];
            Constructor<?> ctor = serviceClass.getConstructor(params);
            serviceInstance = (JposServiceInstance)ctor.newInstance((Object[])params);
        }
        catch(Exception e)
        {
            throw new JposException(JposConst.JPOS_E_NOSERVICE, "Could not create " +
            		"the service instance with logicalName= " + logicalName, e);
        }
        return serviceInstance;
    }
}
