package com.popularmovies.abhis.popularmovies.activities;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.popularmovies.abhis.popularmovies.Database.MovieDatabase;
import com.popularmovies.abhis.popularmovies.Model.MovieData;
import com.popularmovies.abhis.popularmovies.Model.ReviewData;
import com.popularmovies.abhis.popularmovies.Model.TrailerData;
import com.popularmovies.abhis.popularmovies.R;
import com.popularmovies.abhis.popularmovies.Utils.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class MovieDetails extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String> {
    private TextView movieTitleTV, movieDetailsTV, movieVoteTV, movieReleasedTV, trailerTV, reviewTV;
    private ImageView moviePosterIV,bannerIV;
    private ImageButton movieFavourite;
    private RequestQueue queue;
    private RecyclerView mRecyclerView,mReviewRecyclerView;
    private MovieTrailerAdapter trailerAdapter;
    private MovieReviewAdapter reviewAdapter;
    private String Key, movieTitle,movieDesc, movieRelease, movieRating,moviePoster;
    private ArrayList<TrailerData> VideoList;
    private ArrayList<ReviewData> ReviewList;
    private static List<MovieData> moviesInDatabaseList;
    private MovieDatabase movieDatabase;
    private MovieData moviesResultObject;
    private static final int LOADER= 4;
    private int movieID;

    android.support.v7.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        movieTitleTV= findViewById(R.id.movieTitleID);
        movieDetailsTV= findViewById(R.id.movieDetialsID);
        movieVoteTV= findViewById(R.id.movieVoteID);
        movieReleasedTV= findViewById(R.id.movieReleasedID);
        trailerTV =findViewById(R.id.TrailerTV);
        reviewTV =findViewById(R.id.ReviewTV);
        moviePosterIV= findViewById(R.id.moviePosterID);
        bannerIV= findViewById(R.id.banner);
        movieFavourite = findViewById(R.id.button);
        queue = Volley.newRequestQueue(this);
        mRecyclerView = findViewById(R.id.trailer_recyclerViewID);
        mRecyclerView.setHasFixedSize(true);
        mReviewRecyclerView=findViewById(R.id.review_recyclerViewID);
        mReviewRecyclerView.setHasFixedSize(true);
        Intent intent=getIntent();
        VideoList = new ArrayList<TrailerData>();
        ReviewList= new ArrayList<ReviewData>();
        actionBar = getSupportActionBar();
        movieTitle=intent.getStringExtra("movie_name");
        movieRating=intent.getStringExtra("movie_ratings");
        movieDesc=intent.getStringExtra("movie_description");
        movieRelease=intent.getStringExtra("movie_release");
        movieID= Integer.parseInt(intent.getStringExtra("movie_id"));
        moviePoster=intent.getStringExtra("movie_poster");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFEB3B")));
        movieReleasedTV.setText(movieRelease);
        movieTitleTV.setText(movieTitle);
        movieDetailsTV.setText(movieDesc);
        movieVoteTV.setText(movieRating);
        actionBar.setTitle(movieTitle);
        trailerTV.setVisibility(View.INVISIBLE);
        reviewTV.setVisibility(View.INVISIBLE);

        movieDatabase = MovieDatabase.getInstance(MovieDetails.this);
        Picasso.get()
                .load(moviePoster)
                .placeholder(R.drawable.loading)
                .into(moviePosterIV);
        Picasso.get()
                .load(moviePoster)
                .placeholder(R.drawable.loading)
                .into(bannerIV);
        buildURL(intent.getStringExtra("movie_id"));

        moviesInDatabaseList = (List<MovieData>) movieDatabase.moviesDao().getAllMovies();
        for(int i = 0; i <moviesInDatabaseList.size();i++)
    {
    if(Objects.equals(String.valueOf(movieID),moviesInDatabaseList.get(i).getMovieID()))
    {
        movieFavourite.setBackgroundResource(R.drawable.like);
    }

    }


movieFavourite.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        moviesInDatabaseList = (List<MovieData>) movieDatabase.moviesDao().getAllMovies();
        MovieData moviesResultObject = new MovieData(movieTitle,movieDesc,moviePoster,movieRating,movieRelease,String.valueOf(movieID));
        int i = 0;
        do{
            if(moviesInDatabaseList.size() == 0){
                Toast.makeText(view.getContext(),Constants.ADDED_FAV, Toast.LENGTH_SHORT).show();
                movieFavourite.setBackgroundResource(R.drawable.like);
                movieDatabase.moviesDao().insertMovie(moviesResultObject);

                break;
            }

            if(Objects.equals(moviesResultObject.getMovieID(), moviesInDatabaseList.get(i).getMovieID())){
                Toast.makeText(view.getContext(),Constants.REMOVED_FAV, Toast.LENGTH_SHORT).show();
                movieFavourite.setBackgroundResource(R.drawable.unlike);
                movieDatabase.moviesDao().deleteMovies(moviesResultObject);


                break;
            }

            if(i == (moviesInDatabaseList.size() - 1)){
                Toast.makeText(view.getContext(),"Movie added to Favourites", Toast.LENGTH_SHORT).show();
                movieFavourite.setBackgroundResource(R.drawable.like);
                movieDatabase.moviesDao().insertMovie(moviesResultObject);

                break;
            }
            i++;
        }while (i < moviesInDatabaseList.size());

    }
});


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
       mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Key=intent.getStringExtra("movie_id");
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(LOADER);

        if(loader==null){
            loaderManager.initLoader(LOADER, null, this);
        }else{
            loaderManager.restartLoader(LOADER, null, this);
        }
      /*  VideoList= getMovieKey(buildTrailerURL());
        ReviewList=getTrailer(buildReviewURL());
*/
    }


    private ArrayList<ReviewData> getTrailer(String url) {
        ReviewList.clear();


        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray movieArray = response.getJSONArray(Constants.DEFAULT_KEY);

                    for (int i = 0; i < movieArray.length(); i++) {

                        JSONObject movieObj = movieArray.getJSONObject(i);
                        ReviewData reviewData= new ReviewData();
                        reviewData.setAuthor(movieObj.getString("author"));
                        reviewData.setReview(movieObj.getString("content"));
                        ReviewList.add(reviewData);
                        Collections.reverse(VideoList);

                    }
                    reviewAdapter = new MovieReviewAdapter(MovieDetails.this, ReviewList);
                    mReviewRecyclerView.setAdapter(reviewAdapter);
                    reviewAdapter.notifyDataSetChanged();

                    if(ReviewList.size()!=0)
                        reviewTV.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.getMessage());
            }
        });
        queue.add(arrayRequest);

    return  ReviewList;}


    public String buildURL(String id){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(Constants.YOUTUBE_BASE_URL)
                .appendPath("watch")
                .appendQueryParameter("v", String.valueOf(getMovieKey(id)));
        return builder.build().toString();

    }

    public String buildTrailerURL(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(Constants.BASE_URL)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(Key)
                .appendPath("videos")
                .appendQueryParameter("api_key", Constants.KEY);
        return builder.build().toString();

    }
    public String buildReviewURL(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(Constants.BASE_URL)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(Key)
                .appendPath("reviews")
                .appendQueryParameter("api_key", Constants.KEY);
        return builder.build().toString();

    }
    public ArrayList<TrailerData> getMovieKey(String url) {

        VideoList.clear();


        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray movieArray = response.getJSONArray(Constants.DEFAULT_KEY);

                    for (int i = 0; i < movieArray.length(); i++) {

                        JSONObject movieObj = movieArray.getJSONObject(i);
                        TrailerData videoData= new TrailerData();
                        videoData.setKey(movieObj.getString("key"));
                        VideoList.add(videoData);
                        Collections.reverse(VideoList);

                    }
                    trailerAdapter = new MovieTrailerAdapter(MovieDetails.this, VideoList);
                    mRecyclerView.setAdapter(trailerAdapter);
                    trailerAdapter.notifyDataSetChanged();

                    if(VideoList.size()!=0)
                        trailerTV.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.getMessage());
            }
        });
        queue.add(arrayRequest);

        return VideoList;
    }


    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();


                forceLoad();
            }
            @Nullable
            @Override
            public String loadInBackground() {

                getMovieKey(buildTrailerURL());
                getTrailer(buildReviewURL());
                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
