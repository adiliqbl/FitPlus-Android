package app.fitplus.health.ui.tracking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ankushgrover.hourglass.Hourglass;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.TimeUnit;

import app.fitplus.health.R;
import app.fitplus.health.data.DataProvider;
import app.fitplus.health.system.ClearMemory;
import app.fitplus.health.system.events.PedometerEvent;
import app.fitplus.health.system.events.SessionEndEvent;
import app.fitplus.health.system.service.PedoMeterService;
import app.fitplus.health.system.service.Pedometer.CaloryCalculator;
import app.fitplus.health.util.AnimUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static app.fitplus.health.util.Constants.SERVICE.PEDOMETER_START;

public class TrackingActivity extends RxAppCompatActivity implements OnMapReadyCallback,
        OnSuccessListener<Location>, ClearMemory, SensorEventListener {

    private int ACTIVITY_STATUS = 0;
    private int TOTAL_TIME = 20;
    private int WEIGHT = 0;

    @BindView(R.id.timer)
    TextView timer;
    @BindView(R.id.calorie_burned)
    TextView calorie;
    @BindView(R.id.total_steps)
    TextView steps;
    @BindView(R.id.distance_covered)
    TextView distance;

    private int stepCount = 0;
    private double calorieBurnCount = 0;
    private int distanceCount = 0;
    private boolean running = false;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient = null;
    private Hourglass clock;
    private CaloryCalculator calculate;

    private DataProvider dataProvider;

    private SensorManager sensorManager;
    private Sensor countSensor;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Reset
        calorie.setText("0 calorie");
        steps.setText("0 steps");
        distance.setText("0 km");

        dataProvider = (DataProvider) getIntent().getSerializableExtra("dataProvider");
        if (dataProvider.getUser() != null && dataProvider.getUser().getSessionLength() > 5) {
            TOTAL_TIME = dataProvider.getUser().getSessionLength();
            WEIGHT = dataProvider.getUser().getWeight();
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    void setMapGestures() {
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap == null) mMap = googleMap;

        if (!isGPSEnabled()) {
            enableGPS();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                                location.getLongitude()), 17));
                    }
                });
            else requestGPSPermission();
        }
        mMap.setPadding(20, 20, 20, 20);

        startLocationManager();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        setMapGestures();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }

                    startLocationManager();

                    if (!isGPSEnabled()) enableGPS();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        running = true;
        countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);

        super.onPause();
        running = false;
        sensorManager.unregisterListener(this, countSensor);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            if (mMap != null) mMap.clear();
        }
    }

    private void startLocationManager() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ((LocationManager) this.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Timber.tag("LocationService").d("Starting Location Service");

            if (fusedLocationClient != null && locationCallback != null)
                fusedLocationClient.removeLocationUpdates(locationCallback);

            fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, null);
        }
    }

    LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            onLocationChanged(locationResult.getLastLocation());
        }
    };

    private LocationRequest createLocationRequest() {
        return LocationRequest.create()
                .setInterval(500)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(10);
    }

    void animateMapCamera(final LatLng latLng) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    @Override
    public void onSuccess(Location location) {
        // Got last known location. In some rare situations this can be null.
        if (location != null) {
            LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
            animateMapCamera(myLoc);
            location = null;
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(createLocationRequest(), new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult.getLocations().get(0) != null) {
                        Location location1 = locationResult.getLocations().get(0);
                        LatLng myLoc = new LatLng(location1.getLatitude(), location1.getLongitude());
                        animateMapCamera(myLoc);
                    }
                }
            }, null);
        }
    }

    private void onLocationChanged(Location location) {
        if (location != null) {
            LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
            animateMapCamera(myLoc);
            location = null;
        }
    }

    void goToCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this);
        } else requestGPSPermission();
    }

    void enableGPS() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        task.addOnCompleteListener(task1 -> {
            try {
                LocationSettingsResponse response = task1.getResult(ApiException.class);
                // All location settings are satisfied. The client can initialize location
                // requests here.

                goToCurrentLocation();

            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(this, 10);
                        } catch (IntentSender.SendIntentException | ClassCastException e) {
                            Timber.e(e);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    private void requestGPSPermission() {
        Toast.makeText(this, "Please allow this permission for app to work properly.", Toast.LENGTH_LONG).show();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
    }

    boolean isGPSEnabled() {
        return ((LocationManager) this.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onDestroy() {
        clearMemory();
        super.onDestroy();
    }

    @Override
    public void clearMemory() {
        if (mMap != null) mMap.clear();
        mMap = null;

        fusedLocationClient.removeLocationUpdates(locationCallback);
        fusedLocationClient = null;
        clock = null;
    }

    @Override
    public void onBackPressed() {
        if (ACTIVITY_STATUS != 0) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder
                    .setTitle("")
                    .setMessage("Are you sure you want to stop activity?")
                    .setCancelable(false)
                    .setPositiveButton("Stop", (dialog, id) -> {
                        saveTrackData();
                        finish();
                    })
                    .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else super.onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.start_activity)
    public void startOrPause() {
        if (!isGPSEnabled()) {
            enableGPS();
            return;
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestGPSPermission();
            return;
        }

        switch (ACTIVITY_STATUS) {
            case 0: // Started

                running = true;
                startPedometerService();

                // Start timer
                clock = new Hourglass(TimeUnit.MINUTES.toMillis(TOTAL_TIME)) {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onTimerTick(long timeRemaining) {
                        long secs = timeRemaining / 1000;
                        timer.setText(String.format("%02d:%02d", (secs % 3600) / 60, secs % 60));
                    }

                    @Override
                    public void onTimerFinish() {
                        stop();
                    }
                };
                clock.startTimer();

                findViewById(R.id.total_time).setVisibility(View.VISIBLE);

                AnimUtil.setChangeText(findViewById(R.id.tracking_layout));
                ((Button) findViewById(R.id.start_activity)).setText("Pause");

                ((TextView) findViewById(R.id.total_time)).setText(TOTAL_TIME + " MIN");
                ((FloatingActionButton) findViewById(R.id.stop)).setImageResource(R.drawable.ic_stop_black_24dp);
                ACTIVITY_STATUS = 1;
                break;
            case 1: // Paused
                running = false;
                AnimUtil.setChangeText(findViewById(R.id.tracking_layout));
                ((Button) findViewById(R.id.start_activity)).setText("Resume");
                clock.pauseTimer();
                ACTIVITY_STATUS = 2;
                break;
            case 2: // Resumed
                running = true;
                AnimUtil.setChangeText(findViewById(R.id.tracking_layout));
                ((Button) findViewById(R.id.start_activity)).setText("Pause");
                clock.resumeTimer();
                ACTIVITY_STATUS = 1;
                break;
        }
    }

    @OnClick(R.id.stop)
    public void onStopPressed() {
        if (ACTIVITY_STATUS == 0) {
            finish();
            return;
        }

        clock.stopTimer();
    }

    @SuppressLint("SetTextI18n")
    private void stop() {
        ACTIVITY_STATUS = 0;

        saveTrackData();

        stopPedometerService();

        AnimUtil.setChangeText(findViewById(R.id.tracking_layout));
        timer.setText("");
        timer.setHint("00:00");
        AnimUtil.setChangeText(findViewById(R.id.tracking_layout));
        ((Button) findViewById(R.id.start_activity)).setText("Start");
        findViewById(R.id.total_time).setVisibility(View.GONE);
        ((FloatingActionButton) findViewById(R.id.stop)).setImageResource(R.drawable.ic_close_black_24dp);
    }

    private void saveTrackData() {
//        Stats stats = FirebaseStorage.getProgress(this);
//        if (stats == null) stats = new Stats();
//        stats.setCalorieBurned(stats.getCalorieBurned() + ((Double) calorieBurnCount).intValue());
//        stats.setSteps(stats.getSteps() + stepCount);
    }

    boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
            if (serviceClass.getName().equals(service.service.getClassName())) return true;
        return false;
    }

    void startPedometerService() {
        Intent intent = new Intent(this, PedoMeterService.class);
        intent.setAction(PEDOMETER_START);
        startService(intent);
    }

    void stopPedometerService() {
        if (isMyServiceRunning(PedoMeterService.class)) {
            stopService(new Intent(this, PedoMeterService.class));
        }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    public void onEvent(PedometerEvent event) {
        stepCount += event.getSteps();
        calorieBurnCount = calculate.steptocal(event.getSteps(), 0);
        steps.setText(String.valueOf(event.getSteps()) + " steps");
        double dist = event.getSteps() * 0.001;
        distance.setText(String.valueOf(dist) + " km");
        calorie.setText(String.valueOf(calculate.steptocal(event.getSteps(), 0)));
    }

    @Subscribe(sticky = true)
    public void onEvent(SessionEndEvent event) {
        findViewById(R.id.stop).performClick();

        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (running) {
            steps.setText(String.format("%s steps", String.valueOf(event.values[0])));
            distance.setText(String.format("%s km", String.valueOf(getDistanceRun(event.values[0]))));
            calorie.setText(String.format("%s calories", String.valueOf(Float.floatToIntBits(getCaloriesBurnt(event.values[0])))));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Calculate distance from Steps
    public float getDistanceRun(float steps) {
        return (steps * 78) / (float) 100000;
    }

    // Calculate Calories from Steps
    public float getCaloriesBurnt(float steps) {
        float stepsPerMile = (float) (1.6 / getDistanceRun(steps)) * steps;
        return (float) (stepsPerMile / (WEIGHT * 1.2565));
    }
}
