/**
 * 
 */
package printer.helper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * <p>
 *  First of all some thoughts:
 * </p>
 * <p>
 *  The device wrapper classes lack methods to change themselves the device configuration. 
 * These methods can be found in the Logical layer, so this layer is device-dependent
 * (it just will work with JavaPOS devices)
 * Actually we have two options in order to write specific methods related to the device
 * configuration. We can add these methods to the Logical layer or create a new Helper Class 
 * for them but... What about if we need to add device specific methods? The Logical layer is right
 * now JavaPOS dependent (although it was intended to be a device abstraction...) if we add 
 * device specific methods such as methods related to a specific kind of Printer, the Logical
 * layer will be useless and the code will be a mess (if some day a device is changed we will 
 * have to search the whole code to find out what code is related with that device and 
 * we will have to create new classes in the Logical layer for every device ) 
 * </p>
 * <p>
 *      So this Helper is intended to store methods which do not fit into the Logical layer because
 * they make this layer not just JPOS dependent but completely device-dependent. All of this because
 * of the wrapper classes lack the required methods for these kinds of operations.
 * </p>
 * <p>
 *  To sum up, before adding in this class new methods you should ask yourself two questions:
 * <ul>
 * <li>1. Is your method device independent? If the answer is yes, without doubt write your method in
 * the Logical layer. If the answer is no you have to ask yourself a second question.</li>
 * <br>
 * <li>2. Is this method device or JavaPOS dependent? If the answer is JavaPOS-dependent write 
 * your new method in the Logical layer and if it is device-dependent use this class for them.</li>
 * </ul>
 * </p>
 * <p>
 * Instances of this class are mutable. To use them concurrently, clients must surround each
 * method invocation (or invocation sequence) with external synchronization of the clients' choosing.
 * </p>
 */
public final class PrinterHelper {
	private static final Logger logger = Logger.getLogger(PrinterHelper.class);
	private static final String HOME_ENV_KEY = "HOME";
	private static final String FORM_PROPERTIES_FILE_RELATIVE_PATH = "etc/"+ FormConfiguration.PROPERTIES_FILE;
	private static final String TEMPLATE_FILE_EXTENSION = ".frm";
	private static final String PRINTER_PROPERTY = "MethodToPrintImage";
	private static final String PRINTER_PROPERTY_VALUE = "ESC/POS";
	
	 // Prevents instantiation from other classes. Why do you want to extend this class?
    private PrinterHelper() { }

	
    /**
	 * This method must try and fetch the model of the currently connected form printer (physically)
	 * on the given port
	 * 
	 * @param      formPrinterPort the printer port.
	 * @return     the model printer as a {@code FormPrinterModel enum} class
     *             The first <code>char</code> value is at index <code>0</code>.
	 */
	public static FormPrinterModel autoDetetectFormPrinterModel(final Port formPrinterPort) 
	{
		FormPrinterModel formPrinterModelDetected = null;
		
		final File scriptAutoDetect = new File(System.getProperty(HOME_ENV_KEY),"tools/detectatm88.sh");
		if (scriptAutoDetect.exists() && scriptAutoDetect.canExecute()){


			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final PrintStream output = new PrintStream(baos);

			final String command = "sh " + scriptAutoDetect.getAbsolutePath() +  	" "  + formPrinterPort.getPhysicalPath();
			try {
				int result;

				//result field stores exit status of the executed command
				result = LauncherProcesses.exec(command,output);

				if(result==0)
				{
					logger.debug("detectatm88 raw output: "+baos.toString());
					//From detectatm88.sh script we receive a string like this one: "RESULT: 2"
					//we must split the String and retrieve the integer value
					final String[] parameters = baos.toString().split(" ");
					final int model = Integer.parseInt(parameters[1].trim());
					switch(model){
					case 0:
						logger.warn("TM88 not physically detected: run again detectatm88.sh script");
						formPrinterModelDetected = null;
						break;
					case 2:
						logger.info("TM-T88II detected");
						formPrinterModelDetected = FormPrinterModel.EPSON_TM_88_II_III;
						PrinterHelper.addFormPrinterProperty(PRINTER_PROPERTY, PRINTER_PROPERTY_VALUE);
						break;
					case 3:
						logger.info("TM-T88III detected");
						formPrinterModelDetected = FormPrinterModel.EPSON_TM_88_II_III;
						PrinterHelper.addFormPrinterProperty(PRINTER_PROPERTY, PRINTER_PROPERTY_VALUE);
						break;
					case 4:
						logger.info("TM-T88IV detected");
						formPrinterModelDetected = FormPrinterModel.EPSON_TM_88_IV;
						PrinterHelper.addFormPrinterProperty(PRINTER_PROPERTY, PRINTER_PROPERTY_VALUE);
						break;
					case 5:
						logger.info("TM-T88V detected");
						formPrinterModelDetected = FormPrinterModel.EPSON_TM_88_V;
						PrinterHelper.removeFormPrinterProperty(PRINTER_PROPERTY);
						break;
					default:
						logger.warn("Code retrieved from detectatm88.sh script does not match.");
						formPrinterModelDetected = null;
						break;
					}		
				}
				//model not detected
				else{
					logger.warn("TM88 not physically detected. Not conected printer or it is not a TM-T88");
				}
			} catch (Exception e) {
				logger.error(e);
			} 
		}
		else {
			logger.warn("Script for tm88 detection not found or not exectuable");
		}

		return formPrinterModelDetected;
	}

