/*
 * Copyright (C) 2016 mendhak
 *
 * This file is part of GPSLogger for Android.
 *
 * GPSLogger for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * GPSLogger for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPSLogger for Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.deeperwire.gpslogger.loggers.opengts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.deeperwire.gpslogger.common.SerializableLocation;
import com.deeperwire.gpslogger.common.Strings;
import com.deeperwire.gpslogger.common.events.UploadEvents;
import com.deeperwire.gpslogger.common.slf4j.Logs;
import com.deeperwire.gpslogger.senders.opengts.OpenGTSManager;
import de.greenrobot.event.EventBus;
import org.slf4j.Logger;

import java.net.*;


public class OpenGtsUdpJob extends Job {

    String server;
    int port ;
    String accountName ;
    String path ;
    String deviceId ;
    String communication;
    SerializableLocation[] locations;
    private static final Logger LOG = Logs.of(OpenGtsUdpJob.class);

    public OpenGtsUdpJob(String server, int port, String accountName, String path, String deviceId, String communication, SerializableLocation[] locations){
        super(new Params(1).requireNetwork().persist());

        this.server = server;
        this.port = port;
        this.accountName = accountName;
        this.path = path;
        this.deviceId = deviceId;
        this.communication = communication;
        this.locations = locations;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {

        LOG.debug("Running OpenGTS Job");
        sendRAW(deviceId, accountName, locations);
        EventBus.getDefault().post(new UploadEvents.OpenGTS().succeeded());
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        LOG.error("Could not send to OpenGTS", throwable);
        EventBus.getDefault().post(new UploadEvents.OpenGTS().failed("Could not send to OpenGTS", throwable));
        return RetryConstraint.CANCEL;
    }


    public void sendRAW(String id, String accountName, SerializableLocation[] locations) throws Exception {
        for (SerializableLocation loc : locations) {
            if(Strings.isNullOrEmpty(accountName)){
                accountName = id;
            }
            String message = accountName + "/" + id + "/" + OpenGTSManager.gprmcEncode(loc);
            DatagramSocket socket = new DatagramSocket();
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(server), port);
            LOG.debug("Sending UDP " + message);
            socket.send(packet);
            socket.close();
        }
    }





}
