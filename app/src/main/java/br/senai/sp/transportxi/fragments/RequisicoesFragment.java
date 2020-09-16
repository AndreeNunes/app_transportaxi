package br.senai.sp.transportxi.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.senai.sp.transportxi.R;
import br.senai.sp.transportxi.activity.PrincipalActivityTransportador;
import br.senai.sp.transportxi.adapter.PedidoAdapter;
import br.senai.sp.transportxi.adapter.RequisicoesAdapter;
import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.helper.UsuarioFirebase;
import br.senai.sp.transportxi.objetos.Obj_Usuario;
import br.senai.sp.transportxi.objetos.Pedido;

public class RequisicoesFragment extends Fragment {


    private DatabaseReference firebaseRef;
    RecyclerView recyclerRequisitos;
    List<Pedido> requisitos = new ArrayList<>();
    List<Obj_Usuario> usuarios = new ArrayList<>();
    private RequisicoesAdapter adapter;
    String idUsuario;
    public static TextView TextNada;

    public RequisicoesFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_requisicoes, container, false);

        idUsuario = UsuarioFirebase.getIdUsuario();

        recyclerRequisitos = (RecyclerView) v.findViewById(R.id.recycler_requisitos_2);

        TextNada = (TextView) v.findViewById(R.id.textSemPedido);

        TextNada.setVisibility(View.GONE);

        firebaseRef = Config_firebase.getBanco_tansportaxi();

        //Define layout
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerRequisitos.setLayoutManager( layoutManager );

        //Define adapter
        adapter = new RequisicoesAdapter ( requisitos, getContext());
        recyclerRequisitos.setAdapter(adapter);
        recyclerRequisitos.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerRequisitos.getContext(),
                ((LinearLayoutManager) layoutManager).getOrientation());
        recyclerRequisitos.addItemDecoration(dividerItemDecoration);

        recuperarRequisitos();

        return v;
    }

    private void recuperarRequisitos()
    {
        DatabaseReference ref = firebaseRef.child("pedidos");
        Query pesquisaRef = ref.orderByChild("status").equalTo(Pedido.STATUS_AGUARDANDO);


        pesquisaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0){
                    recyclerRequisitos.setVisibility(View.VISIBLE);
                    TextNada.setVisibility(View.GONE);
                }else {
                    recyclerRequisitos.setVisibility(View.GONE);
                    TextNada.setVisibility(View.VISIBLE);
                }

                requisitos.clear();

                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    Pedido p = ds.getValue(Pedido.class);

                    DatabaseReference ref = Config_firebase.getBanco_tansportaxi()
                            .child("usuarios")
                            .child(p.getIdUsuario());

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Obj_Usuario u = dataSnapshot.getValue(Obj_Usuario.class);

                            usuarios.add(u);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    requisitos.add(p);
                }

                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
