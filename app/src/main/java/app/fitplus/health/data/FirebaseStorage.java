package app.fitplus.health.data;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import app.fitplus.health.data.model.Stats;
import timber.log.Timber;

import static app.fitplus.health.system.Application.getUser;
import static app.fitplus.health.util.Constants.DATABASE.GOALS;
import static app.fitplus.health.util.Constants.DATABASE.PROGRESS;
import static app.fitplus.health.util.Constants.DATABASE.USER_PREFS_NAME;

public class FirebaseStorage {

    public static DatabaseReference usersReference() {
        return FirebaseDatabase.getInstance().getReference("users").child(getUser().getUid());
    }

    public static DatabaseReference goalsReference() {
        return FirebaseDatabase.getInstance().getReference("goals").child(getUser().getUid());
    }

    public static DatabaseReference statsReference() {
        return FirebaseDatabase.getInstance().getReference("stats").child(getUser().getUid());
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
