package de.example.fsync;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * If you want to make sure what you are writing to disk is not going to get
 * lost you should use this class.
 * 
 * <p>Hopefully you are already closing your output streams in a finally block
 * (if you aren't, you don't deserve my respect) and in that case you could be
 * wondering what this class is for. <b>From Linux's close(2)</b>:
 * 
 * <p><i>A successful close does not guarantee that the data has been successfully
 * saved to disk, as the kernel defers writes. It is not common for a file system
 * to flush the buffers when the stream is closed. If you need to be sure that
 * the data is physically stored use fsync(2). (It will depend on the disk hardware
 * at this point.)</i>
 * 
 * <p> See also: {@link http://thunk.org/tytso/blog/2009/03/15/dont-fear-the-fsync/}
 *
 */
public class FileSafeWriter {
	private final SafeWriter safeWriter;

	/**
	 * This interface must be used to implement the user's methods in charge of
	 * writing to and closing output stream.
	 */
	public interface SafeWriter extends Closeable {

		public void doWrite(final OutputStream out) throws IOException;
	}
	
	/**
	 * Creates object with the user's implementation in charge of writing to
	 * output stream.
	 * 
	 * @param safeWriter user's implementation in charge of writing to output
	 * stream
	 */
	public FileSafeWriter(final SafeWriter safeWriter) {
		this.safeWriter = safeWriter;
	}

	/**
	 * Writes data to file in a safe way.
	 * 
	 * <p> This write is atomic (as long as you are not using FAT16, FAT32 or NFS)
	 * in the sense that it is first written to a temporary file which is then
	 * renamed to the final name.
	 * 
	 * @param file destination file
	 * @throws FileNotFoundException if the file exists but is a directory rather
	 * than a regular file, does not exist but cannot be created, or cannot be
	 * opened for any other reason
	 * @throws IOException if an I/O error occurs
	 */
	public void writeFile(final File file)
			throws FileNotFoundException, IOException {

		final File tempFile = this.writeTempFile(file);

		this.renameFile(tempFile, file);
	}

	/**
	 * Writes data to temporary file.
	 * 
	 * <p>
	 * Note that the name for the temporary file is constructed by appending
	 * random characters to the name (just its name, not the whole path) of
	 * file.
	 * </p>
	 * 
	 * @param file destination file
	 * @return temporary file with the temporarily stored data
	 * @throws FileNotFoundException if the file exists but is a directory rather
	 * than a regular file, does not exist but cannot be created, or cannot be
	 * opened for any other reason
	 * @throws IOException if an I/O error occurs
	 */
	private File writeTempFile(final File file)
			throws FileNotFoundException, IOException {

		final File tempFile = File.createTempFile(file.getName(), ".tmp", null);
		final FileOutputStream tempFileStream = new FileOutputStream(tempFile, false);
		try {
			safeWriter.doWrite(tempFileStream);

			/* We want to sync the newly written file to ensure the data is on disk
			 * when we rename over the destination. Otherwise if we get a system
			 * crash we can lose both the new and the old file on some filesystems.
			 * (I.E. those that don't guarantee the data is written to the disk
			 * before the metadata.)
			 */
			tempFileStream.flush();
			tempFileStream.getFD().sync();
		} finally {
			// Override exception (if there is one) Hopefully it will not be a problem.
			try {
				safeWriter.close();
			} finally {
				tempFileStream.close();
			}
		}

		return tempFile;
	}

	/**
	 * This method renames file and tries to clear everything in case of error.
	 * 
	 * <p>Rename file should be atomic (if you are not using FAT16, FAT32 or NFS)
	 * 
	 * <p> See also: {@link http://rcrowley.org/2010/01/06/things-unix-can-do-atomically.html}
	 * 
	 * @param fromFile destination file
	 * @param toFile target file
	 * @throws IOException if there was some error
	 */
    private void renameFile(final File fromFile, final File toFile) throws IOException {
        if (!fromFile.renameTo(toFile)) {
            if (!fromFile.delete()) {
            	System.out.println("[FileSafeWriter] Could not remove temporary file: "
						+ fromFile.getAbsolutePath());
            }
            throw new IOException("[FileSafeWriter] Could not move from "
					+ fromFile.getAbsolutePath() + " to " + toFile.getAbsolutePath());
        }
    }
}
