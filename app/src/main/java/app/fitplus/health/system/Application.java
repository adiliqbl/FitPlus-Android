package app.fitplus.health.system;

import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;

import org.jetbrains.annotations.Contract;

import app.fitplus.health.data.User;
import app.fitplus.health.R;
import app.fitplus.health.system.receiver.ConnectionReceiver;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Application extends android.app.Application {

    private static Application INSTANCE = null;
    public static User user = null;
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
}
