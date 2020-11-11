package com.shilo.golanimanage.mainactivity.fragments;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.shilo.golanimanage.R;
import com.shilo.golanimanage.mainactivity.fragments.dummy.DummyContent.DummyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ReportRecyclerViewAdapter extends RecyclerView.Adapter<ReportRecyclerViewAdapter.ViewHolder> {
    private List< String > mQuestions;
    private RecyclerViewClickListener listener;

    public ReportRecyclerViewAdapter() {
        mQuestions = new ArrayList<>();
    }

    public List<String> getQuestions() {
        return mQuestions;
    }

    public void setQuestions(List<String> mQuestions) {
        this.mQuestions = mQuestions;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_question_raw, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mQuestions.get(position);
        Log.i("ReportRecyclerViewAdapter: ", "mValues.size(): " + mQuestions.size());
        holder.mIdView.setText(mQuestions.get(position));
        //holder.mContentView.setText(mValues.get(position).content);
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final RatingBar mRatingBar;

        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.question);
            mRatingBar = view.findViewById(R.id.ratingBar);
            mRatingBar.setStepSize(0.5f);
            mRatingBar.setRating(2.0f);

            mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    Log.i("ReportRecyclerViewAdapter: ", "onRatingChanged observed");
                }
            });

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mIdView.getText() + "'";
        }
    }

    interface RecyclerViewClickListener {
        void onClick(String string);
    }

    public void setOnRVClickListener(RecyclerViewClickListener listener) {
        this.listener = listener;
    }
}