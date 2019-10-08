package com.popularmovies.abhis.popularmovies.activities;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.popularmovies.abhis.popularmovies.BuildConfig;
import com.popularmovies.abhis.popularmovies.Database.MovieDatabase;
import com.popularmovies.abhis.popularmovies.Model.MovieData;
import com.popularmovies.abhis.popularmovies.R;
import com.popularmovies.abhis.popularmovies.Utils.Constants;

import org.aviran.cookiebar2.CookieBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MovieAdapter movieAdapter;
    private RequestQueue queue;
    private MovieDatabase movieDatabase;
    private BottomNavigationView bottomNavigation;
    private Toolbar actionBar;
    ImageButton toolbarSeach;
    ConstraintLayout toolbarLayout;
    private FloatingSearchView mSearchView;
    private static final int LOADER = 2;
    private String currentOrder = Constants.SORT_BY_POPULAR;
    boolean searchVisible=false;
    boolean doubleBackToExitPressedOnce=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);
        movieDatabase = MovieDatabase.getInstance(MainActivity.this);
        actionBar = findViewById(R.id.toolbar);
        toolbarSeach = findViewById(R.id.toolbar_seach);
        setSupportActionBar(actionBar);
        toolbarLayout= findViewById(R.id.toolbar_layout);
        mSearchView =findViewById(R.id.search_view);
        mRecyclerView = findViewById(R.id.recyclerViewID);
        mRecyclerView.setHasFixedSize(true);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.sort_ratings:
                    mRecyclerView.smoothScrollToPosition(0);
                    currentOrder = Constants.SORT_BY_HIGHEST_RATED;
                    updateUI();
                    break;
                case R.id.sort_popularity:
                    mRecyclerView.smoothScrollToPosition(0);
                    currentOrder = Constants.SORT_BY_POPULAR;
                    updateUI();
                    break;

                case R.id.sort_upcoming:
                    mRecyclerView.smoothScrollToPosition(0);
                    currentOrder = Constants.SORT_BY_UPCOMING;
                    updateUI();
                    break;

                case R.id.sort_now_playing:
                    mRecyclerView.smoothScrollToPosition(0);
                    currentOrder = Constants.SORT_BY_NOW_PLAYING;
                    updateUI();
                    break;
                case R.id.sort_fav:
                    mRecyclerView.smoothScrollToPosition(0);
                    currentOrder = Constants.FAVORITES;
                    showFavorites();
                    break;
                default:
                    currentOrder = Constants.SORT_BY_HIGHEST_RATED;
                    updateUI();
            }
            return true;
        });


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        } else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }

        if (checkConnection(this) == true)
            getMovies(buildURL(currentOrder));
        else
        {
            CookieBar.build(MainActivity.this)
                    .setTitle("No internet connection")
                    .setMessage("Please connect to the internet and try again later.")
                    .setBackgroundColor(R.color.titleSecondaryText)
                    .setCookiePosition(CookieBar.TOP)
                    .show();
        }

        toolbarSeach.setOnClickListener(view -> {
            toolbarLayout.setVisibility(View.INVISIBLE);
            mSearchView.setVisibility(View.VISIBLE);
            mSearchView.setOnFocusChangeListener((view1, b) -> view1.requestFocus());
            mSearchView.setSearchText("");
            searchVisible=true;
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }

            @Override
            public void onSearchAction(String currentQuery) {
                currentQuery=currentQuery.replaceAll("\\s+","\u002B");
                getMovies(buildURLSearch(currentQuery));
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_about:
               // startActivity(new Intent(MainActivity.this,About.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {

        if(searchVisible)
        {
            toolbarLayout.setVisibility(View.VISIBLE);
            mSearchView.setVisibility(View.INVISIBLE);
            searchVisible=false;
        }else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }else {

            this.doubleBackToExitPressedOnce = true;
            CookieBar.build(MainActivity.this)
                    .setTitle("Press back again to exit")
                    .setBackgroundColor(R.color.titleSecondaryText)
                    .setCookiePosition(CookieBar.BOTTOM)
                    .show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    private boolean checkConnection(Context applicationContext) {
        ConnectivityManager conMgr = (ConnectivityManager) applicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        assert conMgr != null;
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }


    private void updateUI() {

        getMovies(buildURL(currentOrder));

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("orderBy", currentOrder);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentOrder = savedInstanceState.getString("orderBy");
    }

    private void showFavorites() {

        final LiveData<List<MovieData>> movie = movieDatabase.moviesDao().getAllMovies();
        movie.observe(this, movieData -> {
            movieAdapter = new MovieAdapter(MainActivity.this, movieData);
            mRecyclerView.setAdapter(movieAdapter);
            movieAdapter.notifyDataSetChanged();
        });

    }

    public List<MovieData> getMovies(String url) {

        final List<MovieData> movieList = new ArrayList<>();

        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray movieArray = response.getJSONArray(Constants.DEFAULT_KEY);

                    for (int i = 0; i < movieArray.length(); i++) {

                        JSONObject movieObj = movieArray.getJSONObject(i);
                        MovieData movieData = new MovieData();
                        movieData.setMovieTitle(movieObj.getString(Constants.TITLE_KEY));
                        movieData.setMovieImg(Constants.BASE_POSTER_URL + movieObj.getString(Constants.POSTER_KEY));
                        movieData.setMovieID(movieObj.getString(Constants.ID));
                        movieData.setMovieVotes(movieObj.getString(Constants.VOTES));
                        //  movieData.setMovieDirector(movieObj.getString(Constants.DIRECTOR));
                        movieData.setMovieID(movieObj.getString(Constants.ID));
                        movieData.setMovieDesc(movieObj.getString(Constants.SYMBOSIS_KEY));
                        movieData.setMovieRating(movieObj.getString(Constants.RATING_KEY));
                        movieData.setMovieRelease(movieObj.getString(Constants.RELEASE_KEY));
                        movieList.add(movieData);

                    }
                    movieAdapter = new MovieAdapter(MainActivity.this, movieList);
                    mRecyclerView.setAdapter(movieAdapter);
                    movieAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    //e.printStackTrace();
                    Log.d("Error", e.getMessage());
                }
            }
        }, error -> Log.d("Error", error.getMessage()));
        queue.add(arrayRequest);
        return movieList;
    }
//http://api.themoviedb.org/3/movie/now_playing?api_key=ffb376532eb8191f70f30d69fbc249c1
   // https://api.themoviedb.org/3/search/movie?api_key=ffb376532eb8191f70f30d69fbc249c1&query=the+avengers
    public String buildURL(String order) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(Constants.BASE_URL)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(order)
                .appendQueryParameter(Constants.API_PARAM, BuildConfig.API_KEY);
        return builder.build().toString();
    }
    public String buildURLSearch(String searchQuery) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(Constants.BASE_URL)
                .appendPath("3")
                .appendPath("search")
                .appendPath("movie")
                .appendQueryParameter(Constants.API_PARAM, BuildConfig.API_KEY)
                .appendQueryParameter("query", searchQuery);
        return builder.build().toString();
    }
}