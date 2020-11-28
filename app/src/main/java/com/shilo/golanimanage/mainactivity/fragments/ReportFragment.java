package com.shilo.golanimanage.mainactivity.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.shilo.golanimanage.R;
import com.shilo.golanimanage.Utility;
import com.shilo.golanimanage.databinding.FragmentPlainQuestionListBinding;
import com.shilo.golanimanage.mainactivity.dialog.SaveDialog;
import com.shilo.golanimanage.mainactivity.model.Question;
import com.shilo.golanimanage.mainactivity.model.Report;
import com.shilo.golanimanage.mainactivity.model.Soldier;
import com.shilo.golanimanage.model.LoggedInUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment representing a list of Items.
 */
public class ReportFragment extends Fragment implements SaveDialog.SaveDialogListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    ReportFragment fragment = this;
    DialogFragment dialogFragment;
    ReportViewModel viewModel;
    Soldier soldier;
    LoggedInUser user;
    Report report;
    boolean isNewReport;
    //MutableLiveData<Report> reportMutableLiveData;
    ReportRecyclerViewAdapter adapter;
    FragmentPlainQuestionListBinding binding;
    public static final String PLAIN = "plain";
    public static final String INTERVIEW = "interview";
    public static final String REPORT_TYPE = "reportType";
    String reportType;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReportFragment() {
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
        //TODO: change report type for plain or interview mechanic
        SharedPreferences prefs
                = getActivity().getSharedPreferences("UserData", MODE_PRIVATE);
        user =
                (LoggedInUser) Utility.fromSharedPreferences(prefs, "user");

        if (getArguments() != null) {
            reportType = getArguments().getString(REPORT_TYPE);
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            soldier = (Soldier)getArguments().getSerializable("soldier");
            if (soldier.getReports() == null) {
                soldier.setReports(new ArrayList<Report>());
            }
            Log.i("ReportFragment. the argumend id:"
                    , ((Soldier)fragment.getArguments().getSerializable("soldier")).getId());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*View view = inflater
                .inflate(R.layout.fragment_plain_question_list, container, false);*/
        binding
                = DataBindingUtil.inflate
                (inflater, R.layout.fragment_plain_question_list, container,false);

        final View view = binding.getRoot();
        //progress bar
        progressBarManage();
        //view model
        viewModel = new ViewModelProvider(this).get(ReportViewModel.class);
        viewModel.setSoldier(soldier);
        //recycler view
        initRecyclerView();
        //set title
        binding.reportTitle.setText(reportType.equals(PLAIN)?R.string.report_plain:R.string.report_interview);

        manageKeyBack(view);
        manageFab();
        return view;
    }

    private void manageKeyBack(View view) {
        //view.setFocusableInTouchMode(true);
        //view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ( keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.i("ReportFragment","back key pressed");
                    //fragment.getChildFragmentManager().popBackStack();
                    //fragment.getParentFragmentManager().beginTransaction().remove(fragment).commit();
                    //return true;
                }
                return false;
            }
        });
    }


    /**
     * progressBarManage
     */
    private void progressBarManage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                binding.layoutProgressBar.setVisibility(View.VISIBLE);
                binding.questionList.setVisibility(View.INVISIBLE);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        binding.layoutProgressBar.setVisibility(View.GONE);
                        binding.questionList.setVisibility(View.VISIBLE);
                    }
                });

            }
        }).start();
    }

    private void initRecyclerView() {
        // Set the adapter
        Log.i("ReportFragment -> initRecyclerView", "executed");
        adapter = new ReportRecyclerViewAdapter();

        Log.i("ReportFragment -> initRecyclerView", "report from user: " + user.getReports());
        Log.i("ReportFragment -> initRecyclerView", "report from soldier: " + soldier.getReports());
        //////////////////////////////////////
        //sort report for specific soldier and specific user
        /*List<Report> tempList = new ArrayList<>();//list of reports for specific soldier and specific user
        tempList.addAll(user.getReports());
        tempList.retainAll(soldier.getReports());
        boolean reportExist = false;
        for (Report report : tempList) {
            if (report.getDescription().equals(reportType)) {
                reportExist = true;
                break;
            }
        }*/
        //reportMutableLiveData = viewModel.getReport(soldier, reportType);



        //if (tempList.isEmpty() || !reportExist) {//search report in soldier and user reports
        /*if (report == null) {
            report = new Report();
            report.setDescription(reportType);
            report.setQuestionList(fillReport());
            report.setIdSoldier(soldier.getId());
            report.setIdLeader(user.getUserId());
        } else {//TODO:test fetch existing report
            for (Report temp : tempList) {
                if (temp.getDescription().equals(reportType)) {
                    report = temp;
                    break;
                }
            }
            //if report already in cloud then lock for editing
            binding.saveFab.hide();
        }*/

        binding.questionList.setItemViewCacheSize(10);
        binding.questionList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.questionList.setHasFixedSize(true);
        binding.questionList.setAdapter(adapter);
        //adapter.setQuestions(report.getQuestionList() );
        report = new Report();
        report.setQuestionList(fillReport());
        adapter.setQuestions(report.getQuestionList());
        viewModel.getReport(soldier, reportType).observe(getViewLifecycleOwner(), new Observer<Report>() {
            @Override
            public void onChanged(Report report) {
                Log.i("ReportFragment", "reportMutableLiveData is " + report);
                if (report == null) {
                    fragment.report = new Report();
                    fragment.report.setQuestionList(fillReport());
                    adapter.setQuestions(fragment.report.getQuestionList());
                    isNewReport = true;
                    Log.i("ReportFragment", "new report");
                    return;
                }
                adapter.setQuestions(report.getQuestionList() );
                isNewReport = false;
                Log.i("ReportFragment", "onChanged -> setQuestions " + report);

            }
        });

    }

    /**
     * manage float button. if new report show fab. if not, hide it.
     */
    private void manageFab() {
        binding.saveFab.hide();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (isNewReport) {
                            if (!user.getName().contains("admin") && !user.getName().contains("Admin")) {
                                binding.saveFab.show();
                            }
                        } else {
                            binding.saveFab.hide();
                        }
                    }
                });
            }
        }).start();

        //listener
        binding.saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("ReportFragment -> fab.setOnClickListener", "button clicked");
                Toast.makeText(getContext(), "send to cloud", Toast.LENGTH_SHORT).show();

                dialogFragment = new SaveDialog(fragment);
                //getActivity().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                dialogFragment.show(getChildFragmentManager(), null);

            }
        });
    }

    @Override
    public void onPositiveClick(DialogFragment dialog) {
        //fill report details and send to cloud
        report.setId("report:" + UUID.randomUUID());
        report.setDescription(reportType);
        report.setQuestionList(adapter.getQuestions());
        report.setIdLeader(user.getUserId());
        report.setIdSoldier(soldier.getId());
        viewModel.setReport(report);
        dialog.dismiss();
        binding.saveFab.hide();
    }

    @Override
    public void onNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }



    /**
     * create new report
     * @return
     *///TODO: to accurate the report object and add here the data
    private List<Question> fillReport() {
        ArrayList<Question> questions = new ArrayList<>();
         if (reportType.equals(PLAIN)) {
             Question question1 = new Question(getString(R.string.plain_second), 0);
             Question question2 = new Question(getString(R.string.plain_third), 0);
             Question question3 = new Question(getString(R.string.plain_fourth), 0);
             Question question4 = new Question(getString(R.string.plain_fifth), 0);
             Question question5 = new Question(getString(R.string.plain_sixth), 0);
             Question question6 = new Question(getString(R.string.plain_seventh), 0);
             Question question7 = new Question(getString(R.string.plain_eighth), 0);
             Question question8 = new Question(getString(R.string.plain_ninth), 0);
             Question question9 = new Question(getString(R.string.plain_tenth), 0);

             questions.add(question1);
             questions.add(question2);
             questions.add(question3);
             questions.add(question4);
             questions.add(question5);
             questions.add(question6);
             questions.add(question7);
             questions.add(question8);
             questions.add(question9);
        } else {
             Question question1 = new Question(getString(R.string.interview_second), 0);
             Question question2 = new Question(getString(R.string.interview_third), 0);
             Question question3 = new Question(getString(R.string.interview_fourth), 0);
             Question question4 = new Question(getString(R.string.interview_fifth), 0);
             Question question5 = new Question(getString(R.string.interview_sixth), 0);
             Question question6 = new Question(getString(R.string.interview_seventh), 0);

             questions.add(question1);
             questions.add(question2);
             questions.add(question3);
             questions.add(question4);
             questions.add(question5);
             questions.add(question6);
         }

        return questions;
    }
}