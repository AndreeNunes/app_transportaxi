package br.senai.sp.transportxi.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import br.senai.sp.transportxi.R;
import br.senai.sp.transportxi.activity.PrincipalActivityTransportador;
import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.fragments.ViagemFragment;
import br.senai.sp.transportxi.helper.UsuarioFirebase;
import br.senai.sp.transportxi.objetos.Agenda;
import br.senai.sp.transportxi.objetos.Obj_Usuario;
import br.senai.sp.transportxi.objetos.Pedido;
import br.senai.sp.transportxi.objetos.Solicitacao;
import br.senai.sp.transportxi.objetos.Viagem;
import de.hdodenhof.circleimageview.CircleImageView;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.MyViewHolder> {

    private List<Agenda> agenda;
    private Context context;
    private AlertDialog alert;
    private Viagem teste;


    public AgendaAdapter(List<Agenda> listaAgenda, Context context){

        this.agenda = listaAgenda;
        this.context = context;
        System.out.println("++++++++++++++++++++++++++++++++"+agenda.toString());

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_agenda, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int i) {

        final Agenda a = agenda.get(i);

        holder.imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref = Config_firebase.getBanco_tansportaxi()
                        .child("usuarios")
                        .child(a.getIdEncomendador());

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

        System.out.println("**********************************" + a.getDataConfirmada());

        holder.dataP.setText("Data: " + a.getData());
        holder.horarioP.setText("Horário: " + a.getHora());
        holder.dataS.setText("Data: " + a.getDataAux());
        holder.horarioS.setText("Horário: " + a.getHoraAux());

        final DatabaseReference ref = Config_firebase.getBanco_tansportaxi();

        DatabaseReference ref3 = ref.child("solicitacoes")
                .child(a.getIdSolicitacao());

        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Solicitacao s = dataSnapshot.getValue(Solicitacao.class);

                try {
                    holder.carga.setText("Carga: " + s.getCarga());
                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference ref2 = ref.child("usuarios")
                .child(a.getIdEncomendador());

        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Obj_Usuario u = dataSnapshot.getValue(Obj_Usuario.class);

                if (!u.getCaminho_foto().equalsIgnoreCase("vazio")) {

                    Glide.with(context)
                            .load(Uri.parse(u.getCaminho_foto()))
                            .into(holder.fotoPerfil);

                } else {

                    holder.fotoPerfil.setImageResource(R.drawable.semftperfil);

                }

                holder.nome.setText(u.getNome_user());
                holder.email.setText(u.getEmail_user());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(a.getEstadoBotao().equals("Confirmar")){
            holder.confirmar.setText("Confirmar");
        }else if (a.getEstadoBotao().equals("INICIAR")){
            holder.confirmar.setText("INICIAR");
        }

        holder.confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String botao = holder.confirmar.getText().toString();

                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!"+botao);

                if(botao.equals("INICIAR")){


                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Viagem");
                    builder.setMessage("Realmente deseja iniciar essa viagem ? Isso apagará a viagem atual");
                    builder.setPositiveButton("Iniciar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            DatabaseReference ref = Config_firebase.getBanco_tansportaxi()
                                    .child("viagem");

                            final Query query = ref.orderByChild("idAgenda")
                                    .equalTo(a.getIdAgenda());

                            query.getRef().removeValue();

                            Viagem vAux = new Viagem();

                            vAux.setIdViagem(UUID.randomUUID().toString());
                            vAux.setIdSolicitacao(a.getIdSolicitacao());
                            vAux.setIdAgenda(a.getIdAgenda());
                            vAux.setIdPedido(a.getIdPedido());
                            vAux.setIdTransportador(a.getIdTransportador());
                            vAux.setIdEncomendador(a.getIdEncomendador());
                            vAux.salvar();

                            Pedido p = new Pedido();
                            p.setIdPedido(a.getIdPedido());
                            p.setStatus(Pedido.STATUS_VIAGEM);
                            p.atualizarStatus();

                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });



                    builder.show();

                }else if (botao.equals("Confirmar")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater li = LayoutInflater.from(context);
                    View visualAlert = li.inflate(R.layout.alert_confima_data_para_viagem, null);
                    final RadioButton dataP = visualAlert.findViewById(R.id.viagem_alert_dataP);
                    final RadioButton dataS = visualAlert.findViewById(R.id.viagem_alert_dataS);

                    String dp = a.getData();
                    String ds = a.getDataAux();
                    String hp = a.getHora();
                    String hs = a.getHoraAux();

                    dataP.setText("Data: " + dp + " ás " + hp);
                    dataS.setText("Data: " + ds + " ás " + hs);

                    builder.setPositiveButton("Confimar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final Viagem v = new Viagem();

                            if (dataP.isChecked()) {
                                Pedido p = new Pedido();
                                p.setIdPedido(a.getIdPedido());
                                p.setDataConfirmada("Data: " + a.getData() + " ás " + a.getHora());
                                p.atualizarData();
                                Agenda agenda = new Agenda();
                                agenda.setIdAgenda(a.getIdAgenda());
                                agenda.setEstadoBotao("INICIAR");
                                agenda.atualizarBotao();
                                agenda.setDataConfirmada(a.getData() + " ás " + a.getHora());
                                agenda.atualizarData();
                                holder.confirmar.setText("INICIAR");
                            } else if (dataS.isChecked()) {
                                Pedido p = new Pedido();
                                p.setIdPedido(a.getIdPedido());
                                p.setDataConfirmada("Data: " + a.getDataAux() + " ás " + a.getHoraAux());
                                p.atualizarData();
                                Agenda agenda = new Agenda();
                                agenda.setIdAgenda(a.getIdAgenda());
                                agenda.setEstadoBotao("INICIAR");
                                agenda.atualizarBotao();
                                agenda.setDataConfirmada(a.getDataAux() + " ás " + a.getHoraAux());
                                agenda.atualizarData();
                                holder.confirmar.setText("INICIAR");
                            } else {
                                Toast.makeText(context, "Por favor escolha uma data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setView(visualAlert);
                    builder.show();
                }
            }
        });


        holder.cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Deseja cancelar");
                builder.setIcon(R.drawable.ic_error_black_24dp);
                builder.setMessage("Após a exclusão, ação não poderá mais ser desfeita, tem certeza que deseja excluir ?");
                builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseReference ref = Config_firebase.getBanco_tansportaxi()
                                .child("agenda")
                                .child(a.getIdAgenda());

                        ref.removeValue();

                        Solicitacao s1 = new Solicitacao();
                        s1.setIdSolicitacao(a.getIdSolicitacao());
                        s1.setSubStatus("Cancelado_pelo_transportador");
                        s1.atualizarStatus();


                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return agenda.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome, email, dataP, horarioP, dataS, horarioS, carga;
        CircleImageView fotoPerfil;
        Button cancelar, confirmar;
        ImageView imgInfo;

        public MyViewHolder(View itemView) {

            super(itemView);

            imgInfo = itemView.findViewById(R.id.requisicoes_info);
            carga = itemView.findViewById(R.id.agenda_carga);
            nome = itemView.findViewById(R.id.viagem_nome);
            email = itemView.findViewById(R.id.agenda_email);
            dataP = itemView.findViewById(R.id.agenda_data_primaria);
            dataS = itemView.findViewById(R.id.agenda_data_segundaria);
            horarioP = itemView.findViewById(R.id.agenda_horario_primario);
            horarioS = itemView.findViewById(R.id.agenda_horario_segundario);
            fotoPerfil = itemView.findViewById(R.id.viagem_foto_usuario);
            confirmar = itemView.findViewById(R.id.agenda_confirmar);
            cancelar = itemView.findViewById(R.id.agenda_recusar);


        }
    }
}
