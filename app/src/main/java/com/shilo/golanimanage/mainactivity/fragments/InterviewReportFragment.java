package com.shilo.golanimanage.mainactivity.fragments;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.shilo.golanimanage.R;
import com.shilo.golanimanage.mainactivity.dialog.SaveDialog;
import com.shilo.golanimanage.mainactivity.model.Question;
import com.shilo.golanimanage.mainactivity.model.Report;
import com.shilo.golanimanage.mainactivity.model.Soldier;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InterviewReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InterviewReportFragment extends Fragment implements SaveDialog.SaveDialogListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    InterviewReportFragment fragment = this;
    DialogFragment dialogFragment;
    Soldier soldier;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public InterviewReportFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ReportFragment newInstance(int columnCount) {
        ReportFragment fragment = new ReportFragment();
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
            soldier = (Soldier) getArguments().getSerializable("soldier");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plain_question_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.questionList);
        initRecyclerView(recyclerView);

        TextView textView = view.findViewById(R.id.reportTitle);
        textView.setText(R.string.report_interview);

        //////////////Floating Action Button
        FloatingActionButton fab = view.findViewById(R.id.saveFab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ReportRecyclerViewAdapter -> fab.setOnClickListener", "button clicked");
                Toast.makeText(getContext(), "send to cloud", Toast.LENGTH_SHORT).show();

                dialogFragment = new SaveDialog(fragment);
                dialogFragment.show(getParentFragmentManager(), null);
            }
        });

        return view;
    }

    private void initRecyclerView(RecyclerView recyclerView) {
        // Set the adapter
        Log.i("ReportFragment -> onCreateView", "executed");
        ReportRecyclerViewAdapter adapter = new ReportRecyclerViewAdapter();

        Question question1 = new Question("question", 6);
        Question question2 = new Question("question2", 5);
        Question question3 = new Question("question3", 1);
        Question question4 = new Question("question4", 10);

        ArrayList<Question> questionsTemp = new ArrayList<>();
        questionsTemp.add(question1);
        questionsTemp.add(question2);
        questionsTemp.add(question3);
        questionsTemp.add(question4);

        Report report = new Report();
        report.setDescription("interview");
        report.setQuestionList(questionsTemp);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setQuestions(report.getQuestionList() );
    }

    @Override
    public void onPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onNegativeClick(DialogFragment dialog) {

    }
}