	/**
	 * This method is intended to add new properties in DtgFormToPrint.properties file used by TM88 printers.
	 * 
	 * @param  property  the property to be added
	 * @param  value     value of property param. Possible values:
	 * 					 <br>
	 * 					 <ul>
	 * 					 <li>printBitmap</li>
	 * 					 <li>printMemoryBitmap</li>
	 * 					 <li>setBitmap</li>
	 * 					 <li>ESC/POS</li>
	 * 					 </ul>
	 * @throws FileNotFoundException 
	 * 		   If the file called DtgFormToPrint.properties
	 *         is not found.
	 */
	public static void addFormPrinterProperty(final String property, final String value) throws FileNotFoundException
	{
		//If the property does not exist we do not have to remove it
		if (FormConfiguration.getConfiguration().getSection().get(property) == null)
		{
			FormConfiguration.getConfiguration().getSection().add(property,value);
			OutputStream formConfigurationFile;
			formConfigurationFile = new FileOutputStream
					(new File(System.getProperty(HOME_ENV_KEY),FORM_PROPERTIES_FILE_RELATIVE_PATH));
			FormConfiguration.getConfiguration().save(formConfigurationFile);
		}
	}
	
	/**
	 * This method is intended to remove properties in DtgFormToPrint.properties file used by TM88 printers.
	 * 
	 * @param  property property to be removed
	 * @throws FileNotFoundException 
	 * 		   If the file called DtgFormToPrint.properties
	 *         is not found.
	 */
	public static void removeFormPrinterProperty(final String property) throws FileNotFoundException
	{
		FormConfiguration.getConfiguration().getSection().remove(property);
		OutputStream formConfigurationFile;
		formConfigurationFile = new FileOutputStream
				(new File(System.getProperty(HOME_ENV_KEY),FORM_PROPERTIES_FILE_RELATIVE_PATH));
		FormConfiguration.getConfiguration().save(formConfigurationFile);

	}
	
	/**
	 * This method returns a {@code List<File>} with the found files.
	 * 
	 * @param  extension the file extension name
	 * @return the found files
	 */
	public static List<File> findFilebyExtension(final String extension)
	{
		final File dir = ConfigurationHelper.getInstance().getFormsFolder();
		
		final FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(final File dir, final String name) {
		        return name.endsWith(extension);
		    }
		};
		List<File> filesList = new ArrayList<File>();

		for (File file : dir.listFiles(filter))
		{
		    filesList.add(file);
		}
		
