package com.popularmovies.abhis.popularmovies.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.popularmovies.abhis.popularmovies.BuildConfig;
import com.popularmovies.abhis.popularmovies.Database.MovieDatabase;
import com.popularmovies.abhis.popularmovies.Model.MovieData;
import com.popularmovies.abhis.popularmovies.R;
import com.popularmovies.abhis.popularmovies.Utils.Constants;

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
    android.support.v7.app.ActionBar actionBar;
    private static final int LOADER = 2;
    private String currentOrder = Constants.SORT_BY_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);

        movieDatabase = MovieDatabase.getInstance(MainActivity.this);
        actionBar = getSupportActionBar();
        actionBar.setTitle("  Movie Time");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFEB3B")));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.movie_tickets);
        // getSupportLoaderManager().initLoader(LOADER, null, this);
        mRecyclerView = findViewById(R.id.recyclerViewID);
        mRecyclerView.setHasFixedSize(true);
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
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sort_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sort_ratings:
                currentOrder = Constants.SORT_BY_HIGHEST_RATED;
                updateUI();
                break;
            case R.id.sort_popularity:
                currentOrder = Constants.SORT_BY_POPULAR;
                updateUI();
                break;
            case R.id.sort_fav:
                currentOrder = Constants.FAVORITES;
                showFavorites();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        movie.observe(this, new Observer<List<MovieData>>() {
            @Override
            public void onChanged(@Nullable List<MovieData> movieData) {
                movieAdapter = new MovieAdapter(MainActivity.this, movieData);
                mRecyclerView.setAdapter(movieAdapter);
                movieAdapter.notifyDataSetChanged();
            }
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
                        movieData.setMovieDesc(movieObj.getString(Constants.SYMBOSIS_KEY));
                        movieData.setMovieRating(movieObj.getString(Constants.RATING_KEY));
                        movieData.setMovieRelease(movieObj.getString(Constants.RELEASE_KEY));
                        movieList.add(movieData);

                    }
                    movieAdapter = new MovieAdapter(MainActivity.this, movieList);
                    mRecyclerView.setAdapter(movieAdapter);
                    movieAdapter.notifyDataSetChanged();


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
        return movieList;
    }


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


   /* @NonNull
    @Override
    public Loader<LiveData<MovieData>> onCreateLoader(int i, final Bundle bundle) {

        return new AsyncTaskLoader<LiveData<MovieData>>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                Toast.makeText(getContext(),"Loading in background",Toast.LENGTH_SHORT).show();
                forceLoad();
            }

            @Nullable
            @Override

            public LiveData<MovieData> loadInBackground() {

        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<LiveData<MovieData>> loader, LiveData<MovieData> arrayListLiveData) {
        movieAdapter = new MovieAdapter(MainActivity.this, (LiveData<MovieData>) arrayListLiveData);
        mRecyclerView.setAdapter(movieAdapter);
        movieAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<LiveData<MovieData>>loader) {

    }*/
}