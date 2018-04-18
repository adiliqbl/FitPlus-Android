package app.fitplus.health.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.fitplus.health.system.Application;
import app.fitplus.health.ui.user.CompleteRegistration;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    private Disposable initial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initial = Observable.fromCallable(this::initializer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o == 2) {
                        if (ContextCompat.checkSelfPermission(SplashActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                        else launchApp();
                    } else if (o == 1) {
                        startActivity(new Intent(this, CompleteRegistration.class));
                        finish();
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

    private int initializer() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Application.user = user;
            if (user.getDisplayName() == null || "".equals(user.getDisplayName()))
                return 1;
            else return 2;
        } else return 0;
    }

    @Override
    protected void onDestroy() {
        if (initial != null && !initial.isDisposed()) initial.dispose();
        super.onDestroy();
    }
}

