package com.lucascabral.apploc.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.lucascabral.apploc.R;
import com.lucascabral.apploc.helper.Permissoes;
import com.santalu.maskara.widget.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CriarAnuncioActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText camposTitulo, campoDescricao;
    private CurrencyEditText campoValor;
    private MaskEditText campoTelefone;
    private ImageView imagem1, imagem2, imagem3;

    private String[] permissoes = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    private List<String> listaFotosRecuperadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_anuncio);

        //Validar Permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        inicializarComponenetes();
    }

    public void salvarAnuncio(View view) {

        String valor = campoValor.getText().toString();

        String telefone = campoTelefone.getMasked();
        Log.d("telefone", "Telefone anuncio: " + telefone);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.criarAnunImageView1:
                escolherImagem(1);
                break;
            case R.id.criarAnunImageView2:
                escolherImagem(2);
                break;
            case R.id.criarAnunImageView3:
                escolherImagem(3);
                break;
        }
    }

    public void escolherImagem(int requestCode) {

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            //Recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //Configura imagem no ImageView
            if (requestCode == 1) {
                imagem1.setImageURI(imagemSelecionada);
            } else if (requestCode == 2) {
                imagem2.setImageURI(imagemSelecionada);
            } else if (requestCode == 3) {
                imagem3.setImageURI(imagemSelecionada);
            }
            listaFotosRecuperadas.add(caminhoImagem);
        }
    }

    private void inicializarComponenetes() {

        camposTitulo = findViewById(R.id.criarAnunTituloEdit);
        campoDescricao = findViewById(R.id.criarAnunDescricaoEdit);
        campoValor = findViewById(R.id.criarAnunValorEdit);
        campoTelefone = findViewById(R.id.criarAnunTelefoneEdit);
        imagem1 = findViewById(R.id.criarAnunImageView1);
        imagem2 = findViewById(R.id.criarAnunImageView2);
        imagem3 = findViewById(R.id.criarAnunImageView3);
        imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);

        //Configura a localidade para pt-BR
        Locale locale = new Locale("pt", "BR");
        campoValor.setLocale(locale);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertValidacaoPermissao();
            }
        }
    }

    private void alertValidacaoPermissao() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}