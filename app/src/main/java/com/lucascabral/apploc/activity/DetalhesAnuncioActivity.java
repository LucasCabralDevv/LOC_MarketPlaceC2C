package com.lucascabral.apploc.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lucascabral.apploc.R;
import com.lucascabral.apploc.model.Anuncio;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class DetalhesAnuncioActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private TextView titulo;
    private TextView descricao;
    private TextView preco;
    private TextView regiao;
    private Anuncio anuncioSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_anuncio);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();

        // Recupera Anuncio
        anuncioSelecionado = (Anuncio) getIntent().getSerializableExtra("anuncioSelecionado");
        if(anuncioSelecionado != null){

            titulo.setText(anuncioSelecionado.getTitulo());
            descricao.setText(anuncioSelecionado.getDescricao());
            preco.setText(anuncioSelecionado.getValor());
            regiao.setText(anuncioSelecionado.getEstado());

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {

                    String urlString = anuncioSelecionado.getFotos().get(position);
                    Picasso.with(DetalhesAnuncioActivity.this).load(urlString).into(imageView);
                }
            };

            carouselView.setPageCount(anuncioSelecionado.getFotos().size());
            carouselView.setImageListener(imageListener);
        }
    }

    public void entrarEmContato(View view){

        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                anuncioSelecionado.getTelefone(), null));
        startActivity(i);
    }

    private void inicializarComponentes(){

        carouselView = findViewById(R.id.carouselView);
        titulo = findViewById(R.id.detalheTituloTextView);
        descricao = findViewById(R.id.detalheDescricaoTextView);
        preco = findViewById(R.id.detalhePrecoTextView);
        regiao = findViewById(R.id.detalheRegiaoTextView);

    }
}