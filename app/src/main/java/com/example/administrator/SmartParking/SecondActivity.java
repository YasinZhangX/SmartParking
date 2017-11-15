package com.example.administrator.SmartParking;


import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;


public class SecondActivity extends AppCompatActivity {

    private TextView mTextMessage;
    MainActivity s=new MainActivity();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:{
                    mTextMessage.setText("hello");
                    return true;}
                //return true;
                case R.id.navigation_dashboard:{
                    mTextMessage.setText("hello!");
                    return true;}
                case R.id.navigation_notifications:{
                    mTextMessage.setText("hello!!");
                    return true;}
            }
            return false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_main);

        initControl();


    }






    void initControl() {

        mTextMessage = (TextView) findViewById(R.id.newword);
        mTextMessage.setText("导航服务");
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }



}

