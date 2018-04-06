package app.fitplus.health.data;

import android.content.Context;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import timber.log.Timber;

public class DataManager {

    public static final String USER_PREFS_NAME = "data";
    public static final String USER = "user";
    public static final String isLogged = "isLogged";
    public static final String GOALS = "goals";
    public static final String PROGRESS = "progress";

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

    public static void saveProgress(Context context, Progress progress) {
        try {
            DB snappydb = DBFactory.open(context, USER_PREFS_NAME);
            snappydb.put(GOALS, progress);
            snappydb.close();
        } catch (SnappydbException e) {
            Timber.e(e);
        }
        context = null;
        progress = null;
    }

    public static synchronized Progress getProgress(Context context) {
        Progress goal = null;
        try {
            DB snappydb = DBFactory.open(context, USER_PREFS_NAME);
            if (snappydb.exists(PROGRESS)) {
                goal = snappydb.get(PROGRESS, Progress.class);
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
