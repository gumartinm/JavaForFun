package de.javapos.example;
 
import de.javapos.example.hardware.BaseKeyBoardDriver;
import de.javapos.example.queue.JposEventListener;
import de.javapos.example.queue.JposEventQueue;
import de.javapos.example.queue.JposEventQueueImpl;
import jpos.JposConst;
import jpos.JposException;
import jpos.POSKeyboardConst;
import jpos.loader.JposServiceLoader;
import jpos.loader.JposServiceManager;
import jpos.services.EventCallbacks;
import jpos.services.POSKeyboardService112;
import jpos.config.JposEntry;
import jpos.config.JposEntryRegistry;
import jpos.events.DataEvent;
import jpos.events.JposEvent;
 
public class MyPOSKeyboard implements POSKeyboardService112, JposConst, POSKeyboardConst
{
	private static final int deviceVersion12  = 1002000;
	private String logicalname;
	private EventCallbacks callbacks;
	private JposEntryRegistry jposEntryRegistry;
	private JposEntry jposEntry;
	private String device;
	private int maxEvents;
	private BaseKeyBoardDriver deviceDriver;
	private JposDriverInstanceFactory jposDriverFactory;
	private JposEventQueue jposEventQueue;
	private JposEventListener eventListener;
	
	@Override
	public int getCapPowerReporting() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPowerNotify() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPowerState() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPowerNotify(int arg0) throws JposException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearInput() throws JposException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getAutoDisable() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getCapKeyUp() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDataCount() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getDataEventEnabled() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getEventTypes() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPOSKeyData() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPOSKeyEventType() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAutoDisable(boolean arg0) throws JposException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDataEventEnabled(boolean arg0) throws JposException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEventTypes(int arg0) throws JposException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkHealth(int arg0) throws JposException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void claim(int arg0) throws JposException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws JposException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void directIO(int arg0, int[] arg1, Object arg2)
			throws JposException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCheckHealthText() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getClaimed() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getDeviceEnabled() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDeviceServiceDescription() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDeviceServiceVersion() throws JposException {
		return this.deviceVersion12;
	}

