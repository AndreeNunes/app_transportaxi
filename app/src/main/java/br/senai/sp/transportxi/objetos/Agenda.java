package br.senai.sp.transportxi.objetos;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.senai.sp.transportxi.config.Config_firebase;

public class Agenda {

    private String Data;
    private String Hora;
    private String DataAux;
    private String HoraAux;
    private String idTransportador;
    private String idAgenda;
    private String idEncomendador;
    private String idPedido;
    private String dataConfirmada;
    private String status;
    private String idSolicitacao;
    private String estadoBotao;



    public static final String STATUS_CANCELOU = "cancelou";
    public static final String STATUS_AGENDADO = "agendado";
    public static final String STATUS_CONFIRMADO = "confirmado";


    public void salvar(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarios = firebaseRef.child("agenda").child( getIdAgenda() );
        usuarios.setValue(this);

    }

    public void atualizarStatus(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarioref = firebaseRef
                .child("agenda")
                .child(getIdAgenda());

        Map<String, Object> valoresUsuarios = converterParaMapStatus();
        usuarioref.updateChildren(valoresUsuarios);

    }

    public Map<String, Object> converterParaMapStatus(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("status", getStatus());

        return usuarioMap;
    }


    public void atualizarData(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarioref = firebaseRef
                .child("agenda")
                .child(getIdAgenda());

        Map<String, Object> valoresUsuarios = converterParaMapData();
        usuarioref.updateChildren(valoresUsuarios);

    }

    public Map<String, Object> converterParaMapData(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("dataConfirmada", getDataConfirmada());

        return usuarioMap;
    }

    public void atualizarBotao(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarioref = firebaseRef
                .child("agenda")
                .child(getIdAgenda());

        Map<String, Object> valoresUsuarios = converterParaMapBotao();
        usuarioref.updateChildren(valoresUsuarios);

    }

    public Map<String, Object> converterParaMapBotao(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("estadoBotao", getEstadoBotao());

        return usuarioMap;
    }


    public void atualizarDataP(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarioref = firebaseRef
                .child("agenda")
                .child(getIdAgenda());

        Map<String, Object> valoresUsuarios = converterParaMapDataP();
        usuarioref.updateChildren(valoresUsuarios);

    }

    public Map<String, Object> converterParaMapDataP(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("data", getData());

        return usuarioMap;
    }

    public void atualizarDataS(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarioref = firebaseRef
                .child("agenda")
                .child(getIdAgenda());

        Map<String, Object> valoresUsuarios = converterParaMapDataS();
        usuarioref.updateChildren(valoresUsuarios);

    }

    public Map<String, Object> converterParaMapDataS(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("dataAux", getDataAux());

        return usuarioMap;
    }

    public void atualizarHoraP(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarioref = firebaseRef
                .child("agenda")
                .child(getIdAgenda());

        Map<String, Object> valoresUsuarios = converterParaMapHoraP();
        usuarioref.updateChildren(valoresUsuarios);

    }

    public Map<String, Object> converterParaMapHoraP(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("hora", getHora());

        return usuarioMap;
    }

    public void atualizarHoraS(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarioref = firebaseRef
                .child("agenda")
                .child(getIdAgenda());

        Map<String, Object> valoresUsuarios = converterParaMapHoraS();
        usuarioref.updateChildren(valoresUsuarios);

    }

    public Map<String, Object> converterParaMapHoraS(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("horaAux", getHoraAux());

        return usuarioMap;
    }

    public List<Agenda> buscaAgenda(String idTransportador){

        final ArrayList<Agenda> agenda = new ArrayList<>();

        DatabaseReference reference = Config_firebase.getBanco_tansportaxi().child("agenda");

        Query query = reference.orderByChild("idTransportador")
                .equalTo(idTransportador);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    Agenda a = ds.getValue(Agenda.class);
                    agenda.add(a);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return agenda;
    }


    public String getEstadoBotao() {
        return estadoBotao;
    }

    public void setEstadoBotao(String estadoBotao) {
        this.estadoBotao = estadoBotao;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getIdSolicitacao() {
        return idSolicitacao;
    }

    public void setIdSolicitacao(String idSolicitacao) {
        this.idSolicitacao = idSolicitacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDataConfirmada() {
        return dataConfirmada;
    }

    public void setDataConfirmada(String dataConfirmada) {
        this.dataConfirmada = dataConfirmada;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getHora() {
        return Hora;
    }

    public void setHora(String hora) {
        Hora = hora;
    }

    public String getDataAux() {
        return DataAux;
    }

    public void setDataAux(String dataAux) {
        DataAux = dataAux;
    }

    public String getHoraAux() {
        return HoraAux;
    }

    public void setHoraAux(String horaAux) {
        HoraAux = horaAux;
    }

    public String getIdTransportador() {
        return idTransportador;
    }

    public void setIdTransportador(String idTransportador) {
        this.idTransportador = idTransportador;
    }

    public String getIdAgenda() {
        return idAgenda;
    }

    public void setIdAgenda(String idAgenda) {
        this.idAgenda = idAgenda;
    }

    public String getIdEncomendador() {
        return idEncomendador;
    }

    public void setIdEncomendador(String idEncomendador) {
        this.idEncomendador = idEncomendador;
    }
}
