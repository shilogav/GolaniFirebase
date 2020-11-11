package com.shilo.golanimanage.mainactivity.livedata;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shilo.golanimanage.mainactivity.model.Team;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class TeamLiveData extends LiveData<List<Team>> //implements EventListener<QuerySnapshot>
         {
    private List<Team> teamListTemp;
    private HashMap<String,Object> dataMap = new HashMap<>();
    private MutableLiveData<List<Team>> teamList;
    private CollectionReference CollectionReference;
    private DocumentReference documentReference;
    //private TeamListener listener;

    public TeamLiveData(CollectionReference CollectionReference) {
        this.CollectionReference = CollectionReference;
        this.teamListTemp = new ArrayList<>();
        this.teamList = new MutableLiveData<>();
        //CollectionReference.addSnapshotListener(this);

        /*listener = new TeamListener() {
            @Override
            public void onDataChanged() {
                setValue(teamListTemp);
            }
        };*/
    }

    /*public MutableLiveData<List<Team>> getTeamList() {
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.i("team live data: ", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.i("team live data: ", "No such document");
                    }
                } else {
                    Log.i("team live data: ", "get failed with ", task.getException());
                }
            }
        });
        return teamList;
    }*/


    @Override
    protected void onActive() {
        super.onActive();
    }

    @Override
    protected void onInactive() {
        super.onInactive();
    }

    /*@Override
    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
        Log.i("team live data: ", "get into onEvent method");
        if (value != null && value.exists()) {
            Map<String, Object> teamListItem =
                    value.getData();

            teamListTemp.clear();

            assert teamListItem != null;
            for (Map.Entry<String, Object> entry :
            teamListItem.entrySet()) {
                Team team = new Team();
                team.setId();
                //teamListTemp.add((Team) entry.getValue());
                dataMap.put(entry.getKey(),entry.getValue());

            }

            teamList.setValue(teamListTemp);
            Log.i("team live data: ", "success taking firestore data");
        } else {
            Log.i("team live data: ", "error taking firestore data");
        }
    }*/


    /*private class MyValueEventListener implements OnCompleteListener<DocumentSnapshot>{
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            setValue(task.g);
        }
    }*/
}
