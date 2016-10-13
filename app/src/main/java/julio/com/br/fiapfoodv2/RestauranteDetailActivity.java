package julio.com.br.fiapfoodv2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import julio.com.br.fiapfoodv2.dao.RestauranteDAO;
import julio.com.br.fiapfoodv2.fragment.SobreDialogFragment;
import julio.com.br.fiapfoodv2.model.Restaurante;

public class RestauranteDetailActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap map;

    private RestauranteDAO dao;

    private Restaurante restaurante;

    @Bind(R.id.ivDetailsFoto)
    ImageView ivDetailsFoto;



    @Bind(R.id.detailstoolbar)
    Toolbar toolbar;

    @Bind(R.id.tvDetailsCusto)
    TextView custoMedio;

    @Bind(R.id.tvDetailsObservacao)
    TextView observacao;

    @Bind(R.id.tvDetailsTipo)
    TextView tipo;

    @Bind(R.id.tvDetailsTelefone)
    TextView telefone;


    @Bind(R.id.editFab)
    FloatingActionButton editFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurante_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
       // Esconder o  titulo
       // getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
        int idSelected = getIntent().getIntExtra(Globals.SELECTEDRESTAURANTE,0);
            if(idSelected!=0){
                dao = new RestauranteDAO(this);
                restaurante = dao.buscarPorId(idSelected);

               //Log.i("TESTE", restaurante.getNome());
                Log.i("TIPO", restaurante.getTipo());
                setRestauranteImage();
                getSupportActionBar().setTitle(restaurante.getNome());
                telefone.setText(restaurante.getTelefone());
                tipo.setText(restaurante.getTipo());
                observacao.setText(restaurante.getObservacao());
                custoMedio.setText(String.valueOf(restaurante.getCustoMedio()));
                Log.i("CustoMedio", custoMedio.getText().toString());



            }else{
                finish();
            }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detailsMap);
        mapFragment.getMapAsync(this);




    }




    @OnClick(R.id.editFab)
    public void startEditActivity(FloatingActionButton fab){
        Intent i = new Intent(this, EditRestauranteActivity.class);
        i.putExtra("idRestaurante", restaurante.getIdRestaurante());
        startActivity(i);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(RestauranteDetailActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

public void setRestauranteImage() {
    try {
        if (restaurante.getFotoPath() == null) {
            //Utilizando o Logo como default
            InputStream is = RestauranteDetailActivity.this.getAssets().open(Globals.FOTOINDISPONIVELNAME);
            Drawable d = Drawable.createFromStream(is, null);
            ivDetailsFoto.setImageDrawable(d);
        } else {
            //Caso seja salvo com uri.toString();
            // File fotoFile2 = new File(new URI(restaurantes.get(position).getFotoPath()));

            //Caso tenha sido salvo com uri.getPath();
            File fotoFile = new File(restaurante.getFotoPath());
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(fotoFile.getAbsolutePath(), bmOptions);
            ivDetailsFoto.setImageBitmap(bitmap);
        }


    } catch (IOException e) {
        e.printStackTrace();
    }

}

    @Override
    public boolean  onCreateOptionsMenu(Menu menu){
        //Colocando o menu criado "menu_main"
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        //Menu item = o que o usuario clicou, pega-se  o id
        switch (id){
            case R.id.mn_sobre:
                //Toast.makeText(this, "Sobre", Toast.LENGTH_LONG).show();

                SobreDialogFragment edit = new SobreDialogFragment();
                FragmentManager fm = getSupportFragmentManager();
                edit.show(fm, getResources().getString(R.string.menu_sobre));



                break;
            case R.id.mn_sair:
                finish();

                break;

        }

        return super.onOptionsItemSelected(menuItem);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        LatLng resPos = new LatLng(Double.valueOf(restaurante.getLatitude()), Double.valueOf(restaurante.getLongitude()));
        map.addMarker(new MarkerOptions().position(resPos).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));
        map.moveCamera(CameraUpdateFactory.newLatLng(resPos));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(resPos, 17);
        map.animateCamera(cameraUpdate);
    }


}
