package app.fitplus.health.system;

import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

class CrashlyticsTree extends Timber.Tree {

    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable throwable) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return;
        }

        Throwable t = throwable != null ? throwable : new Exception(message);

        // Firebase Crashlytics
        Crashlytics.setInt("priority", priority);
        Crashlytics.setString("tag", tag);
        Crashlytics.setString("message", message);
        Crashlytics.logException(t);
    }
}
