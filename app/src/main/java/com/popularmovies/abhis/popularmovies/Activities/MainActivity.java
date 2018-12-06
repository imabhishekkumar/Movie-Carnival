package com.popularmovies.abhis.popularmovies.Activities;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.popularmovies.abhis.popularmovies.Model.MovieData;
import com.popularmovies.abhis.popularmovies.R;
import com.popularmovies.abhis.popularmovies.Utils.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
implements LoaderManager.LoaderCallbacks<String> {

    private RecyclerView mRecyclerView;
    private MovieAdapter movieAdapter;
    private RequestQueue queue;
    private ArrayList<MovieData> movieList;

    android.support.v7.app.ActionBar actionBar;
    private static final int LOADER= 2;
    private static final String LOADER_ORDER_KEY="order";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);

        movieList = new ArrayList<>();

actionBar = getSupportActionBar();
actionBar.setTitle("  Movie Time");
actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFEB3B")));
actionBar.setDisplayShowHomeEnabled(true);
actionBar.setIcon(R.drawable.movie_tickets);
        getSupportLoaderManager().initLoader(LOADER, null, this);
        mRecyclerView = findViewById(R.id.recyclerViewID);
        mRecyclerView.setHasFixedSize(true);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }
        else {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);
        }
        updateUI(Constants.SORT_BY_POPULAR);


    }

    private void updateUI(String order) {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(LOADER_ORDER_KEY,order);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(LOADER);

        if(loader==null){
            loaderManager.initLoader(LOADER, queryBundle, this);
        }else{
            loaderManager.restartLoader(LOADER, queryBundle, this);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.sort_menu,menu);
        return super.onCreateOptionsMenu(menu);
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== R.id.sort_ratings)

            updateUI(Constants.SORT_BY_HIGHEST_RATED);
        else
            updateUI(Constants.SORT_BY_POPULAR);

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<MovieData> get_movies(String url) {

        movieList.clear();

        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray movieArray = response.getJSONArray(Constants.DEFAULT_KEY);

                    for (int i = 0; i < movieArray.length(); i++) {

                        JSONObject movieObj = movieArray.getJSONObject(i);
                        MovieData movieData= new MovieData();
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


    public String buildURL(String order){

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(Constants.BASE_URL)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(order)
                .appendQueryParameter(Constants.API_PARAM, Constants.KEY);
        String myUrl = builder.build().toString();
        return myUrl;

    }


    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, final Bundle bundle) {

        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                Toast.makeText(getContext(),"Loading in background",Toast.LENGTH_SHORT).show();
                forceLoad();
                  }

            @Nullable
            @Override

            public String loadInBackground() {
                String order = bundle.getString(LOADER_ORDER_KEY);

                movieList = get_movies(buildURL(order));
//                Toast.makeText(getContext(),"Running in background",Toast.LENGTH_SHORT).show();
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
