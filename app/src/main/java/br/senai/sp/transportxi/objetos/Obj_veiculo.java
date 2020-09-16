package br.senai.sp.transportxi.objetos;

import com.google.firebase.database.DatabaseReference;

import br.senai.sp.transportxi.config.Config_firebase;

public class Obj_veiculo
{
    private String Id;
    private String nomeVeiculo;
    private String placaVeiculo;
    private String tipoServiço;

    public Obj_veiculo()
    {
    }

    public void salvar(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarios = firebaseRef.child("usuarios").child( getId() ).child("veiculo");
        usuarios.setValue(this);

    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNomeVeiculo() {
        return nomeVeiculo;
    }

    public void setNomeVeiculo(String nomeVeiculo) {
        this.nomeVeiculo = nomeVeiculo;
    }

    public String getPlacaVeiculo() {
        return placaVeiculo;
    }

    public void setPlacaVeiculo(String placaVeiculo) {
        this.placaVeiculo = placaVeiculo;
    }

    public String getTipoServiço() {
        return tipoServiço;
    }

    public void setTipoServiço(String tipoServiço) {
        this.tipoServiço = tipoServiço;
    }
}
