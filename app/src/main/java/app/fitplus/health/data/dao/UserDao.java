package app.fitplus.health.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import app.fitplus.health.data.model.User;

@Dao
public abstract class UserDao extends BaseDao<User> {
    @Query("SELECT * FROM User WHERE id = :id")
    public abstract User getUserById(String id);

    @Query("DELETE FROM User")
    public abstract void deleteAllUsers();
}
