package de.fork.java;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.xml.sax.SAXException;

/**
 * <p>
 * With this class we can run processes using the intended daemon which is 
 * waiting for TCP connections in a specified port.
 * </p>
 * <p>
 * Receiving the results from the daemon where we can find three kinds of 
 * different fields: stderror, stdout and the return value of the command which was
 * run by the remote daemon. Each field is related to the stderr, stdout and 
 * return code respectively.
 * </p>
 * <p>
 * This class has to retrieve the results from the remote daemon and it offers two 
 * methods wich can be used to retrieve the stderr and stdout in a right way 
 * without having to know about the coding used by the daemon to send us the results. 
 * The user does not have to know about how the daemon sends the data, he or she 
 * will work directly with the strings related to each stream using these methods:
 * {@link TCPForkDaemon#getStdout()} and {@link TCPForkDaemon#getStderr()}.
 * The return code from the command executed by the daemon can be retrieved as the 
 * return parameter from the method {@link TCPForkDaemon#exec(String, String, int)}
 * </p>
 * <p>
 * Instances of this class are mutable. To use them concurrently, clients must surround each
 * method invocation (or invocation sequence) with external synchronization of the clients choosing.
 * </p>
 */
public class TCPForkDaemon {
	private final XmlForkParser parser;
	private final String host;
	private final int port;
	
	
	/**
	 * Default constructor for this {@link TCPForkDaemon} implementation.
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
	 * @param parser instance implemeting {@link XmlForkParser} which knows about what 
	 * codification uses the daemon to send us the results of the command sent to
	 * by the remote daemon by the {@link TCPForkDaemon.#exec(String)} method.
	 * @param host the specified host.
	 * @param port the TCP port where the daemon accepts connections.
	 * 
	 */
	public TCPForkDaemon (final XmlForkParser parser, final String host, final int port) {
		this.parser = parser;
		this.host = host;
		this.port = port;
	}
	
	
	/**
	 * <p>
	 * This method sends commands to a remote daemon using a TCP socket.
	 * We create a new TCP socket every time we send commands.
	 * </p>
	 * <p>
	 * It uses a TCP connection in order to send commands and receive
	 * the results related to that command from the remote daemon. The command's 
	 * result code which was run by the remote daemon can be retrieved from the
	 * return parameter of this method.
	 * </p>
	 * @param  command the command to be executed by the daemon.
	 * @return the executed command's return code.
	 * @throws IOException 
	 * @throws UnknownHostException
	 * @throws SAXException 
	 * @throws SecurityException if a security manager exists
	 */
	public int exec(final String command) throws UnknownHostException, IOException, SAXException {
		PrintWriter out = null;
		Socket socket = null;
		
	/******************************************************************************************/
	/*          Just over 1 TCP connection                                                    */
	/*          COMMAND_LENGTH: Java integer 4 bytes, BIG-ENDIAN (the same as network order)  */
	/*          ACK: integer 4 bytes big-endian (for Java) with the sent comand length 	  	  */
	/*          COMMAND: TPV locale character set encoding                                    */
	/*          RESULTS: TPV locale character set encoding                                    */
	/*                                                                                        */
	/*              JAVA CLIENT: ------------ COMMAND_LENGTH -------> :SERVER                 */
	/*              JAVA CLIENT: <---------------- ACK -------------- :SERVER                 */
	/*              JAVA CLIENT: -------------- COMMAND ------------> :SERVER                 */
	/*              JAVA CLIENT: <-------------- RESULTS ------------ :SERVER                 */
	/*              JAVA CLIENT: <---------- CLOSE CONNECTION ------- :SERVER                 */
	/*                                                                                        */
	/******************************************************************************************/


		
		socket = new Socket(InetAddress.getByName(host), port);
		try {
			/*Must be used the remote charset :S*/
			byte [] commandEncoded = command.getBytes("UTF-8"); 
			
			DataOutputStream sendData = new DataOutputStream(socket.getOutputStream());
			DataInputStream receiveData = new DataInputStream(socket.getInputStream());
						
			// 1. COMMAND_LENGTH
			sendData.writeInt(commandEncoded.length);

			// 2. ACK
			// TODO: if the server close the connection we could stay here probably
			// until TCP keepalive is sent (20 hours by default in Linux)
			int ack = receiveData.readInt();
			if (ack != commandEncoded.length) 
				throw new IOException("invalid ACK, something went wrong " +
					"with the TCPForkDaemon. Check the /var/log/messages file in the TPV");
			
			
			// 3. COMMAND
			sendData.write(commandEncoded);
			
			
			// 4. RESULTS
			// TODO: if the server closes the connection we could stay here probably
			// until TCP keepalive is sent (20 hours by default in Linux)
			parser.setStream(socket.getInputStream());
			
			
			// 5. SERVER CLOSES CONNECTION
		}
		finally {
			if (out != null) {
				out.close();
			}
			socket.close();
		}
		
		//If everything went alright we should be able to retrieve the return 
		//status of the remotely executed command.
		return parser.getReturnValue();
	}

	
	/**
	 * Retrieve the standard output. <br>
	 * When there is nothing from the standard output this method returns null.
	 * 
	 * @see {@link TCPForkDaemon#getStderr()}
	 * @return the stdout stream
	 */
	public String getStdout() {
		return parser.getStdout();
	}
	
	
	/**
	 * Retrieve the stderr stream as a {@link String} from the command which 
	 * was run by the remote daemon 
	 * 
	 * @see {@link TCPForkDaemon#getStdout()}
	 * @return the stderr stream
	 */
	public String getStderr() {
		return parser.getStderr();
	}
}
