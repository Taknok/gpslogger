package com.deeperwire.gpslogger.loggers.geojson;

import android.location.Location;

import com.deeperwire.gpslogger.common.PreferenceHelper;
import com.deeperwire.gpslogger.common.RejectionHandler;
import com.deeperwire.gpslogger.common.Strings;
import com.deeperwire.gpslogger.loggers.FileLogger;

import java.io.File;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by clemens on 10.05.17.
 */

public class GeoJSONLogger implements FileLogger {
    final static Object lock = new Object();
    private final static ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(10), new RejectionHandler());
    private final File file;
    protected final String name;

    public GeoJSONLogger(File file, boolean addNewTrackSegment) {
        this.file = file;
        name = "GeoJSON";
    }

    @Override
    public void write(Location loc, float[] rotation) throws Exception {
        annotate(null, loc);
    }

    @Override
    public void annotate(String description, Location loc) throws Exception {
        String dateTimeString = Strings.getIsoDateTime(new Date(loc.getTime()));
        if(PreferenceHelper.getInstance().shouldWriteTimeWithOffset()){
            dateTimeString = Strings.getIsoDateTimeWithOffset(new Date(loc.getTime()));
        }
        Runnable gw = new GeoJSONWriterPoints(file, loc, description, dateTimeString);
        EXECUTOR.execute(gw);
    }

    @Override
    public String getName() {
        return name;
    }

    public static int getCount(){
        return EXECUTOR.getActiveCount();
    }
}

