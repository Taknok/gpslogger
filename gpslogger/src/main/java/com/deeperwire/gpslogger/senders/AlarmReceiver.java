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

package com.deeperwire.gpslogger.senders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.ContextCompat;

import com.deeperwire.gpslogger.GpsLoggingService;
import com.deeperwire.gpslogger.common.AppSettings;
import com.deeperwire.gpslogger.common.events.CommandEvents;
import com.deeperwire.gpslogger.common.slf4j.Logs;
import de.greenrobot.event.EventBus;
import org.slf4j.Logger;


public class AlarmReceiver extends BroadcastReceiver {

    private static final Logger LOG = Logs.of(AlarmReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            LOG.debug("Alarm received");

            EventBus.getDefault().post(new CommandEvents.AutoSend(null));

            Intent serviceIntent = new Intent(AppSettings.getInstance(), GpsLoggingService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
        } catch (Exception ex) {
            LOG.error("AlarmReceiver", ex);
        }
    }
}
