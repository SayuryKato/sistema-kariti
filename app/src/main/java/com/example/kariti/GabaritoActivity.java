package com.example.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GabaritoActivity extends AppCompatActivity {
    TextView notaProva, nProva,nturma, ndata, txtTeste;
    Button cadProva;
    ImageButton voltar;
    BancoDados bancoDados;
    Map<String, Object> info;
    LinearLayout layoutHorizontal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gabarito);

        voltar = findViewById(R.id.imgBtnVoltaDescola);
        cadProva = findViewById(R.id.btnCadProva);
        nProva = findViewById(R.id.textViewProva);
        nturma = findViewById(R.id.textViewTurma);
        ndata = findViewById(R.id.textViewData);
        notaProva = findViewById(R.id.txtViewNotaProva);
        layoutHorizontal = findViewById(R.id.layoutHorizontalAlternat);

        bancoDados = new BancoDados(this);
        info = new HashMap<>();
        List<RadioGroup> listRadioGroups = new ArrayList<>();
        HashMap<Integer, Integer> alternativasEscolhidas = new HashMap<>();

        String prova = getIntent().getExtras().getString("nomeProva");
        String turma = getIntent().getExtras().getString("turma");
        String data = getIntent().getExtras().getString("data");
        Integer quest = getIntent().getExtras().getInt("quest");
        Integer alter = getIntent().getExtras().getInt("alter");
        nProva.setText(String.format("Prova: %s", prova));
        nturma.setText("Turma: "+turma);
        ndata.setText("Data: "+data);
        cadProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean respostaSelecionada = false;
                for (RadioGroup radioGroup : listRadioGroups) {
                    if (radioGroup.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(GabaritoActivity.this, "Por favor, selecione uma resposta para todas as questões.", Toast.LENGTH_SHORT).show();
                        respostaSelecionada = false;
                        break;
                    } else {
                        respostaSelecionada = true;
                    }
                }
                if (respostaSelecionada) {
                    Integer id_turma = bancoDados.pegaIdTurma(turma);
                    Boolean insProva = bancoDados.inserirProva(prova, data, quest, alter, id_turma);;
                    if(insProva) {
                        Integer id_prova = bancoDados.pegaIdProva(prova);
                        ArrayList<Integer> nPquest = (ArrayList<Integer>)info.get("notaQuest");
                        if(!nPquest.isEmpty() && !id_prova.equals(null)){
                            for(int i = 0; i < quest; i++){
                                Integer resp = alternativasEscolhidas.get(i);
                                bancoDados.inserirGabarito(id_prova, i+1, resp+1, nPquest.get(i));
                            }
                            dialogProvaSucess();
                        }
                    }
                }
            }
       });

       //sayury
        int quantidadeQuestoes = quest;
        int quantidadeAlternativas = alter;
        notaProva.setText("Nota total da prova " + quantidadeQuestoes + " pontos.");


        String[] letras = new String[quantidadeAlternativas];
        for (int i = 0; i < quantidadeAlternativas; i++) {
            char letra = (char)('A' + i);
            letras[i] = String.valueOf(letra);
        }

        //Questões e Radio
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < quantidadeQuestoes; i++) {
            LinearLayout layoutQuestao = new LinearLayout(this);
            layoutQuestao.setOrientation(LinearLayout.HORIZONTAL);

            TextView textViewNumeroQuestao = new TextView(this);
            textViewNumeroQuestao.setText((i + 1) + " ");
            layoutQuestao.addView(textViewNumeroQuestao);

            //Agrupar os RadioButtons
            RadioGroup radioGroupAlternativas = new RadioGroup(this);
            radioGroupAlternativas.setOrientation(LinearLayout.HORIZONTAL);
            listRadioGroups.add(radioGroupAlternativas);

            // Loop para criar Radio para as respostas
            for (int j = 0; j < quantidadeAlternativas; j++) {
                params.setMargins(0, 20, 20, 0);

                RadioButton radioAlternativa = new RadioButton(this);
                radioAlternativa.setLayoutParams(params);
                radioAlternativa.setText(letras[j]);
                radioGroupAlternativas.addView(radioAlternativa);
            }
            radioGroupAlternativas.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    for (int i = 0; i < listRadioGroups.size(); i++) {
                        if (listRadioGroups.get(i) == group) {
                            int positionDaQuestao = i;
                            int selectedRadioButtonId = group.getCheckedRadioButtonId();
                            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                            int position = group.indexOfChild(selectedRadioButton);
                            alternativasEscolhidas.put(positionDaQuestao, position);
                            break;
                        }
                    }
                }
            });
            layoutQuestao.addView(radioGroupAlternativas);

            LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(
                    150,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            EditText editTextPontos = new EditText(this);
            editTextPontos.setInputType(InputType.TYPE_CLASS_NUMBER);
            editTextPontos.setText(String.valueOf(1));
            editTextPontos.setGravity(Gravity.CENTER);
            editTextPontos.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            editTextPontos.setBackground(ContextCompat.getDrawable(this, R.drawable.borda_fina));
            paramsText.setMargins(5, 15, 0, 0);

            editTextPontos.setLayoutParams(paramsText);

            layoutQuestao.addView(editTextPontos);

            editTextPontos.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void afterTextChanged(Editable editable) {
                    calcularNotaTotal();
                }
            });

            layoutHorizontal.addView(layoutQuestao);
            calcularNotaTotal();

        }
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }
    public void dialogProvaSucess(){
        AlertDialog.Builder builder = new AlertDialog.Builder(GabaritoActivity.this);
        builder.setTitle("Prova cadastrada com sucesso!")
                .setMessage("Selecione uma das opções a seguir, para ter acesso aos Cartões Resposta.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        telaConfim();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void telaConfim() {
        Intent intent = new Intent(this, ProvaCartoesActivity.class);
        startActivity(intent);
    }
    private void calcularNotaTotal() {
        int notas = 0;
        ArrayList<Integer> nPquest = new ArrayList<>();
        info.put("notaQuest", nPquest);

        //modificado
        for (int j = 0; j < layoutHorizontal.getChildCount(); j++) {
            LinearLayout questaoLayout = (LinearLayout) layoutHorizontal.getChildAt(j);
            EditText pontosEditText = (EditText) questaoLayout.getChildAt(2);
            String nt = pontosEditText.getText().toString();
            if (!nt.isEmpty()) {
                Integer n = Integer.valueOf(nt);
                nPquest.add(n);
                notas += n;
            }
        }

        notaProva.setText("Nota total da prova " + notas + " pontos.");
    }
}
