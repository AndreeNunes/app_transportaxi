package br.senai.sp.transportxi.adapter;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import br.senai.sp.transportxi.R;
import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.fragments.PedidosFragment;
import br.senai.sp.transportxi.helper.UsuarioFirebase;
import br.senai.sp.transportxi.objetos.Obj_Usuario;
import br.senai.sp.transportxi.objetos.Pedido;
import de.hdodenhof.circleimageview.CircleImageView;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.MyViewHolder>
{
    private List<Pedido> pedidos;
    private Context context;
    private AlertDialog alertExcluir;
    public String temImg;
    private String idPedido;
    private boolean temImgOuNao;
    private boolean viewDetalheC = true;


    public PedidoAdapter(List<Pedido> listaPedido, Context context)
    {
        this.pedidos = listaPedido;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pedido_detalhe, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position)
    {
        final Pedido pedido = pedidos.get(position);

        String idImg = pedido.getIdImg();

        StorageReference storageReference = Config_firebase.getBanco_storage();
        StorageReference imagens = storageReference.child("imagens/pedidos");
        StorageReference imagemRef = imagens.child(idImg+".png");
        System.out.println("+++++++++++++++++++++"+pedido.getIdImg());

        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(imagemRef)
                .into(holder.cargaImg);

        holder.status.setText("Status: "+pedido.getStatus());


        switch (pedido.getStatus()){

            case Pedido.STATUS_VIAGEM:

                holder.deletar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Aviso");
                        builder.setMessage("O pedido não pode ser deletado pois está sendo transportado, aguarde a finalização.");
                        builder.show();

                    }
                });

                break;

                default:

                    holder.deletar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setIcon(R.drawable.ic_delete_24dp);
                            builder.setTitle("Excluir");
                            builder.setMessage("Deseja realmente excluir esse pedido ?");
                            builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    PedidosFragment.TextNada.setVisibility(View.VISIBLE);
                                    excluirPedido(pedido);
                                }
                            });
                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            alertExcluir = builder.create();
                            alertExcluir.show();
                            alertExcluir.setCanceledOnTouchOutside(false);
                        }
                    });

                    break;
        }

        holder.dadosCarga.setText(pedido.getObjeto());

        holder.alerta.setText(pedido.getAlerta());

        holder.dadosData.setText(pedido.getDataConfirmada());

        holder.dadosPartida.setText("Cidade: "+pedido.getCidadeP()+"\n\nBairro: "+pedido.getBairroP()+"\n\nRua: "
                +pedido.getRuaP()+"\n\nNumero: "+pedido.getNumeroP());

        temImg = pedido.getTemImg();

        holder.dadosDestino.setText("Cidade: "+pedido.getCidadeD()+"\n\nBairro: "+pedido.getBairroD()+"\n\nRua: "
                +pedido.getRuaD()+"\n\nNumero: "+pedido.getNumeroD());

        holder.detalheCarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewDetalheC){

                    holder.detalheCarga.setImageResource(R.drawable.ic_remove_circle_24dp);
                    holder.dadosCarga.setVisibility(View.VISIBLE);
                    holder.dadosData.setVisibility(View.VISIBLE);
                    holder.alerta.setVisibility(View.VISIBLE);

                    if(!pedido.getTemImg().equals("vazio")){
                        holder.layoutKorner.setVisibility(View.VISIBLE);
                    }else{
                        holder.layoutKorner.setVisibility(View.GONE);
                    }

                    viewDetalheC = false;
                }else{

                    holder.detalheCarga.setImageResource(R.drawable.ic_add_circle_black_24dp);
                    holder.dadosCarga.setVisibility(View.GONE);
                    holder.layoutKorner.setVisibility(View.GONE);
                    holder.dadosData.setVisibility(View.GONE);
                    holder.alerta.setVisibility(View.GONE);
                    viewDetalheC = true;
                }
            }
        });
    }

    public void excluirPedido(Pedido p){

        DatabaseReference banco = Config_firebase.getBanco_tansportaxi();
        DatabaseReference ref = banco.child("pedidos");
        DatabaseReference ref2 = ref.child(p.getIdPedido());
        ref2.removeValue();

    }

    @Override
    public int getItemCount()
    {
        return pedidos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView status, dadosCarga, dadosPartida, dadosDestino, dadosData, alerta;
        private ImageView detalhePartida, detalheDestino, detalheCarga, cargaImg;
        private Button deletar;
        private RoundKornerRelativeLayout layoutKorner;
        private boolean viewDetalheP = true;
        private boolean viewDetalheD = true;
        private String idUser;


        public MyViewHolder(View itemView) {
            super(itemView);

            alerta = itemView.findViewById(R.id.pedido_alerta);
            dadosData = itemView.findViewById(R.id.pedido_data_confirma);
            deletar = itemView.findViewById(R.id.excluirPedido);
            layoutKorner = itemView.findViewById(R.id.roundKornerRelativeLayout4);
            status = itemView.findViewById(R.id.statusPedido);
            cargaImg = itemView.findViewById(R.id.cargaImg);
            dadosCarga = itemView.findViewById(R.id.dadosCarga);
            dadosDestino = itemView.findViewById(R.id.dadosDestino);
            dadosPartida = itemView.findViewById(R.id.dadosPartida);
            detalhePartida = itemView.findViewById(R.id.detalhePartida);
            detalheCarga = itemView.findViewById(R.id.detalheCarga);
            detalheDestino = itemView.findViewById(R.id.detalheDestino);

            idUser = UsuarioFirebase.getIdUsuario();

            buscarTemImg(idUser);

            detalhePartida.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(viewDetalheP){
                        detalhePartida.setImageResource(R.drawable.ic_remove_circle_24dp);
                        dadosPartida.setVisibility(View.VISIBLE);
                        viewDetalheP = false;
                    }else{
                        detalhePartida.setImageResource(R.drawable.ic_add_circle_black_24dp);
                        dadosPartida.setVisibility(View.GONE);
                        viewDetalheP = true;
                    }
                }
            });

            detalheDestino.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewDetalheD){

                        detalheDestino.setImageResource(R.drawable.ic_remove_circle_24dp);
                        dadosDestino.setVisibility(View.VISIBLE);
                        viewDetalheD = false;
                    }else{
                        detalheDestino.setImageResource(R.drawable.ic_add_circle_black_24dp);
                        dadosDestino.setVisibility(View.GONE);
                        viewDetalheD = true;
                    }
                }
            });
        }

        public void buscarTemImg(String id){

        }
    }
}
