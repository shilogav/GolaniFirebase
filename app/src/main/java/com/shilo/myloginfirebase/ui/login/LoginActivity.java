package com.shilo.myloginfirebase.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shilo.myloginfirebase.MainActivity;
import com.shilo.myloginfirebase.R;
import com.shilo.myloginfirebase.ui.login.LoginViewModel;
import com.shilo.myloginfirebase.ui.login.LoginViewModelFactory;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

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

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);



        //Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }


    //TODO: send the UI to activity that create new user
    private void addCreateActivityUser()
    {
        Log.i("shilo","get into addCreateActivityUser-for first time");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivityForResult(intent,1);
            }
        }).start();
    }

    /*private void goToMainActivity(User user) {
        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
        finish();
    }*/

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}