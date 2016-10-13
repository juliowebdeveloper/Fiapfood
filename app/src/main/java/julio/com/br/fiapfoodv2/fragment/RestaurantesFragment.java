package julio.com.br.fiapfoodv2.fragment;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import julio.com.br.fiapfoodv2.Globals;
import julio.com.br.fiapfoodv2.R;
import julio.com.br.fiapfoodv2.RestauranteDetailActivity;
import julio.com.br.fiapfoodv2.adapter.RecyclerViewClickListener;
import julio.com.br.fiapfoodv2.adapter.RestauranteAdapter;
import julio.com.br.fiapfoodv2.dao.RestauranteDAO;
import julio.com.br.fiapfoodv2.model.Restaurante;

/**
 * Created by Shido on 01/02/2016.
 */
public class RestaurantesFragment  extends Fragment implements RecyclerViewClickListener{
    

    @Bind(R.id.rvListRestaurantes)
    RecyclerView rvListRestaurantes;

    List<Restaurante> restaurantes;

    private RecyclerView.LayoutManager layoutManager;

    private RestauranteAdapter adapter;

    private RestauranteDAO dao;


    //**************Filtros

    @Bind(R.id.etFilterNome)
    TextView etFilterNome;

    @Bind(R.id.etFilterDe)
    TextView etFilterDe;

    @Bind(R.id.etFilterAte)
    TextView etFilterAte;

    @Bind(R.id.spinnerTipoRestauranteFilter)
    Spinner spinnerTipoFilter;

    @Bind(R.id.filterSearch)
    ImageButton filterSearch;

    @Bind(R.id.filterTitle)
    TextView filterTitle;

    @Bind(R.id.linearLayoutCollapse)
    LinearLayout linearCollapse;

    private boolean isExpanded;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_listrestaurantes, container, false);

        ButterKnife.bind(this, rootView);



        List<String> tiposRestaurante = Arrays.asList(getResources().getStringArray(R.array.arrayTipoRestauranteFilter));
        spinnerTipoFilter.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, tiposRestaurante));


        layoutManager = new LinearLayoutManager(container.getContext());

        rvListRestaurantes.setLayoutManager(layoutManager);


        adapter = new RestauranteAdapter(container.getContext(), restaurantes, this);
        rvListRestaurantes.setAdapter(adapter);



        //***************************Swype To Dismiss *****************************************/

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Log.i("MOVED","MOVED");

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                if(swipeDir != ItemTouchHelper.LEFT){
                    //Não exclui caso o swype seja pra esquerda, para nao ter problemas entre a troca desse fragment e do mostrar no mapa
                /*    Log.i("MOVED","SWYPED");
                    Log.i("MOVED", String.valueOf(viewHolder.getAdapterPosition()));
*/
                    //Pegando o restaurante para fazer a exclusão
                    dao = new RestauranteDAO(getActivity());
                    dao.removeRestaurante(restaurantes.get(viewHolder.getAdapterPosition()));
                    //Resetando o adapter no Recycler View já basta para que a lista seja atualizada (?)
                    rvListRestaurantes.setAdapter(adapter);
                    Snackbar.make(linearCollapse, R.string.removedRestaurant, Snackbar.LENGTH_LONG).show();
                }


            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvListRestaurantes);




        expandOrCollapse(linearCollapse, "collapse");


        return rootView;
    }








    @OnClick(R.id.filterTitle)
    public void expand(TextView tv){
        if(isExpanded == true){
            expandOrCollapse(linearCollapse, "collapse");
            isExpanded = false;
        }else{
            expandOrCollapse(linearCollapse, "expand");
            isExpanded = true;
        }
    }







    public void expandOrCollapse(final View v,String exp_or_colpse) {
        TranslateAnimation anim = null;
        if(exp_or_colpse.equals("expand"))
        {
            anim = new TranslateAnimation(0.0f, 0.0f, -v.getHeight(), 0.0f);
            v.setVisibility(View.VISIBLE);
        }
        else{
            anim = new TranslateAnimation(0.0f, 0.0f, 0.0f, -v.getHeight());
            Animation.AnimationListener collapselistener= new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.setVisibility(View.GONE);
                }
            };

            anim.setAnimationListener(collapselistener);
        }
        // To Collapse
        //
        anim.setDuration(300);
        anim.setInterpolator(new AccelerateInterpolator(0.5f));
        v.startAnimation(anim);
    }






    @OnClick(R.id.filterSearch)
    public void applyFilters(ImageButton im){

        String nomeFilter = null;
        double custoDE = 0 ;
        double custoAte= 0;
        String tipo = null;

        if (!etFilterNome.getText().toString().equals("") && etFilterNome.getText() != null) {
            nomeFilter = etFilterNome.getText().toString();
        }

        if (!etFilterDe.getText().toString().equals("") && etFilterDe.getText() != null
                && !etFilterAte.getText().toString().equals("") && etFilterAte.getText() != null) {
            custoDE = Double.valueOf(etFilterDe.getText().toString());
            custoAte = Double.valueOf(etFilterAte.getText().toString());

        }

        if (!spinnerTipoFilter.getSelectedItem().equals("Selecione Algum")|| spinnerTipoFilter.getSelectedItem().equals("Select One")) {
            tipo = spinnerTipoFilter.getSelectedItem().toString();
        }


        RestauranteDAO dao = new RestauranteDAO(getActivity());
        restaurantes = dao.buscarPorFiltros(nomeFilter, tipo, custoDE, custoAte);
        adapter = new RestauranteAdapter(getContext(), restaurantes, this);
        rvListRestaurantes.setAdapter(adapter);

    }






    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Retornar a lista de restaurantes vindo do bando
        restaurantes = new ArrayList<>();
        RestauranteDAO dao = new RestauranteDAO(getActivity());
        restaurantes = dao.buscarTodosRestaurantes();
        super.onCreate(savedInstanceState);
    }





    @Override
    public void recyclerViewListClicked(View v, int position){
        //set up adapter and pass clicked listener this
        adapter =  new RestauranteAdapter(getActivity(), restaurantes, this);

        Restaurante selectedItem  = restaurantes.get(position);

       // adapter.notifyItemRemoved(position);

        //Abre a ActivityDetails com o restaurante selecionado - A PARTIR DO ID.
        Intent i = new Intent(getActivity(), RestauranteDetailActivity.class);
        i.putExtra(Globals.SELECTEDRESTAURANTE,selectedItem.getIdRestaurante() );
        startActivity(i);


    }



}
