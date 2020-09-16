package br.senai.sp.transportxi.objetos;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import br.senai.sp.transportxi.config.Config_firebase;

public class Solicitacao {

    private String idTransportador;
    private String idEncomendador;
    private String idSolicitacao;
    private String valor;
    private String idPedido;
    private String status;
    private String subStatus;
    private String carga;

    public static final String STATUS_ENVIADO = "enviado";
    public static final String STATUS_AGENDOU = "agendado";
    public static final String STATUS_ENVIADO_2 = "enviado2";
    public static final String STATUS_AGENDOU_2 = "agendado2";
    public static final String STATUS_ACEITO = "aceito";
    public static final String STATUS_RECUSADO = "recusado";

    public void salvar(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference solicitacoes = firebaseRef.child("solicitacoes").child( getIdSolicitacao() );
        solicitacoes.setValue(this);

    }

    public void atualizarStatus2(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarioref = firebaseRef
                .child("solicitacoes")
                .child(getIdSolicitacao());

        Map<String, Object> valoresUsuarios = converterParaMapStatus2();
        usuarioref.updateChildren(valoresUsuarios);

    }

    public Map<String, Object> converterParaMapStatus2(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("status", getStatus());

        return usuarioMap;
    }

    public void atualizarStatus(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarioref = firebaseRef
                .child("solicitacoes")
                .child(getIdSolicitacao());

        Map<String, Object> valoresUsuarios = converterParaMapStatus();
        usuarioref.updateChildren(valoresUsuarios);

    }

    public Map<String, Object> converterParaMapStatus(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("subStatus", getSubStatus());

        return usuarioMap;
    }


    public String getCarga() {
        return carga;
    }

    public void setCarga(String carga) {
        this.carga = carga;
    }

    public String getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(String subStatus) {
        this.subStatus = subStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdTransportador() {
        return idTransportador;
    }

    public void setIdTransportador(String idTransportador) {
        this.idTransportador = idTransportador;
    }

    public String getIdEncomendador() {
        return idEncomendador;
    }

    public void setIdEncomendador(String idEncomendador) {
        this.idEncomendador = idEncomendador;
    }

    public String getIdSolicitacao() {
        return idSolicitacao;
    }

    public void setIdSolicitacao(String idSolicitacao) {
        this.idSolicitacao = idSolicitacao;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

}
