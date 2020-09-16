package br.senai.sp.transportxi.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import br.senai.sp.transportxi.R;
import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.helper.UsuarioFirebase;
import br.senai.sp.transportxi.objetos.Mask;
import br.senai.sp.transportxi.objetos.Obj_Usuario;
import br.senai.sp.transportxi.objetos.Obj_veiculo;
import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {

    private TextView alterar, voltar;
    private EditText nome, email, telefone, data, veiculo, placa, tipo;
    private CircleImageView image;
    private DatabaseReference databaseReference;
    String id;
    private Obj_Usuario usuarioLogado;
    final static int REQUEST_IMAGE_CAPTURE = 1, SELECT_FILE = 0;
    StorageReference imageRef;
    Uri localImageSelecionada;
    private android.app.AlertDialog carrega;
    private AlertDialog adAltera;
    private android.app.AlertDialog adAltera2;
    EditText campoAlterar;
    private  Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        id = UsuarioFirebase.getIdUsuario();

        tipo = (EditText) findViewById(R.id.perfil_tipo_usuario);
        alterar = (TextView) findViewById(R.id.AlterarImg);
        voltar = (TextView) findViewById(R.id.perfil_voltar);
        nome = (EditText) findViewById(R.id.perfil_nome);
        email = (EditText) findViewById(R.id.perfil_email);
        telefone = (EditText) findViewById(R.id.perfil_telefone);
        data = (EditText) findViewById(R.id.perfil_data);
        veiculo = (EditText) findViewById(R.id.perfil_veiculo);
        placa = (EditText) findViewById(R.id.perfil_placa);
        image = (CircleImageView) findViewById(R.id.perfil_image);

        databaseReference = Config_firebase.getBanco_tansportaxi();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        imageRef = Config_firebase.getBanco_storage();

        DatabaseReference ref = databaseReference
                .child("usuarios")
                .child(id);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Obj_Usuario usuario = dataSnapshot.getValue(Obj_Usuario.class);

                nome.setText(usuario.getNome_user());
                email.setText(usuario.getEmail_user());
                telefone.setText(usuario.getTelefone_user());
                data.setText(usuario.getData_user());

                if(usuario.getTipo_usuario().equalsIgnoreCase("T")){
                    tipo.setText("Tranportador");
                }else if (usuario.getTipo_usuario().equalsIgnoreCase("E")){
                    tipo.setText("Encomendador");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        try {

            DatabaseReference refT = databaseReference
                    .child("usuarios")
                    .child(id)
                    .child("veiculo");

            if (refT != null) {

                refT.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Obj_veiculo veiculo2 = dataSnapshot.getValue(Obj_veiculo.class);

                        try {
                            veiculo.setVisibility(View.VISIBLE);
                            placa.setVisibility(View.VISIBLE);
                            veiculo.setText(veiculo2.getNomeVeiculo());
                            placa.setText(veiculo2.getPlacaVeiculo());
                        }catch (Exception e){

                            veiculo.setVisibility(View.GONE);
                            placa.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            } else {

            }
        }
        catch (Exception e)
        {

        }

        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();

        Uri url = usuarioPerfil.getPhotoUrl();

        if(url != null){
            Glide.with(PerfilActivity.this)
                    .load(url)
                    .into(image);
        }else{
            image.setImageResource(R.drawable.semftperfil);
        }

        acoesBotoes();
    }

    public void alertCarregando(Boolean b){

        if(b)
        {
            dialog = new Dialog(this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.carregando);
            dialog.show();
        }
        else
        {
            dialog.dismiss();
        }
    }

    private void alertAlteraItemSelecionado(final String titulo, final android.app.AlertDialog ad){

        AlertDialog.Builder criaAlerta = (new AlertDialog.Builder(this));
        LayoutInflater li = getLayoutInflater();
        final View visual = li.inflate(R.layout.alert_alterar,null);
        campoAlterar = visual.findViewById(R.id.alert_campo_altera);
        final TextView item = visual.findViewById(R.id.alert_info);
        item.setText(titulo);

        if(titulo.equals("Telefone")){
            campoAlterar.addTextChangedListener(Mask.mask(campoAlterar, Mask.FORMAT_FONE));
        }

        criaAlerta.setPositiveButton("Alterar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (titulo.equals("Nome")) {

                            String n = campoAlterar.getText().toString();

                            if (!n.isEmpty()) {
                                usuarioLogado.setNome_user(n);
                                usuarioLogado.atualizarNome();
                            } else {
                                ad.show();
                                Toast.makeText(PerfilActivity.this, "Preencha o campo.", Toast.LENGTH_SHORT).show();
                            }

                        } else if (titulo.equals("Telefone")) {

                            String t = campoAlterar.getText().toString();

                            if (!t.isEmpty()) {
                                ad.dismiss();
                                usuarioLogado.setTelefone_user(t);
                                usuarioLogado.atualizarTelefone();
                            } else {
                                ad.show();
                                Toast.makeText(PerfilActivity.this, "Preencha o campo.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }).setNegativeButton("Calcelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ad.dismiss();
                adAltera.dismiss();
            }
        });

        criaAlerta.setView(visual);
        adAltera = criaAlerta.create();
        adAltera.show();
    }

    private void alertAlterar(){

        final CharSequence[] itensE={"Nome", "Telefone", "Cancelar"};

        final CharSequence[] itensT={"Nome", "Telefone", "Cancelar"};

        DatabaseReference ref = Config_firebase.getBanco_tansportaxi()
                .child("usuarios")
                .child(UsuarioFirebase.getUsuarioAtual().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Obj_Usuario usuario = dataSnapshot.getValue(Obj_Usuario.class);

                if(usuario.getTipo_usuario().equalsIgnoreCase("E")){

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PerfilActivity.this);
                    builder.setTitle("Atualizar dados");
                    builder.setIcon(R.drawable.ic_update_black_24dp);
                    builder.setItems(itensE, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            if(itensE[i].equals("Nome")){

                                dialog.dismiss();
                                alertAlteraItemSelecionado("Nome", adAltera2);


                            }else if (itensE[i].equals("Telefone")){

                                dialog.dismiss();
                                alertAlteraItemSelecionado("Telefone", adAltera2);


                            }else if(itensE[i].equals("Cancelar"))
                            {
                                dialog.dismiss();
                            }
                        }
                    });

                    adAltera2 = builder.create();
                    adAltera2.show();

                }else if(usuario.getTipo_usuario().equalsIgnoreCase("T")){

                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PerfilActivity.this);
                    builder.setTitle("Atualizar dados");
                    builder.setIcon(R.drawable.ic_update_black_24dp);
                    builder.setItems(itensT, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {

                            if(itensT[i].equals("Nome")){

                                dialog.dismiss();
                                alertAlteraItemSelecionado("Nome", adAltera2);

                            }else if (itensT[i].equals("Telefone")){

                                dialog.dismiss();
                                alertAlteraItemSelecionado("Telefone",adAltera2);

                            }else if(itensT[i].equals("Cancelar")) {

                                dialog.dismiss();
                            }
                        }
                    });

                    adAltera2 = builder.create();
                    adAltera2.show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void acoesBotoes() {

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] itens={"Camera","Galeria","Cancelar"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PerfilActivity.this);
                builder.setTitle("Adicionar Imagem");
                builder.setIcon(R.drawable.ic_photo_camera_24dp);
                builder.setItems(itens, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        if(itens[i].equals("Camera"))
                        {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
                        }
                        else if(itens[i].equals("Galeria"))
                        {
                            Intent i2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            if(i2.resolveActivity(getPackageManager()) != null)
                            {
                                startActivityForResult(i2, SELECT_FILE);
                            }
                        }
                        else if(itens[i].equals("Cancelar"))
                        {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    public void perfilAlterar(View v){

        alertAlterar();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        alertCarregando(true);

        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;

            try {
                switch (requestCode) {
                    case SELECT_FILE:
                        localImageSelecionada = data.getData();
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), localImageSelecionada);
                        break;
                }

                if (bitmap != null) {
                    try {
                        image.setImageBitmap(bitmap);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);

                        try {

                            byte[] dadosImagem = baos.toByteArray();

                            final StorageReference ref = imageRef
                                    .child("imagens")
                                    .child("perfil")
                                    .child(id + ".png");

                            final UploadTask uploadTask = ref.putBytes(dadosImagem);

                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    alertCarregando(false);
                                    Toast.makeText(PerfilActivity.this, "Erro em atualizar a foto de perfil.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            atualizarFotoUsuario(uri);

                                        }
                                    });

                                    alertCarregando(false);
                                    Toast.makeText(PerfilActivity.this, "Sucesso em alterar a foto de perfil.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            alertCarregando(false);
                            Toast.makeText(PerfilActivity.this, "Erro em atualizar a foto de perfil.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        alertCarregando(false);
                        Toast.makeText(PerfilActivity.this, "Erro em atualizar a foto de perfil.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void atualizarFotoUsuario(Uri url){

        UsuarioFirebase.atualizarFotoUsuario(url);
        usuarioLogado.setCaminho_foto(url.toString());
        usuarioLogado.atualizar();

    }


}
