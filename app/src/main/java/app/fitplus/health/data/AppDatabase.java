package app.fitplus.health.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.content.SharedPreferences;

import app.fitplus.health.data.dao.GoalsDao;
import app.fitplus.health.data.dao.StatsDao;
import app.fitplus.health.data.dao.UserDao;
import app.fitplus.health.data.model.Goals;
import app.fitplus.health.data.model.Stats;
import app.fitplus.health.data.model.User;

@Database(entities = {User.class, Goals.class, Stats.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();

    public abstract StatsDao statsDao();

    public abstract GoalsDao goalsDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "friendlylimo-rider.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static void setSession(Context context, final boolean status) {
        SharedPreferences.Editor prefs = context.getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        prefs.putBoolean("status", status);
        prefs.apply();
    }

    public static boolean getSession(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return prefs.getBoolean("status", false);
    }
}