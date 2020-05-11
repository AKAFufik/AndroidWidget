package com.example.clickwidget;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;
import androidx.core.app.NotificationCompat;
import static android.content.Context.NOTIFICATION_SERVICE;

public class MyWidget extends AppWidgetProvider {
    final String LOG_TAG = "myLogs";
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(LOG_TAG, "onUpdate " );

        for (int i : appWidgetIds) {
            Log.d(LOG_TAG, "onUpdate start " );
            updateWidget(context, appWidgetManager, i);
        }
    }
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Editor editor = context.getSharedPreferences(
                ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE).edit();
        for (int widgetID : appWidgetIds) {
            editor.remove(ConfigActivity.WIDGET_TIME + widgetID);
            editor.remove(ConfigActivity.WIDGET_COUNT + widgetID);
        }
        editor.commit();
    }

    static void updateWidget(Context ctx, AppWidgetManager appWidgetManager, int widgetID) {

        SharedPreferences sp = ctx.getSharedPreferences(ConfigActivity.WIDGET_PREF, Context.MODE_PRIVATE);
        String time = sp.getString(ConfigActivity.WIDGET_TIME + widgetID, null);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
        LocalDate startDate = LocalDate.parse(time, formatter);
        LocalDate endDate = LocalDate.parse(currentDate, formatter);
        int days = startDate.getDayOfYear() -endDate.getDayOfYear();
        Period period = Period.ofDays(days);

        if (time == null) return;
        Log.d("myLogs", currentTime );
        if ((currentTime).equals("09")&& period.getDays()==0 && period.getMonths()==0 && period.getYears()==0 ) {
        Log.d("myLogs", "start " );

    NotificationChannel androidChannel = new NotificationChannel("1",
            "chanel 6", NotificationManager.IMPORTANCE_DEFAULT);

    androidChannel.enableLights(true);

    androidChannel.enableVibration(true);
    androidChannel.setLightColor(Color.GREEN);
    androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, "1")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Время вышло!")
                    .setContentText("Я ждал этого! 12 лет ждал! В азкабане!");
    Notification notification = builder.build();
    NotificationManager notificationManager =
            (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
    notificationManager.notify(1, notification);
    Log.d("myLogs", "end" );
}
        RemoteViews widgetView = new RemoteViews(ctx.getPackageName(),R.layout.widget);
        widgetView.setTextViewText(R.id.time, Integer.toString(period.getDays()));
        Intent configIntent = new Intent(ctx, ConfigActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        PendingIntent pIntent = PendingIntent.getActivity(ctx, widgetID, configIntent, 0);
        widgetView.setOnClickPendingIntent(R.id.time, pIntent);
        Intent updateIntent = new Intent(ctx, MyWidget.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { widgetID });
        appWidgetManager.updateAppWidget(widgetID, widgetView);
    }
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}