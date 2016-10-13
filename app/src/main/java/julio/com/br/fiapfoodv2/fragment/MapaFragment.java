package julio.com.br.fiapfoodv2.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import julio.com.br.fiapfoodv2.R;
import julio.com.br.fiapfoodv2.dao.RestauranteDAO;
import julio.com.br.fiapfoodv2.model.Restaurante;

/**
 * Created by Shido on 04/02/2016.
 */
public class MapaFragment extends Fragment implements OnMapReadyCallback, LocationListener {


    private GoogleMap map;
    List<Restaurante> restauranteList;

    private LocationManager locationManager;
    private String provider;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mostrarmap, container, false);


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.showAllMap);
        mapFragment.getMapAsync(this);


        return rootView;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestauranteDAO dao = new RestauranteDAO(getActivity());
        restauranteList = dao.buscarTodosRestaurantes();


    }






    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);


        for (Restaurante r:
            restauranteList ) {
            LatLng resPosition = new LatLng(Double.valueOf(r.getLatitude()), Double.valueOf(r.getLongitude()));
            map.addMarker(new MarkerOptions().position(resPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)));
        }
        LatLng first = new LatLng(Double.valueOf(restauranteList.get(0).getLatitude()),Double.valueOf(restauranteList.get(0).getLongitude()));
       //LatLng userPosition = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
       // map.addMarker(new MarkerOptions().position(userPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_user_marker)));
       // map.moveCamera(CameraUpdateFactory.newLatLng(resPos));
       CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(first, 10);
       map.animateCamera(cameraUpdate);
    }


    @Override
    public void onLocationChanged(Location location) {

    }
}
