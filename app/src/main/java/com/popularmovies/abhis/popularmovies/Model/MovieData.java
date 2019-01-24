package com.popularmovies.abhis.popularmovies.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "moviesfav")
public class MovieData implements Serializable {
    @PrimaryKey
    @NonNull
    private String movieID;
    private String movieTitle;
    private String movieDesc;
    private String movieImg;
    private String movieVotes;
    private String movieDirector;
    private String movieRating;
    private String movieRelease;

    @Ignore
    public MovieData() {
    }


    public MovieData(String movieTitle, String movieDesc, String movieImg, String movieRating, String movieRelease, String movieID) {
        this.movieTitle = movieTitle;
        this.movieDesc = movieDesc;
        this.movieImg = movieImg;
        this.movieRating = movieRating;
        this.movieRelease = movieRelease;
        this.movieID = movieID;
    }


    public String getMovieVotes() {
        return movieVotes;
    }

    public void setMovieVotes(String movieVotes) {
        this.movieVotes = movieVotes;
    }

    public String getMovieDirector() {
        return movieDirector;
    }

    public void setMovieDirector(String movieDirector) {
        this.movieDirector = movieDirector;
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieDesc() {
        return movieDesc;
    }

    public void setMovieDesc(String movieDesc) {
        this.movieDesc = movieDesc;
    }

    public String getMovieImg() {
        return movieImg;
    }

    public void setMovieImg(String movieImg) {
        this.movieImg = movieImg;
    }

    public String getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(String movieRating) {
        this.movieRating = movieRating;
    }

    public String getMovieRelease() {
        return movieRelease;
    }

    public void setMovieRelease(String movieRelease) {
        this.movieRelease = movieRelease;
    }


}
