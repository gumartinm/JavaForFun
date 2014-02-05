package de.remote.agents.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentDateServiceImpl implements CurrentDateService {

    @Override
    public String getCurrentDate() {
        final DateFormat dateFormat = new SimpleDateFormat(
                "yyyy/MM/dd HH:mm:ss");

        final Date date = new Date();

        return dateFormat.format(date);
    }

}
