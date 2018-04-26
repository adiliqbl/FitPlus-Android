package app.fitplus.health.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Dao
public abstract class BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(T entity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract void update(T entity);

    @Delete
    abstract void delete(T entity);

    // Custom
    public Completable add(T entity) {
        return Completable.fromAction(() -> insert(entity))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable save(T entity) {
        return Completable.fromAction(() -> update(entity))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable remove(T entity) {
        return Completable.fromAction(() -> delete(entity))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
