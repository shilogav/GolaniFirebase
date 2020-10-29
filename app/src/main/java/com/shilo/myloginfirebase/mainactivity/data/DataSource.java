package com.shilo.myloginfirebase.mainactivity.data;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shilo.myloginfirebase.mainactivity.livedata.TeamLiveData;
import com.shilo.myloginfirebase.mainactivity.model.Soldier;
import com.shilo.myloginfirebase.mainactivity.model.Team;
import com.shilo.myloginfirebase.model.LoggedInUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class DataSource {
    private static DataSource instance;
    private FirebaseFirestore firebaseFirestore;
    private MutableLiveData<List<Team>> teamsLiveData;
    private MutableLiveData<LoggedInUser> userLiveData;

    public static DataSource getInstance(){
        if (instance == null){
            instance = new DataSource();
        }
        return instance;
    }

     DataSource(){
         firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void setUserLiveData(MutableLiveData<LoggedInUser> userLiveData) {
        this.userLiveData = userLiveData;
    }


    /*public MutableLiveData<List<Team>> getTeamsLiveData() {
        if (teamsLiveData == null) {
            teamsLiveData = new MutableLiveData<>();
        }
        return  teamsLiveData;
    }*/

     public TeamLiveData getFirestoreTeamLiveData() {
         CollectionReference reference = firebaseFirestore
                 .collection("teams");
         return new TeamLiveData(reference);
     }

     public void getFromCloud(LoggedInUser user){
         firebaseFirestore
                 .collection("teams")
                 .whereEqualTo("name", "Reshef")
                 .addSnapshotListener(new EventListener<QuerySnapshot>() {
                     @Override
                     public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                         if (error != null){
                             Log.i("data source", "listen error", error);
                             return;
                         }
                         List<Team> teamsTemp = new ArrayList<>();
                         Team team;
                         for (QueryDocumentSnapshot doc : value) {
                             if (doc.get("name") != null) {
                                 team = new Team();
                                 team.setName(doc.getString("name"));
                                 team.setId(doc.getId());
                                 team.setCrew((ArrayList)doc.get("crew"));
                                 teamsTemp.add(team);
                             }
                         }
                     }
                 });

     }





     ////////////////////

    /*
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

    */









}
