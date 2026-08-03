package se.domarklockan.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
 * Ger Domarklockan en notiskanal som använder LARM-ljudströmmen.
 * Larm spelas även när telefonen står på ljudlöst eller vibration,
 * till skillnad från vanliga aviseringar.
 */
@CapacitorPlugin(name = "AlarmChannel")
public class AlarmChannelPlugin extends Plugin {

    public static final String CHANNEL_ID = "domarklockan-alarm";

    @PluginMethod
    public void createAlarmChannel(PluginCall call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel ch = new NotificationChannel(
                CHANNEL_ID, "Matchlarm", NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("Larm när en period tar slut");

            // Nyckeln: USAGE_ALARM gör att ljudet går via larmströmmen
            AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            ch.setSound(alarmSound, attrs);

            ch.enableVibration(true);
            ch.setVibrationPattern(new long[]{0, 600, 250, 600, 250, 900});
            ch.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            ch.setBypassDnd(true); // ignoreras tyst om appen saknar behörighet

            nm.createNotificationChannel(ch);
        }
        call.resolve();
    }

    /** Rapporterar om telefonen är tystad och hur hög larmvolymen är. */
    @PluginMethod
    public void getSoundState(PluginCall call) {
        AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        JSObject ret = new JSObject();

        int mode = am.getRingerMode(); // 0 = tyst, 1 = vibration, 2 = ljud
        int alarmVol = am.getStreamVolume(AudioManager.STREAM_ALARM);
        int alarmMax = am.getStreamMaxVolume(AudioManager.STREAM_ALARM);

        ret.put("ringerMode", mode);
        ret.put("silent", mode != AudioManager.RINGER_MODE_NORMAL);
        ret.put("alarmVolume", alarmVol);
        ret.put("alarmVolumeMax", alarmMax);
        // Larmet hörs inte alls om larmvolymen är nerdragen till noll
        ret.put("alarmMuted", alarmVol == 0);
        call.resolve(ret);
    }

    /** Öppnar telefonens ljudinställningar. */
    @PluginMethod
    public void openSoundSettings(PluginCall call) {
        Intent i = new Intent(Settings.ACTION_SOUND_SETTINGS);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
        call.resolve();
    }
}
