package app.fitplus.health.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

import app.fitplus.health.R;
import app.fitplus.health.system.Application;
import app.fitplus.health.ui.user.CompleteRegistration;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class AppLaunch extends AppCompatActivity {

    private static final int SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_launch);
        ButterKnife.bind(this);

        if (ContextCompat.checkSelfPermission(AppLaunch.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(AppLaunch.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {

                // TODO : fetch health data here also

                Application.user = FirebaseAuth.getInstance().getCurrentUser();

                if (Application.user.getDisplayName() == null || "".equals(Application.user.getDisplayName())) {
                    startActivity(new Intent(this, CompleteRegistration.class));
                    finish();
                } else {
                    startActivity(new Intent(AppLaunch.this, MainActivity.class));
                    finish();
                }
            } else {
                if (response != null && response.getError() != null) {
                    Timber.e(response.getError());
                }
            }
        }
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

    @OnClick(R.id.login_app)
    public void login() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.PhoneBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setTheme(R.style.AppTheme_Reverse)
                        .setLogo(R.drawable.logo_vector)
                        .build(),
                SIGN_IN);
    }
}