	@Override
	public boolean getFreezeEvents() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPhysicalDeviceDescription() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPhysicalDeviceName() throws JposException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getState() throws JposException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void open(String paramString, EventCallbacks paramEventCallbacks) throws JposException {
		this.logicalname = paramString;
		
		//La clase EventCallbacks se crea en la clase POSKeyboard, y contiene 
		//los objectos listeners generados en nuestro wrapper
		//Esos objectos lo que contienen son punteros a funciones (en Java 
		//realmente todo son punteros) conocidas como callbacks
		//
		//  this.dataListener = new DataListener() {
        //    @Override
		//	  public void dataOccurred(final DataEvent dataEvent) {
		//		 JPosScaleWrapper.this.processDataOccurredOccurred(dataEvent);  <--- Esto es el "puntero" a funcion que será lo que se almacene en la clase EventCallbacs (se almacena el objecto DataListener que implementa la callback o puntero a funcion) ya a nivel Jpos (se introduce en el nivel Jpos algo creado en un nivel superior, es decir, en nuestro Wrapper)
		//	  }
		//  }
		//  this.scale.addDataListener(this.dataListener); <-- addDataListener es un método definido en POSKeyboard que introduce el objeto DataListener en un Vector (al introducir ese objecto lo que está haciendo es pasar el callback, en Java lo han hecho así, en C,C++ por ejemplo podría haberse pasado directamente el puntero a función
		this.callbacks = paramEventCallbacks; //<---- En este objeto dentro del Vector está el objecto DataListener creado en el Wrapper
		
		//Podemos extraer los valores de configuracion del jpos.xml o jpos.properties tal como sigue:
		//(Wincord usa la clase OSServiceConfiguration para hacer lo mismo)
		JposServiceManager localJposServiceManager = JposServiceLoader.getManager();
		//Esto contiene todo el jpos.xml o el jpos.properties correctamente ordenado y parseado
		this.jposEntryRegistry = localJposServiceManager.getEntryRegistry();
		//Así podemos obtener toda la configuracion localizada en el jpos.xml o .properties 
		//para un determinado dispostivo
		//cuyo nombre pasamos aqui como parametro de entrada.
		//NOTA: Roberto lo que hacia era en la Factoria hacer esto mismo (recuperar 
		//el jposEntryRegistry y el jposEntry
		//y pasarselo directamente al constructor. Mi constructor no hace nada 
		//(no lo he implementado, al menos todavia)
		//En mi caso el constructor no hace eso y tengo que recuperarlo aquí
		//(keine Ahnung was ist besser)
		this.jposEntry = this.jposEntryRegistry.getJposEntry(paramString);
		
		
		//Aqui comienzo a leer toda la posible configuracion para cumplir con un Keyboard POS.
		//Finalmente de este modo podemos ir obteniendo la configuracion de ese 
		//dispositivo pasado los campos
		String str = readJposConfiguration(this.jposEntry,this.jposEntryRegistry);
		if ( str != null)
		{
			//En caso de devolver un string, este string es el mensaje de error.
			throw new JposException(JPOS_E_ILLEGAL, str);
		}
		
		//Recuperamos el codigo Java que lee eventos HW del teclado y los almacena en el 
		//DataEvent. Si hubiera que modificar el driver podria hacerse creando un nuevo
		//interfaz que extiende de BaseKeyBoardDriver y aqui deberiamos aniadir algo
		//que hiciera cast al nuevo interfaz que extiende de BaseKeyBoardDriver. De esta forma
		//si hay que aniadir algo al driver no hay que modificar casi nada del codigo. 
		//Ademas si queremos cambiar el driver lo unico que hay que hacer es crear una nueva
		//clase que implemente el interfaz BaseKeyBoardDriver y poner el nombre de la clase
		//en el jpos.xml o en el jpos.properties en el campo driverClass que es donde he definido
		//que se ponga el nombre de la clase que implementa el driver. En Wincord era en dcalClass.
		//Por ejemplo yo ahora tendria que aniadir un campos driverClass al jpos.xml de la N2A
		//con la clase de.javapos.example.hardware.KeyBoardDeviceLinux
		//Lo que no me gusta es que la factoria si se cambiara debe hacerse aquí en el codigo :S
		//TODO: poner la factoria tambien como un campo en el jpos.xml y extraerla por reflexión.
		this.jposDriverFactory = new JposDriverInstanceFactoryImpl();
		this.deviceDriver = this.jposDriverFactory.createInstance(paramString, this.jposEntry, 
																	BaseKeyBoardDriver.class);
		
		if (this.deviceDriver == null) {
			throw new JposException(JPOS_E_NOEXIST, "Class: " + this.jposEntry.getPropertyValue("driverClass") + 
					" not found in current class loader.");
		}
		
		//Crear la cola donde almacenamos eventos estilo FIFO.
		//Esto tambien puede hacerser en jpos.xml y queda todo como un puzle LOL
		//TODO: poner la cola de eventos en el jpos.xml
		this.jposEventQueue = new JposEventQueueImpl();

		//estaria genial poner esto en el jpos.xml y asi puede tambien cambiar el eventlistener
		this.eventListener = new MyPOSKeyBoardEventListener(this.jposEventQueue, this.callbacks);

		this.deviceDriver.addEventListener(eventListener);
		
		
	}
	
