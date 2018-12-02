package com.popularmovies.abhis.popularmovies.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.popularmovies.abhis.popularmovies.Model.MovieData;
import com.popularmovies.abhis.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.Holder> {
    private Context context;
    private List<MovieData>  mMovieDataArray;
    private LayoutInflater mLayoutInflator;

private String movieID=null;

    public MovieAdapter(Context context, ArrayList<MovieData> data){
        mLayoutInflator= LayoutInflater.from(context);
        mMovieDataArray=data;

    }

    @NonNull
    @Override
    public MovieAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= mLayoutInflator.from(viewGroup.getContext())
                    .inflate(R.layout.movie_row,
                            viewGroup,
                        false);


        return new Holder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.Holder holder, int position) {
    MovieData currentMovie = mMovieDataArray.get(position);
    movieID= currentMovie.getMovieID();
        Picasso.get()
                .load(currentMovie.getMovieImg())
                .placeholder(R.drawable.loading)
                .into(holder.movieAvatar);




    }

    @Override
    public int getItemCount() {

        return mMovieDataArray.size();
    }



    public class Holder extends RecyclerView.ViewHolder {

        TextView movieTitle;
        ImageView movieAvatar;


        public Holder(@NonNull View itemView, final Context ctx) {
            super(itemView);
            context = ctx;


            movieAvatar =itemView.findViewById(R.id.movieAvatar);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context= view.getContext();
                    int pos= getAdapterPosition();
                    Intent intent= new Intent(context,MovieDetails.class);
                    intent.putExtra("movie_name",mMovieDataArray.get(pos).getMovieTitle());
                    intent.putExtra("movie_id",mMovieDataArray.get(pos).getMovieID());
                    intent.putExtra("movie_poster",mMovieDataArray.get(pos).getMovieImg());
                    intent.putExtra("movie_ratings",mMovieDataArray.get(pos).getMovieRating());
                    intent.putExtra("movie_description",mMovieDataArray.get(pos).getMovieDesc());
                    intent.putExtra("movie_release",mMovieDataArray.get(pos).getMovieRelease());

                    context.startActivity(intent);
                }
            });



        }
    }
}
