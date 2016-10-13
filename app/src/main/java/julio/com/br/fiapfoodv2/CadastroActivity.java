package julio.com.br.fiapfoodv2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import julio.com.br.fiapfoodv2.dao.RestauranteDAO;
import julio.com.br.fiapfoodv2.model.Restaurante;

public class CadastroActivity extends AppCompatActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{


    @Bind(R.id.toolbarLayout)
    Toolbar toolbar;


    //Constantes de Localização
    private static final String TAG = "CadastroActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;



    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;


    @Bind(R.id.tvLocation)
    TextView tvLocation;


    @Bind(R.id.spinnerTipoRestaurante)
    Spinner spinnerTipoRestaurante;

    @Bind(R.id.ibTirarFoto)
    ImageButton ibTirarFoto;


    //FrameLayout apenas para usar como apontamento para o snackbar em qualquer pedaço de codigo
    static FrameLayout frameLayout;

    @Bind(R.id.ivCadastroFoto)
    ImageView ivCadastroFoto;

    @Bind(R.id.etCadastroNomeRestaurante)
    EditText etNomeRestaurante;

    @Bind(R.id.etCadastroPrecoMedio)
    EditText etPrecoMedio;

    @Bind(R.id.etCadastroTelefone)
    EditText etTelefone;

    @Bind(R.id.etCadastroObservacao)
    EditText etObservacao;




    @Bind(R.id.btSaveRestaurante)
    Button btSalvarRestaurante;


    // Activity Request Codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;


    // Nome do diretório que serão gravadas as imagens  e os vídeos


    // URL de armazenamento do arquivo(imagem/video)
    private Uri fileUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        List<String> tiposRestaurante = Arrays.asList(getResources().getStringArray(R.array.arrayTipoRestaurante));
        spinnerTipoRestaurante.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,tiposRestaurante));

        frameLayout = (FrameLayout)findViewById(R.id.cadastroIvFrameLayout);



        //Checando se o GooglePlayServices está instalado
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();








    }




    @OnClick(R.id.btSaveRestaurante)
    public void salvarRestaurante(Button b){
        if(etNomeRestaurante.getText().toString().equals("")){
            Snackbar.make(frameLayout, R.string.nullNameCadastro, Snackbar.LENGTH_LONG).setAction(R.string.nullNameSugestion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etNomeRestaurante.setText(R.string.nullNameSuggested);
                }
            })
            .show();

        }else{
            if (null != mCurrentLocation) {
                String lat = String.valueOf(mCurrentLocation.getLatitude());
                String lng = String.valueOf(mCurrentLocation.getLongitude());

                try{
                    Restaurante r = new Restaurante();
                    r.setNome(etNomeRestaurante.getText().toString());
                    r.setCustoMedio(Double.parseDouble(etPrecoMedio.getText().toString()));
                    r.setLatitude(lat);
                    r.setLongitude(lng);
                    r.setObservacao(etObservacao.getText().toString());
                    r.setTelefone(etTelefone.getText().toString());
                    r.setTipo(spinnerTipoRestaurante.getSelectedItem().toString());
                    //Salvando no formato getPath, para ser retornado com file.getPath posteriormente.
                    /*
                    url.toString() return a String in the following format: "file:///mnt/sdcard/myPicture.jpg",
                    whereas url.getPath() returns a String in the following format: "/mnt/sdcard/myPicture.jpg",
                    i.e. without the scheme type pre-fixed*/
                    r.setFotoPath(fileUri.getPath());
                    RestauranteDAO dao = new RestauranteDAO(CadastroActivity.this);
                    dao.cadastrarRestaurante(r);

                    Intent i = new Intent(CadastroActivity.this, MainActivity.class);
                    i.putExtra(Globals.CADASTRO, Globals.CADASTRO_SUCCESS);
                    startActivity(i);
                    finish();

                }catch (Exception e){
                    Snackbar.make(frameLayout, R.string.cantRegister, Snackbar.LENGTH_LONG).show();

                }



            } else {
                //TODO
                //Sugestão criar uma tela com FAQ e mandar para ela na sugestão aqui
                Snackbar.make(frameLayout, R.string.cantGetLocation, Snackbar.LENGTH_LONG).show();
                Log.d(TAG, "location is null ...............");
            }


            }



    }






//*********************************************Metodos de tirar foto**************************************************************************//

    @OnClick(R.id.ibTirarFoto)
    public void takePicture(ImageButton ib) {
        // Checking camera availability
        if (!hasACamera()) {
            Snackbar.make(frameLayout, R.string.deviceNoCamera, Snackbar.LENGTH_LONG).show();
        }else{
            tirarFoto();
        }
    }





    private boolean hasACamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            //se tiver camera retorna true
            return true;
        } else {
            //se não tiver camera retorna false
            return false;
        }
    }



    //Abrir a camera para tirar a foto
    private void tirarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // Inicia a Intent para tirar a foto
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }




    //Cria o arquivo (imagem/video)
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile());
    }


    //retorna a imagem ou o video
    private static File getOutputMediaFile() {
        //Caminho onde será gravado o arquivo
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Globals.IMAGE_DIRECTORY_NAME);

        // Cria o diretório caso não exista
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(Globals.IMAGE_DIRECTORY_NAME, R.string.errorCreateFile + Globals.IMAGE_DIRECTORY_NAME);
                Snackbar.make(frameLayout, R.string.errorCreateFile, Snackbar.LENGTH_LONG).show();

                return null;
            }
        }

        // Cria o arquivo
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }




    //Chamado depois que a camera é fechada
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Verifica se o resultado é referente a a chamada para tirar foto
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Snackbar.make(frameLayout, R.string.nullPhoto, Snackbar.LENGTH_LONG).show();

            } else {
                // failed to capture image
                Snackbar.make(frameLayout, R.string.errorPhoto, Snackbar.LENGTH_LONG).show();

            }
        }

    }
















    //exibe a imagem tirada
    private void previewCapturedImage() {
        try {
            // hide video preview
            BitmapFactory.Options options = new BitmapFactory.Options();
            // Redimensionamento da imagem para não lançar exceção OutOfMemory para imagens muito grande
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);
            ivCadastroFoto.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }







//Salvando o caminho do arquivo caso a orientação da tela seja alterada

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // salva a url do arquivo caso seja alterada a orientacao da tela
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // recupera o arquivo caso seja alterada a orientação da tela
        fileUri = savedInstanceState.getParcelable("file_uri");
        previewCapturedImage();
    }







    //***********************************************Metodos de Location*******************************************************************//


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }




    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }


    private void updateUI() {
       // Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());
            /*tvLocation.setText("At Time: " + mLastUpdateTime + "\n" +
                    "Latitude: " + lat + "\n" +
                    "Longitude: " + lng + "\n" +
                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                    "Provider: " + mCurrentLocation.getProvider());*/
        } else {
            Log.d(TAG, "location is null ...............");
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());

    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }



    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }









}
