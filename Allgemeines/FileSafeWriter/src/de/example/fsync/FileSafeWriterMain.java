package de.example.fsync;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;


public class FileSafeWriterMain {
	private static final File exampleFile = new File("/tmp/fileSafeWriterExample");

	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		final FileSafeWriter fileSafeWriter = new FileSafeWriter(new ExampleSafeWriter(9999));
		fileSafeWriter.writeFile(exampleFile);
        
        System.out.println("Result: " + getPersistedData());
	}
	
    private static Integer getPersistedData()
            throws FileNotFoundException, UnsupportedEncodingException, IOException {
        InputStream inputStream = null;
        Reader readerStream = null;
        BufferedReader reader = null;
        try {
            inputStream = new FileInputStream(exampleFile);
            readerStream = new InputStreamReader(inputStream, "UTF-8");
            reader = new BufferedReader(readerStream);
            return Integer.valueOf(reader.readLine());
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (readerStream != null) {
                readerStream.close();
            }
            if (readerStream != null) {
                readerStream.close();
            }
        }
    }

	
    private static class ExampleSafeWriter implements FileSafeWriter.SafeWriter {
        private final Integer number;
        private Writer writerStream = null;
        private BufferedWriter buffer = null;

        private ExampleSafeWriter(final Integer number) {
            this.number = number;
        }

        @Override
        public void doWrite(final OutputStream out) throws IOException {
            writerStream = new OutputStreamWriter(out, "UTF-8");
            buffer = new BufferedWriter(writerStream);
            buffer.write(String.valueOf(number)); buffer.newLine();
        }

        @Override
        public void close() throws IOException {
            if (buffer != null) {
                buffer.close();
            }

            if (writerStream != null) {
                writerStream.close();
            }
        }
    }

}
