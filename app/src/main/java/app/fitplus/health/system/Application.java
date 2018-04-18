package app.fitplus.health.system;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.Contract;

import app.fitplus.health.R;
import app.fitplus.health.data.DataManager;
import app.fitplus.health.system.receiver.ConnectionReceiver;
import app.fitplus.health.ui.AppLaunch;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Application extends android.app.Application {

    private static Application INSTANCE = null;
    public static FirebaseUser user = null;
    public static boolean CONNECTED = Boolean.TRUE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = Application.this;


        // Default Font
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/PierSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        /*
         * Timber Initialization
         */
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected void log(int priority, String tag, @NonNull String message, Throwable t) {
                super.log(priority, "Timber::" + tag, message, t);
            }
        });
        Timber.plant(new CrashlyticsTree());

        final Fabric fabric = new Fabric.Builder(this).kits(new Crashlytics())
                .debuggable(false)           // Enables Crashlytics debugger
                .build();
        Fabric.with(fabric);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            // Registering Internet Receiver
            IntentFilter internetFilter = new IntentFilter();
            internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(new ConnectionReceiver(), internetFilter);
        }
    }

    @Contract(pure = true)
    public static synchronized Application getInstance() {
        return INSTANCE;
    }

    public static FirebaseUser getUser() {
        if (user == null) user = FirebaseAuth.getInstance().getCurrentUser();
        return user;
    }

    public void Logout(Activity activity) {
        DataManager.deleteDB(activity);

        AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener(task -> {
                    startActivity(new Intent(activity, AppLaunch.class));
                    activity.finish();
                });
    }
}
