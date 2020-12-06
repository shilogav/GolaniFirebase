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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import org.apache.poi.ss.usermodel.DataFormatter;

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

        //action bar
        setHasOptionsMenu(true);
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


        binding.questionList.setItemViewCacheSize(10);
        binding.questionList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.questionList.setHasFixedSize(true);
        binding.questionList.setAdapter(adapter);
        report = new Report();
        report.setQuestionList(fillReport());
        //adapter.setQuestions(report.getQuestionList());
        viewModel.getReport(soldier, reportType).observe(getViewLifecycleOwner(), new Observer<Report>() {
            @Override
            public void onChanged(Report report) {
                Log.i("ReportFragment", "reportMutableLiveData is " + report);
                if (report == null) {
                    fragment.report = new Report();
                    fragment.report.setQuestionList(fillReport());
                    adapter.setQuestions(fragment.report.getQuestionList());

                    if (user.getName().contains("Admin") || user.getName().contains("admin")) {
                        for (Question question : adapter.getQuestions()) {
                            question.setMutable(false);
                        }
                    }

                    isNewReport = true;
                    Log.i("ReportFragment", "new report");
                    return;
                } else {
                    adapter.setQuestions(report.getQuestionList() );
                    isNewReport = false;
                    if (user.getName().contains("Admin") || user.getName().contains("admin")) { //if report already filled, not admin user can't change rate
                        for (Question question : adapter.getQuestions()) {
                            question.setMutable(true);
                        }
                    }

                }
                fragment.report = report;
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
                        if (isNewReport) { //if new report for user, can be saved for cloud
                            if (user.getName().contains("user")) {
                                binding.saveFab.show();
                            }
                        } else if (user.getName().contains("Admin") || user.getName().contains("admin"))
                        { //if exist report for admin, can be saved for cloud
                            binding.saveFab.show();
                        } else { //if exist report for user, can't be saved for cloud
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

                dialogFragment = new SaveDialog(fragment);
                //getActivity().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                dialogFragment.show(getChildFragmentManager(), null);

            }
        });
    }

    @Override
    public void onPositiveClick(DialogFragment dialog) {
        //fill report details and send to cloud
        if (isNewReport) {
            report.setId("report:" + UUID.randomUUID());
            report.setDescription(reportType);
            report.setQuestionList(adapter.getQuestions());
            report.setIdLeader(user.getUserId());
            report.setIdSoldier(soldier.getId());
        } else { //if admin edit, change only rate details
            report.setQuestionList(adapter.getQuestions());
        }

        viewModel.setReport(report);
        dialog.dismiss();
        binding.saveFab.hide();

        Toast.makeText(getContext(), "שומר, אנא המתן..", Toast.LENGTH_LONG).show();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.report_menu, menu);
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.export_excel) {
            Toast.makeText(getActivity(),
                    "export excel", Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Utility.saveExcelFile(getContext(),soldier.getName() + "-" + reportType + " report.xlsx",adapter.getQuestions());
                }
            }).start();

        }
        return super.onOptionsItemSelected(item);
    }
}