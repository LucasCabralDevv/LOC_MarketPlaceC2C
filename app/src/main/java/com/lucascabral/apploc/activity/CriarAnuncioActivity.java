package com.lucascabral.apploc.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.lucascabral.apploc.R;
import com.santalu.maskara.Maskara;
import com.santalu.maskara.widget.MaskEditText;

import java.util.Locale;

public class CriarAnuncioActivity extends AppCompatActivity {

    private EditText camposTitulo, campoDescricao;
    private CurrencyEditText campoValor;
    private MaskEditText campoTelefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_anuncio);

        inicializarComponenetes();
    }

    public void salvarAnuncio(View view){

        String valor = campoValor.getText().toString();


        String telefone = campoTelefone.getMasked();
        Log.d("telefone", "Telefone anuncio: " + telefone);
    }

    private void inicializarComponenetes(){

        camposTitulo = findViewById(R.id.criarAnunTituloEdit);
        campoDescricao = findViewById(R.id.criarAnunDescricaoEdit);
        campoValor = findViewById(R.id.criarAnunValorEdit);
        campoTelefone = findViewById(R.id.criarAnunTelefoneEdit);

        //Configura a localidade para pt-BR
        Locale locale = new Locale("pt", "BR");
        campoValor.setLocale(locale);
    }
}