package com.popularmovies.abhis.popularmovies.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.popularmovies.abhis.popularmovies.Model.ReviewData;
import com.popularmovies.abhis.popularmovies.R;

import java.util.List;

import at.blogc.android.views.ExpandableTextView;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.Holder> {
    private Context context;
    private List<ReviewData> mReviewDataArray;


    public MovieReviewAdapter(Context context, List<ReviewData> mReviewDataArray) {
        this.context = context;
        this.mReviewDataArray = mReviewDataArray;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_row, viewGroup, false);


        return new Holder(itemView, context);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int i) {
        ReviewData currentReview = mReviewDataArray.get(i);
        holder.mAuthor.setText(currentReview.getAuthor());

        holder.expandableTextView.setInterpolator(new OvershootInterpolator());
        holder.expandableTextView.setText(currentReview.getReview());
        holder.buttonToggle.setOnClickListener(view -> {
            holder.buttonToggle.setImageResource(holder.expandableTextView.isExpanded() ? R.drawable.outline_expand_less_24 : R.drawable.outline_expand_more_24);
            holder.expandableTextView.toggle();
        });

        holder.parentView.setOnClickListener(view -> {
            holder.buttonToggle.setImageResource(holder.expandableTextView.isExpanded() ? R.drawable.outline_expand_less_24 : R.drawable.outline_expand_more_24);
            holder.expandableTextView.toggle();
        });

    }

    @Override
    public int getItemCount() {
        return mReviewDataArray.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView mAuthor, mReview;
        private LinearLayout parentView;
        private ExpandableTextView expandableTextView;
        private ImageButton buttonToggle;

        public Holder(@NonNull View itemView, final Context context) {
            super(itemView);
            mAuthor = itemView.findViewById(R.id.reviewer_TV);
            //  mReview = itemView.findViewById(R.id.review_TV);
            buttonToggle = itemView.findViewById(R.id.review_expand_button);
            expandableTextView = itemView.findViewById(R.id.review_TV);
            parentView = itemView.findViewById(R.id.constraintLayout);

        }
    }
}
