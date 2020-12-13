package com.shilo.golanimanage.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.util.Log;

import com.shilo.golanimanage.login.data.LoginDataSource;
import com.shilo.golanimanage.login.data.LoginRepository;
import com.shilo.golanimanage.login.data.Result;
import com.shilo.golanimanage.model.LoggedInUser;
import com.shilo.golanimanage.R;

import java.util.HashMap;


public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    HashMap<String,Integer> usersAndPassword = setUserAndPasswordMap();

    public LoginViewModel() {
        this.loginRepository = new LoginRepository(LoginDataSource.getInstance());
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public boolean isLoggedIn(Context context){
        return loginRepository.isLoggedIn(context);
    }


    /**
     * handle user log\register to\from database
     * @param activity for shared preferences
     * @param username string to send to database
     * @param password string to send to database
     */
    public void login(AppCompatActivity activity, String username, String password) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.login(activity, username, password);
        Log.i("LoginViewModel", "login executed");


        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }


    /////////////////////////////
    //this block handle user\password typed in UI


    /**
     * handle user\password validation typed in UI
     *
     * check the validness of user and password and write the message to show
     * called by text watcher
     * @param username string to with typed by user
     * @param password string to with typed by user
     */
    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password,username)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        //ArrayList<String> users = setLegitimateUsers();
        if (username == null) {
            return false;
        }
        if (!usersAndPassword.containsKey(username)) {
            Log.i(" my login firebaste", "is contain:  "+ usersAndPassword.containsKey(username) + ". working!");
            return false;
            //return Patterns.EMAIL_ADDRESS.matcher(username).matches();

        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password,String username) {
        int mPassword;
        if (password.isEmpty()){
            return false;
        }
        mPassword = Integer.parseInt(password);
        return usersAndPassword.get(username) == null || mPassword == usersAndPassword.get(username);
    }

    //TODO: encrypt data
    private HashMap <String,Integer> setUserAndPasswordMap() {
        return new HashMap<String, Integer>() {
            {
                put("Admin",555555);
                put("admin1",561684);
                put("admin2",979791);
                put("admin3",138848);
                put("admin4",684651);
                put("admin5",285648);
                put("admin6",461568);
                put("admin7",256668);
                put("admin8",368124);
                put("admin9",258787);
                put("admin10",786123);

                put("user1",5284759);
                put("user2",801553);
                put("user3", 6584818);
                put("user4", 5465844);
                put("user5",976435);
                put("user6", 354897);
                put("user7", 286475);
                put("user8", 286331);
                put("user9", 266465);
                put("user10", 354897);
                put("user11", 885473);
                put("user12", 436485);
                put("user13", 178615);
                put("user14", 354897);
                put("user15", 286475);
                put("user16", 325124);
                put("user17", 205868);
                put("user18", 795416);
                put("user19", 846887);
                put("user20", 423061);
                put("user21", 389734);
                put("user22", 897725);
                put("user23", 425897);
                put("user24", 746545);
                put("user25", 267836);
                put("user26", 3546756);
                put("user27", 547443);
                put("user28", 954788);
                put("user29", 457844);
                put("user30", 112775);
                put("user31", 905200);
                put("user32", 857320);
                put("user33", 444613);
                put("user34", 987065);
                put("user35", 568753);
                put("user36", 663546);


                put("user37", 5);
            }
        };
    }





}