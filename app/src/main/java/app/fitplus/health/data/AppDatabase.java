package app.fitplus.health.data;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import app.fitplus.health.data.model.Goals;
import app.fitplus.health.data.model.Health;
import app.fitplus.health.data.model.Stats;

@Database(entities = {Goals.class, Health.class, Stats.class}, version = 1)
@TypeConverters({Converters.class})
public class AppDatabase extends RoomDatabase {

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}