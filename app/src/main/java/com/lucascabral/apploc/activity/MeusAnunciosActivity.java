package com.lucascabral.apploc.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucascabral.apploc.R;
import com.lucascabral.apploc.adapter.AdapterMeusAnuncios;
import com.lucascabral.apploc.firebase.ConfiguracaoFirebase;
import com.lucascabral.apploc.model.Anuncio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MeusAnunciosActivity extends AppCompatActivity {

    private RecyclerView recyclerMeusAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AdapterMeusAnuncios adapterMeusAnuncios;
    private DatabaseReference anunciosUsuarioRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        inicializarComponentes();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), CriarAnuncioActivity.class));
            }
        });

        // config Recycler
        recyclerMeusAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerMeusAnuncios.setHasFixedSize(true);
        adapterMeusAnuncios = new AdapterMeusAnuncios(anuncios, this);
        recyclerMeusAnuncios.setAdapter(adapterMeusAnuncios);

        recuperaAnuncios();
    }

    private void recuperaAnuncios(){

        anunciosUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                anuncios.clear();
                for (DataSnapshot ds: snapshot.getChildren()){

                    anuncios.add(ds.getValue(Anuncio.class));
                }
                Collections.reverse(anuncios);
                adapterMeusAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarComponentes(){

        //Configurações iniciais
        anunciosUsuarioRef = ConfiguracaoFirebase.getFirebase().child("meus_anuncios")
                .child(ConfiguracaoFirebase.getIdUsuario());

        recyclerMeusAnuncios = findViewById(R.id.meusAnunciosRecycler);
    }
}