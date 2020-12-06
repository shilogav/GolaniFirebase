package com.shilo.golanimanage.mainactivity.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.shilo.golanimanage.databinding.FragmentSoldierListBinding;
import com.shilo.golanimanage.login.LoginActivity;
import com.shilo.golanimanage.mainactivity.MainActivityV3;
import com.shilo.golanimanage.mainactivity.adapters.RecyclerAdapter;
import com.shilo.golanimanage.mainactivity.model.Soldier;
import com.shilo.golanimanage.model.LoggedInUser;
import java.io.Serializable;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SoldierListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoldierListFragment extends Fragment implements Serializable {
    private SoldierListViewModel viewModel;
    private MutableLiveData<LoggedInUser> userLiveData;
    private RecyclerAdapter adapter;
    private FragmentSoldierListBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SoldierListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SoldierListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SoldierListFragment newInstance(String param1, String param2) {
        SoldierListFragment fragment = new SoldierListFragment();
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
                             final Bundle savedInstanceState) {
        //data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_soldier_list, container, false);
        View view = binding.getRoot();


        //progress bar
        progressBarManage();


        //view model

        viewModel = new ViewModelProvider(this).get(SoldierListViewModel.class);
        viewModel.setActivity(getActivity());
        userLiveData = viewModel.getUser(getActivity());
        userLiveData.observe(getViewLifecycleOwner(), new Observer<LoggedInUser>() {
            @Override
            public void onChanged(LoggedInUser user) {
                Utility.saveUserForSharedPref(getActivity(),user);
            }
        });

        //Toast.makeText(getContext(),"main activity version 1. user is " + userLiveData.getValue().toString(),Toast.LENGTH_LONG).show();
        Log.i("SoldierListFragment", "user is " + userLiveData.getValue().toString());


        manageBackKey(view);

        //fab button
        manageFab();

        //action bar
        //setHasOptionsMenu(true);

        //viewModel.init();

        //initialize
        initRecyclerView();

        /*
         * write data to cloud
         */
        viewModel.toCloud();

        /*
         * general fetch method. to be deleted
         */
        viewModel.getCloud();

        /*
         * observer for soldiers
         */
        viewModel.getSoldiersLiveData().observe(getViewLifecycleOwner(), new Observer<List<Soldier>>() {
            @Override
            public void onChanged(List<Soldier> soldiers) {

                adapter.setSoldiers(soldiers);
                Log.i("SoldierListFragment", "viewModel.getSoldiersLiveData().observe(getViewLifecycleOwner()..........");
                Log.i("SoldierListFragment", "adapter num of soldiers: " + adapter.getItemCount());
            }
        });

        return view;
    }

    private void manageFab() {
        binding.logoutFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),
                        "sign out", Toast.LENGTH_SHORT).show();
                SharedPreferences prefs = getContext().getSharedPreferences("UserData", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void manageBackKey(View view) {
        //view.setFocusableInTouchMode(true);
        //view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ( keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.i("SoldierListFragment","back key pressed");
                    System.exit(100);
                    Log.i("SoldierListFragment","System.exit");
                    return false;
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
                binding.recyclerViewSoldiers.setVisibility(View.INVISIBLE);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //binding.progressBar.setVisibility(View.GONE);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        binding.layoutProgressBar.setVisibility(View.GONE);
                        binding.recyclerViewSoldiers.setVisibility(View.VISIBLE);
                    }
                });

            }
        }).start();
    }

    /**
     * Recycler view initialize
     */
    private void initRecyclerView(){
        adapter = new RecyclerAdapter();


        adapter.setOnRVClickListener(new RecyclerAdapter.RecyclerViewClickListener() {
            @Override
            public void onclick(Soldier soldier) {
                //Toast.makeText(getContext(),"typed soldier: " + soldier.getName(), Toast.LENGTH_LONG).show();

                Log.i("SoldierListFragment", "onclick()");
                loadFragment(soldier);

            }
        });

        binding.recyclerViewSoldiers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewSoldiers.setHasFixedSize(true);
        //Log.i("SoldierListFragment: savedInstanceState is null? ", String.valueOf(savedInstanceState == null));
        //if (savedInstanceState != null) {
        //    Log.i("SoldierListFragment: ","savedInstanceState is null");
        //    binding.recyclerViewSoldiers.setAdapter((RecyclerAdapter)savedInstanceState.getSerializable("adapter"));
        // }else {
        //      Log.i("SoldierListFragment: ","savedInstanceState NOT null");
        binding.recyclerViewSoldiers.setAdapter(adapter);
        //    }

    }

    private void loadFragment(Soldier soldier) {
        Fragment fragment = new SoldierDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("soldier", soldier);
        fragment.setArguments(bundle);
// create a FragmentManager
        FragmentManager fm = getParentFragmentManager();
        fm = getActivity().getSupportFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.content_frame,fragment);
        //fragmentTransaction.addToBackStack("SoldierDetailsFragment");
        fragmentTransaction.commit(); // save the changes
        Log.i("SoldierListFragment", "fragmentTransaction.commit()");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //outState.putSerializable("adapter", adapter);
        //adapter.getSoliders().clear();
        super.onSaveInstanceState(outState);
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.soldier_list_menu, menu);
        return;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.soldier_list_sign_out) {
            Toast.makeText(getActivity(),
                    "soldier list sign out", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }*/
}