package com.example.kariti;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterGaleria extends RecyclerView.Adapter<AdapterGaleria.ViewHolder> {

    private ArrayList<String> nomesDasFotos, datasDasFotos;
//    private byte[] photo;
    private ArrayList<byte[]> photo;
    private Context context;

    public AdapterGaleria(Context context, ArrayList<String> nomesDasFotos, ArrayList<String> datasDasFotos, ArrayList<byte[]> photo) {
        this.context = context;
        this.nomesDasFotos = nomesDasFotos;
        this.datasDasFotos = datasDasFotos;
        this.photo = photo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listagem_galeria, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nomeFoto = nomesDasFotos.get(position);
        String dataFoto = datasDasFotos.get(position);
        byte[] fotoData = photo.get(position);
//        Bitmap foto = fotosDoBanco.get(position); //teste para o banco.

        Bitmap bitPhoto = BitmapFactory.decodeByteArray(fotoData, 0, fotoData.length);
//        Bitmap bitPhoto = BitmapFactory.decodeByteArray(photo, 0, photo.length);

        // Defina os dados nos elementos de visualização
        holder.nomeDaFoto.setText(nomeFoto);
        holder.dataDaFoto.setText(dataFoto);
        holder.imageViewGaleria.setImageBitmap(bitPhoto);

        // Defina o clique do botão
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Botão deletar clicado para " + nomeFoto, Toast.LENGTH_SHORT).show();
            }
        });
        holder.imageViewGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageDialog dialog = new ImageDialog(context, bitPhoto);
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return nomesDasFotos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewGaleria;
        TextView nomeDaFoto;
        TextView dataDaFoto;
        ImageButton deleteImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewGaleria = itemView.findViewById(R.id.imageViewGaleria);
            nomeDaFoto = itemView.findViewById(R.id.nomeDaFoto);
            dataDaFoto = itemView.findViewById(R.id.dataDafoto);
            deleteImg = itemView.findViewById(R.id.deleteImg);
        }
    }
}