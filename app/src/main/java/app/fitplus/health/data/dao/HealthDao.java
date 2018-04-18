package app.fitplus.health.data.dao;

import android.arch.persistence.room.Dao;

import app.fitplus.health.data.model.Health;

@Dao
public abstract class HealthDao extends BaseDao<Health> {
}
