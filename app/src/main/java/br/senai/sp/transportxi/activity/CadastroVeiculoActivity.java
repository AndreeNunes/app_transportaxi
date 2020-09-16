package br.senai.sp.transportxi.activity;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import br.senai.sp.transportxi.R;
import br.senai.sp.transportxi.helper.UsuarioFirebase;
import br.senai.sp.transportxi.objetos.Mask;
import br.senai.sp.transportxi.objetos.Obj_veiculo;

public class CadastroVeiculoActivity extends AppCompatActivity
{

    private TextInputLayout nomeVeiculo;
    private TextInputLayout placaVeiculo;
    private Switch tipo;
    DatabaseReference banco;
    String idUser;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_veiculo);
        idUser = UsuarioFirebase.getIdUsuario();

        nomeVeiculo = (TextInputLayout) findViewById(R.id.text_nome_veiculo);
        placaVeiculo = (TextInputLayout) findViewById(R.id.text_placa_veiculo);
        tipo = (Switch) findViewById(R.id.tipo_transportador);

        placaVeiculo.getEditText().addTextChangedListener(Mask.mask(placaVeiculo.getEditText(), Mask.FORMAT_PLACA));

    }

    public void finalizaBotao(View v)
    {
        String sNomeVeiculo = nomeVeiculo.getEditText().getText().toString();
        String sPlacaVeiculo = placaVeiculo.getEditText().getText().toString();

        if(!sNomeVeiculo.isEmpty()){

            if(!sPlacaVeiculo.isEmpty()){

                Obj_veiculo veiculo = new Obj_veiculo();

                veiculo.setId(idUser);
                veiculo.setNomeVeiculo(sNomeVeiculo);
                veiculo.setPlacaVeiculo(sPlacaVeiculo);
                if(tipo.isChecked()){
                    //MOTOBOY
                    veiculo.setTipoServiço("M");
                }
                else
                {
                    //CAMIONEIRO
                    veiculo.setTipoServiço("C");
                }

                veiculo.salvar();
                Intent i = new Intent(CadastroVeiculoActivity.this, PrincipalActivityTransportador.class);
                startActivity(i);

            }else{
                Toast.makeText(this,"Preencha a placa do veículo",Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this,"Preencha nome do veículo",Toast.LENGTH_SHORT).show();
        }

        System.out.println("+++++++++++++++++++++++++++++="+idUser);
    }
}
