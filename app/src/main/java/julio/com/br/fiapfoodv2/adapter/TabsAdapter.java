package julio.com.br.fiapfoodv2.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import julio.com.br.fiapfoodv2.fragment.MapaFragment;
import julio.com.br.fiapfoodv2.fragment.RestaurantesFragment;

/**
 * Created by Shido on 01/02/2016.
 */
public class TabsAdapter extends FragmentStatePagerAdapter {


    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        RestaurantesFragment restaurantesFragment = null;
        MapaFragment mapaFragment = null;
        //A Partir da posição irá instanciar um Fragment diferente.
        switch (position) {
            case (0):
                //Position 0 Lista de restaurantes
                restaurantesFragment = new RestaurantesFragment();
                return  restaurantesFragment;

            case (1):
                //Position 1  Mapa com Restaurantes

                mapaFragment = new MapaFragment();

            return  mapaFragment;
        }

        Bundle args = new Bundle();

        return  null;
    }


    @Override
    public int getCount() {
        return 2;
    }
}