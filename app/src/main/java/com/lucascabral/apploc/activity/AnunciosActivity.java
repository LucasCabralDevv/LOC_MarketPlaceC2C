package com.lucascabral.apploc.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.lucascabral.apploc.R;
import com.lucascabral.apploc.firebase.ConfiguracaoFirebase;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAutenticacao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (auth.getCurrentUser() == null) { // Usuário deslogado
            menu.setGroupVisible(R.id.group_deslogado, true);

        } else { // Usuário Logado
            menu.setGroupVisible(R.id.group_logado, true);

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_logarCadastrar:
                startActivity(new Intent(getApplicationContext(), AutenticacaoActivity.class));
                break;
            case R.id.menu_sair:
                auth.signOut();
                invalidateOptionsMenu();
                Toast.makeText(AnunciosActivity.this,
                        "Usuário deslogado",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_anuncios:
                startActivity(new Intent(getApplicationContext(), MeusAnunciosActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}