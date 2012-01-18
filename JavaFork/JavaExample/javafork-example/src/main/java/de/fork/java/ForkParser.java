package de.fork.java;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * <p>
 * Class intended to parse the XML stream received from the daemon which is
 * waiting to run commands. These commands are sent by the method 
 * {@link es.dia.pos.n2a.util.os.unix.ForkDaemon#exec(String, String, int)} 
 * </p>
 * <p>
 * After processing one command the daemon sends a XML stream with the stderr,
 * stdout and return status of that command. With this class we extract those values
 * and we can retrieve them using the methods {@link #getStderr() }, {@link #getStdout()}
 * and {@link #getReturnValue()}
 * </p>
 * <p>
 * <pre>
 * <b>Example, stream received from daemon:</b>
 * {@code
 * <?xml version="1.0"?><salida><error><![CDATA[ls: no se puede acceder a bbb: No existe el fichero o el directorio
 * ls: no se puede acceder a aaa: No existe el fichero o el directorio
 * ls: no se puede acceder a dddd: No existe el fichero o el directorio
 * ]]></error><ret><![CDATA[2]]></ret></salida>
 * }
 * </pre>
 * </p>
 */
public class ForkParser extends DefaultHandler2{
	private static final Logger logger = Logger.getLogger(ForkParser.class);
    private StringBuffer accumulator = new StringBuffer();
	private String stderr = new String();
	private String stdout = new String();
	private String returnCode = new String();

	
	@Override
	public void endElement (String uri, String localName, String qName) {
		if (qName.equals("error")) {
			// After </error>, we've got the stderror
			stderr = stderr + accumulator.toString();
		} else if (qName.equals("out")) {
			// After </out>, we've got the stdout
			stdout = stdout + accumulator.toString();
		} else if (qName.equals("ret")) {
			returnCode = returnCode + accumulator.toString();
		}
	}

	
	@Override
	public void endDocument () throws SAXException
	{
		if (stderr.length() != 0) {
			String lastStderr = stderr.replaceFirst("\\\n$", "");
			stderr = lastStderr;
		}
		else {
			stderr = null;
		}
		if (stdout.length() != 0) {
			String lastStdout = stdout.replaceFirst("\\\n$", "");
			stdout = lastStdout;
		}
		else {
			stdout = null;
		}
	}
	
	/**
	 * Retrieve the standard error.
	 * 
	 * <pre>
	 * <b>From the above example with this method we are going to obtain this return parameter:</b>
	 * {@code
	 * ls: no se puede acceder a bbb: No existe el fichero o el directorio
	 * ls: no se puede acceder a aaa: No existe el fichero o el directorio
	 * ls: no se puede acceder a dddd: No existe el fichero o el directorio
	 * }
	 * </pre>
	 * 
	 * @return stderr
	 */
	public String getStderr() {
		return stderr;
		
	}
	
	
	/**
	 * Retrieve the standard output.
	 * 
	 * @see {@link ForkParser#getStderr()}
	 * @return stdout
	 */
	public String getStdout() {
		return stdout;
	}
	
	
	/**
	 * Retrieve the return code from the executed command.
	 * 
	 * @return return status, usually <code>0<code> means the command went OK.
	 */
	public String getReturnValue() {
		return returnCode;
	}
	
	
	@Override
	public void startElement (String uri, String localName, String qName, Attributes attributes) {
		accumulator.setLength(0);
	}
	
	
	@Override
	public void characters(char[] buffer, int start, int length) {
	    accumulator.append(buffer, start, length);
	}
	
	@Override
	public void warning(SAXParseException exception) {
		logger.error("WARNING line:" + exception.getLineNumber(), exception);
	}
	
	@Override
	public void error(SAXParseException exception) {
		logger.error("ERROR line:" + exception.getLineNumber(), exception);
	}
	
	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		logger.error("FATAL ERROR line:" + exception.getLineNumber(), exception);
		throw (exception);
	}
}