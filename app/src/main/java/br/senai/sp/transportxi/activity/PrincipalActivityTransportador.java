package br.senai.sp.transportxi.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import br.senai.sp.transportxi.R;
import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.fragments.AgendaFragment;
import br.senai.sp.transportxi.fragments.PedidosFragment;
import br.senai.sp.transportxi.fragments.RequisicoesFragment;
import br.senai.sp.transportxi.fragments.SolicitacaoFragment;
import br.senai.sp.transportxi.fragments.ViagemFragment;
import br.senai.sp.transportxi.helper.UsuarioFirebase;
import br.senai.sp.transportxi.objetos.Agenda;
import br.senai.sp.transportxi.objetos.Obj_Usuario;
import br.senai.sp.transportxi.objetos.Obj_veiculo;
import de.hdodenhof.circleimageview.CircleImageView;

public class PrincipalActivityTransportador extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference banco_user;
    FirebaseAuth auth_user;
    AlertDialog alertSair;
    public CircleImageView FotoPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_transportador);
        inicializandoTransportador();
        criarNotificacaoAgenda();
    }

    public void criarNotificacaoAgenda(){

        final DatabaseReference reference = Config_firebase.getBanco_tansportaxi()
                .child("agenda");

        Query query = reference.orderByChild("idTransportador")
                .equalTo(UsuarioFirebase.getIdUsuario());

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int id = 1;
                String titulo = "Transportáxi";
                String texto = "Existe um novo agendamento esperando por você.";


                int icone = R.drawable.pedido_icon;

                Intent intent = new Intent(getApplicationContext(), PrincipalActivityTransportador.class);
                PendingIntent p = getPendingIntent(id, intent, getApplicationContext());

                NotificationCompat.Builder notificacao = new NotificationCompat.Builder(getApplicationContext());
                notificacao.setSmallIcon(icone);
                notificacao.setContentTitle(titulo);
                notificacao.setContentText(texto);
                notificacao.setContentIntent(p);

                NotificationManagerCompat nm = NotificationManagerCompat.from(getApplicationContext());
                nm.notify(id, notificacao.build());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int id = 1;

                for (DataSnapshot ds: dataSnapshot.getChildren()) {

                    try {
                        Agenda a = ds.getValue(Agenda.class);

                        if (a.getStatus().equals(Agenda.STATUS_CANCELOU)) {

                            String titulo = "Transportaxi";
                            String texto = "O encomendador desitiu do transporte.";
                            int icone = R.drawable.pedido_icon;

                            Intent intent = new Intent(getApplicationContext(), PrincipalActivityTransportador.class);
                            PendingIntent p = getPendingIntent(id, intent, getApplicationContext());

                            NotificationCompat.Builder notificacao = new NotificationCompat.Builder(getApplicationContext());
                            notificacao.setSmallIcon(icone);
                            notificacao.setContentTitle(titulo);
                            notificacao.setContentText(texto);
                            notificacao.setContentIntent(p);

                            NotificationManagerCompat nm = NotificationManagerCompat.from(getApplicationContext());
                            nm.notify(id, notificacao.build());

                        }
                    }catch (Exception e){

                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private PendingIntent getPendingIntent(int id, Intent intent, Context context) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(intent.getComponent());
        stackBuilder.addNextIntent(intent);

        PendingIntent p = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);
        return p;
    }



        private void inicializandoTransportador() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setElevation(0);
        toolbar.setTitle("Transportáxi");

        setSupportActionBar(toolbar);
        auth_user = Config_firebase.getAuth_transportaxi();
        banco_user = Config_firebase.getBanco_tansportaxi();
        DatabaseReference banco = Config_firebase.getBanco_tansportaxi();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RequisicoesFragment requisicoesFragment = new RequisicoesFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.telaFrameTransportador, requisicoesFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {

        Uri url = UsuarioFirebase.getUsuarioAtual().getPhotoUrl();

        try
        {

            Glide.with(PrincipalActivityTransportador.this)
                    .load(url)
                    .into(FotoPerfil);
        }
        catch (Exception e)
        {

        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_transportador, menu);

        final TextView nome = (TextView) findViewById(R.id.tvNavNomeUsuarioT);
        TextView email = (TextView) findViewById(R.id.tvNavEmailUsuarioT);
        //final TextView tipo = (TextView) findViewById(R.id.tipoServiço);

        FotoPerfil = (CircleImageView) findViewById(R.id.imgPerfilTransportador);


        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();

        Uri url = usuarioPerfil.getPhotoUrl();

        if(url != null){

            Glide.with(PrincipalActivityTransportador.this)
                    .load(url)
                    .into(FotoPerfil);
        }else{
            FotoPerfil.setImageResource(R.drawable.semftperfil);
        }

        System.out.println("++++++++++++++++++++++++++++++++=="+url);


        String id = UsuarioFirebase.getUsuarioAtual().getUid();

        DatabaseReference pesquisa = banco_user.child("usuarios");

        Query pesquisaQ = pesquisa.orderByChild("id").equalTo(id);

        pesquisaQ.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                    Obj_Usuario usuario = dataSnapshot1.getValue(Obj_Usuario.class);

                    nome.setText(usuario.getNome_user());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String sNome = UsuarioFirebase.getUsuarioAtual().getDisplayName();
        String sEmail = UsuarioFirebase.getUsuarioAtual().getEmail();

        email.setText(sEmail);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout_t)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.logout);
            builder.setTitle("Sair");
            builder.setMessage("Deseja realmente fazer logout ?");
            builder.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(PrincipalActivityTransportador.this, LoginActivity.class);
                    startActivity(i);
                    auth_user.signOut();
                    finish();
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });

            alertSair = builder.create();
            alertSair.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if(id == R.id.nav_requisicoes_t){

            RequisicoesFragment requisicoesFragment = new RequisicoesFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.telaFrameTransportador, requisicoesFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_perfil_t) {

            Intent i = new Intent(PrincipalActivityTransportador.this, PerfilActivity.class);
            startActivity(i);

        }else if (id == R.id.nav_agenda_t){

            AgendaFragment agendaFragment = new AgendaFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.telaFrameTransportador, agendaFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_contato_t){

            Intent email = new Intent( Intent.ACTION_SEND );
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"AgileCompany2018@gmail.com" } );
            email.putExtra(Intent.EXTRA_SUBJECT, "Contato pelo App" );

            //configurar apps para e-mail
            email.setType("message/rfc822");
            //email.setType("application/pdf");
            //email.setType("image/png");

            startActivity( Intent.createChooser(email, "Escolha o App de e-mail:" ) );

        } else if (id == R.id.nav_viagem_t){

            ViagemFragment viagemFragment = new ViagemFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.telaFrameTransportador, viagemFragment);
            fragmentTransaction.commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
