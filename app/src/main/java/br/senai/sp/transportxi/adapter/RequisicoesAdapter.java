package br.senai.sp.transportxi.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;

import java.util.List;
import java.util.UUID;

import br.senai.sp.transportxi.R;
import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.helper.UsuarioFirebase;
import br.senai.sp.transportxi.objetos.Obj_Usuario;
import br.senai.sp.transportxi.objetos.Pedido;
import br.senai.sp.transportxi.objetos.Solicitacao;
import de.hdodenhof.circleimageview.CircleImageView;

public class RequisicoesAdapter extends RecyclerView.Adapter<RequisicoesAdapter.MyViewHolder> {
    private List<Pedido> pedidos;
    private Context context;
    android.support.v7.app.AlertDialog alertDialog;

    public RequisicoesAdapter(List<Pedido> pedidos, Context context) {
        this.pedidos = pedidos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_requisicoes, parent, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int i) {

        final Pedido pedido = pedidos.get(i);

        String id = pedido.getIdUsuario();

        StorageReference storageReference = Config_firebase.getBanco_storage();
        StorageReference imagens = storageReference.child("imagens/pedidos");
        StorageReference imagemRef = imagens.child(pedido.getIdImg()+".png");

        holder.carga.setText(pedido.getObjeto());
        holder.cidade.setText("Ponto de partida: "+pedido.getCidadeP());


        DatabaseReference ref = Config_firebase.getBanco_tansportaxi()
                .child("usuarios")
                .child(id);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {

                    Obj_Usuario usuario = dataSnapshot.getValue(Obj_Usuario.class);

                    if (!usuario.getCaminho_foto().equalsIgnoreCase("vazio")) {

                        Glide.with(context)
                                .load(Uri.parse(usuario.getCaminho_foto()))
                                .into(holder.imgPerfil);

                    } else {

                        holder.imgPerfil.setImageResource(R.drawable.semftperfil);

                    }
                    holder.email.setText(usuario.getEmail_user());
                    holder.nome.setText(usuario.getNome_user());
                }catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(pedido.getTemImg().equalsIgnoreCase("tem")){
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(imagemRef)
                    .into(holder.imgCarga);

        }else if (pedido.getTemImg().equalsIgnoreCase("vazio")){
           holder.layoutImg.setVisibility(View.GONE);
        }

        Location locP = new Location("");
        locP.setLatitude(pedido.getLatP());
        locP.setLongitude(pedido.getLonP());

        Location locD = new Location("");
        locD.setLatitude(pedido.getLatD());
        locD.setLongitude(pedido.getLonD());

        float distanceInMeters = locP.distanceTo(locD);

        distanceInMeters = distanceInMeters/1000;

        final float finalDistanceInMeters = distanceInMeters;

        holder.distancia.setText("Distância do ponto de partida ao destino: "+String.valueOf(finalDistanceInMeters).subSequence(0,3)+" Km");

        holder.enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater li = LayoutInflater.from(context);
                View visualAlert = li.inflate(R.layout.alert_envia_proposta, null);
                final TextInputLayout input = visualAlert.findViewById(R.id.alert_edit_enviar);
                builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String valor = input.getEditText().getText().toString();

                        if(!valor.isEmpty()){

                            Solicitacao solicitacao = new Solicitacao();

                            solicitacao.setIdSolicitacao(UUID.randomUUID().toString());
                            solicitacao.setIdTransportador(UsuarioFirebase.getDadosUsuarioLogado().getId());
                            solicitacao.setIdPedido(pedido.getIdPedido());
                            solicitacao.setIdEncomendador(pedido.getIdUsuario());
                            solicitacao.setValor(valor);
                            solicitacao.setCarga(pedido.getObjeto());
                            solicitacao.setStatus(Solicitacao.STATUS_ENVIADO_2);
                            solicitacao.setSubStatus(Solicitacao.STATUS_ENVIADO);
                            solicitacao.salvar();
                        }
                    }
                }).setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setView(visualAlert);
                builder.show();
            }
        });

        holder.imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref = Config_firebase.getBanco_tansportaxi()
                        .child("usuarios")
                        .child(pedido.getIdUsuario());

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Obj_Usuario u = dataSnapshot.getValue(Obj_Usuario.class);

                        android.support.v7.app.AlertDialog.Builder builder = (new android.support.v7.app.AlertDialog.Builder(context));
                        LayoutInflater li = LayoutInflater.from(context);
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

        holder.detalhes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.support.v7.app.AlertDialog.Builder criaAlerta = (new android.support.v7.app.AlertDialog.Builder(context));
                LayoutInflater li = LayoutInflater.from(context);
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
                cidadeP.setText("Cidade: "+pedido.getCidadeP());
                cidadeD.setText("Cidade: "+pedido.getCidadeD());
                bairroP.setText("Bairro: "+pedido.getBairroP());
                bairroD.setText("Bairro: "+pedido.getBairroD());
                ruaP.setText("Rua: "+pedido.getRuaP());
                complentoD.setText("Complento: "+pedido.getComplementoD());
                complentoP.setText("Complento: "+pedido.getComplementoP());
                ruaD.setText("Rua: "+pedido.getRuaD());
                numeroP.setText("Numero: "+pedido.getNumeroP());
                numeroD.setText("Numero: "+pedido.getNumeroD());
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
            }
        });
    }

    @Override
    public int getItemCount() {
       return pedidos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome, cidade, email, carga, distancia;
        ImageView imgCarga, imgInfo;
        CircleImageView imgPerfil;
        Button detalhes, enviar;
        RoundKornerRelativeLayout layoutImg;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            imgInfo = itemView.findViewById(R.id.requisicoes_info);
            enviar = itemView.findViewById(R.id.requisistos_enviar);
            distancia = itemView.findViewById(R.id.ditancia_requisicoes);
            nome = itemView.findViewById(R.id.viagem_nome);
            cidade = itemView.findViewById(R.id.requisicoes_cidade_partida);
            email = itemView.findViewById(R.id.requisitos_email);
            carga = itemView.findViewById(R.id.requisiscoes_carga);
            imgCarga = itemView.findViewById(R.id.requisicoes_img_carga);
            imgPerfil = itemView.findViewById(R.id.viagem_foto_usuario);
            layoutImg = itemView.findViewById(R.id.roundKornerRelativeLayout3);
            detalhes = itemView.findViewById(R.id.requisiscoes_detalhes);

        }
    }
}
