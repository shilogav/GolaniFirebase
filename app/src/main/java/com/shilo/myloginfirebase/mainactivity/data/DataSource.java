package com.shilo.myloginfirebase.mainactivity.data;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shilo.myloginfirebase.mainactivity.livedata.TeamLiveData;
import com.shilo.myloginfirebase.mainactivity.model.Soldier;
import com.shilo.myloginfirebase.mainactivity.model.Team;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;

public class DataSource {
    private static DataSource instance;
    private FirebaseFirestore firebaseFirestore;

    public static DataSource getInstance(){
        if (instance == null){
            instance = new DataSource();
        }
        return instance;
    }

     DataSource(){
         firebaseFirestore = FirebaseFirestore.getInstance();
    }

     public TeamLiveData getFirestoreTeamLiveData(DocumentReference reference) {
         reference = firebaseFirestore
                 .collection("Teams")
                 .document();
         return new TeamLiveData(reference);
     }




}
