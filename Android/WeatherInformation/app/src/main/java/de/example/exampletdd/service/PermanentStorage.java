package de.example.exampletdd.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.MessageFormat;

import android.content.Context;
import android.util.Log;
import de.example.exampletdd.model.currentweather.Current;
import de.example.exampletdd.model.forecastweather.Forecast;


/**
 * TODO: show some error message when there is no enough space for saving files. :/
 *
 */
public class PermanentStorage {
	private static final String TAG = "PermanentStorage";
    private static final String CURRENT_DATA_FILE = "current.file";
    private static final String FORECAST_DATA_FILE = "forecast.file";
    private static final String WIDGET_CURRENT_DATA_FILE = "current.{0}.file";
    private final Context context;

    public PermanentStorage(final Context context) {
        this.context = context;
    }

    public void saveCurrent(final Current current) {
    	
        try {
			this.saveObject(CURRENT_DATA_FILE, current);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "saveCurrent exception: ", e);
		} catch (IOException e) {
			Log.e(TAG, "saveCurrent exception: ", e);
		}
    }

    public Current getCurrent() {
    	
    	try {
			return (Current) this.getObject(CURRENT_DATA_FILE);
		} catch (final StreamCorruptedException e) {
			Log.e(TAG, "getCurrent exception: ", e);
		} catch (final FileNotFoundException e) {
			Log.e(TAG, "getCurrent exception: ", e);
		} catch (final IOException e) {
			Log.e(TAG, "getCurrent exception: ", e);
		} catch (final ClassNotFoundException e) {
			Log.e(TAG, "getCurrent exception: ", e);
		}
    	
    	return null;
    }

    public void saveWidgetCurrentData(final Current current, final int appWidgetId) {

        final String fileName = MessageFormat.format(WIDGET_CURRENT_DATA_FILE, appWidgetId);
        try {
            this.saveObject(fileName, current);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "saveWidgetCurrentData exception: ", e);
        } catch (IOException e) {
            Log.e(TAG, "saveWidgetCurrentData exception: ", e);
        }
    }

    public Current getWidgetCurrentData(final int appWidgetId) {

        final String fileName = MessageFormat.format(WIDGET_CURRENT_DATA_FILE, appWidgetId);
        try {
            return (Current) this.getObject(fileName);
        } catch (final StreamCorruptedException e) {
            Log.e(TAG, "getWidgetCurrentData exception: ", e);
        } catch (final FileNotFoundException e) {
            Log.e(TAG, "getWidgetCurrentData exception: ", e);
        } catch (final IOException e) {
            Log.e(TAG, "getWidgetCurrentData exception: ", e);
        } catch (final ClassNotFoundException e) {
            Log.e(TAG, "getWidgetCurrentData exception: ", e);
        }

        return null;
    }

    public void removeWidgetCurrentData(final int appWidgetId) {

        final String fileName = MessageFormat.format(WIDGET_CURRENT_DATA_FILE, appWidgetId);

        try {
            this.removeFile(fileName);
        } catch (final IOException e) {
            Log.e(TAG, "removeWidgetCurrentData exception: ", e);
        }
    }

    public void saveForecast(final Forecast forecast) {

    	try {
			this.saveObject(FORECAST_DATA_FILE, forecast);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "saveForecast exception: ", e);
		} catch (IOException e) {
			Log.e(TAG, "saveForecast exception: ", e);
		}
    }

    public Forecast getForecast() {
        
    	try {
			return (Forecast) this.getObject(FORECAST_DATA_FILE);
		} catch (final StreamCorruptedException e) {
			Log.e(TAG, "getForecast exception: ", e);
		} catch (final FileNotFoundException e) {
			Log.e(TAG, "getForecast exception: ", e);
		} catch (final IOException e) {
			Log.e(TAG, "getForecast exception: ", e);
		} catch (final ClassNotFoundException e) {
			Log.e(TAG, "getForecast exception: ", e);
		}
    	
    	return null;
    }

    private void saveObject(final String fileName, final Object objectToStore)
    		throws FileNotFoundException, IOException {
    	final String temporaryFileName = fileName.concat(".tmp");
    	
        final FileOutputStream tmpPersistFile = this.context.openFileOutput(
        		temporaryFileName, Context.MODE_PRIVATE);
        try {
        	final ObjectOutputStream oos = new ObjectOutputStream(tmpPersistFile);
        	try {
        		oos.writeObject(objectToStore);
        		
        		// Don't fear the fsync!
        		// http://thunk.org/tytso/blog/2009/03/15/dont-fear-the-fsync/
        		tmpPersistFile.flush();
        		tmpPersistFile.getFD().sync();
        	} finally {
        		oos.close();
        	}
        } finally {
        	tmpPersistFile.close();
        }

        this.renameFile(temporaryFileName, fileName);
    }
 
    private Object getObject(final String fileName) throws StreamCorruptedException, FileNotFoundException,
    								  					   IOException, ClassNotFoundException {
    	final InputStream persistFile = this.context.openFileInput(fileName);
    	try {
    		final ObjectInputStream ois = new ObjectInputStream(persistFile);
    		try {
    			return ois.readObject();
    		} finally {
    			ois.close();
    		}
    	} finally {
    		persistFile.close();
    	}
    } 
    
    private void renameFile(final String fromFileName, final String toFileName) throws IOException {
        final File filesDir = this.context.getFilesDir();
        final File fromFile = new File(filesDir, fromFileName);
        final File toFile = new File(filesDir, toFileName);
        if (!fromFile.renameTo(toFile)) {
        	if (!fromFile.delete()) {
        		throw new IOException("PermanentStorage, delete file error");
        	}	
        	throw new IOException("PermanentStorage, rename file error");
        }
    }

    private void removeFile(final String fileName) throws IOException {
        final File filesDir = this.context.getFilesDir();
        final File file = new File(filesDir, fileName);

        if (!file.delete()) {
            throw new IOException("PermanentStorage, remove file error");
        }
    }
}

