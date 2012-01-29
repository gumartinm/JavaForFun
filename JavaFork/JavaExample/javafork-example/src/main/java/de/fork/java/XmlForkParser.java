package de.fork.java;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * <p>
 * Class intended to parse the XML stream received from the daemon which is
 * waiting to run commands. These commands are sent by the method 
 * {@link de.fork.java.TCPForkDaemon#exec(String, String, int)} 
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
 * <p>
 * Instances of this class are mutable. To use them concurrently, clients must surround each
 * method invocation (or invocation sequence) with external synchronization of the clients choosing.
 * </p>
 */
public class XmlForkParser extends DefaultHandler2 {
	private static final Logger logger = Logger.getLogger(XmlForkParser.class);
    private StringBuffer accumulator = new StringBuffer();
	private String stderr = new String();
	private String stdout = new String();
	private String returnCode = new String();
	final SAXParserFactory spf = SAXParserFactory.newInstance();
	private final SAXParser saxParser;
	
	public XmlForkParser() throws ParserConfigurationException, SAXException {
		saxParser = spf.newSAXParser();
	}
	
	public void setStream(InputStream stream) throws SAXException, IOException {
		saxParser.parse(stream, this);
	}

	/**
	 * <p>
	 * The daemon sends a XML stream, we parse that stream and the results are
	 * stored in the instace fields {@link #stderr}, {@link #stdout} and {@link returnCode}
	 * </p>
	 * <p>
	 * Later we can retrieve the results with {@link #getStderr()}, {@link #getStdout()} and
	 * {@link #getReturnValue()}
	 * </p>
	 */
	@Override
	public void endElement (final String uri, final String localName, final String qName) {
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
	
	/**
	 * <p>
	 * This method removes the <code>\n<code> characters at the end of the stdout 
	 * or stderr stream.
	 * </p>
	 * 
     * @throws SAXException If any SAX errors occur during processing.
	 */
	@Override
	public void endDocument () throws SAXException
	{
		if (stderr.length() != 0) {
			String lastStderr = stderr.replaceFirst("\\\n$", "");
			stderr = lastStderr;
		}
		else {
			//if there is nothing from the stderr stream
			stderr = null;
		}
		if (stdout.length() != 0) {
			String lastStdout = stdout.replaceFirst("\\\n$", "");
			stdout = lastStdout;
		}
		else {
			//if there is nothing from the stdout stream
			stdout = null;
		}
	}
	
	/**
	 * Retrieve the standard error.
	 * When there is nothing from the standard error this method returns null.
	 * 
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
	 * <p>
	 * <pre>
	 * <b>From that example with this method we are going to obtain this return parameter:</b>
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
	 * When there is nothing from the standard output this method returns null.
	 * 
	 * @see {@link XmlForkParser#getStderr()}
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
	public int getReturnValue() {
		return new Integer(returnCode).intValue();
	}
	
	
	@Override
	public void startElement (final String uri, final String localName, 
									final String qName, final Attributes attributes) {
		accumulator.setLength(0);
	}
	
	
	@Override
	public void characters(final char[] buffer, final int start, final int length) {
	    accumulator.append(buffer, start, length);
	}
	
	
	@Override
	public void warning(final SAXParseException exception) {
		logger.error("WARNING line:" + exception.getLineNumber(), exception);
	}
	
	
	@Override
	public void error(final SAXParseException exception) {
		logger.error("ERROR line:" + exception.getLineNumber(), exception);
	}
	
	
	@Override
	public void fatalError(final SAXParseException exception) throws SAXException {
		logger.error("FATAL ERROR line:" + exception.getLineNumber(), exception);
		throw (exception);
	}
}
