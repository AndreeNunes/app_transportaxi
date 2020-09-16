package br.senai.sp.transportxi.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import br.senai.sp.transportxi.activity.PrincipalActivityTransportador;
import br.senai.sp.transportxi.activity.PrincipalActivityEncomendador;
import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.objetos.Obj_Usuario;
import br.senai.sp.transportxi.objetos.Obj_veiculo;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = Config_firebase.getAuth_transportaxi();
        return usuario.getCurrentUser();
    }

    public static Obj_Usuario getDadosUsuarioLogado(){

        FirebaseUser firebaseUser = getUsuarioAtual();

        Obj_Usuario usuario = new Obj_Usuario();
        usuario.setId(firebaseUser.getUid());
        usuario.setEmail_user(firebaseUser.getEmail());
        usuario.setNome_user(firebaseUser.getDisplayName());
        return usuario;

    }




    public static List<Obj_veiculo> getDadosVeiculoUsuarioLogado(){

        final List<Obj_veiculo> lista = new ArrayList<>();


        DatabaseReference ref = Config_firebase.getBanco_tansportaxi()
                .child("usuarios")
                .child(UsuarioFirebase.getUsuarioAtual().getUid())
                .child("veiculos");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Obj_veiculo veiculo = dataSnapshot.getValue(Obj_veiculo.class);

                lista.add(veiculo);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return lista;
    }

    public static void atualizarFotoUsuario(Uri url){

        try {

            FirebaseUser usuarioLogado = getUsuarioAtual();

            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri(url)
                    .build();
            usuarioLogado.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.d("Perfil", "Erro em atualizar a foto");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static boolean atualizarNomeUsuario(String nome){

        try{
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName( nome )
                    .build();
            user.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        System.out.println("Erro ao atualizar o nome de perfil");
                    }
                }
            });

            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static void redirecionaUsuarioLogado(final Activity activity){

        FirebaseUser user = getUsuarioAtual();
        if(user != null){
            DatabaseReference usuarioRef = Config_firebase.getBanco_tansportaxi()
                    .child("usuarios")
                    .child(getIdUsuario());
            usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Obj_Usuario usuario = dataSnapshot.getValue(Obj_Usuario.class);

                    String tipoUsuario = usuario.getTipo_usuario();
                    if( tipoUsuario.equals("E") ){
                        activity.startActivity(new Intent(activity, PrincipalActivityEncomendador.class));
                        activity.finish();
                    } else{
                        activity.startActivity(new Intent(activity, PrincipalActivityTransportador.class));
                        activity.finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public static String getIdUsuario(){
        return getUsuarioAtual().getUid();
    }

}