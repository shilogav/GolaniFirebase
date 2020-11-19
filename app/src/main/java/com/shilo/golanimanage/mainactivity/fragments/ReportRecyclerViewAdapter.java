package com.shilo.golanimanage.mainactivity.fragments;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.shilo.golanimanage.R;
import com.shilo.golanimanage.databinding.FragmentQuestionRawBinding;
import com.shilo.golanimanage.databinding.FragmentSoldierDetailsBinding;
import com.shilo.golanimanage.mainactivity.fragments.dummy.DummyContent.DummyItem;
import com.shilo.golanimanage.mainactivity.model.Question;
import com.shilo.golanimanage.mainactivity.model.Report;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ReportRecyclerViewAdapter extends RecyclerView.Adapter<ReportRecyclerViewAdapter.ViewHolder> {
    private List<Question> mQuestions;
    private RecyclerViewClickListener listener;

    public ReportRecyclerViewAdapter() {
        mQuestions = new ArrayList<>();
    }

    public List<Question> getQuestions() {
        return mQuestions;
    }

    public void setQuestions(List<Question> mQuestions) {
        this.mQuestions = mQuestions;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("ReportRecyclerViewAdapter: ", "onCreateViewHolder executed ");
        FragmentQuestionRawBinding rawBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.fragment_question_raw, parent,false);
        /*View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_question_raw, parent, false);*/
        return new ViewHolder(rawBinding);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Question question = mQuestions.get(position);
        Log.i("ReportRecyclerViewAdapter: ", "mValues.size(): " + mQuestions.size());
        holder.rawBinding.setQuestion(question);

        holder.rawBinding.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.i("ReportRecyclerViewAdapter: ", "onRatingChanged observed");
                getQuestions().get(position).setRate((int) rating);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        FragmentQuestionRawBinding rawBinding;

        public ViewHolder(@NonNull FragmentQuestionRawBinding rawBinding) {
            super(rawBinding.getRoot());
            this.rawBinding = rawBinding;



        }

        @Override
        public String toString() {
            return super.toString() + " '" + rawBinding.question.getText() + "'";
        }
    }

    interface RecyclerViewClickListener {
        void onClick(String string);
    }

    public void setOnRVClickListener(RecyclerViewClickListener listener) {
        this.listener = listener;
    }
}