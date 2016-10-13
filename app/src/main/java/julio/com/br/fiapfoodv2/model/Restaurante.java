package julio.com.br.fiapfoodv2.model;


import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Shido on 01/02/2016.
 */
public class Restaurante extends RealmObject {


    @PrimaryKey
    private int idRestaurante;

    @SerializedName("NOMERESTAURANTE")
    private String nome;

    @SerializedName("CustoMedio")
    private double custoMedio;


    @SerializedName("TELEFONE")
    private String telefone;

    @SerializedName("TIPO")
    private String tipo;

    @SerializedName("OBSERVACAO")
    private String observacao;

    private String fotoPath;

    private String latitude;

    private String longitude;

    @SerializedName("LOCALIZACAO")
    private String localizacao;













    public Restaurante() {
    }

    public Restaurante(int idRestaurante, String nome, double custoMedio, String telefone, String tipo, String observacao, String fotoPath, String latitude, String longitude, String localizacao) {
        this.idRestaurante = idRestaurante;
        this.nome = nome;
        this.custoMedio = custoMedio;
        this.telefone = telefone;
        this.tipo = tipo;
        this.observacao = observacao;
        this.fotoPath = fotoPath;
        this.latitude = latitude;
        this.longitude = longitude;
        this.localizacao = localizacao;
    }

    public int getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(int idRestaurante) {
        this.idRestaurante = idRestaurante;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFotoPath() {
        return fotoPath;
    }

    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getCustoMedio() {
        return custoMedio;
    }

    public void setCustoMedio(double custoMedio) {
        this.custoMedio = custoMedio;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

}
