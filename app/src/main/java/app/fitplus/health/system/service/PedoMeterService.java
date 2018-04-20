package app.fitplus.health.system.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import org.greenrobot.eventbus.EventBus;

import app.fitplus.health.system.service.Pedometer.StepDetector;
import app.fitplus.health.system.service.Pedometer.StepListener;
import app.fitplus.health.system.events.PedometerEvent;

public class PedoMeterService extends Service implements SensorEventListener, StepListener {

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;

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

//        EventBus.getDefault().register(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null) {
//            if (intent.getAction().equals("")) {
//                Intent notificationIntent = new Intent(this, MapController.class);
//                notificationIntent.setAction("app.competition.health");
//                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                        notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//                // Cancel Button
//                Intent cancelIntent = new Intent(this, RequestRideService.class);
//                cancelIntent.setAction(Constants.ACTION.CANCEL_SEARCH);
//                PendingIntent cancelSearch = PendingIntent.getService(this, 0, cancelIntent, 0);
//
//                Notification.Builder notification = buildNotification(pendingIntent, cancelSearch,
//                        "Searching for ride", "Searching for nearby driver");
//
//                startForeground(RIDE_NOTIFICATION_ID, notification.build());
//            }
//        }

        return START_STICKY;
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
        EventBus.getDefault().postSticky(new PedometerEvent(numSteps));
    }

    @Override
    public void onDestroy() {
//        EventBus.getDefault().unregister(this);
//        stopForeground(true);

        super.onDestroy();
    }
}
