package com.shilo.myloginfirebase.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shilo.myloginfirebase.R;
import com.shilo.myloginfirebase.databinding.ActivityMainV1Binding;
import com.shilo.myloginfirebase.mainactivity.adapters.RecyclerAdapter;
import com.shilo.myloginfirebase.mainactivity.model.Soldier;
import com.shilo.myloginfirebase.mainactivity.model.Team;
import com.shilo.myloginfirebase.model.LoggedInUser;

import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity1 extends AppCompatActivity {
    private MainActivityVM viewModel;
    private MutableLiveData<LoggedInUser> userLiveData;
    private RecyclerAdapter adapter;
    private ActivityMainV1Binding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //data binding
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main_v1);
        //view model
        viewModel = new ViewModelProvider(this).get(MainActivityVM.class);

        userLiveData = viewModel.getUser(this);
        Toast.makeText(this,"main activity version 1. user is " + userLiveData.getValue().toString(),Toast.LENGTH_LONG).show();
        Log.i("main activity", "user is " + userLiveData.getValue().toString());

        //viewModel.init();

        //initialize
        initRecyclerView();




        viewModel.getTeamlist().observe(this, new Observer<List<Team>>() {
            @Override
            public void onChanged(List<Team> teams) {
                Log.i("main activity", "on changed called");
                adapter.setSoldiers(teams.get(0).getCrew());
            }
        });

        viewModel.getCloud();

        //viewModel.getTeamlist().onEvent(null,null);
    }



    /**
     * Recycler view initialize
     */
    private void initRecyclerView(){
        adapter = new RecyclerAdapter();
        Soldier soldier = new Soldier("shalom");
        Soldier soldier2 = new Soldier("eli");
        Soldier soldier3 = new Soldier("ronen");
        Soldier soldier4 = new Soldier("yochay");
        Soldier soldier5 = new Soldier("yosi");
        Soldier soldier6 = new Soldier("shalev");
        Soldier soldier7 = new Soldier("shilo");
        Soldier soldier8 = new Soldier("aviad");
        Soldier soldier9 = new Soldier("nathan");
        Soldier soldier10 = new Soldier("asaf");
        Soldier soldier11 = new Soldier("nisi");
        Soldier soldier12 = new Soldier("david");


        ArrayList<Soldier> soldiersExample = new ArrayList<>();
        soldiersExample.add(soldier);
        soldiersExample.add(soldier2);
        soldiersExample.add(soldier3);
        soldiersExample.add(soldier4);
        soldiersExample.add(soldier5);
        soldiersExample.add(soldier6);
        soldiersExample.add(soldier7);
        soldiersExample.add(soldier8);
        soldiersExample.add(soldier9);
        soldiersExample.add(soldier10);
        soldiersExample.add(soldier11);
        soldiersExample.add(soldier12);
        //TODO:adapter.setSoldiers(soldiersExample);
        adapter.setOnRVClickListener(new RecyclerAdapter.RecyclerViewClickListener() {
            @Override
            public void onclick(Soldier soldier) {
                Toast.makeText(getApplicationContext(),"typed soldier: " + soldier.getName(), Toast.LENGTH_LONG).show();
            }
        });

        mainBinding.recyclerViewSoldiers.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.recyclerViewSoldiers.setHasFixedSize(true);
        mainBinding.recyclerViewSoldiers.setAdapter(adapter);
    }
}