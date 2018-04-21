package app.fitplus.health.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import app.fitplus.health.data.model.Stats;

@Dao
public abstract class StatsDao extends BaseDao<Stats> {
    @Query("SELECT * FROM Stats WHERE userId = :id")
    public abstract Stats getStatsById(String id);

    @Query("DELETE FROM Stats")
    public abstract void deleteAllStats();
}
