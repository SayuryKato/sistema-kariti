package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class EditarTurmaActivity extends AppCompatActivity {
    ImageButton voltar;
    ImageView maisAn, menosAn;
    ListView listView;
    EditText editTurma, novosAlAnonimos;
    ArrayList<String> editAlTurma, alunosSpinner, selecionados;
    ArrayList<Integer> idsAlTurma;
    String id_turma, pegaTurma, alunosSelecionados;
    BancoDados bancoDados;
    AdapterExclAluno adapter;
    Spinner spinnerBuscAlun;
    Button salvar;
    Integer qtdAnonimos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_turma);

        listView = findViewById(R.id.listViewEditarTurma);
        editTurma = findViewById(R.id.editTextEditTurma);
        maisAn = findViewById(R.id.imageViewMaisNovosAnonimos);
        menosAn = findViewById(R.id.imageViewMenosNovosAnonimos);
        novosAlAnonimos = findViewById(R.id.editTextNovosAlunosAnonimos);
        voltar = findViewById(R.id.imgBtnVoltaDediturma);
        spinnerBuscAlun = findViewById(R.id.spinnerBuscAlunoNovos);
        salvar = findViewById(R.id.buttonSalvarTurma);
        bancoDados = new BancoDados(this);
        editAlTurma = new ArrayList<>();

        //Lista todos os alunos no Spinner
        alunosSpinner = (ArrayList<String>) bancoDados.obterNomesAlunos();
        alunosSpinner.add(0, "Selecione os Alunos");
        SpinnerAdapter adapterSpinner = new SpinnerAdapter(this, alunosSpinner);
        spinnerBuscAlun.setAdapter(adapterSpinner);
        spinnerBuscAlun.setSelection(0);

        //Mostra a turma a ser editada
        id_turma = getIntent().getExtras().getString("id_turma");
        pegaTurma = bancoDados.pegaNomeTurma(id_turma);
        qtdAnonimos = bancoDados.pegaqtdAnonimos(id_turma);
        editTurma.setText(pegaTurma);
        novosAlAnonimos.setText(qtdAnonimos.toString());

        //Lista os aluno cadastrados nesta turma.
        idsAlTurma = (ArrayList<Integer>) bancoDados.listAlunosDturma(id_turma);
        int num = idsAlTurma.size();
        for(int y = 0; y < num; y++){
            editAlTurma.add(bancoDados.pegaNomeAluno(String.valueOf(idsAlTurma.get(y))));
        }
        adapter = new AdapterExclAluno(this, editAlTurma);
        listView.setAdapter(adapter);

        //Identifica o aluno selecionado no Spinner e adiciona no listView
        spinnerBuscAlun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    alunosSelecionados = spinnerBuscAlun.getSelectedItem().toString();
                    int n = editAlTurma.size();
                    int x = 0;
                    if(n == 0)
                        editAlTurma.add(alunosSelecionados);
                    for(int a = 0; a < n; a++){
                        if(alunosSelecionados.equals(editAlTurma.get(a))) {
                            x = 1;
                            Toast.makeText(EditarTurmaActivity.this, "Aluno já selecionado!", Toast.LENGTH_SHORT).show();
                            break;
                        }else x = 2;

                    }
                    if (x == 2)
                        editAlTurma.add(alunosSelecionados);
                    adapter = new AdapterExclAluno(EditarTurmaActivity.this, editAlTurma);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Remove aluno do listView, após simples click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    editAlTurma.remove(i);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(EditarTurmaActivity.this, "Aluno Removido! ", Toast.LENGTH_SHORT).show();
            }
        });

        menosAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer menos = Integer.valueOf(novosAlAnonimos.getText().toString());
                if(menos > 0)
                    menos --;
                novosAlAnonimos.setText(menos.toString());
            }
        });

        maisAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer mais = Integer.valueOf(novosAlAnonimos.getText().toString());
                mais ++;
                novosAlAnonimos.setText(mais.toString());
            }
        });
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bancoDados.upadateTurma(editTurma.getText().toString(), Integer.valueOf(novosAlAnonimos.getText().toString()), Integer.valueOf(id_turma)); //Alterando Dados da turma
                bancoDados.deletarAlunoDturma(Integer.valueOf(id_turma));  //Deleta todos os alunos pertecentes a essa turma
                if(!editAlTurma.isEmpty()) {
                    int num = listView.getAdapter().getCount();
                    selecionados = (ArrayList<String>) editAlTurma;
                    for (int i = 0; i < num; i++) {
                        Integer id_aluno = bancoDados.pegaIdAluno(editAlTurma.get(i));
                        bancoDados.inserirAlunosNaTurma(Integer.valueOf(id_turma), id_aluno);
                    }
                }
                Toast.makeText(EditarTurmaActivity.this, "Dados Alterados!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    public void PopMenu(View v){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditarTurmaActivity.this, "Preparado para implementação", Toast.LENGTH_SHORT).show();
            }
        });
    }

}