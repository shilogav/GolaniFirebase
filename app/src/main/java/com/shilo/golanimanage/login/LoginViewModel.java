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
                put("ch",5);
                put("Admin",564215);
                put("Shalom",801553);
                put("Shaul", 6584818);
                put("Ron", 5465844);
                put("Yair",976435);
                put("David", 354897);
                put("Efraim", 286475);
            }
        };
    }





}