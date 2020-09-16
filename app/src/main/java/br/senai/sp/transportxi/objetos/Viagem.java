package br.senai.sp.transportxi.objetos;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import br.senai.sp.transportxi.config.Config_firebase;

public class Viagem
{
    private String idPedido;
    private String idAgenda;
    private String idTransportador;
    private String idEncomendador;
    private String idViagem;
    private String Status;
    private String idSolicitacao;
    private String EnviaAlerta;
    private String data;

    public static final String STATUS_INICADO = "iniciado";
    public static final String STATUS_FINALIZADO = "finalizado";
    public static final String STATUS_AGUARDANDO = "aguardando";

    public void salvar(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarios = firebaseRef.child("viagem").child( getIdViagem() );
        usuarios.setValue(this);

    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public void atualizarStatus(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarioref = firebaseRef
                .child("viagem")
                .child(getIdViagem());

        Map<String, Object> valoresUsuarios = converterParaMapStatus();
        usuarioref.updateChildren(valoresUsuarios);

    }


    public Map<String, Object> converterParaMapStatus(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("status", getStatus());

        return usuarioMap;
    }



    public String getIdEncomendador() {
        return idEncomendador;
    }

    public void setIdEncomendador(String idEncomendador) {
        this.idEncomendador = idEncomendador;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getIdAgenda() {
        return idAgenda;
    }

    public void setIdAgenda(String idAgenda) {
        this.idAgenda = idAgenda;
    }

    public String getIdTransportador() {
        return idTransportador;
    }

    public void setIdTransportador(String idTransportador) {
        this.idTransportador = idTransportador;
    }

    public String getIdViagem() {
        return idViagem;
    }

    public void setIdViagem(String idViagem) {
        this.idViagem = idViagem;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getIdSolicitacao() {
        return idSolicitacao;
    }

    public void setIdSolicitacao(String idSolicitacao) {
        this.idSolicitacao = idSolicitacao;
    }

    public String getEnviaAlerta() {
        return EnviaAlerta;
    }

    public void setEnviaAlerta(String enviaAlerta) {
        EnviaAlerta = enviaAlerta;
    }
}
