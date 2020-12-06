package com.shilo.golanimanage.login;

import android.app.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.shilo.golanimanage.R;
import com.shilo.golanimanage.mainactivity.MainActivity1;
import com.shilo.golanimanage.mainactivity.MainActivityV3;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.app_layout_label);
        Log.i("Login activity", "Login activity execute");
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);


        //check user status, if logged in straight to mainActivity
        checkLogin();
        /*
          the main activity can change the UI using observer for 'LoginFormState' state changes.

          called after 'LoginFormState' state changed

          update the UI
         */
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                Log.i(" my login firebaste", "getLoginFormState().observe called");
                if (loginFormState == null) {
                    Log.i(" my login firebaste", "loginFormState == null");
                    return;
                }
                loginButton.setVisibility(View.VISIBLE);
                loginButton.setEnabled(loginFormState.isDataValid());

                /////////
                //check if called error state in 'loginFormState' username\password,
                // and update the editText view to draw the error message
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
                ///////////
            }
        });

        /*
         * take the result of login and move to next stage
         */
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    Log.i(" my login firebaste", "loginResult == null");
                    return;
                }
                //loadingProgressBar.setVisibility(View.INVISIBLE);//maybe delete, can't understand for now why this is necessary
                Log.i(" my login firebaste", "ProgressBar-INVISIBLE");
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                    Log.i(" my login firebaste", "getError()");
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                    Log.i(" my login firebaste", "getSuccess()");
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                //finish();//shilo:guess I need to add intent here
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        /*
        //react to the keyboard enter. I don't want to use it for now


        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //Log.i(" on Editor Action", "onEditorAction execute");
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Log.i(" on Editor Action", "actionId == EditorInfo.IME_ACTION_DONE");



                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());


                }
                return false;
            }
        });*/

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.login(getOuter(), usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
                loadingProgressBar.setVisibility(View.VISIBLE);


                new Thread(){
                    @Override
                    public void run() {
                        try {
                            sleep(3000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingProgressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //finish();
                    }
                }.start();
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },3000);*/
            }
        });
    }

    public LoginActivity getOuter() {
        return LoginActivity.this;
    }

    private void checkLogin(){
        if (loginViewModel.isLoggedIn(this)){
            Intent intent = new Intent(this, MainActivityV3.class);
            startActivity(intent);
            finish();
        }
    }

    private void updateUiWithUser(LoggedInUserView model) {
        Intent intent = new Intent(this, MainActivityV3.class);
        startActivity(intent);
        finish();
    }

    /*private void goToMainActivity(User user) {
        Intent intent = new Intent(AuthActivity.this, MainActivity2.class);
        intent.putExtra(USER, user);
        startActivity(intent);
        finish();
    }*/

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}