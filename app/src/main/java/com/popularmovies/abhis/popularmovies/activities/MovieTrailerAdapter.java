package com.popularmovies.abhis.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.popularmovies.abhis.popularmovies.Model.TrailerData;
import com.popularmovies.abhis.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.Holder> {
    private Context context;
    private String TRAILER_BASE_URL = "http://www.youtube.com/watch?v=";
    private List<TrailerData> mVideoDataArray;
    private LayoutInflater mLayoutInflator;
    private ImageView thumbnailImage;
    public MovieTrailerAdapter(Context context, ArrayList<TrailerData> data) {
        mLayoutInflator= LayoutInflater.from(context);
        mVideoDataArray=data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trailer_row, viewGroup, false);



        return new Holder(itemView,context);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        TrailerData currentTrailer = mVideoDataArray.get(position);
        final TrailerData mData= new TrailerData();
        String TRAILER_THUMBNAIL_END = "/0.jpg";
        String TRAILER_THUMBNAIL_START = "https://img.youtube.com/vi/";
        Picasso.get()
                .load(TRAILER_THUMBNAIL_START + currentTrailer.getKey() + TRAILER_THUMBNAIL_END)
                .into(thumbnailImage);

       thumbnailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = view.getContext();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(TRAILER_BASE_URL+mVideoDataArray.get(position).getKey()));
                context.startActivity(intent);

            }
        });



    }

    @Override
    public int getItemCount() {
        return mVideoDataArray.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        public Holder(@NonNull View itemView, final Context ctx) {
            super(itemView);
            thumbnailImage = itemView.findViewById(R.id.movie_video_thumbnail);

        }
    }
}
