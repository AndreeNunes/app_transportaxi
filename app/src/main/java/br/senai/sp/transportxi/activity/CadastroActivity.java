package br.senai.sp.transportxi.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.UUID;

import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.helper.UsuarioFirebase;
import br.senai.sp.transportxi.objetos.Mask;
import br.senai.sp.transportxi.objetos.Obj_Usuario;
import br.senai.sp.transportxi.R;

public class CadastroActivity extends AppCompatActivity {
    TextInputLayout etNome, etSobre, etData, etTel, etEmail, etSenha, etConfirmaSenha;
    String Nome, Sobre, data, Tel, Email, Senha, ConfirmaSenha;
    Button btnEnviar, btnDate;
    TextView tvTitulo;
    private StorageReference storage;
    private FirebaseAuth auth_usuario;
    private DatabaseReference cadastro;
    private Switch TipoUsuario;
    private AlertDialog carrega;
    static final int DATE_DIALOG_ID = 0;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        iniciando();

        etData.getEditText().addTextChangedListener(Mask.mask(etData.getEditText(), Mask.FORMAT_DATE));
        etTel.getEditText().addTextChangedListener(Mask.mask(etTel.getEditText(), Mask.FORMAT_FONE));
    }


    public void pegandoValorEditText() {
        Nome = etNome.getEditText().getText().toString();
        data = etData.getEditText().getText().toString();
        Tel = etTel.getEditText().getText().toString();
        Email = etEmail.getEditText().getText().toString();
        Senha = etSenha.getEditText().getText().toString();
        ConfirmaSenha = etConfirmaSenha.getEditText().getText().toString();
    }

    public void iniciando() {
        etData = (TextInputLayout) findViewById(R.id.text_data_nascimento);
        etConfirmaSenha = (TextInputLayout) findViewById(R.id.text_confirma_senha);
        etNome = (TextInputLayout) findViewById(R.id.text_nome);
        etTel = (TextInputLayout) findViewById(R.id.text_telefone);
        etEmail = (TextInputLayout) findViewById(R.id.text_email);
        etSenha = (TextInputLayout) findViewById(R.id.text_senha);
        TipoUsuario = (Switch) findViewById(R.id.TipoUsuario);
    }

    public void cadastrar(View v) {
        verificaDadosMask();
        validarData();

        if (TipoUsuario.isChecked()) cadastroTransportador();
        else cadastroEncomendador();
    }

    public void alertCarregando(Boolean loading) {
        if (loading) {
            dialog = new Dialog(this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.carregando);
            dialog.show();
        } else dialog.dismiss();
    }

    public void voltarTelaLogin(View v){
        new Intent(CadastroActivity.this, LoginActivity.class);
        finish();
    }

    public boolean validarData() {
        boolean retorno = false;
        String date = etData.getEditText().getText().toString();

        if(date.length() > 0) {
            String dia = date.substring(0, 2);
            String mes = date.substring(3, 5);
            String ano = date.substring(6, 10);

            if ((Integer.parseInt(dia) >= 1 && Integer.parseInt(dia) <= 31) && Integer.parseInt(mes) >= 1 && Integer.parseInt(mes) <= 12 && Integer.parseInt(ano) >= 1900 && Integer.parseInt(ano) <= 2000) {
                retorno = true;
            }
        }

        return retorno;
    }

    public void cadastroTransportador() {
        alertCarregando(true);
        pegandoValorEditText();

        if (!Nome.equals("")) {
            if (!data.equals("") && validarData()) {
                if (!Tel.equals("")) {
                    if (!Email.equals("")) {
                        if (!Senha.equals("")) {
                            if (!ConfirmaSenha.equals("") && ConfirmaSenha.equals(Senha)) {
                                Obj_Usuario usuario = new Obj_Usuario();
                                usuario.setNome_user(Nome);
                                usuario.setData_user(data);
                                usuario.setTelefone_user(Tel);
                                usuario.setEmail_user(Email);
                                usuario.setSenha_user(Senha);
                                usuario.setCaminho_foto("vazio");
                                cadastrandoTransportador(usuario);
                            } else {
                                alertCarregando(false);
                                Toast.makeText(this,
                                        "Preencha a senha corretamente !",
                                        Toast.LENGTH_SHORT).show();
                                //ivCofirmaError.setImageResource(R.drawable.ic_error_24dp);
                            }
                        } else {
                            alertCarregando(false);
                            Toast.makeText(this,
                                    "Preencha a senha !",
                                    Toast.LENGTH_SHORT).show();
                            //ivSenhaError.setImageResource(R.drawable.ic_error_24dp);
                        }
                    } else {
                        alertCarregando(false);
                        Toast.makeText(this,
                                "Preencha o email !",
                                Toast.LENGTH_SHORT).show();
                        //ivEmailError.setImageResource(R.drawable.ic_error_24dp);

                    }
                } else {
                    alertCarregando(false);
                    Toast.makeText(this,
                            "Preencha o telefone!",
                            Toast.LENGTH_SHORT).show();
                    //ivTelError.setImageResource(R.drawable.ic_error_24dp);
                }
            } else {
                alertCarregando(false);
                Toast.makeText(this,
                        "preencha a data corretamente !",
                        Toast.LENGTH_SHORT).show();
                //ivDataError.setImageResource(R.drawable.ic_error_24dp);
            }
        } else {
            alertCarregando(false);
            Toast.makeText(this,
                    "Preencha o nome !",
                    Toast.LENGTH_SHORT).show();
            //ivNomeError.setImageResource(R.drawable.ic_error_24dp);
        }
    }

    public void cadastrandoTransportador(Obj_Usuario usuario) {
        auth_usuario = Config_firebase.getAuth_transportaxi();
        cadastro = Config_firebase.getBanco_tansportaxi().child("usuarios");

        auth_usuario.createUserWithEmailAndPassword(usuario.getEmail_user(), usuario.getSenha_user())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pegandoValorEditText();
                            Obj_Usuario usuario = new Obj_Usuario();

                            String idUsuario = task.getResult().getUser().getUid();
                            usuario.setId(idUsuario);
                            usuario.setNome_user(Nome);
                            usuario.setData_user(data);
                            usuario.setTelefone_user(Tel);
                            usuario.setEmail_user(Email);
                            usuario.setSenha_user(Senha);
                            usuario.setCaminho_foto("vazio");
                            usuario.setTipo_usuario("T"); //transportador
                            usuario.salvar();

                            Intent i = new Intent(CadastroActivity.this, CadastroVeiculoActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            String erro = "";
                            alertCarregando(false);

                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                erro = "Senha muito fraca, tente novamente.";

                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                erro = "Email digitado é invalido.";
                            } catch (FirebaseAuthUserCollisionException e) {
                                erro = "Conta já existente.";
                            } catch (Exception e) {
                                erro = "Ocorreu algum erro, tente novamente.";
                            }

                            Toast.makeText(CadastroActivity.this,
                                    erro, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void cadastroEncomendador() {

        pegandoValorEditText();
        alertCarregando(true);

        if (!Nome.equals("")) {
            if (!data.equals("") && validarData()) {
                if (!Tel.equals("")) {
                    if (!Email.equals("")) {
                        if (!Senha.equals("")) {
                            if (!ConfirmaSenha.equals("") && ConfirmaSenha.equals(Senha)) {
                                Obj_Usuario usuario = new Obj_Usuario();
                                usuario.setNome_user(Nome);
                                usuario.setData_user(data);
                                usuario.setTelefone_user(Tel);
                                usuario.setEmail_user(Email);
                                usuario.setSenha_user(Senha);
                                usuario.setCaminho_foto("vazio");
                                cadastrandoEncomendador(usuario);
                            } else {
                                alertCarregando(false);
                                Toast.makeText(this,
                                        "preencha a senha corretamente!",
                                        Toast.LENGTH_SHORT).show();
                                //ivCofirmaError.setImageResource(R.drawable.ic_error_24dp);
                            }
                        } else {
                            alertCarregando(false);
                            Toast.makeText(this,
                                    "Preencha a senha !",
                                    Toast.LENGTH_SHORT).show();
                            //ivSenhaError.setImageResource(R.drawable.ic_error_24dp);
                        }
                    } else {
                        alertCarregando(false);
                        Toast.makeText(this,
                                "Preencha o email !",
                                Toast.LENGTH_SHORT).show();
                        //ivEmailError.setImageResource(R.drawable.ic_error_24dp);

                    }
                } else {
                    alertCarregando(false);
                    Toast.makeText(this,
                            "Preencha o telefone !",
                            Toast.LENGTH_SHORT).show();
                    //ivTelError.setImageResource(R.drawable.ic_error_24dp);
                }
            } else {
                alertCarregando(false);
                Toast.makeText(this,
                        "preencha a data corretamente !",
                        Toast.LENGTH_SHORT).show();
                //ivDataError.setImageResource(R.drawable.ic_error_24dp);

            }

        } else {
            alertCarregando(false);
            Toast.makeText(this,
                    "Preencha o nome !",
                    Toast.LENGTH_SHORT).show();
            //ivNomeError.setImageResource(R.drawable.ic_error_24dp);
        }
    }

    public void cadastrandoEncomendador(Obj_Usuario usuario) {
        auth_usuario = Config_firebase.getAuth_transportaxi();
        cadastro = Config_firebase.getBanco_tansportaxi().child("usuarios");

        auth_usuario.createUserWithEmailAndPassword(usuario.getEmail_user(), usuario.getSenha_user())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pegandoValorEditText();
                            Obj_Usuario usuario = new Obj_Usuario();

                            String idUsuario = task.getResult().getUser().getUid();
                            usuario.setId(idUsuario);
                            usuario.setId(idUsuario);
                            usuario.setNome_user(Nome);
                            usuario.setData_user(data);
                            usuario.setTelefone_user(Tel);
                            usuario.setEmail_user(Email);
                            usuario.setSenha_user(Senha);
                            usuario.setCaminho_foto("vazio");
                            usuario.setTipo_usuario("E"); //encomendador
                            usuario.salvar();// (usuario);

                            Intent i = new Intent(CadastroActivity.this, PrincipalActivityEncomendador.class);
                            startActivity(i);
                            finish();

                        } else {
                            String erro = "";
                            alertCarregando(false);

                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                erro = "Senha muito fraca, tente novamente.";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                erro = "Email digitado é invalido.";
                                etEmail.clearFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                erro = "Conta já existente.";
                            } catch (Exception e) {
                                erro = "Ocorreu algum erro." + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(CadastroActivity.this,
                                    erro, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public boolean verificaDadosMask() {


        return true;
    }
}
