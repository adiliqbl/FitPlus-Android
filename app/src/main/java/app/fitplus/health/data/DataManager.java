package app.fitplus.health.data;

import android.content.Context;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import app.fitplus.health.data.model.Goals;
import app.fitplus.health.data.model.Stats;
import app.fitplus.health.data.model.User;
import timber.log.Timber;

import static app.fitplus.health.util.Constants.DATABASE.GOALS;
import static app.fitplus.health.util.Constants.DATABASE.PROGRESS;
import static app.fitplus.health.util.Constants.DATABASE.USER;
import static app.fitplus.health.util.Constants.DATABASE.USER_PREFS_NAME;

public class DataManager {

    public static void saveUser(Context context, User user) {
        try {
            DB snappydb = DBFactory.open(context, USER_PREFS_NAME);
            snappydb.put(USER, user);
            snappydb.close();
        } catch (SnappydbException e) {
            Timber.e(e);
        }
        context = null;
        user = null;
    }

    public static synchronized User getUser(Context context) {
        User user = null;
        try {
            DB snappydb = DBFactory.open(context, USER_PREFS_NAME);
            if (snappydb.exists(USER)) {
                user = snappydb.get(USER, User.class);
            }
            snappydb.close();
        } catch (SnappydbException e) {
            Timber.e(e);
        }
        context = null;
        return user;
    }

    public static void saveGoals(Context context, Goals goal) {
        try {
            DB snappydb = DBFactory.open(context, USER_PREFS_NAME);
            snappydb.put(GOALS, goal);
            snappydb.close();
        } catch (SnappydbException e) {
            Timber.e(e);
        }
        context = null;
        goal = null;
    }

    public static synchronized Goals getGoals(Context context) {
        Goals goal = null;
        try {
            DB snappydb = DBFactory.open(context, USER_PREFS_NAME);
            if (snappydb.exists(GOALS)) {
                goal = snappydb.get(GOALS, Goals.class);
            }
            snappydb.close();
        } catch (SnappydbException e) {
            Timber.e(e);
        }
        context = null;
        return goal;
    }

    public static void saveProgress(Context context, Stats stats) {
        try {
            DB snappydb = DBFactory.open(context, USER_PREFS_NAME);
            snappydb.put(GOALS, stats);
            snappydb.close();
        } catch (SnappydbException e) {
            Timber.e(e);
        }
        context = null;
        stats = null;
    }

    public static synchronized Stats getProgress(Context context) {
        Stats goal = null;
        try {
            DB snappydb = DBFactory.open(context, USER_PREFS_NAME);
            if (snappydb.exists(PROGRESS)) {
                goal = snappydb.get(PROGRESS, Stats.class);
            }
            snappydb.close();
        } catch (SnappydbException e) {
            Timber.e(e);
        }
        context = null;
        return goal;
    }

    public static void deleteDB(Context context) {
        try {
            DB snappydb = DBFactory.open(context, USER_PREFS_NAME);
            snappydb.destroy();
            snappydb.close();
        } catch (SnappydbException e) {
            Timber.e(e);
        }

        context = null;
    }
}
