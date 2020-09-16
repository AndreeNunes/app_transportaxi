package br.senai.sp.transportxi.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.senai.sp.transportxi.R;
import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.objetos.Agenda;
import br.senai.sp.transportxi.objetos.Mask;
import br.senai.sp.transportxi.objetos.Obj_Usuario;
import br.senai.sp.transportxi.objetos.Obj_veiculo;
import br.senai.sp.transportxi.objetos.Solicitacao;
import de.hdodenhof.circleimageview.CircleImageView;

public class SolicitacaoAdapter extends RecyclerView.Adapter<SolicitacaoAdapter.MyViewHolder> {

    private List<Solicitacao> solicitacoes;
    private Context context;
    private AlertDialog alertAgenda;
    private Dialog dialog;

    public SolicitacaoAdapter(List<Solicitacao> solicitacoes, Context context) {
        this.solicitacoes = solicitacoes;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapte_solicitacao, parent, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int i) {

        final Solicitacao solicitacao = solicitacoes.get(i);

        holder.imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatabaseReference ref = Config_firebase.getBanco_tansportaxi()
                        .child("usuarios")
                        .child(solicitacao.getIdTransportador());

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Obj_Usuario u = dataSnapshot.getValue(Obj_Usuario.class);

                        android.support.v7.app.AlertDialog.Builder builder = (new android.support.v7.app.AlertDialog.Builder(context));
                        LayoutInflater li = LayoutInflater.from(context);
                        View visualAlert = li.inflate(R.layout.alert_info_usuario, null);
                        final TextView nome, email, telefone, veiculo, placa;
                        nome = (TextView) visualAlert.findViewById(R.id.info_nome);
                        email = (TextView) visualAlert.findViewById(R.id.info_email);
                        telefone = (TextView) visualAlert.findViewById(R.id.info_telefone);
                        veiculo = (TextView) visualAlert.findViewById(R.id.info_veiculo);
                        placa = (TextView) visualAlert.findViewById(R.id.info_placa);

                        nome.setText("Nome: "+u.getNome_user());
                        email.setText("Email: "+u.getEmail_user());
                        telefone.setText("Telefone: "+u.getTelefone_user());


                        DatabaseReference ref2 = ref.child("veiculo");

                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Obj_veiculo v = dataSnapshot.getValue(Obj_veiculo.class);
                                veiculo.setText("Veiculo: "+v.getNomeVeiculo());
                                placa.setText("Placa: "+v.getPlacaVeiculo());


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

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

        holder.carga.setText("Carga: "+ solicitacao.getCarga());
        holder.valor.setText("Valor R$: " + solicitacao.getValor());

        final DatabaseReference ref1 = Config_firebase.getBanco_tansportaxi()
                .child("usuarios")
                .child(solicitacao.getIdTransportador());

        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Obj_Usuario u = dataSnapshot.getValue(Obj_Usuario.class);
                holder.nome.setText(u.getNome_user());
                holder.email.setText(u.getEmail_user());
                holder.telefone.setText("Telefone: " + u.getTelefone_user());

                if (!u.getCaminho_foto().equalsIgnoreCase("vazio")) {

                    Glide.with(context)
                            .load(Uri.parse(u.getCaminho_foto()))
                            .into(holder.fotoPerfil);

                } else {
                    holder.fotoPerfil.setImageResource(R.drawable.semftperfil);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference ref2 = ref1
                .child("veiculo");

        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Obj_veiculo v = dataSnapshot.getValue(Obj_veiculo.class);

                holder.tipo.setText("Veiculo: " + v.getNomeVeiculo());

                holder.placa.setText("Placa: " + v.getPlacaVeiculo());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //caso o encomendador ja tenha agendado esse pedido ele desabilitara o botao

        if (solicitacao.getSubStatus().equals(Solicitacao.STATUS_AGENDOU)) {
            holder.agendar.setText("Alterar");
        } else if(solicitacao.getSubStatus().equals(Solicitacao.STATUS_ENVIADO)) {
            holder.agendar.setText("Agendar");
        }else if (solicitacao.getStatus().equals(Solicitacao.STATUS_ACEITO)){
            holder.agendar.setEnabled(false);
        }




        //encomedador agendando o serviço
        holder.agendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String agendar = holder.agendar.getText().toString();

                if (agendar.equals("Alterar")) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater li = LayoutInflater.from(context);
                    View view = li.inflate(R.layout.alert_solicitacao_agendar, null);
                    final TextInputLayout dataAgenda = view.findViewById(R.id.data_agendar);
                    final TextInputLayout horaAgenda = view.findViewById(R.id.hora_agendar);
                    final TextInputLayout horaAgendaAux = view.findViewById(R.id.horaAux_agendar);
                    final TextInputLayout dataAgendaAux = view.findViewById(R.id.dataAux_agendar);

                    //campoAlterar.addTextChangedListener(Mask.mask(campoAlterar, Mask.FORMAT_FONE));

                    dataAgenda.getEditText().addTextChangedListener(Mask.mask(dataAgenda.getEditText(), Mask.FORMAT_DATE));
                    dataAgendaAux.getEditText().addTextChangedListener(Mask.mask(dataAgendaAux.getEditText(), Mask.FORMAT_DATE));
                    horaAgenda.getEditText().addTextChangedListener(Mask.mask(horaAgenda.getEditText(), Mask.FORMAT_HOUR));
                    horaAgendaAux.getEditText().addTextChangedListener(Mask.mask(horaAgendaAux.getEditText(), Mask.FORMAT_HOUR));

                    builder.setView(view);
                    builder.setPositiveButton("Agendar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            alertCarregando(true);

                            final String data = dataAgenda.getEditText().getText().toString();
                            final String hora = horaAgenda.getEditText().getText().toString();
                            final String horaAux = horaAgendaAux.getEditText().getText().toString();
                            final String dataAux = dataAgendaAux.getEditText().getText().toString();

                            System.out.println(solicitacao.getSubStatus());

                            if (verificaData(data) && verificaData(dataAux)) {

                                if (veficaHora(hora) && veficaHora(horaAux)) {

                                    //atualizar
                                    try {

                                        final DatabaseReference ref = Config_firebase.getBanco_tansportaxi()
                                                .child("agenda");

                                        final Query query = ref.orderByChild("idSolicitacao")
                                                .equalTo(solicitacao.getIdSolicitacao());

                                        query.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                    Agenda agenda = new Agenda();
                                                    Agenda aId = ds.getValue(Agenda.class);
                                                    agenda.setIdAgenda(aId.getIdAgenda());
                                                    agenda.setData(data);
                                                    agenda.atualizarDataP();
                                                    agenda.setDataAux(dataAux);
                                                    agenda.atualizarDataS();
                                                    agenda.setHora(hora);
                                                    agenda.atualizarHoraP();
                                                    agenda.setHoraAux(horaAux);
                                                    agenda.atualizarHoraS();

                                                    Toast.makeText(context,"Pedido alterado com sucesso !",Toast.LENGTH_SHORT).show();

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                    }catch (Exception e){

                                    }



                                    alertCarregando(false);

                                } else {
                                    alertCarregando(false);
                                    Toast.makeText(context, "A hora inserida é inválida.", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                alertCarregando(false);
                                Toast.makeText(context, "Data inserida é inválida.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertAgenda = builder.create();
                    alertAgenda.show();
                    alertAgenda.setCanceledOnTouchOutside(false);
                }


                else if (agendar.equals("Agendar")) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater li = LayoutInflater.from(context);
                    View view = li.inflate(R.layout.alert_solicitacao_agendar, null);
                    final TextInputLayout dataAgenda = view.findViewById(R.id.data_agendar);
                    final TextInputLayout horaAgenda = view.findViewById(R.id.hora_agendar);
                    final TextInputLayout horaAgendaAux = view.findViewById(R.id.horaAux_agendar);
                    final TextInputLayout dataAgendaAux = view.findViewById(R.id.dataAux_agendar);


                    //campoAlterar.addTextChangedListener(Mask.mask(campoAlterar, Mask.FORMAT_FONE));
                    dataAgenda.getEditText().addTextChangedListener(Mask.mask(dataAgenda.getEditText(), Mask.FORMAT_DATE));
                    dataAgendaAux.getEditText().addTextChangedListener(Mask.mask(dataAgendaAux.getEditText(), Mask.FORMAT_DATE));
                    horaAgenda.getEditText().addTextChangedListener(Mask.mask(horaAgenda.getEditText(), Mask.FORMAT_HOUR));
                    horaAgendaAux.getEditText().addTextChangedListener(Mask.mask(horaAgendaAux.getEditText(), Mask.FORMAT_HOUR));
                    builder.setView(view);
                    builder.setPositiveButton("Agendar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final AlertDialog[] alertDialog = new AlertDialog[1];
                            final String data = dataAgenda.getEditText().getText().toString();
                            final String hora = horaAgenda.getEditText().getText().toString();
                            final String horaAux = horaAgendaAux.getEditText().getText().toString();
                            final String dataAux = dataAgendaAux.getEditText().getText().toString();

                            agendamento(solicitacao, data, hora, dataAux, horaAux);

                            /*final boolean[] verifica = {false};
                            DatabaseReference reference = Config_firebase.getBanco_tansportaxi()
                                    .child("agenda");

                            Query query = reference.orderByChild("idTransportador")
                                    .equalTo(solicitacao.getIdTransportador());

                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    String horas[] = new String[(int) dataSnapshot.getChildrenCount()];
                                    int i = 0;

                                    if(dataSnapshot.getChildrenCount() == 0){

                                    }

                                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                                        Agenda agenda = dataSnapshot1.getValue(Agenda.class);

                                        horas[i] = agenda.getHora();

                                        i++;
                                    }



                                    try {

                                        for (int k = 0; k <= horas.length; k++) {
                                            if(horas[k].equals(hora)){
                                                System.out.println(horas[k]);
                                                verifica[0] = false;
                                                break;
                                            }else if (!horas[k].equals(hora)){
                                                verifica[0] = true;
                                                int a = horas.length;
                                                if(k == a-1){
                                                    agendamento(solicitacao, data, hora, dataAux, horaAux);
                                                }
                                            }
                                        }

                                    }catch (Exception e){
                                        agendamento(solicitacao, data, hora, dataAux, horaAux);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            if(!verifica[0]){
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                builder1.setTitle("Aviso");
                                builder1.setIcon(R.drawable.ic_error_black_24dp);
                                builder1.setMessage("O transportador ja tem uma encomenda nessa data");
                                alertDialog[0] = builder1.create();
                                alertDialog[0].show();
                            }*/
                        }
                    }).setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alertAgenda = builder.create();
                    alertAgenda.show();
                    alertAgenda.setCanceledOnTouchOutside(false);
                }
            }
        });


