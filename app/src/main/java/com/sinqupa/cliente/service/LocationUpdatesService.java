package com.sinqupa.cliente.service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import com.sinqupa.cliente.MainActivity;
import com.sinqupa.cliente.R;
import com.sinqupa.cliente.utility.Utility;

public class LocationUpdatesService extends Service {
    private Handler handler;
    public static final long DEFAULT_SYNC_INTERVAL = 2000;
    private NotificationManager mNotificationManager;
    private boolean isFive,isTen,isFifteen,isTwenty,isTwentyFive,isThirty;

    public LocationUpdatesService() {
    }

    @Override
    public void onCreate() {
        if (Utility.distance != 0) {
            isFive = Utility.distance == 5 ? true : false;
            isTen = Utility.distance == 10 ? true : false;
            isFifteen =  Utility.distance == 15 ? true : false;
            isTwenty = Utility.distance == 20 ? true : false;
            isTwentyFive = Utility.distance == 25 ? true : false;
            isThirty = Utility.distance == 30 ? true : false;
        }

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            NotificationChannel mChannel = new NotificationChannel(Utility.CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setSound(Utility.uriNotification,new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build());
            mChannel.setVibrationPattern(new long[]{1000,1000,1000,1000,1000});
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean startedFromNotification = intent.getBooleanExtra(Utility.EXTRA_STARTED_FROM_NOTIFICATION,false);
        if (startedFromNotification)
            mNotificationManager.cancel(Utility.NOTIFICATION_ID);

        handler = new Handler();
        handler.post(runnableService);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnableService);
        mNotificationManager.cancel(Utility.NOTIFICATION_ID);
    }

    //Metodo para Crear la Notificacion
    private Notification getNotification() {
        Intent intent = new Intent(this, LocationUpdatesService.class);
        intent.putExtra(Utility.EXTRA_STARTED_FROM_NOTIFICATION, true);
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,new Intent(this, MainActivity.class), 0);
        Notification.Action servicePendingAction = new Notification.Action.Builder(Icon.createWithResource(this, R.drawable.icon_cancel),getString(R.string.remove_location_updates),servicePendingIntent).build();
        Notification.Action activityPendingAction = new Notification.Action.Builder(Icon.createWithResource(this,R.drawable.icon_redirection),getString(R.string.open_activity),activityPendingIntent).build();
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.addAction(activityPendingAction);
        builder.addAction(servicePendingAction);
        builder.setSmallIcon(R.drawable.icon_notification);
        builder.setContentText(getString(R.string.notification_text));
        builder.setColor(Color.RED);
        builder.setWhen(System.currentTimeMillis());
        builder.setVibrate(new long[]{1000,1000,1000,1000,1000});
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setSound(Utility.uriNotification);
        builder.setOngoing(true);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            builder.setChannelId(Utility.CHANNEL_ID); // Channel ID
        return builder.build();
    }


    //Metodo para validar la Notificacion
    private void notificationMap(Location locationEmployee){
        if (Utility.locationCustomer != null && locationEmployee != null) {
            float distanceInMeters = Utility.locationCustomer.distanceTo(locationEmployee);
            Toast.makeText(getApplicationContext(),"la distancia es : " + distanceInMeters , Toast.LENGTH_SHORT).show();
            if (distanceInMeters < 6 && isFive){
                mNotificationManager.notify(Utility.NOTIFICATION_ID, getNotification());
                isFive = false;
            }else if ((distanceInMeters < 11 && isTen)){
                mNotificationManager.notify(Utility.NOTIFICATION_ID, getNotification());
                isTen = false;
            }else if (distanceInMeters < 16 && isFifteen){
                mNotificationManager.notify(Utility.NOTIFICATION_ID, getNotification());
                isFifteen = false;
            }else if (distanceInMeters < 21 && isTwenty){
                mNotificationManager.notify(Utility.NOTIFICATION_ID, getNotification());
                isTwenty = false;
            } else if (distanceInMeters < 26 && isTwentyFive){
                mNotificationManager.notify(Utility.NOTIFICATION_ID, getNotification());
                isTwentyFive = false;
            }else if (distanceInMeters < 31 && isThirty){
                mNotificationManager.notify(Utility.NOTIFICATION_ID, getNotification());
                isThirty = false;
            }
        }
    }

    private Runnable runnableService = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(),"Corriendo",Toast.LENGTH_SHORT);
            notificationMap(Utility.locationEmployee);
            handler.postDelayed(runnableService,DEFAULT_SYNC_INTERVAL);
        }
    };
}
