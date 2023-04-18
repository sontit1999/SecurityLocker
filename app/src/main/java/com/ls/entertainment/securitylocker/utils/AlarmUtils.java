package com.ls.entertainment.securitylocker.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.AlarmManagerCompat;

import com.ls.entertainment.securitylocker.receiver.AlarmReceiver;

import java.util.Calendar;

public class AlarmUtils {
    public static final String ACTION_AUTOSTART_ALARM = "com.app.action.alarmmanager";
    public static final String ACTION_CHECK_DEVICE_STATUS = "action_check_devic_status";
    public static final String ACTION_REPEAT_SERVICE = "action_repeat_service";


    public static void cancel(Context context, String str) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(ACTION_AUTOSTART_ALARM);
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(context, getRequestCode(str), intent, PendingIntent.FLAG_IMMUTABLE));
    }

    public static Calendar getCalendar(int i) {
        Calendar instance = Calendar.getInstance();
        instance.set(11, i / 100);
        instance.set(12, i % 100);
        instance.set(13, 0);
        if (instance.getTimeInMillis() < System.currentTimeMillis()) {
            instance.add(5, 1);
        }
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setAlarm(Context context, String str, int i) {
        getCalendar(i);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(ACTION_AUTOSTART_ALARM);
        intent.putExtra(str, Boolean.TRUE);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, getRequestCode(str), intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(broadcast);
        AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, 0, System.currentTimeMillis() + ((long) i), broadcast);
    }

    public static int getRequestCode(String str) {
        if (str.equals(ACTION_REPEAT_SERVICE)) {
            return 1001;
        }
        return str.equals(ACTION_CHECK_DEVICE_STATUS) ? 1002 : 1000;
    }
}
