package com.lucascabral.apploc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lucascabral.apploc.R;
import com.lucascabral.apploc.model.Anuncio;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterMeusAnuncios extends RecyclerView.Adapter<AdapterMeusAnuncios.MyViewHolder> {

    private List<Anuncio> anuncios;
    private Context context;

    public AdapterMeusAnuncios(List<Anuncio> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_meus_anuncios, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Anuncio anuncio = anuncios.get(position);
        holder.titulo.setText(anuncio.getTitulo());
        holder.preco.setText(anuncio.getValor());

        //Pegar a primeira das 3 imagens do anúncio
        List<String> urlFotos = anuncio.getFotos();
        String urlPrincipal = urlFotos.get(0);

        Picasso.with(holder.itemView.getContext()).load(urlPrincipal).into(holder.foto);
    }

    @Override
    public int getItemCount() {
        return anuncios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titulo;
        TextView preco;
        ImageView foto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.mAnunciosTituloTextView);
            preco = itemView.findViewById(R.id.mAnunciosPrecoTextView);
            foto = itemView.findViewById(R.id.mAnunciosImageView);
        }
    }
}
