package com.shilo.myloginfirebase.data;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shilo.myloginfirebase.data.model.LoggedInUser;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

import static android.content.ContentValues.TAG;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private FirebaseAuth mAuth;
    FirebaseAuth firebaseAuth;

    public static final String Token = "ACCESS_AUTHORIZATION_BY_ME_SO_OPEN";

    /**
     * check authentication of user on database
     * @param username
     * @param password
     * @return
     */
    public Result<LoggedInUser> login(Activity activity, String username, String password) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("userId", java.util.UUID.randomUUID().toString());
        user.put("displayName", username);
        user.put("role", username.equals("admin")?Utility.Role.A: Utility.Role.C);

        // Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        try {
            // TODO: handle loggedInUser authentication
            LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            username, username.equals("admin")?Utility.Role.A: Utility.Role.C);
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }



        ///////////////////////////
        //firebase authentication
        /*
        //initializeFirebase();
        FirebaseApp.initializeApp(activity);
        //authImplicit();
        mAuth = FirebaseAuth.getInstance();
// Initiate sign in with custom token
        mAuth.signInWithCustomToken(Token).addOnCompleteListener( activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful()) {
                   Log.i("shilo data source"," task.isSuccessful()");
                   FirebaseUser user = mAuth.getCurrentUser();


               } else {
                   try {
                       Log.i(" shilodatasource"," task fail");
                       throw new Exception();
                   } catch (Exception e) {
                       e.printStackTrace();
                   }

               }
            }
        });
        /////////////////////
         */


    }

    private void authWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i("shilo data source"," task.isSuccessful()");

                }
                else{
                    Log.i("shilo data source"," task.isSuccessful()");
                }
            }
        });
    }

    /*private void initializeFirebase(){

        FirebaseOptions options = null;
        FileInputStream serviceAccount =
                null;
        try {
            serviceAccount = new FileInputStream("C:\\Users\\gavra\\Downloads\\service-account-file.json\n");


            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://fir-users-d9685.firebaseio.com")
                    .build();
        }
         catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    /*static void authImplicit() {
        // If you don't specify credentials when constructing the client, the client library will
        // look for credentials via the environment variable GOOGLE_APPLICATION_CREDENTIALS.
        Storage storage = StorageOptions.getDefaultInstance().getService();

        System.out.println("Buckets:");
        Page<Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll()) {
            System.out.println(bucket.toString());
        }
    }*/

    public void logout() {
        // TODO: revoke authentication
        FirebaseAuth.getInstance().signOut();
    }
}