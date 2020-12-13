package com.shilo.golanimanage.mainactivity.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.shilo.golanimanage.R;
import com.shilo.golanimanage.databinding.FragmentSoldierDetailsBinding;
import com.shilo.golanimanage.mainactivity.dialog.RetirementDialog;
import com.shilo.golanimanage.mainactivity.model.Soldier;
import com.shilo.golanimanage.mainactivity.viewmodel.SoldierListViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SoldierDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoldierDetailsFragment extends Fragment implements RetirementDialog.DialogListener {

    private Fragment fragment = this;
    private SoldierListViewModel viewModel;//also using SoldierListViewModel
    private FragmentSoldierDetailsBinding binding;
    public static final String PLAIN = "plain";
    public static final String INTERVIEW = "interview";
    public static final String REPORT_TYPE = "reportType";

    private Soldier soldier;
    private String tempString;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SoldierDetailsFragment() {
        // Required empty public constructor

    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SoldierDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SoldierDetailsFragment newInstance(String param1, String param2) {
        SoldierDetailsFragment fragment = new SoldierDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_soldier_details, container, false);
        final View view = binding.getRoot();

        //view model
        viewModel = new ViewModelProvider(this).get(SoldierListViewModel.class);

        soldier =(Soldier) getArguments().getSerializable("soldier");
        //update the data binder for soldiers details
        binding.setSoldier(soldier);

        viewModel.getComment(soldier).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String comment) {
                // (comment == null) {
                //    return;
                //}

            }
        });

        binding.commentContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.saveFabComments.show();
                //soldier.setComment(s.toString());
            }
        });

        manageFab();

        ///////////////////////////
        //click listeners

        binding.buttonPlain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ReportFragment fragment = new ReportFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("soldier",(Soldier) getArguments().getSerializable("soldier"));
                bundle.putString(REPORT_TYPE, PLAIN);
                fragment.setArguments(bundle);
                loadFragment(fragment);

            }
        });

        binding.buttonInterview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportFragment fragment = new ReportFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("soldier",(Soldier) getArguments().getSerializable("soldier"));
                bundle.putString(REPORT_TYPE, INTERVIEW);
                fragment.setArguments(bundle);
                loadFragment(fragment);
            }
        });

        binding.buttonRetirement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getContext(), "should open dialog manager", Toast.LENGTH_SHORT).show();
                RetirementDialog dialog = new RetirementDialog();
                dialog.show(getChildFragmentManager(), null);


            }
        });
        manageBackKey(view);

        ////////////
        return view;
    }

    private void manageBackKey(View view) {
        //view.setFocusableInTouchMode(true);
        //view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ( keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.i("SoldierDetailsFragment","back key pressed");
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Log.i("SoldierDetailsFragment","getParentFragmentManager is " + fm);
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.remove(fragment);
                    fragmentTransaction.commit();
                    return false;
                }
                return false;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.content_frame,fragment);
        fragmentTransaction.commit(); // save the changes
        Log.i("MainActivityV3", "fragmentTransaction.commit()");
    }

    @Override
    public void onDialogIndependentRetirement(DialogFragment dialog) {
        viewModel.deleteSoldier(soldier,"פרישה עצמית");
        Toast.makeText(getContext(), "independent",Toast.LENGTH_SHORT).show();
        dialog.dismiss();
        getParentFragmentManager().popBackStack();
    }

    @Override
    public void onDialogMedicalRetirement(DialogFragment dialog) {
        viewModel.deleteSoldier(soldier,"פרישה רפואית");
        Toast.makeText(getContext(), "medical",Toast.LENGTH_SHORT).show();
        dialog.dismiss();
        getParentFragmentManager().popBackStack();
    }

    @Override
    public void onDialogInitiatedRetirement(DialogFragment dialog) {
        viewModel.deleteSoldier(soldier,"פרישה יזומה");
        Toast.makeText(getContext(), "initiated",Toast.LENGTH_SHORT).show();
        dialog.dismiss();
        getParentFragmentManager().popBackStack();
    }

    private void manageFab() {

        binding.saveFabComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(fragment.getView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                SoldierListViewModel.getCommentLiveData().setValue(soldier.getComment());
                viewModel.getComment(soldier);
                Log.i("SoldierDetailsFragment","Fab click listener -> addTextChangedListener executed");
            }
        });
    }
}