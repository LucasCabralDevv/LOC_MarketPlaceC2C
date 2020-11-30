package com.lucascabral.apploc.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.lucascabral.apploc.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        abrirAutenticacao();
                    }
                }, 3000);
    }

    private void abrirAutenticacao(){

        Intent intent = new Intent(SplashActivity.this, AnunciosActivity.class);
        startActivity(intent);
        finish();
    }
}