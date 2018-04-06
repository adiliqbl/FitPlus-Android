package app.fitplus.health.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import app.fitplus.health.R;
import app.fitplus.health.ui.user.LoginActivity;
import app.fitplus.health.ui.user.RegisterActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AppLaunch extends AppCompatActivity {

    private boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_launch);
        ButterKnife.bind(this);

        if (ContextCompat.checkSelfPermission(AppLaunch.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(AppLaunch.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == 10) {
                launchApp();
            } else if (resultCode == 20) {
                startActivityForResult(new Intent(this, RegisterActivity.class), 2);
            }
        } else if (requestCode == 10) {
            launchApp();
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 2: {
                break;
            }
        }
    }

    private void launchApp() {
        startActivity(new Intent(AppLaunch.this, MainActivity.class));
        finish();
    }

    @OnClick(R.id.login_app)
    public void login() {
        startActivityForResult(new Intent(AppLaunch.this, LoginActivity.class), 1);
    }
}