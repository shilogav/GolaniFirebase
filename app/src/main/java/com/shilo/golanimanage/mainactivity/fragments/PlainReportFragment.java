package com.shilo.golanimanage.mainactivity.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shilo.golanimanage.R;
import com.shilo.golanimanage.mainactivity.fragments.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class PlainReportFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlainReportFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PlainReportFragment newInstance(int columnCount) {
        PlainReportFragment fragment = new PlainReportFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plain_question_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.questionList);
        initRecyclerView(recyclerView);

        //////////////Floating Action Button
        FloatingActionButton fab = view.findViewById(R.id.saveFab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ReportRecyclerViewAdapter -> fab.setOnClickListener", "button clicked");
            }
        });

        return view;
    }

    private void initRecyclerView(RecyclerView recyclerView) {
        // Set the adapter
        Log.i("PlainReportFragment -> onCreateView", "executed");
        ReportRecyclerViewAdapter adapter = new ReportRecyclerViewAdapter();

        List<String> questions = new ArrayList<>();
        questions.add("first");
        questions.add("second");
        questions.add("third");
        questions.add("first2");
        questions.add("second2");
        questions.add("third2");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setQuestions(questions );
    }
}