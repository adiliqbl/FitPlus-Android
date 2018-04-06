package app.fitplus.health.Speech;

import android.app.Application;

/**
 * @author Aleksandar Gotev
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Speech.init(this, getPackageName());
        Logger.setLogLevel(Logger.LogLevel.DEBUG);
    }
}
