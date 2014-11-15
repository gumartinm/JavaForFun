package com.weather.information.model;

import android.content.Context;

public class DatabaseQueries {
	private final Context localContext;

	public DatabaseQueries(final Context context) {
		this.localContext = context;
	}
	
	public WeatherLocation queryDataBase() {
        
        final WeatherLocationDbHelper dbHelper = new WeatherLocationDbHelper(this.localContext);
        try {
        	final WeatherLocationDbQueries queryDb = new WeatherLocationDbQueries(dbHelper); 	
        	return queryDb.queryDataBase();
        } finally {
        	dbHelper.close();
        } 
    }
    
	public long insertIntoDataBase(final WeatherLocation weatherLocation) {
        
        final WeatherLocationDbHelper dbHelper = new WeatherLocationDbHelper(this.localContext);
        try {
        	final WeatherLocationDbQueries queryDb = new WeatherLocationDbQueries(dbHelper); 	
        	return queryDb.insertIntoDataBase(weatherLocation);
        } finally {
        	dbHelper.close();
        } 
    }
    
	public void updateDataBase(final WeatherLocation weatherLocation) {
        
        final WeatherLocationDbHelper dbHelper = new WeatherLocationDbHelper(this.localContext);
        try {
        	final WeatherLocationDbQueries queryDb = new WeatherLocationDbQueries(dbHelper); 	
        	queryDb.updateDataBase(weatherLocation);
        } finally {
        	dbHelper.close();
        } 
    }
}
