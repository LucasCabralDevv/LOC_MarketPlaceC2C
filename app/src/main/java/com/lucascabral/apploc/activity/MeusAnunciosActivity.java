package com.lucascabral.apploc.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucascabral.apploc.R;
import com.lucascabral.apploc.adapter.AdapterMeusAnuncios;
import com.lucascabral.apploc.firebase.ConfiguracaoFirebase;
import com.lucascabral.apploc.helper.RecyclerItemClickListener;
import com.lucascabral.apploc.model.Anuncio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MeusAnunciosActivity extends AppCompatActivity {

    private RecyclerView recyclerMeusAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    private AdapterMeusAnuncios adapterMeusAnuncios;
    private DatabaseReference anunciosUsuarioRef;
    private AlertDialog dialog;

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

        ConfiguraRecyclerView();

        recuperaAnuncios();

        // Evento de click
        recyclerMeusAnuncios.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerMeusAnuncios,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                excluirAnuncio(position);
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
    }

    private void excluirAnuncio(int position) {

        Anuncio anuncioSelecionado = anuncios.get(position);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MeusAnunciosActivity.this);

        alertDialog.setTitle("Excluir produto");
        alertDialog.setMessage("Você tem certeza que deseja excluir: "
                + anuncioSelecionado.getTitulo() + " ?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(MeusAnunciosActivity.this,
                        anuncioSelecionado.getTitulo() + " excluído",
                        Toast.LENGTH_SHORT).show();

                anuncioSelecionado.excluirAnuncio();

                adapterMeusAnuncios.notifyItemRemoved(position);
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(MeusAnunciosActivity.this,
                        "Cancelado",
                        Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    private void ConfiguraRecyclerView() {
        recyclerMeusAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerMeusAnuncios.setHasFixedSize(true);
        adapterMeusAnuncios = new AdapterMeusAnuncios(anuncios, this);
        recyclerMeusAnuncios.setAdapter(adapterMeusAnuncios);
    }

    private void recuperaAnuncios() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando Anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        anunciosUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                anuncios.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    anuncios.add(ds.getValue(Anuncio.class));
                }
                Collections.reverse(anuncios);
                adapterMeusAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarComponentes() {

        //Configurações iniciais
        anunciosUsuarioRef = ConfiguracaoFirebase.getFirebase().child("meus_anuncios")
                .child(ConfiguracaoFirebase.getIdUsuario());

        recyclerMeusAnuncios = findViewById(R.id.meusAnunciosRecycler);
    }
}