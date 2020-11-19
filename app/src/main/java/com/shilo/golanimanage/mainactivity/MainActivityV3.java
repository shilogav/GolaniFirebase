package com.shilo.golanimanage.mainactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.shilo.golanimanage.R;
import com.shilo.golanimanage.mainactivity.fragments.SoldierListFragment;

/**
 * last version for main activities for now is V3
 */
public class MainActivityV3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_v3);

        loadFragment(new SoldierListFragment());
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.content_frame,fragment);
        fragmentTransaction.commit(); // save the changes
        Log.i("MainActivityV3", "fragmentTransaction.commit()");
    }
}