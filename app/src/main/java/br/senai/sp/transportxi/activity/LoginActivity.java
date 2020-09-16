package br.senai.sp.transportxi.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.R;
import br.senai.sp.transportxi.helper.Permissoes;
import br.senai.sp.transportxi.helper.UsuarioFirebase;
import br.senai.sp.transportxi.objetos.Obj_Usuario;

public class LoginActivity extends AppCompatActivity
{
    TextView tvCadastro;
    private FirebaseAuth auth_user;
    private DatabaseReference login_banco;
    private TextInputLayout textEmail, textSenha;
    private String[] permissoesNessesarias = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    ProgressBar progressBar;
    boolean CONECTADO;
    AlertDialog alerta;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        isNetworkAvailable();

            setContentView(R.layout.activity_login);

            Permissoes.validarPermissoes(permissoesNessesarias,this,1);

            progressBar =  (ProgressBar)     findViewById(R.id.progressLogin);
            textEmail   =  (TextInputLayout) findViewById(R.id.text_email);
            textSenha   =  (TextInputLayout) findViewById(R.id.text_senha);
            tvCadastro  =  (TextView)        findViewById(R.id.tvCadastro);

            tvCadastro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent Cadastro = new Intent(LoginActivity.this, CadastroActivity.class);
                    startActivity(Cadastro);
                }
            });
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

    @Override
    protected void onStart() {
        super.onStart();

        if(CONECTADO){


            UsuarioFirebase.redirecionaUsuarioLogado(LoginActivity.this);

            FirebaseUser user = UsuarioFirebase.getUsuarioAtual();


            if(user != null){

                //tvCadastro.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

            }else{

                progressBar.setVisibility(View.GONE);
            }

        }else{
            alertSemInternet();
        }

    }

    private void isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            CONECTADO = true;
        } else {
            CONECTADO = false;
        }
    }

    private void alertSemInternet(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.seminternet);
        builder.setTitle("Sem Conexão");
        builder.setMessage("Seu dispositivo não está conectado à internet. Por favor, tente novamente mais tarde.");
        builder.setPositiveButton("Sair", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alerta = builder.create();
        alerta.show();
        alerta.setCanceledOnTouchOutside(false);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i : grantResults){

            if(i == PackageManager.PERMISSION_DENIED){
               permissaoNegada();
            }
        }
    }

    private void permissaoNegada(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões.");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private boolean validarEmail(){
        String email = textEmail.getEditText().getText().toString().trim();

        if(email.isEmpty()){
            return false;
        }else{
            textEmail.setError(null);
            return true;
        }
    }

    private boolean validarSenha(){
        String senha = textSenha.getEditText().getText().toString().trim();

        if(senha.isEmpty()){
            return false;
        }else if (senha.length() < 5 ){
            return false;
        }else{
            textSenha.setError(null);
            return true;
        }
    }

    public void entrar(View v)
    {
        String email = textEmail.getEditText().getText().toString();
        String senha = textSenha.getEditText().getText().toString();

        alertCarregando(true);

        if(validarEmail()){

            if(validarSenha()){

                //Logar um usuario
                logarUsuario( email, senha);

            }else{
                alertCarregando(false);
                Toast.makeText(this, "Preencha a senha.", Toast.LENGTH_SHORT).show();
            }

        }else{
            alertCarregando(false);
            Toast.makeText(this, "Preencha o email.", Toast.LENGTH_SHORT).show();
        }
    }

    public void logarUsuario(String email, String senha){

        auth_user = Config_firebase.getAuth_transportaxi();

        auth_user.signInWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    UsuarioFirebase.redirecionaUsuarioLogado(LoginActivity.this);
                }else{
                    alertCarregando(false);
                    Toast.makeText(LoginActivity.this,"Email ou Senha estão errados.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
