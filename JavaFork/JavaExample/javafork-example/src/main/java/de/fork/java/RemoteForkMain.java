package de.fork.java;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;


public class RemoteForkMain  {

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PrintStream stdout = new PrintStream(baos);
		final String command = "ls -lah ~/Desktop; ls -lah * bbbb aaa";
		final ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		final PrintStream stderr = new PrintStream(baos2);
		int result;
		
		result = LauncherProcesses.exec(command,stdout, stderr, "127.0.0.1", 5193);
		System.out.println(result);
		System.out.println("Stdout: " +  baos.toString());
		System.out.println("Stderr: " +  baos2.toString());
	}

}