package com.popularmovies.abhis.popularmovies.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.popularmovies.abhis.popularmovies.Model.MovieData;

import java.util.ArrayList;
import java.util.List;


@Dao
public interface MoviesDao {
    @Query("SELECT * FROM moviesfav")
    LiveData<List<MovieData>>getAllMovies();

    @Query("SELECT * FROM moviesfav WHERE movieID = :id")
    LiveData<MovieData> getMoviesLiveData(String id);

    @Insert
    void insertMovie(MovieData moviesResult);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieData moviesResult);

    @Delete
    void deleteMovies(MovieData moviesResult);
}
