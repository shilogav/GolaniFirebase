package com.shilo.golanimanage.login.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.shilo.golanimanage.model.LoggedInUser;

import androidx.appcompat.app.AppCompatActivity;

import static android.content.Context.MODE_PRIVATE;
import static com.shilo.golanimanage.Utility.toSharedPreferences;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {


    private LoginDataSource dataSource;
    private Context context;
    private FirebaseAuth mAuth;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    public LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }



    public boolean isLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("UserData", MODE_PRIVATE);
        return !(prefs.getString("user", "")).equals("");
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore

        SharedPreferences prefs = context.getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", user.getName());
        toSharedPreferences(editor,user);
    }

    public Result<LoggedInUser> login(AppCompatActivity activity, String username, String password) {
        context = activity;
        // handle login
        Result<LoggedInUser> result = dataSource.login(activity, username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }



}