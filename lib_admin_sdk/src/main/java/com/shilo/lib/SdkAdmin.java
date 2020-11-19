package com.shilo.lib;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class SdkAdmin {
    public static final String UID = UUID.randomUUID().toString();
    public static void main(String[] args) throws IOException
            , ExecutionException, InterruptedException, FirebaseAuthException {
        FileInputStream serviceAccount = new FileInputStream("\u202AI:\\Projects\\service acoount key- Golani Manage\\ServiceAccountKey.json");

        //using json service account
        /*FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://fir-users-d9685.firebaseio.com")
                .build();*/

        FirebaseApp.initializeApp();

        ////////
        //using json service account
        //FirebaseApp.initializeApp(options);
        ///////

        Map<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("premiumAccount", true);

        ////////
        //using json service account
        //String customToken = FirebaseAuth.getInstance().createCustomTokenAsync(UID, additionalClaims).get();
        ///////
        String customToken = FirebaseAuth.getInstance().createCustomToken(UID,additionalClaims);

        System.out.println("the custom token is " + customToken);
    }
}