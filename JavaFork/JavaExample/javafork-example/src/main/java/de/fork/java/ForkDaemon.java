package de.fork.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 * <p>
 * With this class we can run processes using the intended daemon which is 
 * waiting for TCP connections in a specified port.
 * </p>
 * <p>
 * Receiving the results in a XML format from the daemon where we can find three
 * kinds of different fields: error, out and ret. Every field is related to the
 * stderr, stdout and return code respectively. <br>
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
 * This class has to process the above stream and it offers two methods wich can be used
 * to retrieve the stderr and stdout in a right way without having to know about the XML stream
 * received from the daemon. The user does not have to know about how the daemon sends the data,
 * he or she will work directly with the strings related to each stream. Those methods 
 * are {@link ForkDaemon#getStdout()} and {@link ForkDaemon#getStderr()} The return code from the command 
 * executed by the daemon can be retrieved as a return parameter from the method 
 * {@link ForkDaemon#exec(String, String, int)}
 * </p>
 * <p>
 * Instances of this class are mutable. To use them concurrently, clients must surround each
 * method invocation (or invocation sequence) with external synchronization of the clients choosing.
 * </p>
 */
public class ForkDaemon {
	private final ForkParser handler;
	
	
	/**
	 * Simple constructor.
	 * Nothing special here.
	 * 
	 */
	public ForkDaemon () {
		handler = new ForkParser();
	}
	
	
	/**
	 * This method sends commands to the daemon.
	 * <br>
	 * It uses a TCP connection in order to send commands to the daemon and receive
	 * the results related to that command. 
	 * <br>
	 * The mehtod retrieves the stdout, stderr and the return code of that command. 
	 * 
     * <p> The host name can either be a machine name, such as
     * "<code>java.sun.com</code>", or a textual representation of its
     * IP address. If a literal IP address is supplied, only the
     * validity of the address format is checked.
     * </p>
     * <p> For <code>host</code> specified in literal IPv6 address,
     * either the form defined in RFC 2732 or the literal IPv6 address
     * format defined in RFC 2373 is accepted. IPv6 scoped addresses are also
     * supported. See <a href="Inet6Address.html#scoped">here</a> for a description of IPv6
     * scoped addresses.
	 * </p>
	 * @param  command the command to be executed by the daemon.
	 * @param  host the specified host.
	 * @param  port the TCP port where the daemon accepts connections.
	 * 
	 * @return the executed command's return code.
	 * 
     * @throws UnknownHostException  if no IP address for the
     * <code>host</code> could be found, or if a scope_id was specified
     * @throws SecurityException if a security manager exists
     * and its checkConnect method doesn't allow the operation
     * @throws IOException  if an I/O error occurs when creating the socket.
	 * @throws SAXException 
	 * @throws FactoryConfigurationError if the implementation is not available or cannot be instantiated.
     * @throws SAXException If any SAX errors occur during processing.
	 * @throws ParserConfigurationException 
	 */
	public int exec(final String command, String host, int port) 
			throws UnknownHostException, IOException, SAXException, ParserConfigurationException  {
		final SAXParserFactory spf = SAXParserFactory.newInstance();
		final SAXParser saxParser = spf.newSAXParser();	
		PrintWriter out = null;
		
		final Socket socket = new Socket(InetAddress.getByName(host), port);
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			out.println(command);
			saxParser.parse(socket.getInputStream(), handler);
		} 
		finally {
			if (out != null) {
				out.close();
			}
			socket.close();
		}
		
		//Just for testing the parser by using a file instead of a TCP connection.
		//InputSource input = new InputSource(new FileReader("/tmp/xmlfromdaemon.xml"));
		//saxParser.parse(input, handler);
		
		return new Integer(handler.getReturnValue()).intValue();
	}

	
	/**
	 * Retrieve the standard output.
	 * 
	 * @see {@link ForkDaemon#getStderr()}
	 * @return stdout
	 */
	public String getStdout() {
		return handler.getStdout();
	}
	
	
	/**
	 * <p>
	 * Retrieve the standard error.
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
	 * <pre>
	 * <b>From that example with this method we are going to obtain this return parameter:</b>
	 * {@code
	 * ls: no se puede acceder a bbb: No existe el fichero o el directorio
	 * ls: no se puede acceder a aaa: No existe el fichero o el directorio
	 * ls: no se puede acceder a dddd: No existe el fichero o el directorio
	 * }
	 * </pre>
	 * </p>
	 * @return stderr
	 */
	public String getStderr() {
		return handler.getStderr();
	}
}