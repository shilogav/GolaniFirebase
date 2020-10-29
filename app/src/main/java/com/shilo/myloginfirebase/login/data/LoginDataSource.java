package com.shilo.myloginfirebase.login.data;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shilo.myloginfirebase.Utility;
import com.shilo.myloginfirebase.model.LoggedInUser;

import java.io.IOException;
import java.util.UUID;

import androidx.annotation.NonNull;

import static android.content.ContentValues.TAG;
import static com.shilo.myloginfirebase.model.LoggedInUser.ADMIN;
import static com.shilo.myloginfirebase.model.LoggedInUser.TEAM_LEADER;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private  static LoginDataSource instance;
    private FirebaseAuth mAuth;
    private String userID;
    private FirebaseUser currentUser;
    private String mCustomToken;

    public static final String Token = "eyJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJodHRwczovL2lkZW50aXR5dG9vbGtpdC5nb29nbGVhcGlzLmNvbS9nb29nbGUuaWRlbnRpdHkuaWRlbnRpdHl0b29sa2l0LnYxLklkZW50aXR5VG9vbGtpdCIsImNsYWltcyI6eyJwcmVtaXVtQWNjb3VudCI6dHJ1ZX0sImV4cCI6MTYwMzI4MzMwMCwiaWF0IjoxNjAzMjc5NzAwLCJpc3MiOiJmaXJlYmFzZS1hZG1pbnNkay00ZTBmekBmaXItdXNlcnMtZDk2ODUuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJzdWIiOiJmaXJlYmFzZS1hZG1pbnNkay00ZTBmekBmaXItdXNlcnMtZDk2ODUuaWFtLmdzZXJ2aWNlYWNjb3VudC5jb20iLCJ1aWQiOiJhZWFmMDQ3Zi1kYTJlLTQwMGYtYWNiMS04ZDdmODg3Mzc3ZDMifQ.RM8Bg4ZPXjdw7j_yiN-fs4-HYGohJ8n1lKTROsusolW4h_3D0ZtDn6AP-YpBKjX-1_Bv2tclaEdprT3ZiRRqkqPOZW1PlnFhwV2LL5O2szlz9kneEqpN9knPLWl44KfaNlu1CBENjHeNbPhEuRdWMd4fIm0GSJEwq60wbHP4Pnb-z8sfAinwzIKg2KLTefH_l8COY9PuK4xSV_O03T8qmw4nGr5_yWGourzPRhJgmbJqbhbogFHqnWS1SGdnZw2VWEnH7T3woGA9iMtT6qmlPWfu5Mp3JRr6Xl2rnj36sF4_4Fzka9GmvjYB6u-z9I27kicZ_awT-EFdM9nziMTHlA";
    public static LoginDataSource getInstance(){
        if (instance == null){
            instance = new LoginDataSource();
        }
        return instance;
    }

    /**
     * check authentication of user on database
     * @param username
     * @return
     */
    public Result<LoggedInUser> login(Activity activity, String username, String password) {
        //TODO: finish the authentication
        //firebaseAuth(activity,password);
        //firebaseAuthCustom();
        userID = UUID.randomUUID().toString();
        ////////////////////////
        LoggedInUser mUser =
                new LoggedInUser(
                        userID,
                        username, username.equals("admin")? ADMIN: TEAM_LEADER);
        ////////////////////////

        //firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        /*Map<String, Object> user = new HashMap<>();
        user.put("userId", java.util.UUID.randomUUID().toString());
        user.put("displayName", username);
        user.put("role", username.equals("admin")?Utility.Role.A: Utility.Role.C);*/
        //if (currentUser != null) {
            //Log.i("Firebase Firestore", "mAuth.getUid() is " + mAuth.getUid());
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
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("auth", "signInWithCustomToken failure. Exception: " + e.getMessage());
                    }
                });

        currentUser = mAuth.getCurrentUser();
        Log.i("auth", "mAuth.getUid() is " + mAuth.getUid());
        userID = mAuth.getUid();
    }

    public void logout() {
        // TODO: revoke authentication
        FirebaseAuth.getInstance().signOut();
    }

}

