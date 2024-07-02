package online.padev.kariti;

import static android.os.Environment.getExternalStoragePublicDirectory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import online.padev.kariti.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class VisualProvaCorrigidaActivity extends AppCompatActivity {
    ImageButton voltar;
    Button btnBaixar;
    ArrayList<String> listAlunos, respostasDadas, respostasEsperadas, notasQuestoes;
    ArrayList<Integer> listIdsAlunos, listQuestoes;
    BancoDados bancoDados;
    Integer id_prova;
    String prova, turma;
    TextView provaResult;
    List<String[]> dadosProvaCorrigida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_prova_corrigida);

        voltar = findViewById(R.id.imgBtnVoltar);
        btnBaixar = findViewById(R.id.buttonBaixarResultado);
        provaResult = findViewById(R.id.textViewProvaResult);
        bancoDados = new BancoDados(this);
        listAlunos = new ArrayList<>();

        prova = Objects.requireNonNull(getIntent().getExtras()).getString("prova");
        id_prova = getIntent().getExtras().getInt("id_prova");
        turma = Objects.requireNonNull(getIntent().getExtras()).getString("turma");
        provaResult.setText(prova);
        listIdsAlunos = (ArrayList<Integer>) bancoDados.listAluno(id_prova);
        for(int x = 0; x < listIdsAlunos.size(); x++) {
            float nota = 0;
            int acertos = 0;
            Integer id_aluno = listIdsAlunos.get(x);
            listQuestoes = (ArrayList<Integer>) bancoDados.listQuestoes(id_prova, id_aluno);
            for(int i = 0; i < listQuestoes.size(); i++){
                Integer questao = listQuestoes.get(i);
                Integer respostaDada = bancoDados.pegaRespostaDada(id_prova, id_aluno, questao);
                Integer respostaGabarito = bancoDados.pegaRespostaQuestao(id_prova, questao);
                if(respostaDada.equals(respostaGabarito)){
                    nota += bancoDados.pegaNotaQuestao(id_prova, questao);
                    acertos += 1;
                }
            }

            TableLayout tableLayout = findViewById(R.id.tableLayout);
            TableRow row = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);

            // Cria uma célula para a nova linha para armazenar nome do aluno
            TextView cell1 = new TextView(this);
            cell1.setText(bancoDados.pegaAlunoProvaCorrigida(id_aluno.toString()));
            cell1.setGravity(Gravity.CENTER);
            row.addView(cell1);

            // Cria outra célula para a nova linha para armazenar o total de acertos do aluno na prova
            TextView cell2 = new TextView(this);
            cell2.setText(String.valueOf(acertos));
            cell2.setGravity(Gravity.CENTER);
            row.addView(cell2);

            // Cria outra célula para a nova linha para armazenar a nota total do aluno
            TextView cell3 = new TextView(this);
            cell3.setText(String.valueOf(nota));
            cell3.setGravity(Gravity.CENTER);
            row.addView(cell3);

            // Cria outra célula para a nova linha com botão para exibir detalhamento da nota do aluno
            TextView cell4 = new TextView(this);
            cell4.setText("detalhes");
            cell4.setGravity(Gravity.CENTER);
            row.addView(cell4);

            // Adiciona a nova linha à tabela
            tableLayout.addView(row);
        }
        carregaGabarito();
        btnBaixar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dadosProvaCorrigida = new ArrayList<>();
                String prof = bancoDados.pegaUsuario(BancoDados.USER_ID.toString());
                Integer qtdQuestoes = bancoDados.pegaqtdQuestoes(id_prova.toString());
                String qtdAlternativas = String.valueOf(bancoDados.pegaqtdAlternativas(id_prova.toString()));
                String nota = String.valueOf(bancoDados.listNota(id_prova.toString()));
                String dataProva = bancoDados.pegaData(id_prova.toString());
                for(int id_aluno: listIdsAlunos) {
                    String nomeAluno = bancoDados.pegaAlunoProvaCorrigida(String.valueOf(id_aluno));
                    String respostasDadas = bancoDados.listRespostasAluno(id_prova.toString(), String.valueOf(id_aluno));
                    //respostasDadas = respostasDadas.replaceAll("(?<=\\d)(?=\\d)", ",");
                    String respostasEsperadas = bancoDados.listRespostasGabarito(id_prova.toString());
                    respostasEsperadas = respostasEsperadas.replaceAll("(?<=\\d)(?=\\d)", ",");
                    String notasQuestoes = bancoDados.listNotaQuestao(id_prova.toString());
                    notasQuestoes = notasQuestoes.replaceAll("(?<=\\d)(?=\\d)", ",");
                    dadosProvaCorrigida.add(new String[]{id_prova.toString(), prova, prof, turma, dataProva, qtdQuestoes.toString(), qtdAlternativas, nota, respostasDadas, respostasEsperadas, String.valueOf(id_aluno), nomeAluno, notasQuestoes});
                }
                //File filecsv = null;
                //filecsv = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "dadosCorrecao.csv");
                try {
                    File filecsv  = new File(getExternalFilesDir(null), "/dadosCorrecao.csv");
                    String dateCart = new SimpleDateFormat(" HH_mm_ss").format(new Date());
                    String filePdf = "Corrigida_" + prova + dateCart + ".pdf";
                    GerarCsv.gerar(dadosProvaCorrigida, filecsv);// Gerando e salvando arquivo.csv
                    File fSaida = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filePdf);
                    BaixarResultadoCorrecao.solicitarResultadoCorrecao(filecsv, new FileOutputStream(fSaida), fSaida, filePdf, (DownloadManager) getSystemService(DOWNLOAD_SERVICE));
                    AlertDialog.Builder builder = new AlertDialog.Builder(VisualProvaCorrigidaActivity.this);
                    builder.setTitle("Por favor, Aguarde!")
                            .setMessage("Download em execução. Você será notificado quando o arquivo estiver baixado.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } catch (IOException e) {
                    Log.e("Kariti", e.toString());
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
    private boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnectedOrConnecting()){
            return true;
        }else{
            return false;
        }
    }
    public void PopMenu(View v){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(VisualProvaCorrigidaActivity.this, "Preparado para implementação", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void carregaGabarito(){
        String gabarito = bancoDados.mostraGabarito(id_prova);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Gabarito");
        builder.setMessage(gabarito);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
        });
        builder.show();

    }
    public void dadosCorrecao(String dado){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dados");
        builder.setMessage(dado);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

}