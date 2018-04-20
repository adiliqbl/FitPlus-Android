package app.fitplus.health.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import app.fitplus.health.data.model.User;
import io.reactivex.Flowable;

@Dao
public abstract class UserDao extends BaseDao<User> {
    @Query("SELECT * FROM User LIMIT 1")
    public abstract Flowable<User> getUser();

    @Query("SELECT * FROM User LIMIT 1")
    public abstract User getUserSimple();

    @Query("SELECT * FROM User WHERE id = :id")
    public abstract Flowable<User> getUserById(String id);

    @Query("DELETE FROM User")
    public abstract void deleteAllUsers();
}
