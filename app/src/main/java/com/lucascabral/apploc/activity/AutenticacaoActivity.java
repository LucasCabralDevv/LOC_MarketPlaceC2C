package com.lucascabral.apploc.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.lucascabral.apploc.R;
import com.lucascabral.apploc.firebase.ConfiguracaoFirebase;

public class AutenticacaoActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button buttonAcessar;
    private SwitchMaterial switchTipoAcesso;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);

        inicializarComponentes();

        buttonAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if (!email.isEmpty()) {
                    if (!senha.isEmpty()) {

                        //Verifica estado do switch
                        if (switchTipoAcesso.isChecked()) { //Cadastro

                            fazerCadastro(email, senha);

                        } else { //Login

                            fazerLogin(email, senha);
                        }

                    } else {
                        exibirMensagem("Preencha a senha!");
                    }
                } else {
                    exibirMensagem("Preencha o e-mail!");
                }
            }
        });
    }

    private void fazerLogin(String email, String senha) {

        auth.signInWithEmailAndPassword(
                email, senha
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    exibirMensagem("Bem-vindo ao LOC");
                    startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));
                    finish();

                } else {

                    exibirMensagem("Erro ao fazer login: " + task.getException());
                }
            }
        });
    }

    private void fazerCadastro(String email, String senha) {

        auth.createUserWithEmailAndPassword(
                email, senha
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    exibirMensagem("Cadastro realizado com sucesso");
                    startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));


                } else {

                    // Criando tratamentos de exceções
                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        excecao = "Por favor, digite uma senha mais forte!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "Por favor, digite um e-mail válido!";
                    } catch (FirebaseAuthUserCollisionException e) {
                        excecao = "Esta conta já foi cadastrada";
                    } catch (Exception e) {
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    exibirMensagem("Erro: " + excecao);
                }
            }
        });
    }

    private void exibirMensagem(String texto) {

        Toast.makeText(AutenticacaoActivity.this,
                texto,
                Toast.LENGTH_LONG).show();
    }

    private void inicializarComponentes() {

        //Configuracoes firebase
        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();

        campoEmail = findViewById(R.id.authEmailEdit);
        campoSenha = findViewById(R.id.authSenhaEdit);
        buttonAcessar = findViewById(R.id.authAcessarButton);
        switchTipoAcesso = findViewById(R.id.authSwitchAcesso);
    }
}