package com.popularmovies.abhis.popularmovies.Activities;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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


public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MovieAdapter movieAdapter;
    private RequestQueue queue;
    private ArrayList<MovieData> movieList;

    android.support.v7.app.ActionBar actionBar;


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
        movieList = get_movies(buildURL(Constants.SORT_BY_POPULAR));

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
            movieList = get_movies(buildURL(Constants.SORT_BY_HIGHEST_RATED));
        else
            movieList = get_movies(buildURL(Constants.SORT_BY_POPULAR));


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
   // https://api.themoviedb.org/3/movie/157336/videos?api_key=
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
}
