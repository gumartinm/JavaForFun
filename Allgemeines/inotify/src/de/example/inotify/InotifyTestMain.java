package de.example.inotify;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class InotifyTestMain {
    private static final String directory = "/home/pathtodirectory/";

    public static void main(final String[] args) throws IOException,
    ExecutionException, InterruptedException {
        final ExecutorService exec = Executors.newSingleThreadExecutor();
        final Path filePath = FileSystems.getDefault().getPath(directory);
        final Inotify task = new Inotify(filePath);

        while (true) {

            final Future<WatchKey> futureTask = exec.submit(task);

            final WatchKey token = futureTask.get();

            for (final WatchEvent<?> event : token.pollEvents()) {
                sortEvent(event);
            }

            if (!token.reset()) {
                break;
            }

        }
    }

    private static void sortEvent(final WatchEvent<?> event) {
        final Kind<?> kind = event.kind();
        if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
            final Path pathCreated = (Path) event.context();
            if (pathCreated.getFileName().toString().compareTo("fileOfInterest.txt") == 0) {
                final String absolutFile = directory + pathCreated.getFileName().toString();
                try {
                    readFile(absolutFile);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
            final Path pathDeleted = (Path) event.context();
            if (pathDeleted.getFileName().toString().compareTo("fileOfInterest.txt") == 0) {
                System.out.println("Entry deleted:" + pathDeleted.getFileName());
            }
        } else if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
            final Path pathModified = (Path) event.context();
            if (pathModified.getFileName().toString().compareTo("fileOfInterest.txt") == 0) {
                final String absolutFile = directory
                        + pathModified.getFileName().toString();
                try {
                    readFile(absolutFile);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void readFile(final String file) throws FileNotFoundException, IOException {
        final BufferedReader bufferReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
        String currentLine = null;

        try {
            currentLine = bufferReader.readLine();
            while (currentLine != null) {
                System.out.println(currentLine);
                currentLine = bufferReader.readLine();
            }
        } finally {
            bufferReader.close();
        }
    }
}
