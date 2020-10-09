package com.example.tracepointplugin;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MainAppComActivity extends AppCompatActivity{

    Fragment fragment =  new HFragmentAndroidX();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_androidx);

        findViewById(R.id.text).setOnClickListener(v->{
            startActivity(new Intent(MainAppComActivity.this, MainActivity.class));
        });

        initView();
    }

    private void initView() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container,fragment);
        fragmentTransaction.commit();
    }

}
