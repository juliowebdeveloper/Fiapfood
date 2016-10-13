package julio.com.br.fiapfoodv2.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.games.Game;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import julio.com.br.fiapfoodv2.MainActivity;
import julio.com.br.fiapfoodv2.model.Restaurante;

/**
 * Created by Shido on 10/02/2016.
 */
public class RestauranteDAO {


    private Context context;

    private Realm realm;

    public RestauranteDAO(Context c){
        context = c;
        realm = getRealmInstance();
    }

    public Realm getRealmInstance(){
        realm = Realm.getInstance(context);
        return realm;
    }



    public void cadastrarRestaurantes(List<Restaurante> restaurantes){
        for (Restaurante r: restaurantes
             ) {
            realm.beginTransaction();
            r.setIdRestaurante(returnLastId());
            //Para objetos já prontos: copyToRealm, para Objetos que forem criados aqui, realm.createObject();
            realm.copyToRealm(r);
            realm.commitTransaction();
        }



    }

    public void cadastrarRestaurante(Restaurante r){

            realm.beginTransaction();
            r.setIdRestaurante(returnLastId());
            realm.copyToRealm(r);
            realm.commitTransaction();

    }



    public List<Restaurante> buscarTodosRestaurantes(){
        RealmResults<Restaurante> query = realm.where(Restaurante.class).findAll();
        query.sort("nome", Sort.ASCENDING);
        return query;
    }



    public Restaurante buscarPorId(int id){
        RealmQuery<Restaurante> query = realm.where(Restaurante.class);
        query.equalTo("idRestaurante", id);
        return query.findFirst();
    }




    public List<Restaurante> buscarPorPrecoMedio(){
        //TODO
        //Fazer Between de preços

        return null;
    }


    public int returnLastId(){
        RealmResults<Restaurante> query = realm.where(Restaurante.class).findAll();
        query.sort("idRestaurante", Sort.DESCENDING);

        if(query.isEmpty()) {
            return 1;

        }else{
            int lastid = query.get(0).getIdRestaurante();
            return lastid +1;
        }

    }




    public List<Restaurante> buscarPorFiltros(String nome, String tipo, double custoDe, double custoAte){
            //Se custoDe e custoAte estiverem com 0 não aplica filtro
        RealmQuery<Restaurante> query = realm.where(Restaurante.class);
        if(nome!= null) {
            query.contains("nome", nome);
        }
        if(tipo != null){
            query.equalTo("tipo", tipo);
        }

        if(custoAte != 0){
            query.between("custoMedio",custoDe, custoAte);
        }


        return query.findAll();

    }



    public void removeRestaurante(Restaurante r){
        try {   //Apagando a foto do restaurante
            File fotoFile = new File(r.getFotoPath());
            fotoFile.delete();


            realm.beginTransaction();
            RealmQuery<Restaurante> query = realm.where(Restaurante.class);
            query.equalTo("idRestaurante", r.getIdRestaurante());
            Restaurante result = query.findFirst();
            result.removeFromRealm();



            realm.commitTransaction();

        } catch (Exception e) {
           e.printStackTrace();
        }


    }


    public void updateRestaurante(Restaurante r) {


        realm.beginTransaction();

        Restaurante realmR = realm.copyToRealmOrUpdate(r);

        realm.commitTransaction();


    }



}