        //ta certo so continua
        switch (solicitacao.getSubStatus())
        {

            case "cancelado_pelo_transportador":

                holder.agendar.setEnabled(false);

                holder.recusar.setText("Apagar");

                holder.valor.setText(" O transportador cancelou :(");

                holder.recusar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference ref = Config_firebase.getBanco_tansportaxi()
                                .child("solicitacoes")
                                .child(solicitacao.getIdSolicitacao());

                        ref.removeValue();
                    }
                });

                break;

                default:

                    holder.recusar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setIcon(R.drawable.ic_error_black_24dp);
                            builder.setTitle("Deseja recusar");
                            builder.setMessage("Você tem certeza? Após a exclusão, a ação não poderá mais ser desfeita");
                            builder.setPositiveButton("Recusar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Solicitacao s1 = new Solicitacao();
                                    s1.setIdSolicitacao(solicitacao.getIdSolicitacao());
                                    s1.setSubStatus(Solicitacao.STATUS_RECUSADO);
                                    s1.atualizarStatus();
                                    alertCarregando(false);

                                    DatabaseReference ref = Config_firebase.getBanco_tansportaxi()
                                            .child("agenda");

                                    Query query = ref
                                            .orderByChild("idSolicitacao")
                                            .equalTo(solicitacao.getIdSolicitacao());

                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot ds: dataSnapshot.getChildren()){

                                                Agenda a = ds.getValue(Agenda.class);
                                                Agenda a1 = new Agenda();
                                                a1.setIdAgenda(a.getIdAgenda());
                                                a1.setStatus(Agenda.STATUS_CANCELOU);
                                                a1.atualizarStatus();
                                                alertCarregando(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }).setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.show();
                        }
                    });

                    break;
        }


    }

    public void agendamento (Solicitacao solicitacao, final String data, final String hora,
                         String dataAux, String horaAux){


        System.out.println(solicitacao.getIdEncomendador()+data+hora+dataAux+horaAux);

        if (verificaData(data) && verificaData(dataAux)) {

            if (veficaHora(hora) && veficaHora(horaAux)) {

                Agenda a = new Agenda();
                a.setDataAux(dataAux);
                a.setData(data);
                a.setDataConfirmada("Não definida");
                a.setHora(hora);
                a.setHoraAux(horaAux);
                a.setIdAgenda(UUID.randomUUID().toString());
                a.setStatus(Agenda.STATUS_AGENDADO);
                a.setIdSolicitacao(solicitacao.getIdSolicitacao());
                a.setIdEncomendador(solicitacao.getIdEncomendador());
                a.setIdPedido(solicitacao.getIdPedido());
                a.setIdTransportador(solicitacao.getIdTransportador());
                a.setEstadoBotao("Confirmar");
                a.salvar();
                Solicitacao s1 = new Solicitacao();
                s1.setIdSolicitacao(solicitacao.getIdSolicitacao());
                s1.setSubStatus(Solicitacao.STATUS_AGENDOU);
                s1.atualizarStatus();
                alertCarregando(false);
                Toast.makeText(context,"Pedido agendado com sucesso.",Toast.LENGTH_SHORT).show();


            } else {
                alertCarregando(false);
                Toast.makeText(context, "A hora inserida é inválida.", Toast.LENGTH_LONG).show();
            }


        } else {
            alertCarregando(false);
            Toast.makeText(context, "Data inserida é inválida.", Toast.LENGTH_LONG).show();
        }

    }

    public void alertCarregando(Boolean b) {

        try {
            if (b) {
                dialog = new Dialog(context);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.carregando);
                dialog.show();
            } else {
                dialog.dismiss();
            }
        }catch (Exception e){

        }
    }

    private boolean veficaHora(String h){

        try{

            String hora = h.substring(0, 2);

            String minuto = h.substring(3, 5);

            System.out.println(minuto);

            if(Integer.parseInt(hora) > 0 && Integer.parseInt(hora) <= 24){

                if(Integer.parseInt(minuto) >= 0 && Integer.parseInt(minuto) < 61){

                    return true;

                }else{
                    return false;
                }

            }else{
                return false;
            }

        }catch (Exception e){
            return false;
        }

    }

    private boolean verificaData(String d){

        try {
            String dia = d.substring(0, 2);

            String mes = d.substring(3, 5);

            String ano = d.substring(6, 10);

            if (Integer.parseInt(dia) >= 1 && Integer.parseInt(dia) <= 31) {

                if (Integer.parseInt(mes) >= 10 && Integer.parseInt(mes) <= 12) {

                    if (Integer.parseInt(ano) == 2018  || Integer.parseInt(ano) == 2019) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }catch (Exception e){
            return false;
        }
    }


    @Override
    public int getItemCount() {
        return solicitacoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView nome, email, valor, tipo, telefone, placa, carga;
        private CircleImageView fotoPerfil;
        private Button recusar, agendar;
        private ImageView imgInfo;

        public MyViewHolder(View itemView) {
            super(itemView);

            imgInfo = itemView.findViewById(R.id.requisicoes_info);
            carga = itemView.findViewById(R.id.solicitacao_carga);
            telefone = itemView.findViewById(R.id.solicitacao_tel);
            nome = itemView.findViewById(R.id.viagem_nome);
            email = itemView.findViewById(R.id.solicitacao_email);
            fotoPerfil = itemView.findViewById(R.id.viagem_foto_usuario);
            valor = itemView.findViewById(R.id.solicitacao_valor);
            tipo = itemView.findViewById(R.id.solicitacao_tipo);
            recusar = itemView.findViewById(R.id.solicitacao_recusar);
            agendar = itemView.findViewById(R.id.solicitacao_agendar);
            placa = itemView.findViewById(R.id.solicitacao_placa);
        }
    }
}
