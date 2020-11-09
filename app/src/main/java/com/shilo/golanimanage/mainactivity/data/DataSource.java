package com.shilo.golanimanage.mainactivity.data;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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
import com.shilo.golanimanage.mainactivity.model.Soldier;
import com.shilo.golanimanage.mainactivity.model.Team;
import com.shilo.golanimanage.model.LoggedInUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

public class DataSource {
    private static DataSource instance;
    public static final String LOG_NAME = "dataSource";
    public static FirebaseFirestore firebaseFirestore;
    private MutableLiveData<Team> teamLiveData;
    private MutableLiveData<LoggedInUser> userLiveData;
    private MutableLiveData<List<Soldier>> soldiersLiveData;
    private final DocumentReference[] referencesTemp = new DocumentReference[1];
    private final List<DocumentReference> crewReferenceList = new ArrayList<>();
    private List<Soldier> soldierList = new ArrayList<>();
    Context context;
    //final List<DocumentReference> soldiersListReferences = new ArrayList<>();

    public static DataSource getInstance(){
        if (instance == null){
            instance = new DataSource();
        }
        return instance;
    }

    DataSource(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        teamLiveData = new MutableLiveData<>();
        soldiersLiveData = new MutableLiveData<>();
        //userLiveData = new MutableLiveData<>();
    }

    /**
     * user live data is initialized in repository class
     * so here it just set the user live data for dataSource
     * @param userLiveData
     */
    public void setUserLiveData(MutableLiveData<LoggedInUser> userLiveData) {
        this.userLiveData = userLiveData;
    }

    /**
     * initialize the soldier live data
     * @return
     */
    public MutableLiveData<List<Soldier>> getSoldiersLiveData() {
        return soldiersLiveData;
    }
    /**
     * initialize the team live data
     * @return
     */
    public MutableLiveData<Team> getTeamLiveData() {
        return teamLiveData;
    }

    /*public MutableLiveData<List<Team>> getTeamsLiveData() {
        if (teamsLiveData == null) {
            teamsLiveData = new MutableLiveData<>();
        }
        return  teamsLiveData;
    }*/
/////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //get from cloud



    public TeamLiveData getFirestoreTeamLiveData() {
        CollectionReference reference = firebaseFirestore
                .collection("teams");
        return new TeamLiveData(reference);
    }

    public void getFromCloud(){
        getUser(userLiveData.getValue().getName());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //getUserTeam(userLiveData.getValue().getName());
                Log.i(LOG_NAME, "userLiveData.getValue().getName() is: " + userLiveData.getValue().getName());
            }
        });
        thread.start();
        /*try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //      while (teamLiveData == null) {
        //         Log.i(LOG_NAME, "whilo loop: teamLiveData is " + teamLiveData);
        //      }
    }




    public void test() throws InterruptedException, ExecutionException
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() {
                return 2;
            }
        };
        Future<Integer> future = executor.submit(callable);
        // future.get() returns 2 or raises an exception if the thread dies, so safer
        executor.shutdown();
    }

    private void getUserFromCloud(String username) {
        firebaseFirestore.collection("users")
                .whereEqualTo("name", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i(LOG_NAME, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.i(LOG_NAME, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    /**
     * get User from cloud
     * @param userName
     */
    private void getUser(final String userName) {



        DocumentReference userReference = firebaseFirestore.collection("users")
                .document(userName);

        userReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(LOG_NAME, "getUser2: Listen failed.", error);
                    return;
                }
                if (value != null && value.exists()) {
                    Log.d(LOG_NAME, "getUser2: Current data: " + value.getData());
                    Map<String, Object> userMap = value.getData();
                    userLiveData.setValue(new LoggedInUser   //get from cloud and set the user to app
                            ((String)userMap.get("id"),(String)userMap.get("name"), (String)userMap.get( "role")));
                    getTeamFromUser((DocumentReference) value.getData().get("leaderOfTeam"));
                } else {
                    Log.d(LOG_NAME, "getUser2: Current data: null");
                }
            }
        });
