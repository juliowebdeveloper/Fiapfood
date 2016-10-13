package julio.com.br.fiapfoodv2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import julio.com.br.fiapfoodv2.R;
import julio.com.br.fiapfoodv2.dao.RestauranteDAO;
import julio.com.br.fiapfoodv2.model.Restaurante;

public class EditRestauranteActivity extends AppCompatActivity {


   private RestauranteDAO dao ;

    private Restaurante editRestaurante;


    @Bind(R.id.etEditNomeRestaurante)
    EditText etNomeRestaurante;

    @Bind(R.id.etEditPrecoMedio)
    EditText etPrecoMedio;

    @Bind(R.id.etEditTelefone)
    EditText etTelefone;

    @Bind(R.id.etEditObservacao)
    EditText etObservacao;


    @Bind(R.id.spinnerEditTipoRestaurante)
    Spinner spinnerTipoRestaurante;

    @Bind(R.id.btSaveEdit)
    Button btSalvarEdicao;



    @Bind(R.id.editRelativeLayout)
    RelativeLayout editRelativeLayout;

    @Bind(R.id.ivEditFoto)
    ImageView editFoto;

    @Bind(R.id.ibEditTirarFoto)
    ImageButton editTirarFoto;

    // Activity Request Codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;


    // URL de armazenamento do arquivo(imagem/video)
    private Uri fileUri;


    private boolean changedFoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_restaurante);
        ButterKnife.bind(this);

        int restauranteId = getIntent().getIntExtra("idRestaurante",0);
        if(restauranteId == 0){
            finish();
        }

        dao = new RestauranteDAO(this);

        editRestaurante = dao.buscarPorId(restauranteId);
            etNomeRestaurante.setText(editRestaurante.getNome());
            etPrecoMedio.setText(String.valueOf(editRestaurante.getCustoMedio()));
            etTelefone.setText(editRestaurante.getTelefone());
        etObservacao.setText(editRestaurante.getObservacao());



        if(editRestaurante.getFotoPath()!=null){
            File f = new File(editRestaurante.getFotoPath());
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions);
            editFoto.setImageBitmap(bitmap);
        }



            List<String> tiposRestaurante = Arrays.asList(getResources().getStringArray(R.array.arrayTipoRestaurante));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tiposRestaurante);
             spinnerTipoRestaurante.setAdapter(adapter);
            Log.i("LOCALE",Locale.getDefault().getDisplayLanguage());
            if(Locale.getDefault().getDisplayLanguage().equals(Globals.LOCALEPT)){
                switch (editRestaurante.getTipo()){
                    case ("Rodizio"):
                        spinnerTipoRestaurante.setSelection(0);
                        break;
                    case ("Fast Food"):
                        spinnerTipoRestaurante.setSelection(1);
                        break;
                    case ("Delivery"):
                        spinnerTipoRestaurante.setSelection(2);
                        break;
                    case ("Indefinido"):
                        spinnerTipoRestaurante.setSelection(3);
                        break;
                    case ("Não Sei"):
                        spinnerTipoRestaurante.setSelection(4);
                        break;
                    case ("não sei"):
                        spinnerTipoRestaurante.setSelection(4);
                        break;
                }
            }



        if(Locale.getDefault().getDisplayLanguage().equals(Globals.LOCALEENG)) {
            switch (editRestaurante.getTipo()){
                case ("All you can eat"):
                    spinnerTipoRestaurante.setSelection(0);
                    break;
                case ("Fast Food"):
                    spinnerTipoRestaurante.setSelection(1);
                    break;
                case ("Delivery"):
                    spinnerTipoRestaurante.setSelection(2);
                    break;
                case ("Undefined"):
                    spinnerTipoRestaurante.setSelection(3);
                    break;
                case ("Do not Know"):
                    spinnerTipoRestaurante.setSelection(4);
                    break;

            }

        }



        }



    @OnClick(R.id.btSaveEdit)
    public void saveEdit(Button b) {
        dao = new RestauranteDAO(this);
        Restaurante edit = new Restaurante();

        if (etNomeRestaurante.getText().toString().equals("")) {
            Snackbar.make(editRelativeLayout, R.string.nullNameCadastro, Snackbar.LENGTH_LONG).setAction(R.string.nullNameSugestion, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etNomeRestaurante.setText(R.string.nullNameSuggested);
                }
            }).show();


        } else {
            edit.setIdRestaurante(editRestaurante.getIdRestaurante());

            if(changedFoto == true){

                //Se for nulo quer dizer que está usando a imagem padrão pois nao foi salva foto
                if(editRestaurante.getFotoPath()!= null) {
                    File fotoFile = new File(editRestaurante.getFotoPath());
                    //Deletando antiga foto
                    fotoFile.delete();
                }

                edit.setFotoPath(fileUri.getPath());
            }else{
                edit.setFotoPath(editRestaurante.getFotoPath());
            }
            edit.setLatitude(editRestaurante.getLatitude());
            edit.setLongitude(editRestaurante.getLongitude());
            edit.setNome(etNomeRestaurante.getText().toString());
            edit.setCustoMedio(Double.valueOf(etPrecoMedio.getText().toString()));
            edit.setTelefone(etTelefone.getText().toString());
            edit.setObservacao(etObservacao.getText().toString());
            edit.setTipo(spinnerTipoRestaurante.getSelectedItem().toString());
            dao.updateRestaurante(edit);

            //Abre a ActivityDetails com o restaurante editado - A PARTIR DO ID.
            Intent i = new Intent(this, RestauranteDetailActivity.class);
            i.putExtra(Globals.SELECTEDRESTAURANTE, edit.getIdRestaurante());
            startActivity(i);

        }


    }










    //*********************************************Metodos de tirar foto**************************************************************************//

    @OnClick(R.id.ibEditTirarFoto)
    public void takePicture(ImageButton ib) {
        // Checking camera availability
        if (!hasACamera()) {
            Snackbar.make(editRelativeLayout, R.string.deviceNoCamera, Snackbar.LENGTH_LONG).show();
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
                //Snackbar.make(editRelativeLayout, R.string.errorCreateFile, Snackbar.LENGTH_LONG).show();

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
                Snackbar.make(editRelativeLayout, R.string.nullPhoto, Snackbar.LENGTH_LONG).show();

            } else {
                // failed to capture image
                Snackbar.make(editRelativeLayout, R.string.errorPhoto, Snackbar.LENGTH_LONG).show();

            }
        }

    }
















    //exibe a imagem tirada
    private void previewCapturedImage() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            // Redimensionamento da imagem para não lançar exceção OutOfMemory para imagens muito grande
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);
            editFoto.setImageBitmap(bitmap);
            changedFoto = true;
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

















}
