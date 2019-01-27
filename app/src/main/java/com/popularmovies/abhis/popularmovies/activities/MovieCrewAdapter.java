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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.popularmovies.abhis.popularmovies.Model.CrewData;
import com.popularmovies.abhis.popularmovies.R;
import com.popularmovies.abhis.popularmovies.Utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieCrewAdapter extends RecyclerView.Adapter<MovieCrewAdapter.Holder> {
    private Context context;
    private List<CrewData> mCrewDataArray;
    private LayoutInflater mLayoutInflator;
    private ImageView thumbnailImage;

    public MovieCrewAdapter(Context context, ArrayList<CrewData> data) {
        mLayoutInflator = LayoutInflater.from(context);
        mCrewDataArray = data;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.crew_row, viewGroup, false);


        return new Holder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        CrewData currentCrew = mCrewDataArray.get(position);
        holder.crewName.setText(currentCrew.getCrewName());

        Picasso.get()
                .load(currentCrew.getCrewProfilePath())
                .placeholder(R.drawable.loading)
                .into(holder.crewImage);
        holder.crewLayout.setOnClickListener(view -> {
            Uri uri = Uri.parse("https://www.google.co.in/search?q="+currentCrew.getCrewName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            view.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mCrewDataArray.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView crewName;
        ImageView crewImage;
        LinearLayout crewLayout;

        public Holder(@NonNull View itemView, final Context ctx) {
            super(itemView);
            crewName = itemView.findViewById(R.id.crew_name);
            crewImage = itemView.findViewById(R.id.crew_image);
            crewLayout= itemView.findViewById(R.id.crewLayout);


        }
    }
}
