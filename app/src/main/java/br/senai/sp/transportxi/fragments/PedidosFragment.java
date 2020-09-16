package br.senai.sp.transportxi.fragments;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import br.senai.sp.transportxi.R;
import br.senai.sp.transportxi.activity.PrincipalActivityEncomendador;
import br.senai.sp.transportxi.adapter.PedidoAdapter;
import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.helper.UsuarioFirebase;
import br.senai.sp.transportxi.objetos.Obj_Usuario;
import br.senai.sp.transportxi.objetos.Pedido;

public class PedidosFragment extends Fragment implements View.OnClickListener
{
    private DatabaseReference firebaseRef;
    RecyclerView recyclerPedido;
    List<Pedido> pedidos = new ArrayList<>();
    private PedidoAdapter adapter;
    String idUsuario;
    public static TextView TextNada;

    public PedidosFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_pedido, container, false);

        idUsuario = UsuarioFirebase.getIdUsuario();

        recyclerPedido = (RecyclerView) v.findViewById(R.id.recyclerPedido);
        TextNada = (TextView) v.findViewById(R.id.textSemPedido);

        TextNada.setVisibility(View.VISIBLE);

        //Define layout
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerPedido.setLayoutManager( layoutManager );

        //Define adapter
        adapter = new PedidoAdapter( pedidos, getContext());
        recyclerPedido.setAdapter(adapter);
        recyclerPedido.setHasFixedSize(true);

        recuperarPedido();

        return v;
    }

    @Override
    public void onClick(View v)
    {

    }

    private void recuperarPedido(){

        firebaseRef = Config_firebase.getBanco_tansportaxi();
        DatabaseReference requisicoes = firebaseRef.child("pedidos");


        requisicoes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() > 0){
                    recyclerPedido.setVisibility(View.VISIBLE);
                }else {
                    recyclerPedido.setVisibility(View.GONE);
                }

                pedidos.clear();
                for ( DataSnapshot ds: dataSnapshot.getChildren() ){

                    Pedido p = ds.getValue(Pedido.class);

                    if(p.getIdUsuario().equals(idUsuario)){

                        TextNada.setVisibility(View.GONE);
                        pedidos.add(p);

                    }else{

                        //textNada.setVisibility(View.VISIBLE);

                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