	private String readJposConfiguration(JposEntry jposEntry, JposEntryRegistry jposEntryRegistry)
	{
		String str;
		final String device = "device";
		final String maxEvents = "maxEvents";
		final String keyTable = "keyTable";
	    JposEntry tableJposKey;
		
		//Primero: obtener el dispositivo de caracteres.
		if ( jposEntry.hasPropertyWithName(device) ){
			//Chequeamos que lo que estamos leyendo es un String (deberia ser algo como /dev/wn_javapos_kbd0)
			if (String.class == jposEntry.getPropertyType(device))
			{
				this.device = (String)jposEntry.getPropertyValue(device);
			}else
				return "open-readJposConfiguration(): illegal device name.";
		}else
			return "open-readJposConfiguration(): A property called 'device' with " +
					"the path of the character device of the keyboard is needed";
		
		
		//Segundo: obtener el numero maximo de eventos.
		if ( jposEntry.hasPropertyWithName(maxEvents) ){
			if ((str = (String)jposEntry.getPropertyValue(maxEvents)) != null)
			{
				try
				{
					this.maxEvents = Integer.decode(str).intValue();
				}
				catch (NumberFormatException localNumberFormatException)
				{
					return "open-readJposConfiguration(): illegal jpos property maxEvents '" + 
							str + "is not a number";
				}
			}
		}else
			return "open-readJposConfiguration(): A property called 'maxEvents' with is needed";
		
		
		//Tercero: obtener la tabla de caracteres del teclado (si existe) puede ser de Dia o de Wincor
		//PASO DE HACER ESTO AHORA, ME ABURRE LOL.
		if ( jposEntry.hasPropertyWithName(keyTable)){
			if (String.class == jposEntry.getPropertyType(keyTable)){
				str = (String)jposEntry.getPropertyValue(keyTable);
				tableJposKey=jposEntryRegistry.getJposEntry(str);
			}else
				return "open-readJposConfiguration(): illegal jpos property keyTable '" + 
						str + "is not a String";
			
			/*PASO DE HACER ESTO DE MOMENTO, PARSEAR ESO ME ABURRE LOL*/
			
			
		}
		
		return null;
	}

	@Override
	public void release() throws JposException {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void setDeviceEnabled(boolean deviceEnable) throws JposException {
		this.deviceDriver.enable();
		
	}

	@Override
	public void setFreezeEvents(boolean arg0) throws JposException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteInstance() throws JposException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getCapCompareFirmwareVersion() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getCapUpdateFirmware() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void compareFirmwareVersion(String firmwareFileName, int[] result)
			throws JposException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateFirmware(String firmwareFileName) throws JposException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getCapStatisticsReporting() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getCapUpdateStatistics() throws JposException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resetStatistics(String statisticsBuffer) throws JposException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveStatistics(String[] statisticsBuffer)
			throws JposException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStatistics(String statisticsBuffer) throws JposException {
		// TODO Auto-generated method stub
		
	}

	//o mejor ¿una clase declarada en el jpos.xml que implementa JposEventListener y a la
	//que en el constructor le pasamos las callbacks? :/
	@ThreadSafe
	private class MyPOSKeyBoardEventListener implements JposEventListener {
		private final JposEventQueue jposEventQueue;
		private final EventCallbacks callbacks;

		//cuando sepa como usara la tabla de traduccion de teclas aqui debo pasarsela
		//es esta clase en los metodos que implementa donde uso la tabla de traduccion de teclas
		private MyPOSKeyBoardEventListener (JposEventQueue jposEventQueue, EventCallbacks callbacks) {
			this.jposEventQueue = jposEventQueue;
			this.callbacks = callbacks;
			//En el futuro un campo con la tabla de traduccion tecla/codigo
		}

		@Override
		public void inputAvailable(int input) {
			try {
				this.jposEventQueue.putEvent(new DataEvent(this.callbacks, input));
			} catch (InterruptedException e) {
				//restore interrupt status.
				Thread.currentThread().interrupt();
			}
		}

		@Override
		public void errorOccurred(int error) {
			// TODO Auto-generated method stub
		}

		@Override
		public void statusUpdateOccurred(int status) {
			// TODO Auto-generated method stub
		}
	}
}