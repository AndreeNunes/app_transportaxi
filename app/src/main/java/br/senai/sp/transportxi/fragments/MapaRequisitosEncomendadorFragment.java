package br.senai.sp.transportxi.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.senai.sp.transportxi.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapaRequisitosEncomendadorFragment extends Fragment {


    public MapaRequisitosEncomendadorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mapa, container, false);
    }

}
