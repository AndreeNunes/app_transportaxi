package br.senai.sp.transportxi.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.fragments.ComentariosFragment;
import br.senai.sp.transportxi.fragments.PedidosFragment;
import br.senai.sp.transportxi.fragments.MapaRequisitosEncomendadorFragment;
import br.senai.sp.transportxi.fragments.EncomendarFragment;
import br.senai.sp.transportxi.fragments.SolicitacaoFragment;
import br.senai.sp.transportxi.helper.UsuarioFirebase;
import br.senai.sp.transportxi.objetos.Obj_Usuario;
import br.senai.sp.transportxi.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class PrincipalActivityEncomendador extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private BottomNavigationView BarraMenu;
    private FirebaseAuth auth_mototista;
    private DatabaseReference banco_moto;
    private FirebaseAuth auth_user;
    private DatabaseReference banco_user;
    private Obj_Usuario usuarioLogado;
    private Obj_Usuario LOGADO;
    Button cep, padrao;
    boolean blTipo_usuario;
    AlertDialog alertDialog;
    private List<Obj_Usuario> listUser = new ArrayList<>();
    MenuItem item2;
    private AlertDialog alertSair;
    public static CircleImageView FotoPerfilE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth_user = Config_firebase.getAuth_transportaxi();
        banco_user = Config_firebase.getBanco_tansportaxi();
        iniciandoEncomendador();
        DatabaseReference banco = Config_firebase.getBanco_tansportaxi();
        criarNotificacaoViagem();
        criarNotificacaoOrcamento();

    }

    public void criarNotificacaoOrcamento(){

        DatabaseReference reference = Config_firebase.getBanco_tansportaxi()
                .child("solicitacoes");

        Query query = reference.orderByChild("idEncomendador")
                .equalTo(UsuarioFirebase.getIdUsuario());

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int id = 1;
                String titulo = "Transportáxi";
                String texto = "Existe um novo orçamento para você.";
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

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void criarNotificacaoViagem(){

        DatabaseReference reference = Config_firebase.getBanco_tansportaxi()
                .child("viagem");

        Query query = reference.orderByChild("idEncomendador")
                .equalTo(UsuarioFirebase.getIdUsuario());

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                int id = 1;
                String titulo = "Transportáxi";
                String texto = "A viagem da sua carga foi iniciada.";
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



    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {

        Uri url = UsuarioFirebase.getUsuarioAtual().getPhotoUrl();

        try
        {
            if(url != null) {

                Glide.with(PrincipalActivityEncomendador.this)
                        .load(url)
                        .into(FotoPerfilE);
            }else{
                FotoPerfilE.setImageResource(R.drawable.semftperfil);
            }
        }
        catch (Exception e)
        {
        }

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);

        final TextView nome = (TextView) findViewById(R.id.tvNavNomeUsuario);
        TextView email = (TextView) findViewById(R.id.tvNavEmailUsuario);
        FotoPerfilE = (CircleImageView) findViewById(R.id.imgPerfilEncomendador);

        Uri url = UsuarioFirebase.getUsuarioAtual().getPhotoUrl();

        if(url != null){

            Glide.with(PrincipalActivityEncomendador.this)
                    .load(url)
                    .into(FotoPerfilE);
        }else{
            FotoPerfilE.setImageResource(R.drawable.semftperfil);
        }

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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.logout);
            builder.setTitle("Sair");
            builder.setMessage("Deseja realmente fazer logout ?");
            builder.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent i = new Intent(PrincipalActivityEncomendador.this, LoginActivity.class);
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
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

     if (id == R.id.nav_perfil) {

            Intent i = new Intent(PrincipalActivityEncomendador.this, PerfilActivity.class);
            startActivity(i);

        }  else if (id == R.id.nav_contato) {

            Intent email = new Intent( Intent.ACTION_SEND );
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"AgileCompany2018@gmail.com" } );
            email.putExtra(Intent.EXTRA_SUBJECT, "Contato pelo App" );

            //configurar apps para e-mail
            email.setType("message/rfc822");
            //email.setType("application/pdf");
            //email.setType("image/png");

            startActivity( Intent.createChooser(email, "Escolha o App de e-mail:" ) );

        }  else if (id == R.id.nav_solicitacao) {

            SolicitacaoFragment solicitacaoFragment = new SolicitacaoFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.telaFrame, solicitacaoFragment);
            fragmentTransaction.commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void iniciandoEncomendador()
    {
        setContentView(R.layout.activity_principal_encomendador);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PedidosFragment pedidosFragment = new PedidosFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.telaFrame, pedidosFragment);
        fragmentTransaction.commit();


        BarraMenu = (BottomNavigationView) findViewById(R.id.BarraMenu);

        BarraMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    item2 = item;

                    if (item2.getItemId() == R.id.BarraMenuLista) {

                        PedidosFragment pedidosFragment = new PedidosFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.telaFrame, pedidosFragment);
                        item.setChecked(true);
                        fragmentTransaction.commit();
                    }

                    else if (item2.getItemId() == R.id.BarraMenuPedido){

                        EncomendarFragment encomendarFragment = new EncomendarFragment();
                        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.telaFrame, encomendarFragment);
                        item.setChecked(true);
                        fragmentTransaction.commit();

                    }

                    return false;
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
}
