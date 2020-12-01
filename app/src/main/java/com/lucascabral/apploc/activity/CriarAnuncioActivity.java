package com.lucascabral.apploc.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lucascabral.apploc.R;
import com.lucascabral.apploc.firebase.ConfiguracaoFirebase;
import com.lucascabral.apploc.helper.Permissoes;
import com.lucascabral.apploc.model.Anuncio;
import com.santalu.maskedittext.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class CriarAnuncioActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText campoTitulo, campoDescricao;
    private CurrencyEditText campoValor;
    private MaskEditText campoTelefone;
    private ImageView imagem1, imagem2, imagem3;
    private Spinner campoEstado, campoCategoria;

    private final String[] permissoes = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    private final List<String> listaFotosRecuperadas = new ArrayList<>();
    private final List<String> listaUrlFotos = new ArrayList<>();
    private Anuncio anuncio;
    private StorageReference storage;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_anuncio);

        //Validar Permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        inicializarComponenetes();

        carregarDadosSpinners();
    }

    public void salvarAnuncio() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Anúncio")
                .setCancelable(false)
                .build();
        dialog.show();

        // Salvar imagem no Storage
        for (int i = 0; i < listaFotosRecuperadas.size(); i++) {
            String urlImagem = listaFotosRecuperadas.get(i);
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotoStorage(urlImagem, tamanhoLista, i);
        }
    }

    private void salvarFotoStorage(String urlString, int totalFotos, int contador) {

        //Criar nó no storage
        final StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem" + contador);
        //Fazer o upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imagemAnuncio.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        Uri url = task.getResult();
                        String urlConvertida = url.toString();
                        listaUrlFotos.add(urlConvertida);

                        if (totalFotos == listaUrlFotos.size()){
                            anuncio.setFotos(listaUrlFotos);
                            anuncio.salvar();
                            dialog.dismiss();
                            exibirMensagem("Anúncio salvo com sucesso");
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                exibirMensagem("Falha ao fazer upload");
                Log.i("INFO", "Falha upload: " + e.getMessage());
            }
        });
    }

    private Anuncio configurarAnuncio() {

        String estado = campoEstado.getSelectedItem().toString();
        String categoria = campoCategoria.getSelectedItem().toString();
        String titulo = campoTitulo.getText().toString();
        String valor = campoValor.getText().toString();
        String telefone = campoTelefone.getText().toString();
        String descricao = campoDescricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setTelefone(telefone);
        anuncio.setDescricao(descricao);

        return anuncio;
    }

    public void validarDadosAnuncio(View view) {

        String fone = "";
        anuncio = configurarAnuncio();
        String valor = String.valueOf(campoValor.getRawValue());

        if (campoTelefone.getRawText() != null) {
            fone = campoTelefone.getRawText().toString();
        }

        if (listaFotosRecuperadas.size() != 0) {
            if (!anuncio.getEstado().equals("UF")) {
                if (!anuncio.getCategoria().equals("Categoria")) {
                    if (!anuncio.getTitulo().isEmpty()) {
                        if (!valor.isEmpty() && !valor.equals("0")) {
                            if (!anuncio.getTelefone().isEmpty() && fone.length() >= 10) {
                                if (!anuncio.getDescricao().isEmpty()) {

                                    salvarAnuncio();

                                } else {
                                    exibirMensagem("Por favor, preencha o campo descrição!");
                                }
                            } else {
                                exibirMensagem("Por favor, preencha o campo telefone, " +
                                        "digite ao menos 10 números!");
                            }
                        } else {
                            exibirMensagem("Por favor, preencha o campo valor!");
                        }
                    } else {
                        exibirMensagem("Por favor, preencha o campo título!");
                    }
                } else {
                    exibirMensagem("Por favor, selecione uma categoria!");
                }
            } else {
                exibirMensagem("Por favor, selecione um estado!");
            }
        } else {
            exibirMensagem("Por favor, selecione ao menos uma foto!");
        }
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

    private void carregarDadosSpinners() {

        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, estados
        );
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoEstado.setAdapter(adapterEstado);

        String[] categorias = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, categorias
        );
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCategoria.setAdapter(adapterCategoria);
    }

    private void inicializarComponenetes() {

        // Configuração firebase
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        campoTitulo = findViewById(R.id.criarAnunTituloEdit);
        campoDescricao = findViewById(R.id.criarAnunDescricaoEdit);
        campoValor = findViewById(R.id.criarAnunValorEdit);
        campoTelefone = findViewById(R.id.criarAnunTelefoneEdit);
        campoEstado = findViewById(R.id.spinnerEstado);
        campoCategoria = findViewById(R.id.spinnerCategoria);
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

    private void exibirMensagem(String texto) {

        Toast.makeText(CriarAnuncioActivity.this,
                texto, Toast.LENGTH_LONG).show();
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