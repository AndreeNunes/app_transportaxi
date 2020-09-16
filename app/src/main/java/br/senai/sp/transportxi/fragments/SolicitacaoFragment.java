package br.senai.sp.transportxi.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.senai.sp.transportxi.R;
import br.senai.sp.transportxi.adapter.RequisicoesAdapter;
import br.senai.sp.transportxi.adapter.SolicitacaoAdapter;
import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.helper.UsuarioFirebase;
import br.senai.sp.transportxi.objetos.Solicitacao;

/**
 * A simple {@link Fragment} subclass.
 */
public class SolicitacaoFragment extends Fragment {

    private RecyclerView recyclerView;
    private SolicitacaoAdapter adapter;
    private List<Solicitacao> solicitacoes = new ArrayList<>();
    private DatabaseReference databaseReference;

    public SolicitacaoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_solicitacao, container, false);

        recyclerView = v.findViewById(R.id.recycler_solicitacao);

        databaseReference = Config_firebase.getBanco_tansportaxi();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager( layoutManager );

        adapter = new SolicitacaoAdapter ( solicitacoes, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recuperarSolicitacao();

        return v;
    }

    private void recuperarSolicitacao() {

        DatabaseReference ref = databaseReference
                .child("solicitacoes");

        Query query = ref.orderByChild("idEncomendador")
                .equalTo(UsuarioFirebase.getIdUsuario());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                solicitacoes.clear();

                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    Solicitacao s = ds.getValue(Solicitacao.class);

                    if(!s.getSubStatus().equalsIgnoreCase(Solicitacao.STATUS_RECUSADO)) {

                        solicitacoes.add(s);
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
