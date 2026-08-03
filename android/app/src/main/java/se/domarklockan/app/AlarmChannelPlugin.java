package se.domarklockan.app;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

/**
 * Larmhantering för Domarklockan.
 *
 * Larmet läggs som en exakt AlarmManager-väckning. När den går bygger
 * AlarmReceiver en notis med fullScreenIntent, vilket gör att skärmen
 * tänds och appen visas även om telefonen är släckt och låst.
 * Ljudet går via LARM-strömmen och hörs därför även i ljudlöst läge.
 */
@CapacitorPlugin(name = "AlarmChannel")
public class AlarmChannelPlugin extends Plugin {

    public static final String CHANNEL_ID = "domarklockan-alarm";
    public static final int REQUEST_CODE = 4711;

    @PluginMethod
    public void createAlarmChannel(PluginCall call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel ch = new NotificationChannel(
                CHANNEL_ID, "Matchlarm", NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("Larm när en period tar slut");

            AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (sound == null) sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            ch.setSound(sound, attrs);

            ch.enableVibration(true);
            ch.setVibrationPattern(new long[]{0, 600, 250, 600, 250, 900});
            ch.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            ch.setBypassDnd(true);

            nm.createNotificationChannel(ch);
        }
        call.resolve();
    }

    /** Schemalägger larmet. Anropas med tidpunkt i millisekunder. */
    @PluginMethod
    public void scheduleAlarmAt(PluginCall call) {
        long at = call.getLong("at", 0L);
        String title = call.getString("title", "Domarklockan");
        String body = call.getString("body", "");
        if (at <= System.currentTimeMillis()) { call.resolve(); return; }

        Context ctx = getContext();
        Intent i = new Intent(ctx, AlarmReceiver.class);
        i.setAction("se.domarklockan.ALARM");
        i.putExtra("title", title);
        i.putExtra("body", body);

        PendingIntent pi = PendingIntent.getBroadcast(ctx, REQUEST_CODE, i,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
                // Saknas behörighet för exakta larm – lite mindre precist, men fungerar
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi);
            } else {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi);
            }
        } catch (SecurityException e) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi);
        }
        call.resolve();
    }

    @PluginMethod
    public void cancelAlarm(PluginCall call) {
        Context ctx = getContext();
        Intent i = new Intent(ctx, AlarmReceiver.class);
        i.setAction("se.domarklockan.ALARM");
        PendingIntent pi = PendingIntent.getBroadcast(ctx, REQUEST_CODE, i,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);

        NotificationManager nm =
            (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(REQUEST_CODE);
        call.resolve();
    }

    /** Rapporterar om telefonen är tystad och hur hög larmvolymen är. */
    @PluginMethod
    public void getSoundState(PluginCall call) {
        AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        JSObject ret = new JSObject();
        int mode = am.getRingerMode();
        int vol = am.getStreamVolume(AudioManager.STREAM_ALARM);
        ret.put("ringerMode", mode);
        ret.put("silent", mode != AudioManager.RINGER_MODE_NORMAL);
        ret.put("alarmVolume", vol);
        ret.put("alarmVolumeMax", am.getStreamMaxVolume(AudioManager.STREAM_ALARM));
        ret.put("alarmMuted", vol == 0);

        // Får appen visa fullskärmslarm? (Android 14+ kräver godkännande)
        boolean fsi = true;
        if (Build.VERSION.SDK_INT >= 34) {
            NotificationManager nm =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            fsi = nm.canUseFullScreenIntent();
        }
        ret.put("canWakeScreen", fsi);
        call.resolve(ret);
    }

    @PluginMethod
    public void openSoundSettings(PluginCall call) {
        Intent i = new Intent(Settings.ACTION_SOUND_SETTINGS);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
        call.resolve();
    }

    /** Öppnar inställningen där fullskärmslarm godkänns (Android 14+). */
    @PluginMethod
    public void openFullScreenSettings(PluginCall call) {
        Intent i;
        if (Build.VERSION.SDK_INT >= 34) {
            i = new Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT);
            i.setData(Uri.parse("package:" + getContext().getPackageName()));
        } else {
            i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.parse("package:" + getContext().getPackageName()));
        }
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
        call.resolve();
    }
}