		return filesList;
	}
	
	/**
	 * Read file line by line and stores the lines in a {@code List<String>}
	 * 
	 * @param file
	 * @param charset the file charset
	 * @return the file lines in a list
	 * @throws IOException
	 * 		   If an I/O error occurs while reading or trying to open the file
	 */
	public static List<String> readFile (final File file, final String charset) throws IOException
	{
		final BufferedReader bufferReader = new BufferedReader(new InputStreamReader
				(new FileInputStream(file), charset));
		
		List<String> contents = new ArrayList<String>();
		String currentLine = null;
		
		try {
			currentLine = bufferReader.readLine();
			while (currentLine != null)
			{
				contents.add(currentLine);
				currentLine = bufferReader.readLine();
			}
		} finally {
			bufferReader.close();
		}

		return contents;
		
	}
	
	/**
	 * Write lines to a file.
	 * 
	 * @param contents the lines to be written in a {@code List<String>}
	 * @param charset the file charset
	 * @param fileName the file name. Absolute or abstract pathname, it is your choice
	 * @throws IOException
	 * 		   If an I/O error occurs while writing or trying to open the file
	 */
	public static void writeFile (final List<String> contents, final String fileName, final String charset) throws IOException
	{
		final OutputStream outputStream = new FileOutputStream(fileName);
		final PrintStream output = new PrintStream(outputStream, true, charset);
		
		try {
			for (String line : contents )
			{
				output.println(line);
			}
		}
		finally {
		     output.close();
		}
	}
	
	/**
	 * Replace every line (file lines stored in a {@code List<String>})  
	 * with the lineReplacement if the line contains the pattern
	 * 
	 * @param contents the lines to be searched in a {@code List<String>}
	 * @param pattern the pattern to match in a line
	 * @param lineReplacement the whole line to replace in case of some match
	 * @return <b>true</b> if there is a match, otherwise <b>false</b>
	 */
	public static boolean replaceLine (final List<String> contents, final String pattern, final String lineReplacement)
	{
		final ListIterator<String> itr = contents.listIterator();
		boolean change = false;
		
		while (itr.hasNext())
		{
			if (itr.next().trim().contains(pattern)) {
	            itr.set(lineReplacement);
	            change = true;
			}
		}
		
		return change;
	}
	
	/**
	 * Method intended to be used by the Logical layer. Search the template files
	 * of the form printer, replace the lines in case of match and write on the hard disk the 
	 * new files.
	 * 
	 * @param pattern the pattern to match
	 * @param lineReplacement the whole line to replace in case of some match
	 * @throws IOException
	 * 		   If an I/O error occurs while writing or trying to open a file
	 */
	public static void searchandReplace (final String pattern, final String lineReplacement) throws IOException
	{
		final List<File> fileList = PrinterHelper.findFilebyExtension(TEMPLATE_FILE_EXTENSION);
		for (File file : fileList)
		{
			final List<String> contents = PrinterHelper.readFile(file, FormConfiguration.getCharset());
			if (PrinterHelper.replaceLine(contents, pattern, lineReplacement))
			{
				PrinterHelper.writeFile(contents, file.getAbsolutePath(), FormConfiguration.getCharset());
			}
		}
	}
	
	/**
	 *  Method intended to be used by the Logical layer. It is smart enough to find out
	 *  it there is a new form printer model
	 * 
	 * @param oldModel The previous form printer model
	 * @param newModel The new form printer model
	 * @throws IOException
	 * 		   If an I/O error occurs while writing or trying to open a file
	 */
	public static void changedPrinter (final HardwareModel oldModel, final HardwareModel newModel) throws IOException
	{
		if(FormPrinterModel.EPSON_TM_88_V.getXmlDescriptor().equals(newModel.getXmlDescriptor()))
		{
			PrinterHelper.searchandReplace("methodToPrintImage(ESC/POS)", "         COMMENT      \"usePageMode(false)\"");
			PrinterHelper.removeFormPrinterProperty(PRINTER_PROPERTY);
		}
		else if (FormPrinterModel.EPSON_TM_88_V.getXmlDescriptor().equals(oldModel.getXmlDescriptor()))
		{	
			PrinterHelper.searchandReplace("usePageMode(false)", "         COMMENT      \"usePageMode(false) methodToPrintImage(ESC/POS)\"");
			PrinterHelper.addFormPrinterProperty(PRINTER_PROPERTY, PRINTER_PROPERTY_VALUE);
		}
	}
}