package com.shilo.golanimanage.mainactivity.data;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
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
import com.google.firebase.firestore.Transaction;
import com.shilo.golanimanage.Utility;
import com.shilo.golanimanage.mainactivity.fragments.SoldierDetailsFragment;
import com.shilo.golanimanage.mainactivity.livedata.TeamLiveData;
import com.shilo.golanimanage.mainactivity.model.Question;
import com.shilo.golanimanage.mainactivity.model.Report;
import com.shilo.golanimanage.mainactivity.model.Soldier;
import com.shilo.golanimanage.mainactivity.model.Team;
import com.shilo.golanimanage.mainactivity.viewmodel.SoldierListViewModel;
import com.shilo.golanimanage.model.LoggedInUser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
    private MutableLiveData<String> commentLiveData;
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
        commentLiveData = SoldierListViewModel.getCommentLiveData();
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

    /////////////////////////////////////////////////////
    //report

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
                                    if (reportTemp.get("idSoldier").equals(soldier.getId()) //check for specific soldier report
                                            && (reportTemp.get("idLeader").equals(userLiveData.getValue().getUserId())  //check for specific user report
                                            || (userLiveData.getValue().getUserId().contains("Admin") //if Admin should see reports
                                            || (userLiveData.getValue().getUserId().contains("admin")))) //if admin should see reports
                                            && reportTemp.get("description").equals(reportType)) //check for plain or interview report
                                    {
                                        List<Question> questions = convertHashMapToQuestion((List<HashMap>) reportTemp.get("questionList"));
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

    ///////////////////////////////////////////////////////

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
        soldierList.clear();
        soldiersLiveData.setValue(soldierList);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getUser(userLiveData.getValue().getName());//TODO:should execute
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
        Log.i(LOG_NAME, "getUser execute!!");
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
                    userLiveData.setValue(user);

                    //add the report to frontend//TODO: decide if update reports now or later
                    //getReportsFromUser((ArrayList<DocumentReference>) userMap.get("reports"), user);

                    //add the team to frontend
                    Object object = value.getData().get("leaderOfTeam");
                    if (((ArrayList<DocumentReference>) object).get(0) == null) {
                        getUser(userName);
                        return;
                    }
                    ArrayList <DocumentReference> listTemp = new ArrayList<>();
                    if (object instanceof DocumentReference) {
                        listTemp.add((DocumentReference) object);
                        getTeamFromUser(listTemp);
                    } else if (object instanceof ArrayList){

                        getTeamFromUser((ArrayList<DocumentReference>) value.getData().get("leaderOfTeam"));
                    }

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

    /**
     * listen to team in User from cloud
     * @param list
     */
    private void getTeamFromUser(ArrayList<DocumentReference> list) {
        Log.i(LOG_NAME, "getTeamFromUser execute");
        final List<DocumentReference> crewReferenceList = new ArrayList<>();
        if (list == null) {
            new Exception("getTeamFromUser: team list is null");
            return;
        }

        for (DocumentReference reference : list) {
            if (reference == null) {
                new Exception("no team added for user");
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
                        LoggedInUser user = userLiveData.getValue();
                        user.getLeaderOfTeam().add((String) teamMap.get("id"));
                        userLiveData.setValue(user);
                        Log.d(LOG_NAME, "getTeamFromUser: teamMap: " + teamMap);
                        crewReferenceList.addAll((List)teamMap.get("crew"));
                        //soldierList.clear();//maybe can be cleared
                        ReferenceToSoldierList(crewReferenceList);
                        Log.i(LOG_NAME, "getTeamFromUser, task.getResult().getData()" + value.getData());
                        Log.i(LOG_NAME, "getTeamFromUser, teamLiveData: " + teamLiveData.getValue());
                        //getCrewFromTeam((DocumentReference) teamMap.get("crew"));
                    } else {
                        Log.w(LOG_NAME, "getTeamFromUser: Current data: null");
                    }
                }
            });
        }



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
    private void ReferenceToSoldierList(final List<DocumentReference> referenceList) {
        if (referenceList.isEmpty()) {
            new Exception("ReferenceToSoldierList: list is empty");
        }

        Log.d(LOG_NAME, "ReferenceToSoldierList executed!!!");
    //    new Thread(new Runnable() {
    //        @Override
  //          public void run() {


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
                                        Log.i(LOG_NAME, "ReferenceToSoldierList: soldier name " + (String) soldierMap.get("name"));
                                        //TODO: decide to update report for fronend now or later
                                        //reportReferencesToReportObjectsForSoldiers((ArrayList<DocumentReference>) soldierMap.get("reports"), soldier);
                                        //updateSoldierListReport(soldier);//add the report so soldier list in background. TODO: delete. won't work for now

                                        Log.i(LOG_NAME, "ReferenceToSoldierList: soldierList " + soldierList);
                                        Log.i(LOG_NAME, "ReferenceToSoldierList: soldierList size " + soldierList.size());
                                        if (!soldierList.contains(soldier)) {
                                            soldierList.add(soldier);
                                            soldiersLiveData.setValue(soldierList);
                                            Log.i(LOG_NAME, "ReferenceToSoldierList: soldier added");
                                        }
                                        //Log.d(LOG_NAME, "ReferenceToSoldierList: soldier size: " + soldierList.size());
                                    } else  {
                                        Log.w(LOG_NAME, "ReferenceToSoldierList: soldier data: null");
                                    }
                                }
                            });
                }


                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
   //         }
  //      }).start();

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

    public MutableLiveData<String> getComment(Soldier soldier) {
        final DocumentReference soldierRef = firebaseFirestore.collection(SOLDIERS_COLLECTION).document(soldier.getId());
        firebaseFirestore.runTransaction(new Transaction.Function<Object>() {
            @Nullable
            @Override
            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                final DocumentSnapshot snapshot = transaction.get(soldierRef);
                String temp =(String) snapshot.get("comment");
                String temp2 = commentLiveData.getValue();
                Log.d(LOG_NAME, "getComment -> commentLiveData updated from cloud");
                if (temp != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            commentLiveData.setValue((String) snapshot.get("comment"));
                        }
                    });

                }
                transaction.update(soldierRef, "comment", commentLiveData.getValue());

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Object>() {
            @Override
            public void onSuccess(Object o) {
                Log.d(LOG_NAME, "getComment -> Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(LOG_NAME, "getComment -> Transaction failure.", e);
            }
        });
        Log.d(LOG_NAME, "getComment -> method returned result");
        return commentLiveData;
    }

    public MutableLiveData<String> getComment2(Soldier soldier) {
        final DocumentReference soldierRef = firebaseFirestore.collection(SOLDIERS_COLLECTION).document(soldier.getId());
        soldierRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(LOG_NAME, "Listen failed.", error);
                    return;
                }

                if (value != null && value.exists()) {
                    Log.d(LOG_NAME, "Current data: " + value.getData());
                    String comment = (String) value.get("comment");
                    commentLiveData.setValue(comment);
                } else {
                    Log.d(LOG_NAME, "Current data: null");
                }
            }
        });
        return commentLiveData;
    }


    //TODO: write data to cloud area
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * write data to cloud
     */
    public void toCloud() {
        Log.i(LOG_NAME, "toCloud executed");
        for (int i = 0; i < 37; i++){
            createUsers(i+1); //users
        }

        ///////////////create teams
        final ArrayList<String> teamName = new ArrayList<>();
        //teamName.add("Reshef");
        //teamName.add("Drakon");
        //teamName.add("Namer");
        //teamName.add("Keren");
        teamName.add("1");
        teamName.add("2");
        teamName.add("3");
        teamName.add("4");
        teamName.add("5");
        teamName.add("6");
        teamName.add("7");
        teamName.add("8");
        teamName.add("9");
        teamName.add("10");

        //createTeams(teamName); //teams
        for (String name : teamName) {
            //createCrewForTeam(name); //crew
        }
        for (int i=1; i<11;i++) {//add all teams for admin users
            //addAllTeamsForAdminUser(firebaseFirestore.collection("users").document("admin".concat(String.valueOf(i))));
        }

    }




    //team for cloud
    private void createTeams(final ArrayList<String> teamName) {
        Log.i(LOG_NAME, "createTeams execute");

        new Thread(new Runnable() {
            @Override
            public void run() {
                final int[] i = new int[1];
                for (i[0] = 0; i[0] < teamName.size() ; i[0]++) {
                    Log.i(LOG_NAME, "createTeams -> teamName.size() " + teamName.size());
                    Log.i(LOG_NAME, "createTeams -> loop " + i[0]);
                    Map<String, Object> team = new HashMap<>();
                    team.put("id", String.valueOf(i[0]+1));
                    team.put("name", teamName.get(i[0]));
                    team.put("crew", null);
                    Log.i(LOG_NAME, "createTeams -> team name:" + teamName.get(i[0]));
                    Log.i(LOG_NAME, "1. i[0] is " + i[0] + " for team " + teamName.get(i[0]));
                    firebaseFirestore.collection("teams")
                            .document(team.get("name").toString())
                            .set(team)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void Void) {
                                    Log.i(LOG_NAME, "2. i[0] is " + i[0]);
                                    Log.i(LOG_NAME, "createTeams -> team created");
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
        Log.i(LOG_NAME, "createCrewForTeam executed");
        final Thread getCrewThread = new Thread(new Runnable() {
            @Override
            public void run() {
                crewReferenceList.clear();
                getCrew(teamName);



                /*
                important sleep!!! waiting for fetching the crew reference list from soldiers collection
                 */
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Log.i(LOG_NAME, "createCrewForTeam -> crewReferenceList is: " + crewReferenceList);
                //Log.i(LOG_NAME, "createCrewForTeam -> crewReferenceList size: " + crewReferenceList.size());
                //addTheCrew(teamName);



            }
        });
        getCrewThread.start();



    }

    /**
     * get crew from soldiers collection in cloud
     * then, call func addTheCrew to add the reference to crew array in teams collection cloud
     * @param teamID
     */
    public void getCrew(final String teamID) {
        final List<DocumentReference> crewReferenceList = new ArrayList<>();
        firebaseFirestore.collection("soldiers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.i(LOG_NAME, "getCrew -> document.getId() ->" + document.getId() + " => " + document.getData());
                                //int currentID = Integer.parseInt(document.getId());
                                if (teamID.equals("1")                                                                                  // for team 1
                                        &&  document.getId().length() == 3
                                        && document.getId().charAt(0) == teamID.charAt(0)) {
                                    crewReferenceList.add(document.getReference());
                                }
                                else if (teamID.equals("10") && document.getId().length() == 4)                                         //for team 10
                                {
                                    crewReferenceList.add(document.getReference());
                                }
                                else if (!teamID.equals("1") && !teamID.equals("10") && teamID.charAt(0) == document.getId().charAt(0)) //for teams 2-9
                                {
                                    crewReferenceList.add(document.getReference());
                                }
                                Log.i(LOG_NAME, "getCrew -> crewReferenceList.size " + crewReferenceList.size());
                            }

                            addTheCrew(teamID, crewReferenceList);
                        } else {
                            Log.i(LOG_NAME, "getCrew: Error getting documents: ", task.getException());
                            new Exception("error in firestore");
                        }
                    }
                });

    }


    /**
     * add crew for the team
     * @param soldierID
     * @param crewReferenceList
     */
    private void addTheCrew(String soldierID, List<DocumentReference> crewReferenceList) {
        Log.i(LOG_NAME, "addTheCrew execute. teamName: " + soldierID);
        firebaseFirestore.collection("teams")
                .document(soldierID)
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
     * create user and add just one team for user
     */
    private void createUsers(final int counter) {
        Log.i(LOG_NAME, "createUsers executed");
        ArrayList<DocumentReference> teamsReferences = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String,Object> users = new HashMap<>();
                users.put("id", "user" + counter);
                users.put("name","user" + counter);
                users.put("role","C");

                //add team to user.TODO: I canceled it for now
                //getMyTeamReference("1");
                //do {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    users.put("leaderOfTeam",FieldValue.arrayUnion(referencesTemp[0]));
                 //   if (referencesTemp[0] != null){
                        addTheUser(users);
                //    }
                //} while (referencesTemp[0] == null);


            }
        }).start();
    }



    /**
     * assistance method for createUsers method
     * @param data
     */
    private void addTheUser(Map<String,Object> data){
        Log.i(LOG_NAME, "addTheUser -> data: " + data);
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



    /**
     * add list of all team references for the admin users who should see all soldiers
     * @param userReference
     */
    private void addAllTeamsForAdminUser(final DocumentReference userReference) {
        Log.d(LOG_NAME, "addAllTeamsForAdminUser executed!!");
        final ArrayList<DocumentReference> teamsReferences = new ArrayList<>();
        firebaseFirestore.collection("teams")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Log.d(LOG_NAME, "addAllTeamsForAdminUser -> "
                                        + documentSnapshot.getId() + " => " + documentSnapshot.getData());
                                teamsReferences.add(documentSnapshot.getReference());
                            }
                            Log.d(LOG_NAME, "addAllTeamsForAdminUser -> teamsReferences size is " + teamsReferences.size());
                            userReference.update("leaderOfTeam", teamsReferences)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(LOG_NAME, "addAllTeamsForAdminUser -> leaderOfTeams updated!!! ");
                                }
                            });
                        }
                    }
                });
    }




    /**
     * take the soldier list from excel and add soldiers to cloud
     * @param soldiers
     */
    public void createSoldiers(ArrayList<Soldier> soldiers) {
        Log.i(LOG_NAME, "createSoldiers execute");

        for (Soldier soldier : soldiers) {
            // Add a new document with a generated ID
            firebaseFirestore.collection("soldiers")
                    .document(soldier.getId())
                    .set(soldier)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void Void) {
                            Log.i(LOG_NAME, "createSoldiers -> DocumentSnapshot added");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(LOG_NAME, "createSoldiers -> Error adding document", e);
                        }
                    });
        }
    }

    public void deleteSoldier(final Soldier soldier, String reason) {
        final DocumentReference fromPath = firebaseFirestore.collection(SOLDIERS_COLLECTION).document(soldier.getId());
        DocumentReference toPath = firebaseFirestore.collection(reason).document(soldier.getId());
        moveFirestoreDocument(fromPath,toPath);
        //delete soldier from team
        firebaseFirestore.collection(TEAMS_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(LOG_NAME, document.getId() + " => " + document.getData());
                                //Map<String,Object> updates = new HashMap<>();
                                //updates.put("crew", FieldValue.delete());
                                document.getReference().update("crew", FieldValue.arrayRemove(fromPath))
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d(LOG_NAME, "update execute");
                                                if (task.isSuccessful()) {
                                                    Log.d(LOG_NAME, "crew soldier successfully deleted!");
                                                } else {
                                                    Log.d(LOG_NAME, "Error deleting crew soldier ", task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d(LOG_NAME, "Error getting teams: ", task.getException());
                        }
                    }
                });

    }

    /**
     * secondary method for delete soldier
     * @param fromPath
     * @param toPath
     */
    public void moveFirestoreDocument(final DocumentReference fromPath, final DocumentReference toPath) {
        fromPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        toPath.set(document.getData())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(LOG_NAME, "retire soldier successfully written!");
                                        fromPath.delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(LOG_NAME, "soldier successfully deleted!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(LOG_NAME, "Error deleting soldier", e);
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(LOG_NAME, "Error writing retire soldier", e);
                                    }
                                });
                    } else {
                        Log.d(LOG_NAME, "No such document");
                    }
                } else {
                    Log.d(LOG_NAME, "get failed with ", task.getException());
                }
            }
        });
    }

    private void deleteSoldier1(Soldier soldier) {
        firebaseFirestore.collection("soldiers").document(soldier.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(LOG_NAME, "soldier successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_NAME, "Error deleting soldier", e);
                    }
                });
    }

    private void updateSoldierComment() {

    }


    /**
     * deprecate version of adding soldiers
     */
    private void addSoldiers(){
        Log.i(LOG_NAME, "addSoldiers execute");
        /////-/
        /*ArrayList<String> soldierName = new ArrayList<>();
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
        soldierName.add("shar22");*/
        //////-/

        for (int i = 201; i <= 234 ; i++) {
            Map<String, Object> soldier = new HashMap<>();
            soldier.put("id", String.valueOf(i));
            soldier.put("name", String.valueOf(i));
            soldier.put("rate", 0);
            soldier.put("comment", "");

            // Add a new document with a generated ID
            firebaseFirestore.collection("soldiers")
                    .document(soldier.get("name").toString())
                    .set(soldier)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void Void) {
                            Log.i(LOG_NAME, "DocumentSnapshot added");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(LOG_NAME, "Error adding document", e);
                        }
                    });
        }
    }


    /**
     * deprecated
     * fetch crew by specific requirements
     */
    private void getCrew2(final String teamName, final String soldierID) {
        final List<DocumentReference> crewReferenceList = new ArrayList<>();
        Log.i(LOG_NAME, "getCrew: execute");
        Log.i(LOG_NAME, "getCrew -> teamName: " + teamName + ". soldierID: " + soldierID);
        //////////////////////firestore
        firebaseFirestore.collection("soldiers")
                .whereLessThanOrEqualTo("id", soldierID)//the specific requirement
                .whereGreaterThan("id", soldierID)
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

                            addTheCrew(teamName, crewReferenceList);
                        } else {
                            Log.i(LOG_NAME, "getCrew: Error getting documents: ", task.getException());
                            new Exception("error in firestore");
                        }
                    }
                });
        //////////////////////
    }

    /**
     * deprecate
     */
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
        Log.i(LOG_NAME, "addUsers execute");
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
                                user.put("leaderOfTeam",FieldValue.arrayUnion(document.getReference()));
                                Log.d(LOG_NAME, "team: " + document.getId() + " => " + document.getData());

                                firebaseFirestore.collection("users")
                                        .add(user)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.i(LOG_NAME, "user added. DocumentSnapshot added with ID: " + documentReference.getId());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i(LOG_NAME, "Error adding document", e);
                                            }
                                        });

                            }
                        } else {
                            Log.d(LOG_NAME, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////




}
