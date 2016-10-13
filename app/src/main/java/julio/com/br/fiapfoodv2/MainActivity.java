package julio.com.br.fiapfoodv2;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmObject;
import julio.com.br.fiapfoodv2.adapter.TabsAdapter;
import julio.com.br.fiapfoodv2.dao.RestauranteDAO;
import julio.com.br.fiapfoodv2.fragment.SobreDialogFragment;
import julio.com.br.fiapfoodv2.model.Restaurante;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity  {



    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.viewPager)
    ViewPager viewPager;


    @Bind(R.id.cadastroFab)
    FloatingActionButton cadastroFab;




    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkFirstRun();
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        //Verificando se veio do cadastro de restaurante
            if(getIntent().getStringExtra(Globals.CADASTRO)!= null){
                Snackbar.make(toolbar, getIntent().getStringExtra(Globals.CADASTRO), Snackbar.LENGTH_LONG);
            }



        //Adicionando a toolbar como action bar
        setSupportActionBar(toolbar);

        tabs.addTab(tabs.newTab().setIcon(R.drawable.ic_listar2));
        tabs.addTab(tabs.newTab().setIcon(R.drawable.ic_showinmaps));
        tabs.setTabGravity(TabLayout.GRAVITY_FILL); //Gravity Fill completa tudo

        //Passando pro adapter um gerenciador de fragmentos
        TabsAdapter tabsAdapter = new TabsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsAdapter);

        //Permite que as paginas troquem de acordo com o scroll:
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));



        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });






    }




    public void checkFirstRun(){
        SharedPreferences sharedpreferences =  getSharedPreferences(Globals.PREFERENCESTAG , MODE_PRIVATE);

        //Se for a primeira vez busca os restaurantes.
        if (sharedpreferences.getBoolean(Globals.FIRSTRUN, true)) {
            BuscarRestaurantes buscarRestaurantes = new BuscarRestaurantes();
            buscarRestaurantes.execute(Globals.URLJSON);
            sharedpreferences.edit().putBoolean(Globals.FIRSTRUN, false).commit();
        }
    }






@OnClick(R.id.cadastroFab)
public void startCadastroActivity(FloatingActionButton fab){
    Intent i = new Intent(this, CadastroActivity.class);
    startActivity(i);


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
              android.os.Process.killProcess(android.os.Process.myPid());
              super.onDestroy();
              break;

        }

        return super.onOptionsItemSelected(menuItem);
    }







public class BuscarRestaurantes extends AsyncTask<String, Void, String>{

    private ProgressDialog dialog;

    private List<Restaurante> restaurantesJson;

    private String urlResponse;


    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(MainActivity.this, getResources().getString(R.string.loadingWait) ,getResources().getString(R.string.loadingRestaurants));
    }

    @Override
    //Só passará a url de Parametro
    protected String doInBackground(String... params) {
        List<Restaurante> restaurantes = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();
        try{
            Request request = new Request.Builder()
                    .url(params[0])
                    .build();
            Response response = client.newCall(request).execute();
          //restaurantes= getJson(response.body().string());
            urlResponse = response.body().string();

        }catch (IOException io){
            io.printStackTrace();
        }

        return urlResponse;
    }


    public List<Restaurante> getJson(String s) {
        JsonParser parser = new JsonParser();

        JsonArray jArray = parser.parse(s).getAsJsonArray();

        //Adicionando exclusão para que o Gson consiga trabalhar com o Realm
        Gson gson  = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
        List<Restaurante> res = new ArrayList<>();

        Type type = new TypeToken<List<Restaurante>>() {
        }.getType();
        res= gson.fromJson(jArray, type);


        return res;


    }


    @Override
    protected void onPostExecute(String s) {
        //  super.onPostExecute(s);

        restaurantesJson = getJson(urlResponse);

        if (restaurantesJson.size() > 0) {
            for (Restaurante r:
                 restaurantesJson) {
                r.setLatitude(r.getLocalizacao().substring(0, r.getLocalizacao().lastIndexOf(",")));
                r.setLongitude(r.getLocalizacao().substring(r.getLocalizacao().lastIndexOf(",") + 1, r.getLocalizacao().length()));
                if(r.getTipo().equals("não sei")){
                    r.setTipo("Não Sei");
                }

            }
            //Chamar a DAO que irá inserir os restaurantes.
            RestauranteDAO dao = new RestauranteDAO(MainActivity.this);
            dao.cadastrarRestaurantes(restaurantesJson);
        }

        dialog.dismiss();
    }


//Fim BuscarRestaurantes AsyncTask
}






    //Fim main Activity

}
