package de.fork.java;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;


public class MainJavaFork  {

	/**
	 * @param args
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) 
			throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
		
		final ByteArrayOutputStream stdoutByteOut = new ByteArrayOutputStream();
		final PrintStream stdout = new PrintStream(stdoutByteOut);
		final String command = "/home/gustavo/github/JavaForFun/JavaFork/Daemon/script.sh";
		final ByteArrayOutputStream stderrByteOut = new ByteArrayOutputStream();
		final PrintStream stderr = new PrintStream(stderrByteOut);
		int result;
		
		result = LauncherProcesses.exec(command,stdout, stderr, "127.0.0.1", 5193);
		System.out.println(result);
		System.out.println("Stdout: " +  stdoutByteOut.toString());
		System.out.println("Stderr: " +  stderrByteOut.toString());
	}

}