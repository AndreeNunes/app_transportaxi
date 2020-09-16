package br.senai.sp.transportxi.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import br.senai.sp.transportxi.R;
import br.senai.sp.transportxi.adapter.AgendaAdapter;
import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.helper.UsuarioFirebase;
import br.senai.sp.transportxi.objetos.Agenda;

/**
 * A simple {@link Fragment} subclass.
 */
public class AgendaFragment extends Fragment {

    private DatabaseReference ref;
    private RecyclerView recyclerView;
    private List<Agenda> ListaAgendas = new ArrayList<>();
    private AgendaAdapter adapter;
    private String idUsuario;


    public AgendaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_agenda, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView_agenda);

        ref = Config_firebase.getBanco_tansportaxi();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AgendaAdapter(ListaAgendas, getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        recuperaAgenda();

        return v;
    }

    private void recuperaAgenda() {

        DatabaseReference reference = ref.child("agenda");

        Query query = reference.orderByChild("idTransportador")
                .equalTo(UsuarioFirebase.getIdUsuario());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ListaAgendas.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                     Agenda a = ds.getValue(Agenda.class);

                     if(!a.getStatus().equalsIgnoreCase(Agenda.STATUS_CANCELOU) &&
                             !a.getStatus().equalsIgnoreCase(Agenda.STATUS_CONFIRMADO)) {

                         ListaAgendas.add(a);
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
