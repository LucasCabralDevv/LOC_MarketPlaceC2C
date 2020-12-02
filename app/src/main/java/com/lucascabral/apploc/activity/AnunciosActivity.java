package com.lucascabral.apploc.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lucascabral.apploc.R;
import com.lucascabral.apploc.adapter.AdapterAnuncios;
import com.lucascabral.apploc.firebase.ConfiguracaoFirebase;
import com.lucascabral.apploc.helper.RecyclerItemClickListener;
import com.lucascabral.apploc.model.Anuncio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private RecyclerView recyclerAnuncios;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> anunciosPublicos = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;
    private AlertDialog dialog;
    private String filtroRegiao = "";
    private String filtroCategoria = "";
    private boolean filtrandoPorRegiao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        inicializarComponentes();

        configuraRecyclerView();

        recuperaAnunciosPublicos();

        //Evento de click
        recyclerAnuncios.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerAnuncios,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Anuncio anuncioSelecionado = anunciosPublicos.get(position);
                                Intent i = new Intent(AnunciosActivity.this, DetalhesAnuncioActivity.class);
                                i.putExtra("anuncioSelecionado", anuncioSelecionado);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                ));

    }

    public void filtrarPorRegiao(View view) {

        AlertDialog.Builder dialogRegiao = new AlertDialog.Builder(this);
        dialogRegiao.setTitle("Selecione a região desejada");

        //Configurar spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

        Spinner spinnerRegiao = viewSpinner.findViewById(R.id.anunciosFiltroSpinner);
        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapterRegiao = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, estados);
        adapterRegiao.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegiao.setAdapter(adapterRegiao);

        dialogRegiao.setView(viewSpinner);

        dialogRegiao.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                filtroRegiao = spinnerRegiao.getSelectedItem().toString();

                if (!filtroRegiao.equals("UF")) {
                    recuperarAnunciosPorRegiao();
                    filtrandoPorRegiao = true;
                } else {
                    Toast.makeText(AnunciosActivity.this,
                            "Escolha uma região!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        dialogRegiao.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = dialogRegiao.create();
        dialog.show();
    }

    public void filtrarPorCategoria(View view) {

        if (filtrandoPorRegiao == true) {

            AlertDialog.Builder dialogCategoria = new AlertDialog.Builder(this);
            dialogCategoria.setTitle("Selecione a categoria desejada");

            //Configurar spinner
            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

            Spinner spinnerCategoria = viewSpinner.findViewById(R.id.anunciosFiltroSpinner);
            String[] categorias = getResources().getStringArray(R.array.categorias);
            ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, categorias);
            adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategoria.setAdapter(adapterCategoria);

            dialogCategoria.setView(viewSpinner);

            dialogCategoria.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    filtroCategoria = spinnerCategoria.getSelectedItem().toString();

                    if (!filtroCategoria.equals("Categoria")) {
                        recuperarAnunciosPorCategoria();

                    } else {

                        Toast.makeText(AnunciosActivity.this,
                                "Escolha uma categoria!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            dialogCategoria.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dialogCategoria.create();
            dialog.show();

        } else {
            Toast.makeText(AnunciosActivity.this,
                    "Escolha primeiro uma região!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void recuperarAnunciosPorRegiao() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando Anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        //Configura nó por estado
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios")
                .child(filtroRegiao);
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                anunciosPublicos.clear();
                for (DataSnapshot categorias : snapshot.getChildren()) {
                    for (DataSnapshot anuncios : categorias.getChildren()) {

                        Anuncio anuncio = anuncios.getValue(Anuncio.class);
                        anunciosPublicos.add(anuncio);
                    }
                }
                Collections.reverse(anunciosPublicos);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperarAnunciosPorCategoria() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando Anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

        //Configura nó por categoria
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios")
                .child(filtroRegiao)
                .child(filtroCategoria);

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                anunciosPublicos.clear();
                for (DataSnapshot anuncios : snapshot.getChildren()) {

                    Anuncio anuncio = anuncios.getValue(Anuncio.class);
                    anunciosPublicos.add(anuncio);
                }

                Collections.reverse(anunciosPublicos);
                adapterAnuncios.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperaAnunciosPublicos() {

        anunciosPublicos.clear();
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot estados : snapshot.getChildren()) {
                    for (DataSnapshot categorias : estados.getChildren()) {
                        for (DataSnapshot anuncios : categorias.getChildren()) {

                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            anunciosPublicos.add(anuncio);
                        }
                    }
                }
                Collections.reverse(anunciosPublicos);
                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configuraRecyclerView() {

        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anunciosPublicos, this);
        recyclerAnuncios.setAdapter(adapterAnuncios);
    }

    private void inicializarComponentes() {

        //configurações iniciais
        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase().child("anuncios");

        recyclerAnuncios = findViewById(R.id.anunciosRecyclerView);
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