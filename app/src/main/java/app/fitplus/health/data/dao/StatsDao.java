package app.fitplus.health.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import app.fitplus.health.data.model.Stats;
import io.reactivex.Flowable;

@Dao
public abstract class StatsDao extends BaseDao<Stats> {

    @Query("SELECT * FROM Stats LIMIT 1")
    public abstract Flowable<Stats> getStats();

    @Query("SELECT * FROM Stats LIMIT 1")
    public abstract Stats getStatsSimple();

    @Query("SELECT * FROM Stats WHERE userId = :id")
    public abstract Flowable<Stats> getStatsById(String id);

    @Query("DELETE FROM Stats")
    public abstract void deleteAllStatss();
}
