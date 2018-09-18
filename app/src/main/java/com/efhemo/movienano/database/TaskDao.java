package com.efhemo.movienano.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.efhemo.movienano.model.Movie;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM movietable ORDER BY identification DESC")
    LiveData<List<Movie>> loadAllFavouriteMovie();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMovie(Movie movie);

    @Query("DELETE FROM movietable WHERE id = :usedId")
    abstract void oneMovie(Integer usedId);

}