/*
        userReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.i(LOG_NAME, "getUserTeam: get document data: " + document.getData());
                            Map<String, Object> userMap = document.getData();
                            userLiveData.setValue(new LoggedInUser   //get from cloud and set the user to app
                                    ((String)userMap.get("id"),(String)userMap.get("name"), (String)userMap.get( "role")));
                            getTeamFromUser((DocumentReference) document.get("leaderOfTeam"));

                        } else {
                            Log.w(LOG_NAME, "getUserTeam:  failed: ", task.getException());
                            task.getException();
                        }
                    }
                });*/


    };


    private void getUserDataOnce(final String userName) {
        final boolean[] dataArrived = new boolean[1];

    //    new Thread(new Runnable() {
   //         @Override
   //         public void run() {
                firebaseFirestore.collection("users")
                        .document(userName)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    Log.i(LOG_NAME, "getUserTeam: get document data: " + document.getData());
                                    Map<String, Object> userMap = document.getData();
                                    userLiveData.setValue(new LoggedInUser   //get from cloud and set the user to app
                                            ((String)userMap.get("id"),(String)userMap.get("name"), (String)userMap.get( "role")));
                                    getTeamFromUser((DocumentReference) document.get("leaderOfTeam"));

                                } else {
                                    Log.w(LOG_NAME, "getUserTeam:  failed: ", task.getException());
                                    task.getException();
                                }
                            }
                        });
  //      int i = 1000;
  //      while (i != 0) {
 //           Log.i(LOG_NAME, "getUserTeam, while loop, teamLiveData is " +teamLiveData);
 //           i--;
 //       }

       //         try {
       //             Thread.sleep(1500);
       //         } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }


      //      }
       // }).start();


    };

    /**
     * get Team From User
     * @param reference
     */
    private void getTeamFromUser(DocumentReference reference) {

        if (reference == null)
        {
            new Exception("getTeamFromUser: team reference is null");
        }

        DocumentReference teamReference = firebaseFirestore.document(reference.getPath());

        teamReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(LOG_NAME, "getTeamFromUser: Listen failed.", error);
                    return;
                }
                if (value != null && value.exists()) {
                    Log.d(LOG_NAME, "getTeamFromUser: Current data: " + value.getData());
                    Map<String, Object> teamMap = value.getData();
                    teamLiveData.setValue(new Team((String) teamMap.get("id"),(String) teamMap.get( "name")));
                    crewReferenceList.addAll((List)teamMap.get("crew"));
                    ReferenceToSoldierList(crewReferenceList);
                    Log.i(LOG_NAME, "getTeamFromUser, task.getResult().getData()" + value.getData());
                    Log.i(LOG_NAME, "getTeamFromUser, teamLiveData: " + teamLiveData.getValue());
                    //getCrewFromTeam((DocumentReference) teamMap.get("crew"));
                } else {
                    Log.w(LOG_NAME, "getTeamFromUser: Current data: null");
                }
            }
        });

