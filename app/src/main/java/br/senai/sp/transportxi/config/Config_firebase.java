package br.senai.sp.transportxi.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Config_firebase
{
    private static DatabaseReference banco_tansportaxi;
    private static FirebaseAuth auth_transportaxi;
    private static StorageReference banco_storage;

    public static DatabaseReference getBanco_tansportaxi()
    {
        if(banco_tansportaxi == null)
        {
            banco_tansportaxi = FirebaseDatabase.getInstance().getReference();
        }
        return banco_tansportaxi;
    }
    public static FirebaseAuth getAuth_transportaxi()
    {
        if(auth_transportaxi == null)
        {
            auth_transportaxi = FirebaseAuth.getInstance();
        }
        return auth_transportaxi;
    }
    public static StorageReference getBanco_storage()
    {
        if(banco_storage == null)
        {
            banco_storage = FirebaseStorage.getInstance().getReference();
        }
        return banco_storage;
    }
}
