package de.example.inotify;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Callable;

public class Inotify implements Callable<WatchKey> {
    private final WatchService watchService;

    public Inotify(final Path path) throws IOException {
        this.watchService = path.getFileSystem().newWatchService();
        path.register(this.watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);
    }

    @Override
    public WatchKey call() throws Exception {

        return this.watchService.take();

    }

}