/*
        teamReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Map<String, Object> teamMap = task.getResult().getData();
                            teamLiveData.setValue(new Team((String) teamMap.get("id"),(String) teamMap.get( "name")));
                            Log.i(LOG_NAME, "getTeamFromUser, task.getResult().getData()" + task.getResult().getData());
                            Log.i(LOG_NAME, "getTeamFromUser, teamLiveData: " + teamLiveData.getValue());
                            getTeamFromUser((DocumentReference) teamMap.get("crew"));

                        } else {
                            Log.d(LOG_NAME, "Error getting documents: ", task.getException());
                            task.getException();
                        }
                    }
                });*/
    }

    private void getCrewFromTeam(DocumentReference reference) {
        final List<DocumentReference> soldiersListReferences = new ArrayList<>();
        if (reference == null) {
            new Exception("getCrewFromTeam: crew in null");
        }

        firebaseFirestore.document(reference.getPath())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(LOG_NAME, "getCrewFromTeam: Listen failed.", error);
                            return;
                        }

                        if (value != null && value.exists()) {
                            Log.d(LOG_NAME, "getCrewFromTeam: Current data: " + value.getData());
                            Map<String, Object> soldierMap = value.getData();

                        } else  {
                            Log.w(LOG_NAME, "getCrewFromTeam: Current data: null");
                        }
                    }
                });
    }

    /**
     * Reference To Soldier List
     * @param referenceList
     */
    private void ReferenceToSoldierList(List<DocumentReference> referenceList) {
        if (referenceList.isEmpty()) {
            new Exception("ReferenceToSoldierList: list is empty");
        }

        for (DocumentReference reference: referenceList) {
            //Log.d(LOG_NAME, "ReferenceToSoldierList: Current data: " + reference);
            firebaseFirestore.document(reference.getPath())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(LOG_NAME, "ReferenceToSoldierList: Listen failed.", error);
                                return;
                            }

                            if (value != null && value.exists()) {
                                Log.d(LOG_NAME, "ReferenceToSoldierList: Current data: " + value.getData());
                                Map<String, Object> soldierMap = value.getData();
                                //id name rate comment
                                Soldier soldier = new Soldier
                                        ((String) soldierMap.get("id"), (String) soldierMap.get("name")
                                                , (long) soldierMap.get("rate"), (String) soldierMap.get("comment"));
                                    soldierList.add(soldier);
                            } else  {
                                Log.w(LOG_NAME, "ReferenceToSoldierList: Current data: null");
                            }
                        }
                    });
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!soldierList.isEmpty()){
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            soldiersLiveData.setValue(soldierList);
                        }
                    });

                } else {
                    new Exception("ReferenceToSoldierList: soldier list is empty");
                }
            }
        }).start();
    }


    private void deleteSoldier(String id) {

    }

    private void updateSoldier(String id) {

    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * write data to cloud
     */
    public void toCloud() {
        //addSoldiers();
        //addUsers();
        /*for (int i = 0; i < 10; i++){
            createUsers(i+1);
        }*/

        //createTeams();
    }



    //team for cloud
    private void createTeams() {
        /////-/
        final ArrayList<String> teamName = new ArrayList<>();
        teamName.add("Reshef");
        teamName.add("Drakon");
        teamName.add("Namer");
        teamName.add("Keren");
        //////-/

        Log.i(LOG_NAME, "createTeams execute");

        new Thread(new Runnable() {
            @Override
            public void run() {
                final int[] i = new int[1];
                for (i[0] = 0; i[0] < 4 ; i[0]++) {
                    Log.i(LOG_NAME, "loop" + i[0]);
                    Map<String, Object> team = new HashMap<>();
                    team.put("id", String.valueOf(i[0]+100));
                    team.put("name", teamName.get(i[0]));
                    team.put("crew", null);

                    Log.i(LOG_NAME, "1. i[0] is " + i[0]);
                    // Add a new document with a generated ID
                    firebaseFirestore.collection("teams")
                            .document(team.get("name").toString())
                            .set(team)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void Void) {
                                    Log.i(LOG_NAME, "2. i[0] is " + i[0]);
                                    createCrewForTeam(teamName.get(i[0]));
                                    Log.i(LOG_NAME, "DocumentSnapshot added");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(LOG_NAME, "createTeams: Error adding document", e);
                                    new Exception("createTeams: Error adding document");
                                }
                            });

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    /**
     * give the specific team for update the crew
     * @param teamName
     */
    private void createCrewForTeam(final String teamName) {
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
                addTheCrew(teamName);

            }
        });
        getCrewThread.start();



    }


    /**
     * add crew for the team
     * @param teamName
     */
    private void addTheCrew(String teamName) {
        Log.i(LOG_NAME, "addTheCrew execute");
        firebaseFirestore.collection("teams")
                .document(teamName)
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
                        Log.w(LOG_NAME, "addTheCrew: Error updating document", e);
                        new Exception("error in firestore");
                    }
                });
    }

    /**
     * fetch crew by specific requirements
     */
    private void getCrew() {
        Log.i(LOG_NAME, "getCrew: execute");
        //////////////////////firestore
        firebaseFirestore.collection("soldiers")
                .whereGreaterThanOrEqualTo("id", "15")//the specific requirement
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
                            Log.i(LOG_NAME, "getCrew: Error getting documents: ", task.getException());
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
                        Log.i(LOG_NAME, "addTheUser: DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(LOG_NAME, "addTheUser: Error adding document", e);
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
        Log.i("data source", "addUsers execute");
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
        ArrayList<String> soldierName = new ArrayList<>();
        soldierName.add("shar1");
        soldierName.add("shar13");
        soldierName.add("shar14");
        soldierName.add("shar15");
        soldierName.add("shar16");
        soldierName.add("shar17");
        soldierName.add("shar18");
        soldierName.add("shar19");
        soldierName.add("shar20");
        soldierName.add("shar21");
        soldierName.add("shar22");
        //////-/

        for (int i = 0; i < 10 ; i++) {
            Log.i("data source", "addSoldiers execute");
            Map<String, Object> soldier = new HashMap<>();
            soldier.put("id", String.valueOf(i+10));
            soldier.put("name", soldierName.get(i));
            soldier.put("rate", 70 + i);
            soldier.put("comment", "good");

            // Add a new document with a generated ID
            firebaseFirestore.collection("soldiers")
                    .document(soldier.get("name").toString())
                    .set(soldier)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void Void) {
                            Log.i("data source", "DocumentSnapshot added");
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
