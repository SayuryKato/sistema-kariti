package com.example.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;

import android.widget.ImageButton;
public class CadEscolaActivity extends AppCompatActivity {
    ImageButton btnVoltar, btnHome;
    private Toolbar toolbar;
    EditText nomeEscola, bairr0;
    Button cadastrarEscola;
    BancoDados bancoDados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_escola);

        nomeEscola = findViewById(R.id.editTextNomeEscola);
        bairr0 = findViewById(R.id.editTextBairro);
        cadastrarEscola = findViewById(R.id.button);

        bancoDados = new BancoDados(this); // --> Conexão com o banco de dados.
        cadastrarEscola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = nomeEscola.getText().toString();
                String bairro = bairr0.getText().toString();

                if (nome.equals("") || bairro.equals("")) {
                    Toast.makeText(CadEscolaActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean checkEscola = bancoDados.checkEscola(nome);
                    Integer checkScolDesativada = bancoDados.checkEscolaDesativada(nome);
                    if (!checkEscola) {
                        if (checkScolDesativada==null) {
                            Boolean insertEscola = bancoDados.inserirDadosEscola(nome, bairro);
                            if (insertEscola) {
                                Toast.makeText(CadEscolaActivity.this, "Escola cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(CadEscolaActivity.this, "Falha no cadastro da escola!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(CadEscolaActivity.this);
                            builder.setTitle("Atenção!")
                                    .setMessage("Verificamos que esta escola encontra-se desativada em nosso sistema. Deseja reativa-la?")
                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Boolean deletaEscola = bancoDados.deletarEscola(checkScolDesativada);
                                            Boolean reativa = bancoDados.inserirDadosEscola(nome, bairro);
                                            if (deletaEscola!=false && reativa!=false) {
                                                finish();
                                            }else
                                                Toast.makeText(CadEscolaActivity.this, "Erro de ativação!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // cancelou
                                        }
                                    });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    } else {
                        Toast.makeText(CadEscolaActivity.this, "Escola já cadastrada!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnVoltar = findViewById(R.id.imgBtnVoltar);
        btnVoltar.setVisibility(View.VISIBLE);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    public void voltarTelaIncial(){
        BancoDados.USER_ID = null;
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);

        nomeEscola = findViewById(R.id.editTextNomeEscola);
        bairr0 = findViewById(R.id.editTextBairro);
        cadastrarEscola = findViewById(R.id.buttonSalvarEdit);

        cadastrarEscola.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            }
        });
    }
}