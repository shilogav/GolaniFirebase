package com.shilo.golanimanage.mainactivity.data;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shilo.golanimanage.mainactivity.livedata.TeamLiveData;
import com.shilo.golanimanage.mainactivity.model.Question;
import com.shilo.golanimanage.mainactivity.model.Report;
import com.shilo.golanimanage.mainactivity.model.Soldier;
import com.shilo.golanimanage.mainactivity.model.Team;
import com.shilo.golanimanage.model.LoggedInUser;

import java.lang.reflect.Array;
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
    public static final String REPORTS_COLLECTION = "reports";
    public static final String USERS_COLLECTION = "users";
    public static final String SOLDIERS_COLLECTION = "soldiers";
    public static final String TEAMS_COLLECTION = "teams";
    public static FirebaseFirestore firebaseFirestore;
    private MutableLiveData<Team> teamLiveData;
    private MutableLiveData<LoggedInUser> userLiveData;
    private MutableLiveData<List<Soldier>> soldiersLiveData;
    private MutableLiveData<Report> reportLiveData;
    private final DocumentReference[] referencesTemp = new DocumentReference[1];
    private final List<DocumentReference> crewReferenceList = new ArrayList<>();
    private List<Soldier> soldierList = new ArrayList<>();
    private List<DocumentReference> reportsReferences = new ArrayList<>();
    private ArrayList<Report> reports = new ArrayList<>();


    //final List<DocumentReference> soldiersListReferences = new ArrayList<>();

    public static DataSource getInstance(){
        if (instance == null){
            instance = new DataSource();
        }
        return instance;
    }

    private DataSource(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        teamLiveData = new MutableLiveData<>();
        soldiersLiveData = new MutableLiveData<>();
        reportLiveData = new MutableLiveData<>();
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

    public MutableLiveData<Report> getReportLiveData(Soldier soldier, String reportType) {
        getReportFromCloud(soldier, reportType);
        return reportLiveData;
    }

    /**
     * create report to reports collection and call methods to update user and soldier data
     * @param report
     */
    public void setReport(final Report report, final Soldier soldier, final LoggedInUser user) {
        firebaseFirestore.collection(REPORTS_COLLECTION)
                .document(report.getId())
                .set(report)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(LOG_NAME, "setReport -> report successfully written!");
                        addReportToSoldier(report.getId(), soldier);
                        addReportToUser(report.getId(), user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        try {
                            throw e;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
    }

    private void addReportToSoldier(String reportId, Soldier soldier) {
        DocumentReference documentReference = firebaseFirestore.collection(REPORTS_COLLECTION).document(reportId);
        firebaseFirestore.collection(SOLDIERS_COLLECTION).document(soldier.getName())
                .update("reports", FieldValue.arrayUnion(documentReference))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(LOG_NAME, "addReportToSoldier -> report successfully added to soldier!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        try {
                            throw e;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
    }

    private void addReportToUser(String reportId, LoggedInUser user) {
        DocumentReference documentReference = firebaseFirestore.collection(REPORTS_COLLECTION).document(reportId);
        firebaseFirestore.collection(USERS_COLLECTION).document(user.getName())
                .update("reports", FieldValue.arrayUnion(documentReference))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(LOG_NAME, "addReportToUser -> report successfully added to user!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        try {
                            throw e;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
    }

    /**
     * get report from cloud
     * @param soldier
     */
    public void getReportFromCloud(final Soldier soldier, final String reportType) {
        firebaseFirestore.collection(SOLDIERS_COLLECTION)
                .whereEqualTo("id", soldier.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i(LOG_NAME + " -> getReportFromCloud: ", document.getId() + " => " + document.getData());
                                reportsReferences = (ArrayList<DocumentReference>)document.get("reports");
                                getReportsByReference(reportsReferences, soldier, reportType);
                            }
                        } else {
                            task.getException();
                        }
                    }
                });
    }
    /**
     * fetch reports
     * @param reportsReferences
     */
    private void getReportsByReference(final List<DocumentReference> reportsReferences, final Soldier soldier, final String reportType) {
        if (reportsReferences == null) {
            return;
        }
        reportLiveData.setValue(null);//important. delete report if there is from last fetch
        Log.i(LOG_NAME, "getReportsByReference -> reportLiveData value become null");
        for (DocumentReference reference : reportsReferences) {
            firebaseFirestore.document(reference.getPath())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d(LOG_NAME, "getReportsByReference -> DocumentSnapshot data: " + document.getData());
                                    Map<String, Object> reportTemp = document.getData();
                                    List<Question> questions = convertHashMapToQuestion((List<HashMap>) reportTemp.get("questionList"));
                                    if (reportTemp.get("idSoldier").equals(soldier.getId())
                                            && reportTemp.get("idLeader").equals(userLiveData.getValue().getUserId())
                                     && reportTemp.get("description").equals(reportType)) {
                                        Report report = new Report((String) reportTemp.get("description")
                                                ,(String) reportTemp.get("id"),questions
                                                ,(String) reportTemp.get("idLeader"),(String) reportTemp.get("idSoldier"));
                                        reportLiveData.setValue(report);
                                        Log.i(LOG_NAME, "getReportsByReference -> reportLiveData set");
                                    } else {
                                        Log.i(LOG_NAME, "getReportsByReference -> No report for that team leader/user and soldier");
                                    }
                                } else {
                                    Log.i(LOG_NAME, "getReportsByReference -> No such document");
                                }
                            } else {
                                Log.d(LOG_NAME, "getReportsByReference -> get failed with ", task.getException());
                            }
                        }
                    });

        }

    }

    private ArrayList<Question> convertHashMapToQuestion(List<HashMap> mapArrayList) {
        ArrayList<Question> questions = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (HashMap hashMap : mapArrayList) {
            questions.add(mapper.convertValue(hashMap, Question.class));
        }
        return questions;
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



    /*public TeamLiveData getFirestoreTeamLiveData() {
        CollectionReference reference = firebaseFirestore
                .collection(TEAMS_COLLECTION);
        return new TeamLiveData(reference);
    }*/

    /**
     * listeners for data in cloud
     */
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
     * listen for user from cloud
     * @param userName
     */
    private void getUser(final String userName) {
        DocumentReference userReference = firebaseFirestore.collection(USERS_COLLECTION)
                .document(userName);

        userReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(LOG_NAME, "getUser: Listen failed.", error);
                    return;
                }
                if (value != null && value.exists()) {
                    Log.d(LOG_NAME, "getUser: Current data: " + value.getData());
                    Map<String, Object> userMap = value.getData();
                    LoggedInUser user = new LoggedInUser   //get from cloud and set the user to app
                            ((String)userMap.get("id"),(String)userMap.get("name"), (String)userMap.get( "role"));


                    //add the report to frontend//TODO: decide if update reports now or later
                    //getReportsFromUser((ArrayList<DocumentReference>) userMap.get("reports"), user);

                    //add the team to frontend
                    getTeamFromUser((DocumentReference) value.getData().get("leaderOfTeam"));
                } else {
                    Log.d(LOG_NAME, "getUser: Current data: null");
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


    }

    /**
     * convert list of report document references to Report objects for user frontend
     * @param reportsReferences
     */
    private void reportReferencesToReportObjectsForUser(ArrayList<DocumentReference> reportsReferences) {
        Log.i(LOG_NAME, "reportReferencesToReportObjects executed");
        if (reportsReferences == null) {
            new Exception("getTeamFromUser: team reference is null");
            return;
        }
        for (DocumentReference reference : reportsReferences) {
            reference
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            if (task.isSuccessful()) {
                                if (document.exists()) {
                                    Log.d(LOG_NAME, "reportReferencesToReportObjects -> DocumentSnapshot data: " + document.getData());
                                    Map<String,Object> reportMap = document.getData();
                                    Report report = new Report();
                                    report.setDescription((String) reportMap.get("description"));
                                    report.setId((String) reportMap.get("id"));
                                    report.setIdLeader((String) reportMap.get("idLeader"));
                                    report.setQuestionList((ArrayList<Question>) reportMap.get("questionList"));
                                    reports.add(report);
                                    Log.d(LOG_NAME, "reportReferencesToReportObjects -> finish one loop ");
                                } else {
                                    Log.d(LOG_NAME, "reportReferencesToReportObjects ->No such document");
                                }
                            } else {
                                Log.d(LOG_NAME, "reportReferencesToReportObjects -> get failed with ", task.getException());
                            }
                        }
                    });
        }
        Log.d(LOG_NAME, "reportReferencesToReportObjects -> finish all loops");
    }

    private ArrayList<Report> updatedReportList(ArrayList<Report> reports) {
        return reports;
    }

    /**
     * get Reports From User in cloud and update the user at frontend
     * @param reportsReferences
     */
    private void getReportsFromUser(ArrayList<DocumentReference> reportsReferences, final LoggedInUser user) {
        Log.i(LOG_NAME, "getReportsFromUser executed");
        reportReferencesToReportObjectsForUser(reportsReferences);

        //add the user with reports after delay for data to be fetched
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                user.setReports((ArrayList<Report>) reports.clone());
                reports.clear();
                Log.i(LOG_NAME, "getReportsFromUser -> reports cleared");
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        userLiveData.setValue(user);
                    }
                });

                Log.d(LOG_NAME, "getReportsFromUser ->user live data updated");
            }
        }).start();

    }

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
     * listen to team in User from cloud
     * @param reference
     */
    private void getTeamFromUser(DocumentReference reference) {

        if (reference == null) {
            new Exception("getTeamFromUser: team reference is null");
            return;
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
                    Log.d(LOG_NAME, "getTeamFromUser: teamMap: " + teamMap);
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

        Log.d(LOG_NAME, "ReferenceToSoldierList executed!!!");

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
                                Log.d(LOG_NAME, "ReferenceToSoldierList: soldier data: " + value.getData());
                                final Map<String, Object> soldierMap = value.getData();
                                //id name rate comment
                                final Soldier soldier = new Soldier
                                        ((String) soldierMap.get("id"), (String) soldierMap.get("name")
                                                , (long) soldierMap.get("rate"), (String) soldierMap.get("comment"));

                                //TODO: decide to update report for fronend now or later
                                //reportReferencesToReportObjectsForSoldiers((ArrayList<DocumentReference>) soldierMap.get("reports"), soldier);
                                //updateSoldierListReport(soldier);//add the report so soldier list in background. TODO: delete. won't work for now
                                soldierList.add(soldier);
                                soldiersLiveData.setValue(soldierList);
                                //Log.d(LOG_NAME, "ReferenceToSoldierList: soldier size: " + soldierList.size());
                            } else  {
                                Log.w(LOG_NAME, "ReferenceToSoldierList: soldier data: null");
                            }
                        }
                    });
        }
        /*new Thread(new Runnable() {
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
        }).start();*/
    }

    /**
     * convert list of report document references to Report objects for user frontend
     * @param reportsReferences
     */
    private void reportReferencesToReportObjectsForSoldiers(ArrayList<DocumentReference> reportsReferences, final Soldier soldier) {
        Log.i(LOG_NAME, "reportReferencesToReportObjectsForSoldiers executed");
        final ArrayList<Report> reports = new ArrayList<>();
        final ArrayList<Soldier> soldiers = new ArrayList<>();
        if (reportsReferences == null) {
            new Exception("getTeamFromUser: team reference is null");
            return;
        }
        for (DocumentReference reference : reportsReferences) {
            reference
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();
                            if (task.isSuccessful()) {
                                if (document.exists()) {
                                    Log.d(LOG_NAME, "reportReferencesToReportObjectsForSoldiers -> DocumentSnapshot data: " + document.getData());
                                    Map<String,Object> reportMap = document.getData();
                                    Report report = new Report();
                                    report.setDescription((String) reportMap.get("description"));
                                    report.setId((String) reportMap.get("id"));
                                    report.setIdLeader((String) reportMap.get("idLeader"));
                                    report.setQuestionList((ArrayList<Question>) reportMap.get("questionList"));
                                    reports.add(report);
                                    Log.d(LOG_NAME, "reportReferencesToReportObjectsForSoldiers -> finish one loop ");
                                } else {
                                    Log.d(LOG_NAME, "reportReferencesToReportObjectsForSoldiers ->No such document");
                                }
                            } else {
                                Log.d(LOG_NAME, "reportReferencesToReportObjectsForSoldiers -> get failed with ", task.getException());
                            }
                        }
                    });
        }
        Log.d(LOG_NAME, "reportReferencesToReportObjectsForSoldiers -> finish all loops");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                soldier.setReports(reports);
                for (int i = 0 ; i < soldierList.size() ; i++) {
                    if (soldier.getId().equals(soldierList.get(i).getId())) {
                        soldierList.set(i, soldier);
                    }
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        soldiersLiveData.setValue(soldierList);
                    }
                });
            }
        }).start();
    }
    /**
     * add the soldier after report
     * @param soldier
     */
    private void updateSoldierListReport(final Soldier soldier) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                soldier.setReports((ArrayList<Report>) reports.clone());
                Log.d(LOG_NAME, "updateSoldierListReport: soldier id: " + soldier.getReports().get(0).getId());
                reports.clear();
                for (int i = 0 ; i < soldierList.size() ; i++) {
                    if (soldier.getId().equals(soldierList.get(i).getId())) {
                        soldierList.set(i, soldier);
                    }
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        soldiersLiveData.setValue(soldierList);
                    }
                });
            }
        }).start();
    }


    public void deleteSoldier(String id) {

    }

    private void updateSoldier(String id) {

    }


    //TODO: write data to cloud area
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

        createTeams();
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

                    Log.i(LOG_NAME, "1. i[0] is " + i[0] + " for team " + teamName.get(i[0]));
                    // Add a new document with a generated ID
                    firebaseFirestore.collection("teams")
                            .document(team.get("name").toString())
                            .set(team)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void Void) {
                                    Log.i(LOG_NAME, "2. i[0] is " + i[0]);
                                    createCrewForTeam(teamName.get(i[0]));
                                    Log.i(LOG_NAME, "createTeams -> DocumentSnapshot added");
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
                        Thread.sleep(2000);
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
                crewReferenceList.clear();
                getCrew();

                /*
                important sleep!!! waiting for fetching the crew reference list from soldiers collection
                 */
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.i(LOG_NAME, "createCrewForTeam -> crewReferenceList is: " + crewReferenceList);
                Log.i(LOG_NAME, "createCrewForTeam -> crewReferenceList size: " + crewReferenceList.size());
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
        Log.i(LOG_NAME, "addTheCrew execute. teamName: " + teamName);
        firebaseFirestore.collection("teams")
                .document(teamName)
                .update("crew",crewReferenceList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(LOG_NAME, "addTheCrew -> DocumentSnapshot successfully updated!");
                        //crewReferenceList.clear();//TODO: test if this ok
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
                                Log.i(LOG_NAME, "getCrew -> document.getId() ->" + document.getId() + " => " + document.getData());
                                crewReferenceList.add(document.getReference());
                                Log.i(LOG_NAME, "getCrew -> crewReferenceList.size " + crewReferenceList.size());
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
                getMyTeamReference("35");
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







    ////////////////////











}
