package app.fitplus.health.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import app.fitplus.health.data.model.Goals;
import io.reactivex.Flowable;

@Dao
public abstract class GoalsDao extends BaseDao<Goals> {

    @Query("SELECT * FROM Goals LIMIT 1")
    public abstract Flowable<Goals> getGoals();

    @Query("SELECT * FROM Goals LIMIT 1")
    public abstract Goals getGoalsSimple();

    @Query("SELECT * FROM Goals WHERE userId = :id")
    public abstract Flowable<Goals> getGoalsById(String id);

    @Query("DELETE FROM Goals")
    public abstract void deleteAllGoals();
}
