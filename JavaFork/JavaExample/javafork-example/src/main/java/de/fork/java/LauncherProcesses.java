package de.fork.java;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.UnknownHostException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


public class LauncherProcesses {

	private static final Logger logger = Logger.getLogger(LauncherProcesses.class);

	// Exit process status
	private static final int STATUS_ERR = -1;

	private static final int DEFAULT_PORT = 5193;
	private static final String DEFAULT_HOST = "127.0.0.1";
	
	/**
	 * Run a process.
	 * 
	 * @param command system command to be executed.
	 * 
	 * @return return code.
	 */
	public static int exec(final String command) throws IOException, InterruptedException {

		return exec(command, null, null);
	}

	/**
	 * Run a process.
	 * 
	 * @param command system command to execute.
	 * @param standarOutPut if not null, the standard output is redirected to this parameter.
	 *            
	 * @return return code.            
	 */
	public static int exec(final String command, final PrintStream standarOutPut) throws IOException, InterruptedException {

		return exec(command, standarOutPut, null);
	}

	
	/**
	 * Run a process.
	 * 
	 * @param command system command to be executed.
	 * @param standarOutPut if not null, the standard output is redirected to this parameter.
	 * @param errorOutPut if not null, the error output is redirected to this parameter.
	 * 
	 * @return return code from the executed system command.     
	 */
	public static int exec(final String command, final PrintStream standarOutPut, final PrintStream errorOutPut) throws IOException, InterruptedException {

		return exec(command, standarOutPut, errorOutPut, DEFAULT_HOST, DEFAULT_PORT);
	}

	/**
	 * Run a process.
	 * 
	 * @param command system command to be executed.
	 * @param aLogger send the information to log.
	 */
	public static int exec(final String command, final Logger aLogger) throws IOException, InterruptedException {

		//calling private method to handle logger input/ouput in a common method
		return execHandlingLogger(command, aLogger, DEFAULT_HOST, DEFAULT_PORT);
	}
	
	
	/**
	 * @param commandAndArguments String array containing system command and its 
	 * arguments to be executed.<br>
	 * <b>For example:</b> 
	 * <pre>
	 * commandAndArguments[0]="ls";
	 * commandAndArguments[1]="-lr";
	 * </pre>
	 * @param aLogger
	 * 
	 * @return return code from the executed system command.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int exec(final String[] commandAndArguments, final Logger aLogger) throws IOException, InterruptedException {
		String wholeCommand="";
		
		for(String argument : commandAndArguments) {
			wholeCommand = wholeCommand + " " + argument;
		}
		
		//calling private method to handle logger input/ouput in a common method
		return execHandlingLogger(wholeCommand, aLogger, DEFAULT_HOST, DEFAULT_PORT);
	}
	
	
	/**
	 * 
	 * @param command system command to be executed.
	 * @param standarOutPut the stdout stream from that command as a <code>PrintStream</code>
	 * @param errorOutPut the stderr stream from that command as a <code>PrintStream</code>
	 * @param host the specified host.
	 * @param port the TCP port where the daemon accepts connections.
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
	 * 
	 * @return the executed command's return code.
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static int exec(final String command, final PrintStream standarOutPut, 
			final PrintStream errorOutPut, final String host, int port) throws IOException {
		int exitStatus = LauncherProcesses.STATUS_ERR;

		ForkDaemon process = new ForkDaemon();
		try {
			exitStatus = process.exec(command, host, port);
		} catch (SAXException e) {
			// This is not a crazy thing, we are trying to insert this new method without
			// breaking the old methods which did not throw SAXException or ParserConfigurationException
			//[§EJ Item 61] Do not blame me.
			throw new IOException(e);
		} catch (ParserConfigurationException e) {
			// This is not a crazy thing, we are trying to insert this new method without
			// breaking the old methods which did not throw SAXException or ParserConfigurationException
			//[§EJ Item 61] Do not blame me.
			throw new IOException(e);
		}
		
		if (standarOutPut != null) {
			if (process.getStdout() != null) {
				standarOutPut.println(process.getStdout());
			}
		}

		if (errorOutPut != null) {
			if (process.getStderr() != null) {
				errorOutPut.println(process.getStderr());
			}
		}

		return exitStatus;
	}
	
	
	/**
	 * 
	 * @param command system command to be executed.
	 * @param aLogger
	 * @param host the specified host.
	 * @param port the TCP port where the daemon accepts connections.
	 * 
	 * @return the executed command's return code.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static int execHandlingLogger(final String command, final Logger aLogger, 
				final String host, int port) throws IOException, InterruptedException {
		int exitStatus = LauncherProcesses.STATUS_ERR;

		ForkDaemon process = new ForkDaemon();
		try {
			exitStatus = process.exec(command, host, port);
		} catch (SAXException e) {
			// This is not a crazy thing, we are trying to insert this new method without
			// breaking the old methods which did not throw SAXException or ParserConfigurationException
			// Do not blame me.
			throw new IOException(e);
		} catch (ParserConfigurationException e) {
			// This is not a crazy thing, we are trying to insert this new method without
			// breaking the old methods which did not throw SAXException or ParserConfigurationException
			// Do not blame me.
			throw new IOException(e);
		}
		
		if (process.getStdout() != null) {
			aLogger.info(process.getStdout());
		}
		if (process.getStderr() != null) {
			aLogger.error(process.getStderr());
		}

		return exitStatus;
	}
	
	
	/**
	 * @param command command and its arguments to be executed.<br>
	 * <b>For example:</b> 
	 * <pre>
	 * commandAndArguments[0]="ls";
	 * commandAndArguments[1]="-lr";
	 * </pre>
	 * @param aLogger send information to log
	 * 
	 * @return the executed command's return code.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static InputStream execStream (final String [] command, final Logger aLogger) 
													throws IOException, InterruptedException {
		int exitStatus = LauncherProcesses.STATUS_ERR;
		
		InputStream stdInput = null;
	
		String wholeCommand="";
		for(String argument : command) {
			wholeCommand = wholeCommand + " " + argument;
		}
		
		ForkDaemon process = new ForkDaemon();
		try {
			exitStatus = process.exec(wholeCommand, DEFAULT_HOST, DEFAULT_PORT);
		} catch (SAXException e) {
			// This is not a crazy thing, we are trying to insert this new method without
			// breaking the old methods which did not throw SAXException or ParserConfigurationException
			throw new IOException(e);
		} catch (ParserConfigurationException e) {
			// This is not a crazy thing, we are trying to insert this new method without
			// breaking the old methods which did not throw SAXException or ParserConfigurationException
			throw new IOException(e);
		}
		
		if(exitStatus == 0) {
			stdInput = new ByteArrayInputStream(process.getStdout().getBytes("UTF-8"));
		}
		else {
			aLogger.error(process.getStderr());
		}
		

		return stdInput;
	}
	
	/**
	 * <p>The <em>command</em> is lunched from <em>location</em>
	 * <li>#>cd <em>location</em></li>
	 * <li>#location> <em>command</em></li></p>
	 * 
	 * @param command the command to be executed by the daemon.
	 * @param location
	 * 
	 * @return the executed command's return code. <br>
	 * Usually <code>0</code> if execution is OK, otherwise <code>!=0</code> 
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static int execInLocation (final String command, final String location) throws IOException, InterruptedException {
		int exitStatus = LauncherProcesses.STATUS_ERR;
		final String wholeCommand = "cd " + location + " && " + command;
		
		exitStatus =  exec(wholeCommand, null, null, DEFAULT_HOST, DEFAULT_PORT);
		return exitStatus;
	}
}