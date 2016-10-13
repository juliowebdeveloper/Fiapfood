package julio.com.br.fiapfoodv2.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import julio.com.br.fiapfoodv2.Globals;
import julio.com.br.fiapfoodv2.R;
import julio.com.br.fiapfoodv2.model.Restaurante;

/**
 * Created by Shido on 01/02/2016.
 */
public class RestauranteAdapter extends RecyclerView.Adapter<RestauranteAdapter.RestauranteViewHolder>{


    private Context context;

    private List<Restaurante> restaurantes;

    private Restaurante selectedRestaurante;

    private static RecyclerViewClickListener itemListener;


    public RestauranteAdapter(Context context, List<Restaurante> restaurantes) {
        this.context = context;
        this.restaurantes = restaurantes;
    }

    public RestauranteAdapter(Context context, List<Restaurante> restaurantes, RecyclerViewClickListener itemListener) {
        this.context = context;
        this.restaurantes = restaurantes;
        this.itemListener = itemListener;
    }


    @Override
    public void onBindViewHolder(RestauranteAdapter.RestauranteViewHolder holder, int position) {
        holder.nomeRestaurante.setText(restaurantes.get(position).getNome());
        holder.precoRestaurante.setText(String.valueOf(restaurantes.get(position).getCustoMedio()));
        holder.tipoRestaurante.setText(restaurantes.get(position).getTipo());

        try{
            if(restaurantes.get(position).getFotoPath()==null){
                //Utilizando o Logo como default
                InputStream is = context.getAssets().open(Globals.FOTOINDISPONIVELNAME);
                Drawable d = Drawable.createFromStream(is, null);
                holder.fotoRestaurante.setImageDrawable(d);
            }else {
                //Caso seja salvo com uri.toString();
               // File fotoFile2 = new File(new URI(restaurantes.get(position).getFotoPath()));

                //Caso tenha sido salvo com uri.getPath();
                File fotoFile = new File(restaurantes.get(position).getFotoPath());
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inSampleSize = 8;
                Bitmap bitmap = BitmapFactory.decodeFile(fotoFile.getAbsolutePath(), bmOptions);
                holder.fotoRestaurante.setImageBitmap(bitmap);
            }




        }catch (Exception e){
            e.printStackTrace();
        }





    }

    @Override
    public RestauranteAdapter.RestauranteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurante_row, parent, false);

        return new RestauranteViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return restaurantes.size();
    }






    static  class RestauranteViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        @Bind(R.id.tvNomeRestaurante)
        TextView nomeRestaurante;

        @Bind(R.id.tvPrecoRestaurante)
        TextView precoRestaurante;


        @Bind(R.id.tvTipoRestaurante)
        TextView tipoRestaurante;


        @Bind(R.id.ivFotoRestaurante)
        ImageView fotoRestaurante;

        public RestauranteViewHolder(View v){
            super(v);
            ButterKnife.bind(this, v);
            v.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
           // Log.i("POSITION adapter", String.valueOf(getAdapterPosition()));
           // Log.i("POSITION alayout", String.valueOf(getLayoutPosition()));


        }


    }




}
