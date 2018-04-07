package com.knowledge.mobile_lab1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View view){
        Intent intent = new Intent(this, Main4Activity.class);
        startActivity(intent);
    }

    public void signUp(View view){
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }
}
