package com.popularmovies.abhis.popularmovies.Activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.popularmovies.abhis.popularmovies.Model.ReviewData;
import com.popularmovies.abhis.popularmovies.R;

import java.util.List;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.Holder> {
    private Context context;
    private List<ReviewData> mReviewDataArray;
    TextView mAuthor, mReview;

    public MovieReviewAdapter(Context context, List<ReviewData> mReviewDataArray) {
        this.context = context;
        this.mReviewDataArray = mReviewDataArray;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.review_row, viewGroup, false);



        return new Holder(itemView,context);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        ReviewData currentReview = mReviewDataArray.get(i);
        mAuthor.setText(currentReview.getAuthor());
        mReview.setText(currentReview.getReview());

    }

    @Override
    public int getItemCount() {
        return mReviewDataArray.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        public Holder(@NonNull View itemView, Context context) {
            super(itemView);
            mAuthor= itemView.findViewById(R.id.reviewer_TV);
            mReview=itemView.findViewById(R.id.review_TV);
        }
    }
}
