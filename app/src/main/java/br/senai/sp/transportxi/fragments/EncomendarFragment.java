package br.senai.sp.transportxi.fragments;


import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import br.senai.sp.transportxi.Cep.APIRetrofitService;
import br.senai.sp.transportxi.Cep.CEP;
import br.senai.sp.transportxi.Cep.CEPDeserializer;
import br.senai.sp.transportxi.R;
import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.helper.UsuarioFirebase;
import br.senai.sp.transportxi.objetos.Pedido;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class EncomendarFragment extends Fragment implements View.OnClickListener{

    private TextInputLayout objeto, cidadeP, bairroP, ruaP, numeroP, cidadeD, bairroD, ruaD,
            numeroD, complementoP, complementoD, cepP, cepD;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SELECT_FILE = 2;
    private StorageReference storageReference;
    private CheckBox checkBox;
    private DatabaseReference pedido_banco;
    boolean envia_altera = true;
    boolean mudança = false;
    String idImg, temImg;
    Button enviar, tirar, buscarPartidaCep, buscarDestinoCep;
    String localPartida, localDestino, objetoC;
    AlertDialog alertDialog, erro, carregando;

    String cCidade, cBairro, cRua, cNumero, cEstado, cCep, cdCidade, cdBairro, cdRua, cdNumero,
            cdEstado, cdCep, cObjeto, cId, sComplementoP, sComplementoD;

    TextView objetoA, cidadeAP, bairroAP, ruaAP, numeroAP, cidadeAD, bairroAD, ruaAD, numeroAD,
            alterarPedido, complementoPAD, complementoDAD;

    ImageView image_foto;
    RoundKornerRelativeLayout roundKornerRelativeLayout;

    private ArrayList<CEP> arrayCEPs;

    private static final String TAG = "logCEP";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_encomendar, container, false);

        buscarDestinoCep = (Button) v.findViewById(R.id.buscarDestinoPorCep);
        cepD = (TextInputLayout) v.findViewById(R.id.cepD);
        cepP = (TextInputLayout) v.findViewById(R.id.cepP);
        buscarPartidaCep = (Button) v.findViewById(R.id.buscarPartidaPorCep);
        complementoP = (TextInputLayout) v.findViewById(R.id.encomendar_complementarP);
        complementoD = (TextInputLayout) v.findViewById(R.id.encomendar_complementoD);
        checkBox = (CheckBox) v.findViewById(R.id.checkBox_encomendar);
        alterarPedido = (TextView) v.findViewById(R.id.textAlterarPedido);
        roundKornerRelativeLayout = (RoundKornerRelativeLayout) v.findViewById(R.id.roundKornerRelativeLayout);
        enviar = (Button) v.findViewById(R.id.button_enviar);
        tirar = (Button) v.findViewById(R.id.button_tirar);
        image_foto = (ImageView) v.findViewById(R.id.imageViewCarga);
        objeto = (TextInputLayout) v.findViewById(R.id.textInputLayoutObjeto);
        cidadeP = (TextInputLayout) v.findViewById(R.id.textInputLayoutCidadeP);
        cidadeD = (TextInputLayout) v.findViewById(R.id.textInputLayoutCidade_d);
        bairroP = (TextInputLayout) v.findViewById(R.id.textInputLayoutBairroP);
        bairroD = (TextInputLayout) v.findViewById(R.id.textInputLayout1Bairro_d);
        ruaP = (TextInputLayout) v.findViewById(R.id.textInputLayoutRuaP);
        ruaD = (TextInputLayout) v.findViewById(R.id.textInputLayout1Rua_d);
        numeroP = (TextInputLayout) v.findViewById(R.id.textInputLayoutNumeroP);
        numeroD = (TextInputLayout) v.findViewById(R.id.textInputLayoutNumero_d);


        buscarDestinoCep.setOnClickListener(this);
        buscarPartidaCep.setOnClickListener(this);
        checkBox.setOnClickListener(this);
        enviar.setOnClickListener(this);
        tirar.setOnClickListener(this);

        return v;
    }

    public void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(Intent.createChooser(intent, "Tire uma Foto"), REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            roundKornerRelativeLayout.setVisibility(View.VISIBLE);
            Bundle bundle = data.getExtras();
            final Bitmap bitmap = (Bitmap) bundle.get("data");
            image_foto.setImageBitmap(bitmap);
            envia_altera = false;
            tirar.setText("Excluir Foto");
            tirar.setBackgroundResource(R.drawable.estilo_botao_excluir);
        }

        if(requestCode == SELECT_FILE && resultCode == RESULT_OK){

            roundKornerRelativeLayout.setVisibility(View.VISIBLE);
            Uri selectImageUri = data.getData();
            image_foto.setImageURI(selectImageUri);
            envia_altera = false;
            tirar.setText("Excluir Foto");
            tirar.setBackgroundResource(R.drawable.estilo_botao_excluir);

        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId())
        {

            case R.id.checkBox_encomendar:

                if(checkBox.isChecked()){
                   mudança = true;
                   objeto.setEnabled(false);
                }else{
                    objeto.setEnabled(true);
                    mudança = false;
                }

                break;

            case R.id.button_tirar:

                if(envia_altera)
                {
                    selectImage();
                }
                else
                {
                    envia_altera = true;

                    storageReference = Config_firebase.getBanco_storage();

                    StorageReference imagens = storageReference.child("imagens/pedidos");

                    String id = UsuarioFirebase.getIdUsuario();

                    StorageReference pedidoRef = imagens.child(id+".png");

                    pedidoRef.delete();

                    tirar.setText("Adicionar Foto");
                    tirar.setBackgroundResource(R.drawable.estilo_botao_comentario);

                    roundKornerRelativeLayout.setVisibility(View.GONE);
                }
                break;

            case R.id.button_enviar:

                    String cidade = cidadeP.getEditText().getText().toString();
                    String bairro = bairroP.getEditText().getText().toString();
                    String rua = ruaP.getEditText().getText().toString();
                    String numero = numeroP.getEditText().getText().toString();
                    String sComplementoP = complementoP.getEditText().getText().toString();
                    String sComplementoD = complementoD.getEditText().getText().toString();
                    String cidade2 = cidadeD.getEditText().getText().toString();
                    String bairro2 = bairroD.getEditText().getText().toString();
                    String rua2 = ruaD.getEditText().getText().toString();
                    String numero2 = numeroD.getEditText().getText().toString();

                    localPartida = cidade + " " + bairro + " " + rua + " " + numero;
                    localDestino = cidade2 + " " + bairro2 + " " + rua2 + " " + numero2;

                    Address addressPartida = recuperarEndereco(localPartida);
                    Address addressDestino = recuperarEndereco(localDestino);

                    try {

                        image_foto.setDrawingCacheEnabled(true);
                        image_foto.buildDrawingCache();

                        Bitmap bitmap = image_foto.getDrawingCache();

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                        byte[] dadosImagem = baos.toByteArray();

                        storageReference = Config_firebase.getBanco_storage();

                        StorageReference imagens = storageReference.child("imagens/pedidos");

                        idImg = UUID.randomUUID().toString();

                        StorageReference pedidoRef = imagens.child(idImg + ".png");

                        UploadTask uploadTask = pedidoRef.putBytes(dadosImagem);

                        temImg = "tem";

                    }catch (Exception e){

                        temImg = "vazio";

                    }


                    if (addressPartida != null && addressDestino != null && verificaDados()) {

                        final Pedido pedido = new Pedido();

                        Uri foto = UsuarioFirebase.getUsuarioAtual().getPhotoUrl();


                        pedido.setDataConfirmada("Data ainda não confirmada");
                        pedido.setCaminhoFotoPerfil(String.valueOf(foto));
                        pedido.setLatP(addressPartida.getLatitude());
                        pedido.setLonP(addressPartida.getLongitude());
                        pedido.setCidadeP(addressPartida.getSubAdminArea());
                        pedido.setBairroP(addressPartida.getSubLocality());
                        pedido.setRuaP(addressPartida.getThoroughfare());
                        pedido.setNumeroP(addressPartida.getFeatureName());
                        pedido.setCepP(addressPartida.getPostalCode());
                        pedido.setEstadoP(addressPartida.getAdminArea());
                        pedido.setLatD(addressDestino.getLatitude());
                        pedido.setLonD(addressDestino.getLongitude());
                        pedido.setCidadeD(addressDestino.getSubAdminArea());
                        pedido.setBairroD(addressDestino.getSubLocality());
                        pedido.setRuaD(addressDestino.getThoroughfare());
                        pedido.setNumeroD(addressDestino.getFeatureName());
                        pedido.setCepD(addressDestino.getPostalCode());
                        pedido.setEstadoD(addressDestino.getAdminArea());
                        pedido.setIdImg(idImg);


                        cId = UUID.randomUUID().toString();

                        String idUsuario = UsuarioFirebase.getIdUsuario();

                        if(temImg.equalsIgnoreCase("vazio")){
                            pedido.setTemImg("vazio");
                        }else if (temImg.equalsIgnoreCase("tem")){
                            pedido.setTemImg("tem");
                        }

                        pedido.setAlerta("Nenhuma Alerta");
                        pedido.setComplementoP(sComplementoP);
                        pedido.setComplementoD(sComplementoD);
                        pedido.setIdUsuario(idUsuario);
                        pedido.setStatus(Pedido.STATUS_AGUARDANDO);
                        pedido.setIdPedido(cId);
                        pedido.setObjeto(objetoC);
                        cCidade = pedido.getCidadeP();
                        cBairro = pedido.getBairroP();
                        cRua = pedido.getRuaP();
                        cNumero = pedido.getNumeroP();
                        cEstado = pedido.getEstadoP();
                        cCep = pedido.getCepP();
                        cdCidade = pedido.getCidadeD();
                        cdBairro = pedido.getBairroD();
                        cdRua = pedido.getRuaD();
                        cdNumero = pedido.getNumeroD();
                        cdEstado = pedido.getEstadoD();
                        cdCep = pedido.getCepD();

                        AlertDialog.Builder criaAlerta = (new AlertDialog.Builder(getContext()));
                        LayoutInflater li = getLayoutInflater();
                        View visualAlert = li.inflate(R.layout.confirmacao_pedido, null);

                        complementoPAD = visualAlert.findViewById(R.id.confirma_pedido_complementoP);
                        complementoDAD = visualAlert.findViewById(R.id.confirma_pedido_complementoD);
                        objetoA = visualAlert.findViewById(R.id.requisiscao_objetoA);
                        cidadeAP = visualAlert.findViewById(R.id.requisiscao_cidade_ap);
                        bairroAP = visualAlert.findViewById(R.id.requisiscao_bairro_ap);
                        ruaAP = visualAlert.findViewById(R.id.requisiscao_rua_ap);
                        numeroAP = visualAlert.findViewById(R.id.requisiscao_numero_ap);
                        cidadeAD = visualAlert.findViewById(R.id.requisiscao_cidade_ad);
                        bairroAD = visualAlert.findViewById(R.id.requisiscao_bairro_ad);
                        ruaAD = visualAlert.findViewById(R.id.requisiscao_rua_ad);
                        numeroAD = visualAlert.findViewById(R.id.requisiscao_numero_ad);

                        objetoA.setText("Objeto: " + objetoC);

                        cidadeAP.setText("Cidade: " + cCidade);
                        bairroAP.setText("Bairro: " + cBairro);
                        ruaAP.setText("Rua: " + cRua);
                        numeroAP.setText("Numero: " + cNumero);

                        String cp = complementoP.getEditText().getText().toString();
                        String cd = complementoD.getEditText().getText().toString();

                        if(cp.equalsIgnoreCase("")){
                            complementoP.setVisibility(View.GONE);
                        }else if (cd.equalsIgnoreCase("")){
                            complementoD.setVisibility(View.GONE);
                        }

                        if(!cp.equalsIgnoreCase("")){
                           complementoPAD.setText("Complemento: "+cp);
                        }else if (!cd.equalsIgnoreCase("")){
                            complementoDAD.setText("Complemento: "+cd);
                        }

                        cidadeAD.setText("Cidade: " + cdCidade);
                        bairroAD.setText("Bairro: " + cdBairro);
                        ruaAD.setText("Rua: " + cdRua);
                        numeroAD.setText("Numero: " + cdNumero);


                        criaAlerta.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                System.out.println("Contém imagem ou nãooooooo monte de p encher linguiça"+pedido.getTemImg());
                                pedido.salvar();
                                Toast.makeText(getContext(), "Pedido cadastrado com sucesso.", Toast.LENGTH_SHORT).show();

                                PedidosFragment pedidosFragment = new PedidosFragment();
                                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.telaFrame, pedidosFragment);
                                fragmentTransaction.commit();


                            }
                        });

                        criaAlerta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(getContext(), "Pedido cancelado", Toast.LENGTH_SHORT).show();
                            }
                        });

                        criaAlerta.setView(visualAlert);
                        alertDialog = criaAlerta.create();
                        alertDialog.show();

                    } else {

                        AlertDialog.Builder errou = new AlertDialog.Builder(getContext());
                        errou.setIcon(R.drawable.ic_error_24dp);
                        errou.setTitle("Falha");
                        errou.setMessage("Aconteceu algum erro, por favor verifique os dados e tente novamente.");
                        errou.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        erro = errou.create();
                        erro.show();

                }
            break;


            case R.id.buscarPartidaPorCep:

                Gson g = new GsonBuilder().registerTypeAdapter(CEP.class, new CEPDeserializer()).create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(APIRetrofitService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(g))
                        .build();

                final APIRetrofitService service = retrofit.create(APIRetrofitService.class);

                Call<CEP> callCEPByCEP = service.getEnderecoByCEP(cepP.getEditText().getText().toString());

                callCEPByCEP.enqueue(new Callback<CEP>() {
                    @Override
                    public void onResponse(Call<CEP> call, Response<CEP> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(
                                    getActivity().getApplicationContext(),
                                    getResources().getString(R.string.toast_erro_cep),
                                    Toast.LENGTH_LONG).show();
                        } else {

                            CEP cep = response.body();

                            cidadeP.getEditText().setText(cep.getLocalidade());
                            bairroP.getEditText().setText(cep.getBairro());
                            ruaP.getEditText().setText(cep.getLogradouro());

                            //Retorno no Log
                            Log.d(TAG, cep.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<CEP> call, Throwable t) {
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                getResources().getString(R.string.toast_erro_generico) + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });


                break;

            case R.id.buscarDestinoPorCep:

                Gson g2 = new GsonBuilder().registerTypeAdapter(CEP.class, new CEPDeserializer()).create();

                Retrofit retrofit2 = new Retrofit.Builder()
                        .baseUrl(APIRetrofitService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(g2))
                        .build();

                final APIRetrofitService service2 = retrofit2.create(APIRetrofitService.class);

                Call<CEP> callCEPByCEP2 = service2.getEnderecoByCEP(cepD.getEditText().getText().toString());

                callCEPByCEP2.enqueue(new Callback<CEP>() {
                    @Override
                    public void onResponse(Call<CEP> call, Response<CEP> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(
                                    getActivity().getApplicationContext(),
                                    getResources().getString(R.string.toast_erro_cep),
                                    Toast.LENGTH_LONG).show();
                        } else {

                            CEP cep = response.body();

                            cidadeD.getEditText().setText(cep.getLocalidade());
                            bairroD.getEditText().setText(cep.getBairro());
                            ruaD.getEditText().setText(cep.getLogradouro());

                            //Retorno no Log
                            Log.d(TAG, cep.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<CEP> call, Throwable t) {
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                getResources().getString(R.string.toast_erro_generico) + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });

                break;
        }
    }

    public boolean verificaDados(){


        if(mudança) {
            objetoC = "Mudança de casa";
            System.out.println(objetoC);

        }else{
            objetoC = objeto.getEditText().getText().toString();
            System.out.println(objetoC);
        }

        String cidade = cidadeP.getEditText().getText().toString();
        String bairro = bairroP.getEditText().getText().toString();
        String rua = ruaP.getEditText().getText().toString();
        String numero = numeroP.getEditText().getText().toString();

        String cidade2 = cidadeD.getEditText().getText().toString();
        String bairro2 = bairroD.getEditText().getText().toString();
        String rua2 = ruaD.getEditText().getText().toString();
        String numero2 = numeroD.getEditText().getText().toString();

        if(!objetoC.isEmpty()){
            if(!cidade.isEmpty()){
                if(!bairro.isEmpty()){
                    if (!rua.isEmpty()){
                        if (!numero.isEmpty()){
                            if (!cidade2.isEmpty()){
                                if (!bairro2.isEmpty()){
                                    if (!rua2.isEmpty()){
                                        if (!numero2.isEmpty()){
                                            return true;
                                        }else{
                                            return false;
                                        }
                                    }else {
                                        return false;
                                    }
                                }else {
                                    return false;
                                }
                            }else {
                                return false;
                            }
                        }else{
                            return false;
                        }
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }else {
            return false;
        }
    }

    private Address recuperarEndereco(String endereco){

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> listaEnderecos = geocoder.getFromLocationName(endereco,1);
            if( listaEnderecos != null && listaEnderecos.size() > 0 ){
                Address address = listaEnderecos.get(0);

                double lat = address.getLatitude();
                double lon = address.getLongitude();

                return address;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void selectImage()
    {
        final CharSequence[] itens={"Camera","Galeria","Cancelar"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Adicionar Imagem");
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
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent.createChooser(intent,"Select File"), SELECT_FILE);
                }
                else if(itens[i].equals("Cancelar"))
                {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
}
