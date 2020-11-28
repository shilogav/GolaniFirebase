package com.shilo.golanimanage.login.data;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shilo.golanimanage.Utility;
import com.shilo.golanimanage.model.LoggedInUser;

import java.io.IOException;
import java.util.UUID;

import androidx.annotation.NonNull;

import static android.content.Context.MODE_PRIVATE;
import static com.shilo.golanimanage.Utility.toSharedPreferences;
import static com.shilo.golanimanage.model.LoggedInUser.ADMIN;
import static com.shilo.golanimanage.model.LoggedInUser.TEAM_LEADER;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private  static LoginDataSource instance;
    private FirebaseAuth mAuth;
    private String userID;
    private FirebaseUser currentUser;
    private String mCustomToken;
    public static final String LOG = "LoginDataSource";
    LoggedInUser mUser;
    Activity activity;


    //firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static final String Token = "eyJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJodHRwczovL2lkZW50aXR5dG9vbGtpdC5nb29nbGVhcGlzLmNvbS9nb29nbGUuaWRlbnRpdHkuaWRlbnRpdHl0b29sa2l0LnYxLklkZW50aXR5VG9vbGtpdCIsImNsYWltcyI6eyJwcmVtaXVtQWNjb3VudCI6dHJ1ZX0sImV4cCI6MTYwNTgxMjE5OSwiaWF0IjoxNjA1ODA4NTk5LCJpc3MiOiJmaXJlYmFzZS1hZG1pbnNkay00ZTBmekBmaXItdXNlcnMtZDk2ODUuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJzdWIiOiJmaXJlYmFzZS1hZG1pbnNkay00ZTBmekBmaXItdXNlcnMtZDk2ODUuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJ1aWQiOiIxM2ZkYzkyZi00MTAzLTRmZDktYTU5MC0yMjNhN2JkOWU2ODcifQ.tswS-mz-T6dTFKIo-I-6P9FZhknR3IGYLyQIJbGq1JXfcXN-Y39u6hL_6ljTSUqsqIc-MxGdq8AtcWZa3gEAlekBzspwaQnR7deIe9b-cBmziDO2lWPLOcp3BkkVAKaty9wIge0OHycwrPpQWrokII3dP0P6mSQKCLwsrzHiL0eAEt5mKj_QhM28jrCGPKvZ2tBE6ZitJF7xTCOD5P76_0-bPTPAIeFM1_J7G0UPYBPAArscP2U5So4MRjz42TSujMtkIj7ZWdJB4Ei5JTMVk9tHTIgi5ctcGmuKa0aBPZKzNUKfy0mZesWTKHghBDDiJB-E2ZI-_lWzd8svWPj1GQ";
    public static LoginDataSource getInstance(){
        if (instance == null){
            instance = new LoginDataSource();
        }
        return instance;
    }

    /**
     * check authentication of user on database
     * The users won't create now. these are already exist
     * so, just fetch the whole user data
     * @param username
     * @return
     */
    public Result<LoggedInUser> login(final Activity activity, final String username, String password) {
        Log.i("LoginDataSource", "login executed");
        this.activity = activity;
        //TODO: finish the authentication
        //firebaseAuth(activity,password);
        //firebaseAuthCustom();
        //userID = UUID.randomUUID().toString();
        ////////////////////////
        mUser = new LoggedInUser(username);
        Log.i("LoginDataSource", "firebaseAuthCustom execute");
        firebaseAuthCustom();
                /*new LoggedInUser(
                        userID,
                        username, username.equals("admin")? ADMIN: TEAM_LEADER);*/
        ////////////////////////


        /*Map<String, Object> user = new HashMap<>();
        user.put("userId", java.util.UUID.randomUUID().toString());
        user.put("displayName", username);
        user.put("role", username.equals("admin")?Utility.Role.A: Utility.Role.C);*/
        //if (currentUser != null) {
            //Log.i("Firebase Firestore", "mAuth.getUid() is " + mAuth.getUid());


        /*db.collection("users")
                .document(mUser.getName())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        mUser.setUserId(documentSnapshot.getString("id"));
                        mUser.setRole(documentSnapshot.getString("role"));
                        if (username.contains("admin") || username.contains("Admin")) {
                            mUser.setLeaderOfTeam(documentSnapshot.get("leaderOfTeam"));
                        } else {

                        }

                        Utility.saveUserForSharedPref(mUser);
                        Log.i("LoginDataSource -> fetched user: ", mUser.toString());
                    }
                });*/


        /*
            // Add a new document with a generated ID
            db.collection("users")
                    .add(mUser)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.i("Firebase Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("Firebase Firestore", "Error adding document", e);
                        }
                    });
*/
        //}

        try {
            return new Result.Success<>(mUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }

    }



    private void firebaseAuthEmail(Activity activity, String password){
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Log.i("auth", "Current User is " + currentUser);
        mAuth.signInWithEmailAndPassword("golanimanage79@gmail.com", password+"55555").
                addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.i("auth", "Login successful: " + task.isSuccessful());
                        }else {
                            Log.i("auth", "Login Failed");
                        }
                    }
                });
        userID = mAuth.getUid();
    }


    //using custom authentication
    private void firebaseAuthCustom(){
        mAuth = FirebaseAuth.getInstance();
        Log.i("auth", "firebaseAuth2 execute");
        mAuth.signInWithCustomToken(Token)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d("auth", "signInWithCustomToken:success");
                        currentUser = mAuth.getCurrentUser();
                        Log.i("auth", "currentUser is " + currentUser);
                        Log.i("auth", "mAuth.getUid() is " + mAuth.getUid());
                        userID = mAuth.getUid();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("auth", "signInWithCustomToken failure. Exception: " + e.getMessage());
                    }
                });


    }

    public void logout() {
        // TODO: revoke authentication
        FirebaseAuth.getInstance().signOut();
    }

}

