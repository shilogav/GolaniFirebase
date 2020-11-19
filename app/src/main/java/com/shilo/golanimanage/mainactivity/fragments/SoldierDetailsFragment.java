package com.shilo.golanimanage.mainactivity.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.shilo.golanimanage.R;
import com.shilo.golanimanage.databinding.FragmentSoldierDetailsBinding;
import com.shilo.golanimanage.mainactivity.dialog.RetirementDialog;
import com.shilo.golanimanage.mainactivity.model.Soldier;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SoldierDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoldierDetailsFragment extends Fragment implements RetirementDialog.DialogListener {

    private SoldierListViewModel viewModel;//also using SoldierListViewModel
    private FragmentSoldierDetailsBinding binding;
    public static final String PLAIN = "plain";
    public static final String INTERVIEW = "interview";
    public static final String REPORT_TYPE = "reportType";

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

        //update the data binder for soldiers details
        binding.setSoldier((Soldier) getArguments().getSerializable("soldier"));

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

                Toast.makeText(getContext(), "should open dialog manager", Toast.LENGTH_SHORT).show();
                RetirementDialog dialog = new RetirementDialog();
                dialog.show(getChildFragmentManager(), null);


            }
        });


        ////////////
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.content_frame,fragment);
        fragmentTransaction.commit(); // save the changes
        Log.i("MainActivityV3", "fragmentTransaction.commit()");
    }

    @Override
    public void onDialogIndependentRetirement(DialogFragment dialog) {
        Toast.makeText(getContext(), "independent",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogMedicalRetirement(DialogFragment dialog) {
        Toast.makeText(getContext(), "medical",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDialogInitiatedRetirement(DialogFragment dizlog) {
        Toast.makeText(getContext(), "initiated",Toast.LENGTH_SHORT).show();
    }
}