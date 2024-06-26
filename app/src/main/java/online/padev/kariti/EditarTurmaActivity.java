package online.padev.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
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

import online.padev.kariti.R;

import java.util.ArrayList;

public class EditarTurmaActivity extends AppCompatActivity {
    ImageButton voltar, iconHelpEditTurma;
    ImageView maisAn, menosAn;
    ListView listView;
    EditText editTurma, novosAlAnonimos;
    ArrayList<String> editAlTurma, alunosSpinner, selecionados, anonimos;
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
        spinnerBuscAlun = findViewById(R.id.spinnerBuscAlunoNovos);
        salvar = findViewById(R.id.buttonSalvarTurma);
        bancoDados = new BancoDados(this);
        editAlTurma = new ArrayList<>();
        voltar = findViewById(R.id.imgBtnVoltaDescola);
        iconHelpEditTurma = findViewById(R.id.iconHelp);

        iconHelpEditTurma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelpDetalhes();
            }
        });

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

        informAnonimos(qtdAnonimos);

        //Lista os aluno cadastrados nesta turma.
        idsAlTurma = (ArrayList<Integer>) bancoDados.listAlunosDturma(id_turma);
        int num = idsAlTurma.size();
        for(int y = 0; y < num; y++){
            String aluno = bancoDados.pegaNomeAluno(String.valueOf(idsAlTurma.get(y)));
            if(aluno != null){
                editAlTurma.add(aluno);
            }
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
                String turmaedit = editTurma.getText().toString();
                if(!turmaedit.isEmpty()) {
                    Integer an = Integer.valueOf(novosAlAnonimos.getText().toString());
                    bancoDados.upadateTurma(turmaedit, an, Integer.valueOf(id_turma)); //Alterando Dados da turma
                    bancoDados.deletarAlunoDturma(Integer.valueOf(id_turma));  //Deleta todos os alunos pertecentes a essa turma
                    for (int n = 0; n < idsAlTurma.size(); n++) {
                        bancoDados.deletaAnonimos(idsAlTurma.get(n));//Deleta todos os alunos Anonimos pertecentes a essa turma
                    }
                    if (!editAlTurma.isEmpty()) {
                        int num = listView.getAdapter().getCount();
                        selecionados = (ArrayList<String>) editAlTurma;
                        for (int i = 0; i < num; i++) {
                            Integer id_aluno = bancoDados.pegaIdAluno(editAlTurma.get(i));
                            bancoDados.inserirAlunosNaTurma(Integer.valueOf(id_turma), id_aluno);
                        }
                    }
                    if (!an.equals(0)) {
                        anonimos = new ArrayList<>();
                        for (int x = 1; x <= an; x++) {
                            String anonimo = "Aluno " + turmaedit + " " + x;
                            anonimos.add(anonimo);
                            bancoDados.inserirDadosAluno(anonimo, null, 0);
                        }
                        for (int a = 0; a < anonimos.size(); a++) {
                            Integer idAnonimo = bancoDados.pegaIdAnonimo(anonimos.get(a));
                            bancoDados.inserirAlunosNaTurma(Integer.valueOf(id_turma), idAnonimo);
                        }
                    }
                    Toast.makeText(EditarTurmaActivity.this, "Dados Alterados!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), DadosTurmaActivity.class);
                    intent.putExtra("idTurma", Integer.valueOf(id_turma));
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(EditarTurmaActivity.this, "Informe o nome da turma!", Toast.LENGTH_SHORT).show();
                }
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
    public void informAnonimos(Integer anonimos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("KARITI");
        builder.setMessage("Esta turma possui "+anonimos+" alunos anônimos cadastrados, caso deseje alterar essa quantidade, basta informar um novo valor no campo referente 'Incluir Alunos Anônimos'");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void dialogHelpDetalhes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Nesta tela os dados da turma podem sem alterados, seguindo o mesmo padrão de cadastro de turma.\n\n" +
                "1 - Nome: caso deseje alterar o nome, basta informar um novo\n\n" +
                "2 - Alunos: podem ser incluídos novos alunos para essa turma selecionando-os no campo 'Selecione os Alunos', os quais antecipadamente já devem estar cadastrados no KARITI na tela de cadastro de alunos. Caso deseje remover, basta clicar no nome do aluno para remove-lo da turma. \n\n" +
                "3 - Anônimos: caso não deseje cadastrar alunos para essa turma, podem ser incluidos alunos anônimos no campo 'Incluir Alunos Anônimos', informando a quantidade no campo sugerido, sem a necessidade de cadastrar todos ou nenhum aluno como descrito na opção 2. \n\n" +
                "Obs. A Turma não pode ser cadastrada sem alunos.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}