package app.fitplus.health.system.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.ankushgrover.hourglass.Hourglass;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import app.fitplus.health.R;
import app.fitplus.health.system.events.NoSensorEvent;
import app.fitplus.health.system.events.SessionEndEvent;
import app.fitplus.health.ui.tracking.TrackingActivity;

import static app.fitplus.health.util.Constants.SERVICE.PEDOMETER_CHANNEL_ID;
import static app.fitplus.health.util.Constants.SERVICE.PEDOMETER_CHANNEL_NAME;
import static app.fitplus.health.util.Constants.SERVICE.PEDOMETER_CLICK;
import static app.fitplus.health.util.Constants.SERVICE.PEDOMETER_START;

public class PedometerService extends Service implements SensorEventListener {

    private float numSteps;
    private int TOTAL_TIME;

    private final IBinder mBinder = new LocalBinder();
    private PedometerListener callback;

    private NotificationManager notificationManager;

    private SensorManager sensorManager;
    private Sensor countSensor;

    private Hourglass clock;

    public PedometerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            EventBus.getDefault().postSticky(new NoSensorEvent());

            stopSelf();
        }

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (PEDOMETER_START.equals(intent.getAction())) {

                Intent notificationIntent = new Intent(this, TrackingActivity.class);
                notificationIntent.setAction(PEDOMETER_CLICK);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                Notification.Builder notification = buildNotification(pendingIntent);

                startForeground(100, notification.build());
            }
        }

        return START_STICKY;
    }

    @NonNull
    private Notification.Builder buildNotification(@NonNull PendingIntent pendingIntent) {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(PEDOMETER_CHANNEL_ID, PEDOMETER_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
            builder = new Notification.Builder(this, PEDOMETER_CHANNEL_ID);
        } else builder = new Notification.Builder(this);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        return builder.setContentTitle("Activity going on")
                .setContentText("Your location is being tracked for health data")
                .setLargeIcon(icon)
                .setSmallIcon(R.drawable.logo_vector)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(true)
                .setContentIntent(pendingIntent);
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this, countSensor);

        stopForeground(true);
        if (clock.isRunning()) clock.stopTimer();
        clock = null;

        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        public PedometerService getService() {
            return PedometerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        callback = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        numSteps = event.values[0];

        if (callback != null) callback.onStep(numSteps);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void startTimer(int TOTAL_TIME) {
        // Start timer
        this.TOTAL_TIME = TOTAL_TIME;
        clock = new Hourglass(TimeUnit.MINUTES.toMillis(TOTAL_TIME)) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTimerTick(long timeRemaining) {
                if (callback != null) callback.onTimerTick(timeRemaining);
            }

            @Override
            public void onTimerFinish() {
                if (callback == null)
                    EventBus.getDefault().postSticky(new SessionEndEvent(numSteps));
                else callback.onSessionEnd(numSteps);
            }
        };

        clock.startTimer();
    }

    public void pauseTimer(boolean IS_PAUSE) {
        if (IS_PAUSE) clock.pauseTimer();
        else clock.resumeTimer();
    }

    public void stopTimer() {
        clock.stopTimer();
    }

    public void registerListener(PedometerListener listener) {
        this.callback = listener;
    }

    public interface PedometerListener {
        void onStep(float steps);

        void onSessionEnd(float totalSteps);

        void onTimerTick(long timeRemaining);
    }
}
