package com.shilo.myloginfirebase.mainactivity.data;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shilo.myloginfirebase.mainactivity.livedata.TeamLiveData;
import com.shilo.myloginfirebase.mainactivity.model.Soldier;
import com.shilo.myloginfirebase.mainactivity.model.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class DataSource {
    private static DataSource instance;
    private FirebaseFirestore firebaseFirestore;
    private MutableLiveData<List<Team>> teamsLiveData;
    //private MutableLiveData<>

    public static DataSource getInstance(){
        if (instance == null){
            instance = new DataSource();
        }
        return instance;
    }

     DataSource(){
         firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<List<Team>> getTeamsLiveData() {
        if (teamsLiveData == null) {
            teamsLiveData = new MutableLiveData<>();
        }
        return  teamsLiveData;
    }

     public TeamLiveData getFirestoreTeamLiveData() {
         DocumentReference reference = firebaseFirestore
                 .collection("teams")
                 .document();
         return new TeamLiveData(reference);
     }

     public void updateTeams(){
         FirebaseFirestore db = FirebaseFirestore.getInstance();

         CollectionReference collectionReference = db.collection("teams");
         Task<QuerySnapshot> querySnapshotTask = collectionReference.get();
         querySnapshotTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
             @Override
             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                 HashMap map;
                 if(task.isSuccessful()){
                     for (QueryDocumentSnapshot document : task.getResult()) {
                         Log.i("main activity", document.getId() + " => "
                                 + document.getData() + " and: " + document.getData().get("crew"));
                         map = (HashMap) document.getData();
                         updateSoldiers(map);
                         //Log.i("main activity", "document2...name is " + document2.get().getResult().getData().get("name"));
                         //Log.i("main activity", "document2 is " + document2);
                     }
                 } else {
                     Log.i("main activity", "get failed with " + task.getException());
                 }
             }
         });
     }


    private void updateSoldiers(HashMap<String,Object> map) {
        final DocumentReference reference = (DocumentReference)map.get("crew");
        final Soldier[] soldier = new Soldier[1];
        new Thread(new Runnable() {
            @Override
            public void run() {

                reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult();
                            Log.i("main activity", task.getResult() + "\n => get data: "
                                    + task.getResult().getData() +
                                    "\n => get name: " + task.getResult().getData().get("name"));
                            soldier[0] = convertDatatoSoldier(task.getResult().getData());
                            Log.i("main activity", "\n soldier: " + soldier[0].toString());
                        } else {
                            Log.i("main activity", "get failed with " + task.getException());
                        }
                    }
                });



                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }).start();
        Log.i("main activity", " \n soldier" + soldier[0].toString());
    }

    private Soldier convertDatatoSoldier(Map<String,Object> data){
        Soldier soldier = new Soldier();
        soldier.setName((String) data.get("name"));
        soldier.setRate((int) data.get("rate"));
        soldier.setComment((String) data.get("comment"));
        return soldier;
    }









}
