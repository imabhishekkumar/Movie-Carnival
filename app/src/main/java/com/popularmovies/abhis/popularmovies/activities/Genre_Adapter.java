package com.popularmovies.abhis.popularmovies.activities;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.popularmovies.abhis.popularmovies.Model.GenreData;

import com.popularmovies.abhis.popularmovies.R;


import java.util.List;

public class Genre_Adapter extends RecyclerView.Adapter<Genre_Adapter.Holder> {
    private Context context;
    private List<GenreData> mMovieGenreArray;


    public Genre_Adapter(Context context, List<GenreData> mMovieGenreArray) {
        this.context = context;
        this.mMovieGenreArray = mMovieGenreArray;
    }

    @NonNull
    @Override
    public Genre_Adapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.genre_row,
                        viewGroup,
                        false);


        return new Holder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        GenreData currentGenre = mMovieGenreArray.get(i);

        holder.genreTitle.setText(currentGenre.getName());
    }


    @Override
    public int getItemCount() {

        return mMovieGenreArray.size();

    }


    public class Holder extends RecyclerView.ViewHolder {

        private TextView genreTitle;


        public Holder(@NonNull View itemView, final Context ctx) {
            super(itemView);
            context = ctx;
            genreTitle = itemView.findViewById(R.id.genre_textView);


        }
    }
}