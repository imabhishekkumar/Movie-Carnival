package com.popularmovies.abhis.popularmovies.Activities;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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


public class MovieDetails extends AppCompatActivity {
    private TextView movieTitleTV, movieDetailsTV, movieVoteTV, movieReleasedTV;
    private ImageView moviePosterIV,bannerIV;
    private RequestQueue queue;
    private RecyclerView mRecyclerView,mReviewRecyclerView;
    private MovieTrailerAdapter trailerAdapter;
    private MovieReviewAdapter reviewAdapter;
    private String Key;
    private ArrayList<TrailerData> VideoList;
    private ArrayList<ReviewData> ReviewList;
    android.support.v7.app.ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        movieTitleTV= findViewById(R.id.movieTitleID);
        movieDetailsTV= findViewById(R.id.movieDetialsID);
        movieVoteTV= findViewById(R.id.movieVoteID);
        movieReleasedTV= findViewById(R.id.movieReleasedID);
        moviePosterIV= findViewById(R.id.moviePosterID);
        bannerIV= findViewById(R.id.banner);
        queue = Volley.newRequestQueue(this);
        mRecyclerView = findViewById(R.id.trailer_recyclerViewID);
        mRecyclerView.setHasFixedSize(true);
        mReviewRecyclerView=findViewById(R.id.review_recyclerViewID);
        mReviewRecyclerView.setHasFixedSize(true);
        Intent intent=getIntent();
        VideoList = new ArrayList<TrailerData>();
        ReviewList= new ArrayList<ReviewData>();
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFEB3B")));
        movieReleasedTV.setText(intent.getStringExtra("movie_release"));
        movieTitleTV.setText(intent.getStringExtra("movie_name"));
        movieDetailsTV.setText(intent.getStringExtra("movie_description"));
        movieVoteTV.setText(intent.getStringExtra("movie_ratings"));
        actionBar.setTitle(intent.getStringExtra("movie_name"));
        Picasso.get()
                .load(intent.getStringExtra("movie_poster"))
                .placeholder(R.drawable.loading)
                .into(moviePosterIV);
        Picasso.get()
                .load(intent.getStringExtra("movie_poster"))
                .placeholder(R.drawable.loading)
                .into(bannerIV);
buildURL(intent.getStringExtra("movie_id"));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
       mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Key=intent.getStringExtra("movie_id");
        VideoList= getMovieKey(buildTrailerURL());
        ReviewList=getTrailer(buildReviewURL());

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
//1C1C1C
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


}
