package br.senai.sp.transportxi.objetos;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import br.senai.sp.transportxi.config.Config_firebase;

public class Pedido {

    private String idPedido;
    private String objeto;
    private String cidadeP;
    private String bairroP;
    private String ruaP;
    private String numeroP;
    private String cepP;
    private String estadoP;
    private String cidadeD;
    private String bairroD;
    private String ruaD;
    private String numeroD;
    private String cepD;
    private String estadoD;
    private String idUsuario;
    private String complementoP;
    private String complementoD;
    private String idImg;
    private String status;
    private String temImg;
    private Double LatP;
    private Double LonP;
    private Double LatD;
    private Double LonD;
    private String caminhoFotoPerfil;
    private String dataConfirmada;
    private String alerta;

    public Pedido() {
    }

    public static final String STATUS_AGUARDANDO = "aguardando";
    public static final String STATUS_A_CAMINHO = "acaminho";
    public static final String STATUS_VIAGEM = "viajando";
    public static final String STATUS_FINALIZADA = "finalizada";

    public void salvar(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference pedidos = firebaseRef.child("pedidos").child(getIdPedido());
        pedidos.setValue(this);
    }

    public String getCaminhoFotoPerfil() {
        return caminhoFotoPerfil;
    }

    public void setCaminhoFotoPerfil(String caminhoFotoPerfil) {
        this.caminhoFotoPerfil = caminhoFotoPerfil;
    }

    public void atualizarStatus(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarioref = firebaseRef
                .child("pedidos")
                .child(getIdPedido());

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
                .child("pedidos")
                .child(getIdPedido());

        Map<String, Object> valoresUsuarios = converterParaMapData();
        usuarioref.updateChildren(valoresUsuarios);

    }


    public Map<String, Object> converterParaMapData(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("dataConfirmada", getDataConfirmada());

        return usuarioMap;
    }


    public void atualizarAlerta(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarioref = firebaseRef
                .child("pedidos")
                .child(getIdPedido());

        Map<String, Object> valoresUsuarios = converterParaMapAlerta();
        usuarioref.updateChildren(valoresUsuarios);

    }


    public Map<String, Object> converterParaMapAlerta(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("alerta", getAlerta());

        return usuarioMap;
    }




    public String getAlerta() {
        return alerta;
    }

    public void setAlerta(String alerta) {
        this.alerta = alerta;
    }

    public String getComplementoP() {
        return complementoP;
    }

    public void setComplementoP(String complementoP) {
        this.complementoP = complementoP;
    }

    public String getComplementoD() {
        return complementoD;
    }

    public void setComplementoD(String complementoD) {
        this.complementoD = complementoD;
    }

    public String getDataConfirmada() {
        return dataConfirmada;
    }

    public void setDataConfirmada(String dataConfirmada) {
        this.dataConfirmada = dataConfirmada;
    }

    public Double getLatP() {
        return LatP;
    }

    public void setLatP(Double latP) {
        LatP = latP;
    }

    public Double getLonP() {
        return LonP;
    }

    public void setLonP(Double lonP) {
        LonP = lonP;
    }

    public Double getLatD() {
        return LatD;
    }

    public void setLatD(Double latD) {
        LatD = latD;
    }

    public Double getLonD() {
        return LonD;
    }

    public void setLonD(Double lonD) {
        LonD = lonD;
    }

    public String getTemImg() {
        return temImg;
    }

    public void setTemImg(String temImg) {
        this.temImg = temImg;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public String getCidadeP() {
        return cidadeP;
    }

    public void setCidadeP(String cidadeP) {
        this.cidadeP = cidadeP;
    }

    public String getBairroP() {
        return bairroP;
    }

    public void setBairroP(String bairroP) {
        this.bairroP = bairroP;
    }

    public String getRuaP() {
        return ruaP;
    }

    public void setRuaP(String ruaP) {
        this.ruaP = ruaP;
    }

    public String getNumeroP() {
        return numeroP;
    }

    public void setNumeroP(String numeroP) {
        this.numeroP = numeroP;
    }

    public String getCepP() {
        return cepP;
    }

    public void setCepP(String cepP) {
        this.cepP = cepP;
    }

    public String getEstadoP() {
        return estadoP;
    }

    public void setEstadoP(String estadoP) {
        this.estadoP = estadoP;
    }

    public String getCidadeD() {
        return cidadeD;
    }

    public void setCidadeD(String cidadeD) {
        this.cidadeD = cidadeD;
    }

    public String getBairroD() {
        return bairroD;
    }

    public void setBairroD(String bairroD) {
        this.bairroD = bairroD;
    }

    public String getRuaD() {
        return ruaD;
    }

    public void setRuaD(String ruaD) {
        this.ruaD = ruaD;
    }

    public String getNumeroD() {
        return numeroD;
    }

    public void setNumeroD(String numeroD) {
        this.numeroD = numeroD;
    }

    public String getCepD() {
        return cepD;
    }

    public void setCepD(String cepD) {
        this.cepD = cepD;
    }

    public String getEstadoD() {
        return estadoD;
    }

    public void setEstadoD(String estadoD) {
        this.estadoD = estadoD;
    }

    public String getIdImg() {
        return idImg;
    }

    public void setIdImg(String idImg) {
        this.idImg = idImg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
