package app.fitplus.health.system.service;

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
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import app.fitplus.health.R;
import app.fitplus.health.system.events.PedometerEvent;
import app.fitplus.health.system.service.Pedometer.StepDetector;
import app.fitplus.health.system.service.Pedometer.StepListener;
import app.fitplus.health.ui.tracking.TrackingActivity;

import static app.fitplus.health.util.Constants.SERVICE.PEDOMETER_CHANNEL_ID;
import static app.fitplus.health.util.Constants.SERVICE.PEDOMETER_CHANNEL_NAME;
import static app.fitplus.health.util.Constants.SERVICE.PEDOMETER_CLICK;
import static app.fitplus.health.util.Constants.SERVICE.PEDOMETER_START;

public class PedoMeterService extends Service implements SensorEventListener, StepListener {

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;

    private NotificationManager notificationManager;

    public PedoMeterService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

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
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(true)
                .setContentIntent(pendingIntent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        EventBus.getDefault().post(new PedometerEvent(numSteps));
    }

    @Override
    public void onDestroy() {
        stopForeground(true);

        super.onDestroy();
    }
}
