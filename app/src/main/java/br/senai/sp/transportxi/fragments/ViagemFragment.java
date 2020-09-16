package br.senai.sp.transportxi.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.test.ViewAsserts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import br.senai.sp.transportxi.R;
import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.helper.UsuarioFirebase;
import br.senai.sp.transportxi.objetos.Agenda;
import br.senai.sp.transportxi.objetos.Obj_Usuario;
import br.senai.sp.transportxi.objetos.Pedido;
import br.senai.sp.transportxi.objetos.Viagem;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViagemFragment extends Fragment implements View.OnClickListener {

    private Button detalhes, alerta, finalizado;
    private DatabaseReference refPrincipal;
    private TextView nome, email, carga, distancia;
    private CircleImageView fotoPerfil;
    private android.support.v7.app.AlertDialog alertDialog;
    private CardView cardView;
    private ImageView imgInfo;
    String pedido;
    String agenda;
    String viage;
    String usuario;

    public ViagemFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_viagem, container, false);
        refPrincipal = Config_firebase.getBanco_tansportaxi();

        imgInfo = (ImageView) v.findViewById(R.id.requisicoes_info);
        finalizado = (Button) v.findViewById(R.id.viagem_finalizar);
        carga = (TextView) v.findViewById(R.id.viagem_carga);
        distancia = (TextView) v.findViewById(R.id.viagem_distancia);
        cardView = (CardView) v.findViewById(R.id.viagem_cardView);
        detalhes = (Button) v.findViewById(R.id.viagem_detalhes);
        alerta = (Button) v.findViewById(R.id.viagem_alerta);
        finalizado = (Button) v.findViewById(R.id.viagem_finalizar);
        nome = (TextView) v.findViewById(R.id.viagem_nome);
        email = (TextView) v.findViewById(R.id.viagem_email);
        fotoPerfil = (CircleImageView) v.findViewById(R.id.viagem_foto_usuario);

        finalizado.setOnClickListener(this);
        alerta.setOnClickListener(this);
        detalhes.setOnClickListener(this);




        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        final DatabaseReference ref = refPrincipal.child("viagem");

        Query query = ref.orderByChild("idTransportador")
                .equalTo(UsuarioFirebase.getIdUsuario());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() == 0){
                    cardView.setVisibility(View.GONE);
                }

                for (DataSnapshot ds: dataSnapshot.getChildren()) {

                    Viagem v = ds.getValue(Viagem.class);

                    viage = v.getIdViagem();
                    agenda = v.getIdAgenda();
                    usuario = v.getIdEncomendador();

                    DatabaseReference reference = refPrincipal.child("usuarios")
                            .child(v.getIdEncomendador());

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Obj_Usuario usuario = dataSnapshot.getValue(Obj_Usuario.class);

                            nome.setText(usuario.getNome_user());
                            email.setText(usuario.getEmail_user());

                            if (!usuario.getCaminho_foto().equalsIgnoreCase("vazio")) {

                                try {
                                    Glide.with(getContext())
                                            .load(Uri.parse(usuario.getCaminho_foto()))
                                            .into(fotoPerfil);
                                } catch (Exception e) {

                                }

                            } else {
                                fotoPerfil.setImageResource(R.drawable.semftperfil);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference ref2 = refPrincipal.child("viagem");

        Query query2 = ref2.orderByChild("idTransportador")
                .equalTo(UsuarioFirebase.getIdUsuario());

        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()) {

                    Viagem v = ds.getValue(Viagem.class);

                    pedido = v.getIdPedido();

                    DatabaseReference reference = refPrincipal.child("pedidos")
                            .child(v.getIdPedido());

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Pedido pedido = dataSnapshot.getValue(Pedido.class);

                            try {

                                Location locP = new Location("");
                                locP.setLatitude(pedido.getLatP());
                                locP.setLongitude(pedido.getLonP());

                                Location locD = new Location("");
                                locD.setLatitude(pedido.getLatD());
                                locD.setLongitude(pedido.getLonD());

                                float distanceInMeters = locP.distanceTo(locD);

                                distanceInMeters = distanceInMeters / 1000;

                                final float finalDistanceInMeters = distanceInMeters;
                                carga.setText("Carga: " + pedido.getObjeto());
                                distancia.setText("Distância: " + String.valueOf(finalDistanceInMeters).subSequence(0, 3) + " aproximadamente");
                            }catch (Exception e){

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref = Config_firebase.getBanco_tansportaxi()
                        .child("usuarios")
                        .child(usuario);

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Obj_Usuario u = dataSnapshot.getValue(Obj_Usuario.class);

                        android.support.v7.app.AlertDialog.Builder builder = (new android.support.v7.app.AlertDialog.Builder(getContext()));
                        LayoutInflater li = getLayoutInflater();
                        View visualAlert = li.inflate(R.layout.alert_info_usuario, null);
                        TextView nome, email, telefone, veiculo, placa;
                        nome = (TextView) visualAlert.findViewById(R.id.info_nome);
                        email = (TextView) visualAlert.findViewById(R.id.info_email);
                        telefone = (TextView) visualAlert.findViewById(R.id.info_telefone);
                        veiculo = (TextView) visualAlert.findViewById(R.id.info_veiculo);
                        placa = (TextView) visualAlert.findViewById(R.id.info_placa);

                        nome.setText("Nome: "+u.getNome_user());
                        email.setText("Email: "+u.getEmail_user());
                        telefone.setText("Telefone: "+u.getTelefone_user());
                        veiculo.setVisibility(View.GONE);
                        placa.setVisibility(View.GONE);

                        builder.setPositiveButton("Voltar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.setView(visualAlert);
                        builder.show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.viagem_detalhes:

                DatabaseReference ref = refPrincipal.child("viagem");

                Query query = ref.orderByChild("idTransportador")
                        .equalTo(UsuarioFirebase.getIdUsuario());

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds: dataSnapshot.getChildren()) {

                            Viagem v = ds.getValue(Viagem.class);


                            DatabaseReference reference = refPrincipal.child("pedidos")
                                    .child(v.getIdPedido());

                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    try {

                                        Pedido pedido = dataSnapshot.getValue(Pedido.class);

                                        android.support.v7.app.AlertDialog.Builder criaAlerta = (new android.support.v7.app.AlertDialog.Builder(getContext()));
                                        LayoutInflater li = LayoutInflater.from(getContext());
                                        View visualAlert = li.inflate(R.layout.detalhes_requiscoes, null);
                                        TextView carga, cidadeP, bairroP, ruaP, numeroP, cidadeD, bairroD, ruaD, numeroD, complentoP, complentoD;

                                        complentoP = visualAlert.findViewById(R.id.requisicoes_complementoP);
                                        complentoD = visualAlert.findViewById(R.id.requisicoes_complentoD);
                                        carga = visualAlert.findViewById(R.id.requisiscao_objetoA);
                                        cidadeP = visualAlert.findViewById(R.id.requisiscao_cidade_ap);
                                        cidadeD = visualAlert.findViewById(R.id.requisiscao_cidade_ad);
                                        bairroP = visualAlert.findViewById(R.id.requisiscao_bairro_ap);
                                        bairroD = visualAlert.findViewById(R.id.requisiscao_bairro_ad);
                                        ruaP = visualAlert.findViewById(R.id.requisiscao_rua_ap);
                                        ruaD = visualAlert.findViewById(R.id.requisiscao_rua_ad);
                                        numeroP = visualAlert.findViewById(R.id.requisiscao_numero_ap);
                                        numeroD = visualAlert.findViewById(R.id.requisiscao_numero_ad);
                                        cidadeP.setText("Cidade: " + pedido.getCidadeP());
                                        cidadeD.setText("Cidade: " + pedido.getCidadeD());
                                        bairroP.setText("Bairro: " + pedido.getBairroP());
                                        complentoD.setText("Complento: "+pedido.getComplementoD());
                                        complentoP.setText("Complento: "+pedido.getComplementoP());
                                        bairroD.setText("Bairro: " + pedido.getBairroD());
                                        ruaP.setText("Rua: " + pedido.getRuaP());
                                        ruaD.setText("Rua: " + pedido.getRuaD());
                                        numeroP.setText("Numero: " + pedido.getNumeroP());
                                        numeroD.setText("Numero: " + pedido.getNumeroD());
                                        carga.setText(pedido.getObjeto());
                                        criaAlerta.setPositiveButton("Voltar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });

                                        criaAlerta.setView(visualAlert);
                                        alertDialog = criaAlerta.create();
                                        alertDialog.show();

                                    }catch (Exception e){
                                        cardView.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                break;

            case R.id.viagem_alerta:

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater li = getLayoutInflater();
                View visualAlert = li.inflate(R.layout.alert_alerta, null);
                final EditText editText = visualAlert.findViewById(R.id.alert_alerta_edit);
                builder.setPositiveButton("Enivar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String alert = editText.getText().toString();

                        if(!alert.isEmpty()){

                           Pedido p = new Pedido();
                           p.setIdPedido(pedido);
                           p.setAlerta(alert);
                           p.atualizarAlerta();

                           Toast.makeText(getContext(),"Alerta enviado com sucesso.",Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(getContext(),"Prencha o campo.",Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setView(visualAlert);
                builder.show();

                break;

            case R.id.viagem_finalizar:

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setIcon(R.drawable.ic_error_black_24dp);
                builder1.setTitle("Finalizar");
                builder1.setMessage("Deseja realmente finalizar esse transporte ?");
                builder1.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Pedido p = new Pedido();
                        p.setIdPedido(pedido);
                        p.setStatus(Pedido.STATUS_FINALIZADA);
                        p.atualizarStatus();

                        DatabaseReference ref = refPrincipal
                                .child("agenda")
                                .child(agenda);

                        ref.removeValue();

                        DatabaseReference ref2 = refPrincipal
                                .child("viagem")
                                .child(viage);
                        ref2.removeValue();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder1.show();

                break;

        }
    }
}
