package com.shilo.golanimanage.notification;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.shilo.golanimanage.R;

public class NotificationView extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);
        //mTextView = (TextView) findViewById(R.id.text);
    }
}