package se.domarklockan.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

/**
 * Tar emot larmet från AlarmManager, väcker skärmen och visar en notis
 * med fullScreenIntent så att appen kommer upp direkt.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");
        if (title == null) title = "Domarklockan";
        if (body == null) body = "";

        // 1. Väck skärmen
        try {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE,
                "domarklockan:alarm");
            wl.acquire(30_000);
        } catch (Exception ignored) {}

        // 2. Intent som öppnar appen
        Intent open = new Intent(context, MainActivity.class);
        open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        open.putExtra("fromAlarm", true);
        PendingIntent pi = PendingIntent.getActivity(context, AlarmChannelPlugin.REQUEST_CODE,
            open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 3. Notis med fullskärmslarm – tänder skärmen och visar appen
        Notification.Builder b;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            b = new Notification.Builder(context, AlarmChannelPlugin.CHANNEL_ID);
        } else {
            b = new Notification.Builder(context);
            b.setPriority(Notification.PRIORITY_MAX);
        }
        b.setContentTitle(title)
         .setContentText(body)
         .setSmallIcon(context.getApplicationInfo().icon)
         .setCategory(Notification.CATEGORY_ALARM)
         .setAutoCancel(true)
         .setOngoing(false)
         .setContentIntent(pi)
         .setFullScreenIntent(pi, true);

        try {
            int iconId = context.getResources().getIdentifier(
                "ic_stat_icon", "drawable", context.getPackageName());
            if (iconId != 0) b.setSmallIcon(iconId);
        } catch (Exception ignored) {}

        NotificationManager nm =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(AlarmChannelPlugin.REQUEST_CODE, b.build());
    }
}
