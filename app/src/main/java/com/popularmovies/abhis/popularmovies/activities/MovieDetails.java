package com.popularmovies.abhis.popularmovies.activities;


import android.arch.lifecycle.LiveData;
import android.content.Intent;
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
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.popularmovies.abhis.popularmovies.BuildConfig;
import com.popularmovies.abhis.popularmovies.Database.MovieDatabase;
import com.popularmovies.abhis.popularmovies.Model.CrewData;
import com.popularmovies.abhis.popularmovies.Model.GenreData;
import com.popularmovies.abhis.popularmovies.Model.MovieData;
import com.popularmovies.abhis.popularmovies.Model.ReviewData;
import com.popularmovies.abhis.popularmovies.Model.TrailerData;
import com.popularmovies.abhis.popularmovies.R;
import com.popularmovies.abhis.popularmovies.Utils.Constants;
import com.squareup.picasso.Picasso;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MovieDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<LiveData<MovieData>> {
    private TextView movieTitleTV, movieDetailsTV, movieVoteTV, movieReleasedTV, trailerTV, reviewTV, movieVotesTV, toolbarTitle,movieDirector;
    private ImageView moviePosterIV, bannerIV;
    private ImageButton movieFavourite, movieUnfavourite, toolbarBack;
    private RequestQueue queue;
    Toolbar toolbar;
    private RecyclerView mRecyclerView, mReviewRecyclerView,mGenreRecyclerView,mCrewRecyclerView;
    private MovieTrailerAdapter trailerAdapter;
    private MovieReviewAdapter reviewAdapter;
    private Genre_Adapter genreAdapter;
    private MovieCrewAdapter CrewAdapter;
    private String Key, movieTitle, movieDesc, movieRelease, movieRating, moviePoster, movieVotes;
    private ArrayList<TrailerData> VideoList;
    private ArrayList<GenreData> genresList;
    private ArrayList<CrewData> crewList;
    private ArrayList<ReviewData> ReviewList;
    private static List<MovieData> moviesInDatabaseList;
    private MovieDatabase movieDatabase;
    private MovieData moviesResultObject;
    private static final int LOADER = 4;
    private int movieID;

    android.support.v7.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        movieTitleTV = findViewById(R.id.movieTitleID);
        movieDetailsTV = findViewById(R.id.movieDetialsID);
        movieVoteTV = findViewById(R.id.movieVoteID);
        movieReleasedTV = findViewById(R.id.movieReleasedID);
        trailerTV = findViewById(R.id.TrailerTV);
        reviewTV = findViewById(R.id.ReviewTV);
        movieVotesTV = findViewById(R.id.movieVotes);
        moviePosterIV = findViewById(R.id.moviePosterID);
        bannerIV = findViewById(R.id.banner);
        movieDirector=findViewById(R.id.movieDirector);
        movieUnfavourite = findViewById(R.id.unfavBtn);
        movieFavourite = findViewById(R.id.favBtn);
        toolbar = findViewById(R.id.toolbar);
        toolbarBack = findViewById(R.id.toolbar_back);
        toolbarTitle = findViewById(R.id.toolbar_title);
        queue = Volley.newRequestQueue(this);
        mRecyclerView = findViewById(R.id.trailer_recyclerViewID);
        mRecyclerView.setHasFixedSize(true);
        mGenreRecyclerView= findViewById(R.id.genre_recyclerView);
        mGenreRecyclerView.setHasFixedSize(true);
        mReviewRecyclerView = findViewById(R.id.review_recyclerViewID);
        mReviewRecyclerView.setHasFixedSize(true);
        mCrewRecyclerView = findViewById(R.id.casts_recyclerView);
        mCrewRecyclerView.setHasFixedSize(true);
        Intent intent = getIntent();
        VideoList = new ArrayList<TrailerData>();
        ReviewList = new ArrayList<ReviewData>();
        genresList = new ArrayList<GenreData>();
        crewList= new ArrayList<CrewData>();
        actionBar = getSupportActionBar();
        movieTitle = intent.getStringExtra("movie_name");
        movieRating = intent.getStringExtra("movie_ratings");
        movieDesc = intent.getStringExtra("movie_description");
        movieRelease = intent.getStringExtra("movie_release");
        movieID = Integer.parseInt(intent.getStringExtra("movie_id"));
        moviePoster = intent.getStringExtra("movie_poster");
        movieVotes = intent.getStringExtra("movie_votes");
        movieReleasedTV.setText(movieRelease);
        movieTitleTV.setText(movieTitle);
        movieDetailsTV.setText(movieDesc);
        movieVoteTV.setText(movieRating);
        movieVotesTV.setText(movieVotes);
        trailerTV.setVisibility(View.INVISIBLE);
        reviewTV.setVisibility(View.INVISIBLE);


        setSupportActionBar(toolbar);
        toolbarTitle.setText(movieTitle);
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

        setFavBtn(movieID);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(LOADER);

        if (loader == null) {
            loaderManager.initLoader(LOADER, null, this);
        } else {
            loaderManager.restartLoader(LOADER, null, this);
        }

        final LiveData<List<MovieData>> movies = movieDatabase.moviesDao().getAllMovies();
        movies.observe(this, movieData -> {

        });
        toolbarBack.setOnClickListener(view -> {
            MovieDetails.this.finish();
        });

        movieUnfavourite.setOnClickListener(view -> {
            MovieData moviesResultObject = new MovieData(movieTitle, movieDesc, moviePoster, movieRating, movieRelease, String.valueOf(movieID));
            CookieBar.build(MovieDetails.this)
                    .setTitle("Movie removed from Favourites")
                    .setMessage("Click on the favourites tab on the main screen to view favourites")
                    .setBackgroundColor(R.color.titleSecondaryText)
                    .setCookiePosition(CookieBar.TOP)
                    .show();
            movieDatabase.moviesDao().deleteMovies(moviesResultObject);
            movieFavourite.setVisibility(View.VISIBLE);
            movieUnfavourite.setVisibility(View.GONE);

        });

        movieFavourite.setOnClickListener(view -> {
            MovieData moviesResultObject = new MovieData(movieTitle, movieDesc, moviePoster, movieRating, movieRelease, String.valueOf(movieID));
            CookieBar.build(MovieDetails.this)
                    .setTitle("Movie added to Favourites")
                    .setMessage("Click on the favourites tab on the main screen to view favourites")
                    .setBackgroundColor(R.color.titleSecondaryText)
                    .setCookiePosition(CookieBar.TOP)
                    .show();
            movieDatabase.moviesDao().insertMovie(moviesResultObject);
            movieFavourite.setVisibility(View.GONE);
            movieUnfavourite.setVisibility(View.VISIBLE);
        });


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mGenreRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mCrewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        Key = intent.getStringExtra("movie_id");


    }

    private void setFavBtn(int movieID) {
        final LiveData<MovieData> movie = movieDatabase.moviesDao().getMoviesLiveData(String.valueOf(movieID));
        movie.observe(this, movieData -> {
            if (movieData == null) {
                movieFavourite.setVisibility(View.VISIBLE);
                movieUnfavourite.setVisibility(View.GONE);
            } else {
                movieFavourite.setVisibility(View.GONE);
                movieUnfavourite.setVisibility(View.VISIBLE);

            }
        });


    }

    private ArrayList<GenreData> getGenre(String url) {
        genresList.clear();


        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, response -> {

            try {
                JSONArray movieArray = response.getJSONArray("genres");

                for (int i = 0; i < movieArray.length(); i++) {

                    JSONObject movieObj = movieArray.getJSONObject(i);
                    GenreData genreData = new GenreData();
                    genreData.setId(movieObj.getInt("id"));
                    genreData.setName(movieObj.getString("name"));
                    genresList.add(genreData);

                }

                genreAdapter = new Genre_Adapter(MovieDetails.this, genresList);
                mGenreRecyclerView.setAdapter(genreAdapter);
                genreAdapter.notifyDataSetChanged();



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        });
        queue.add(arrayRequest);

        return genresList;
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
                        ReviewData reviewData = new ReviewData();
                        reviewData.setAuthor(movieObj.getString("author"));
                        reviewData.setReview(movieObj.getString("content"));
                        ReviewList.add(reviewData);
                        Collections.reverse(ReviewList);

                    }

                    reviewAdapter = new MovieReviewAdapter(MovieDetails.this, ReviewList);
                    mReviewRecyclerView.setAdapter(reviewAdapter);
                    reviewAdapter.notifyDataSetChanged();

                    if (ReviewList.size() != 0)
                        reviewTV.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, error -> {

        });
        queue.add(arrayRequest);

        return ReviewList;
    }
    public String buildmovieURL() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(Constants.BASE_URL)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(Key)
                .appendQueryParameter("api_key", BuildConfig.API_KEY);
        return builder.build().toString();
    }

    public String buildURL(String id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(Constants.YOUTUBE_BASE_URL)
                .appendPath("watch")
                .appendQueryParameter("v", String.valueOf(getMovieKey(id)));
        return builder.build().toString();

    }
    public String buildDataURL(String data) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(Constants.BASE_URL)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(Key)
                .appendPath(data)
                .appendQueryParameter("api_key", BuildConfig.API_KEY);
        return builder.build().toString();

    }
    public ArrayList<CrewData> getMovieCast(String url) {

        crewList.clear();


        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, response -> {

            try {
                JSONArray movieArray = response.getJSONArray("cast");

                for (int i = 0; i < movieArray.length(); i++) {

                    JSONObject movieObj = movieArray.getJSONObject(i);
                    CrewData crewData = new CrewData();
                    crewData.setCrewName(movieObj.getString("name"));
                    crewData.setCrewProfilePath(Constants.BASE_POSTER_URL+movieObj.getString("profile_path"));
                    crewData.setCrewId(movieObj.getString("id"));
                    crewList.add(crewData);

                }
                CrewAdapter = new MovieCrewAdapter(MovieDetails.this, crewList);
                mCrewRecyclerView.setAdapter(CrewAdapter);
                CrewAdapter.notifyDataSetChanged();




            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
//                Log.d("Error", error.getMessage());
        });
        queue.add(arrayRequest);

        return crewList;
    }
    public void getMovieCrew(String url) {
        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray movieArray = response.getJSONArray("crew");
                for (int i = 0; i < movieArray.length(); i++) {
                    JSONObject movieObj = movieArray.getJSONObject(i);
                    if(movieObj.getString("job").equals("Director"))
                        movieDirector.setText(movieObj.getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        });queue.add(arrayRequest);
    }

    public ArrayList<TrailerData> getMovieKey(String url) {

        VideoList.clear();


        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, response -> {

            try {
                JSONArray movieArray = response.getJSONArray(Constants.DEFAULT_KEY);

                for (int i = 0; i < movieArray.length(); i++) {

                    JSONObject movieObj = movieArray.getJSONObject(i);
                    TrailerData videoData = new TrailerData();
                    videoData.setKey(movieObj.getString("key"));
                    VideoList.add(videoData);
                    Collections.reverse(VideoList);

                }
                trailerAdapter = new MovieTrailerAdapter(MovieDetails.this, VideoList);
                mRecyclerView.setAdapter(trailerAdapter);
                trailerAdapter.notifyDataSetChanged();

                if (VideoList.size() != 0)
                    trailerTV.setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        });
        queue.add(arrayRequest);

        return VideoList;
    }


    @NonNull
    @Override
    public Loader<LiveData<MovieData>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new AsyncTaskLoader<LiveData<MovieData>>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Nullable
            @Override
            public LiveData<MovieData> loadInBackground() {
                getMovieKey(buildDataURL("videos"));
                getTrailer(buildDataURL("reviews"));
                getGenre(buildmovieURL());
                getMovieCrew(buildDataURL("credits"));
                getMovieCast(buildDataURL("casts"));

                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<LiveData<MovieData>> loader, LiveData<MovieData> movieDataLiveData) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<LiveData<MovieData>> loader) {

    }
}
