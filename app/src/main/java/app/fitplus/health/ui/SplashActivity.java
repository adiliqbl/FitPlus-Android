package app.fitplus.health.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.SupportMapFragment;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import app.fitplus.health.data.User;
import app.fitplus.health.system.Application;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static app.fitplus.health.data.DataManager.USER;
import static app.fitplus.health.data.DataManager.USER_PREFS_NAME;
import static app.fitplus.health.data.DataManager.isLogged;

public class SplashActivity extends AppCompatActivity {

    private Disposable initial;
    private boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initial = Observable.fromCallable(this::initializer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (check) {
                        if (ContextCompat.checkSelfPermission(SplashActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                        else launchApp();
                    } else {
                        startActivity(new Intent(this, AppLaunch.class));
                        finish();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 2: {
                launchApp();
                break;
            }
        }
    }

    private void launchApp() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private boolean initializer() {
        try {
            DB snappydb = DBFactory.open(this, USER_PREFS_NAME);
            if (snappydb.exists(isLogged)) {
                check = (snappydb.getBoolean(isLogged));
                if (check) {
                    Application.user = snappydb.get(USER, User.class);
                }
            }
            snappydb.close();
        } catch (SnappydbException e) {
            Timber.e(e);
        }

        // async map
        if (check) {
            runOnUiThread(() -> new SupportMapFragment().getMapAsync(googleMap ->
                    Timber.tag("MapManager").i("Initializing map on launch")));
        }

        return check;
    }

    @Override
    protected void onDestroy() {
        if (initial != null && !initial.isDisposed()) initial.dispose();
        super.onDestroy();
    }
}

