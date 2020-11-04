package com.shilo.golanimanage.mainactivity.data;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shilo.golanimanage.mainactivity.livedata.TeamLiveData;
import com.shilo.golanimanage.mainactivity.model.Team;
import com.shilo.golanimanage.model.LoggedInUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

public class DataSource {
    private static DataSource instance;
    public static final String LOG_NAME = "dataSource";
    public static FirebaseFirestore firebaseFirestore;
    private MutableLiveData<List<Team>> teamsLiveData;
    private MutableLiveData<LoggedInUser> userLiveData;
    private final DocumentReference[] referencesTemp = new DocumentReference[1];
    private final List<DocumentReference> crewReferenceList = new ArrayList<>();

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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * write data to cloud
     */
    public void toCloud() {
        //addUsers();
        //createUsers(i);
        //createCrewForTeam("Tt3uPluMgn7bMW4CG6yI");
    }

    /**
     * give the specific team for update the crew
     * @param teamId
     */
    private void createCrewForTeam(final String teamId) {
        Thread getCrewThread = new Thread(new Runnable() {
            @Override
            public void run() {
                getCrew();

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.i(LOG_NAME, "crewReferenceList is: " + crewReferenceList);
                addTheCrew(teamId);
            }
        });
        getCrewThread.start();



    }



    private void addTheCrew(String teamId) {
        firebaseFirestore.collection("teams")
                .document(teamId)
                .update("crew",crewReferenceList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(LOG_NAME, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_NAME, "Error updating document", e);
                        new Exception("error in firestore");
                    }
                });
    }

    private void getCrew() {
        Log.i(LOG_NAME, "getCrew: execute");
        //////////////////////firestore
        firebaseFirestore.collection("soldiers")
                .whereGreaterThanOrEqualTo("id", "6")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i(LOG_NAME, document.getId() + " => " + document.getData());
                                crewReferenceList.add(document.getReference());
                            }
                        } else {
                            Log.i(LOG_NAME, "Error getting documents: ", task.getException());
                            new Exception("error in firestore");
                        }
                    }
                });
        //////////////////////
    }

    /**
     *
     */
    private void createUsers(final int counter) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String,Object> users = new HashMap<>();
                users.put("id", String.valueOf(1001+ counter));
                users.put("name","user" + counter);
                users.put("role","C");
                getMyTeamReference("35");//TODO
                do {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    users.put("leaderOfTeam",referencesTemp[0]);
                    if (referencesTemp[0] != null){
                        addTheUser(users);
                    }
                } while (referencesTemp[0] == null);
            }
        }).start();
    }

    private void addTheUser(Map<String,Object> data){
        firebaseFirestore.collection("users")
                .document(data.get("name").toString())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void Void) {
                        //Log.i("data source", "user added. DocumentSnapshot added with ID: " + documentReference.getId());
                        Log.d(LOG_NAME, "addTheUser: DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(LOG_NAME, "Error adding document", e);
                    }
                });
    }

    /**
     * get the reference to the team which user would be its leader
     * @param id specific team
     */
    private void getMyTeamReference(String id) {
        //referencesTemp[0];
        ////////put team owner
        firebaseFirestore.collection("teams")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                referencesTemp[0] = document.getReference();
                                Log.i("data source", "team: " + document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.i("data source", "Error getting documents: ", task.getException());
                        }
                    }
                });

        /*firebaseFirestore.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i("data source", "user added. DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("data source", "Error adding document", e);
                    }
                });*/
    }

    private void addUsers() {
        /////-/
        ArrayList<String> userName = new ArrayList<>();
        userName.add("user");
        userName.add("user2");
        userName.add("user3");
        userName.add("user4");
        userName.add("user5");
        userName.add("user6");
        userName.add("user7");
        userName.add("user8");
        userName.add("user9");
        userName.add("user10");
        userName.add("user11");
        userName.add("user12");
        userName.add("user13");
        userName.add("user14");
        userName.add("user15");
        userName.add("user16");

        //////-/
        /*
        private String userId;
    private String displayName;
    private String role;
    private Team leaderOfTeam;
         */
        Log.i("data source", "toCloud execute");
        final Map<String, Object> user = new HashMap<>();
        user.put("id", 8888);
        user.put("name", userName.get(0));
        user.put("role", "C");
        user.put("comment", null);
        ////////put team owner
        firebaseFirestore.collection("teams")
                .whereEqualTo("id", "35")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user.put("leaderOfTeam",document.getReference());
                                Log.d("data source", "team: " + document.getId() + " => " + document.getData());

                                firebaseFirestore.collection("users")
                                        .add(user)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.i("data source", "user added. DocumentSnapshot added with ID: " + documentReference.getId());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("data source", "Error adding document", e);
                                            }
                                        });

                            }
                        } else {
                            Log.d("data source", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void addSoldiers(){
        /////-/
        ArrayList<String> teamName = new ArrayList<>();
        teamName.add("shar12");
        teamName.add("shar13");
        teamName.add("shar14");
        teamName.add("shar15");
        teamName.add("shar16");
        teamName.add("shar17");
        teamName.add("shar18");
        teamName.add("shar19");
        teamName.add("shar20");
        teamName.add("shar21");
        teamName.add("shar22");
        //////-/

        for (int i = 0; i < 10 ; i++) {
            Log.i("data source", "toCloud execute");
            Map<String, Object> soldier = new HashMap<>();
            soldier.put("id", String.valueOf(i+10));
            soldier.put("name", teamName.get(i));
            soldier.put("rate", 70 + i);
            soldier.put("comment", "good");

            // Add a new document with a generated ID
            firebaseFirestore.collection("soldiers")
                    .add(soldier)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.i("data source", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("data source", "Error adding document", e);
                        }
                    });
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void getFromCloud(LoggedInUser user){}

    public void getFromCloud2(LoggedInUser user){

        firebaseFirestore
                .collection("teams").document(user.getLeaderOfTeam().getId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            Log.i("data source", "listen error", error);
                            new Exception("problem");
                            return;
                        }
                        List<Team> teamsTemp = new ArrayList<>();
                        Team team;


                         /*while (DocumentSnapshot doc : value) {
                             if (doc.get("name") != null) {
                                 team = new Team();
                                 team.setName(doc.getString("name"));
                                 team.setId(doc.getId());
                                 team.setCrew((ArrayList)doc.get("crew"));
                                 teamsTemp.add(team);
                             }
                         }*/
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
