package br.senai.sp.transportxi.objetos;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import br.senai.sp.transportxi.config.Config_firebase;

public class Obj_Usuario
{
    private String id;
    private String Nome_user;
    private String Telefone_user;
    private String Data_user;
    private String Email_user;
    private String Senha_user;
    private String tipo_usuario;
    private String caminho_foto;
    private String idImgFotoPerfil;

    public Obj_Usuario() {

    }

    public void salvar(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarios = firebaseRef.child("usuarios").child( getId() );
        usuarios.setValue(this);

    }

    public void atualizar(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarioref = firebaseRef
                .child("usuarios")
                .child(getId());

        Map<String, Object> valoresUsuarios = converterParaMap();
        usuarioref.updateChildren(valoresUsuarios);
    }

    public void atualizarNome(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarioref = firebaseRef
                .child("usuarios")
                .child(getId());

        Map<String, Object> valoresUsuarios = converterParaMapNome();
        usuarioref.updateChildren(valoresUsuarios);

    }

    public Map<String, Object> converterParaMapNome(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("nome_user", getNome_user());

        return usuarioMap;
    }

    public Map<String, Object> converterParaMap(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

       // usuarioMap.put("email_user", getEmail_user());
        //usuarioMap.put("nome_user", getNome_user());
       // usuarioMap.put("telefone_user", getTelefone_user());
       // usuarioMap.put("tipo_usuario",getTipo_usuario());
        //usuarioMap.put("senha_user", getSenha_user());
        // suarioMap.put("id_user", getId());
        usuarioMap.put("caminho_foto", getCaminho_foto());

        return usuarioMap;
    }


    public void atualizarTelefone(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference usuarioref = firebaseRef
                .child("usuarios")
                .child(getId());

        Map<String, Object> valoresUsuarios = converterParaMapTelefone();
        usuarioref.updateChildren(valoresUsuarios);

    }

    public Map<String, Object> converterParaMapTelefone(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("telefone_user", getTelefone_user());

        return usuarioMap;
    }


    public void atualizarIdFotoPerfil(){

        DatabaseReference firebaseRef = Config_firebase.getBanco_tansportaxi()
                .child("usuarios")
                .child(getId());

        Map<String, Object> valoresUsuarios = converterParaMapFotoPerfil();
        firebaseRef.updateChildren(valoresUsuarios);

    }

    public Map<String, Object> converterParaMapFotoPerfil(){

        HashMap<String, Object> usuarioMap = new HashMap<>();

        usuarioMap.put("idFotoPerfil", getTelefone_user());

        return usuarioMap;

    }


    public String getIdImgFotoPerfil() {
        return idImgFotoPerfil;
    }

    public void setIdImgFotoPerfil(String idImgFotoPerfil) {
        this.idImgFotoPerfil = idImgFotoPerfil;
    }

    public String getCaminho_foto() {
        return caminho_foto;
    }

    public void setCaminho_foto(String caminho_foto) {
        this.caminho_foto = caminho_foto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome_user() {
        return Nome_user;
    }

    public void setNome_user(String nome_user) {
        Nome_user = nome_user;
    }

    public String getTelefone_user() {
        return Telefone_user;
    }

    public void setTelefone_user(String telefone_user) {
        Telefone_user = telefone_user;
    }

    public String getData_user() {
        return Data_user;
    }

    public void setData_user(String data_user) {
        Data_user = data_user;
    }

    public String getEmail_user() {
        return Email_user;
    }

    public void setEmail_user(String email_user) {
        Email_user = email_user;
    }

    public String getSenha_user() {
        return Senha_user;
    }

    public void setSenha_user(String senha_user) {
        Senha_user = senha_user;
    }

    public String getTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(String tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }
}
