package julio.com.br.fiapfoodv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import julio.com.br.fiapfoodv2.dao.UserDAO;

public class LoginActivity extends AppCompatActivity {



    @Bind(R.id.username)
    EditText username;

    @Bind(R.id.password)
    EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if(isLogado()){
            iniciarApp(false);
        }



    }








    @OnClick(R.id.loginbutton)
    public void cadastroOrLogin(View v) {

        String usuario = username.getText().toString();
        String senha = password.getText().toString();

        UserDAO udao = new UserDAO(this);

        int result = udao.loginManager(usuario, senha);
        if (result == UserDAO.LOGIN_NOTOK){
            //Login efetuado
            Snackbar.make(username, R.string.wrongPassword, Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.RED)
                    .show();

        }


        if(result == UserDAO.CADASTRAR){
            manterConectado();
            iniciarApp(true);
        }

        if(result == UserDAO.LOGIN_OK){
            manterConectado();
            iniciarApp(false);
        }


    }







    private void iniciarApp(boolean foiCadastrado){
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();

    }






    private void manterConectado(){
        SharedPreferences settings = getSharedPreferences(Globals.PREFERENCESTAG, MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Globals.MANTERCONECTADO, true);

        editor.commit();

    }



    private boolean isLogado(){
        SharedPreferences settings = getSharedPreferences(Globals.PREFERENCESTAG, MODE_PRIVATE);
        return settings.getBoolean(Globals.MANTERCONECTADO, false);

    }











}
