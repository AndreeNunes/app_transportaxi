package br.senai.sp.transportxi.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.Calendar;

import br.senai.sp.transportxi.R;
import br.senai.sp.transportxi.config.Config_firebase;
import br.senai.sp.transportxi.helper.UsuarioFirebase;

public class  StartUpBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

            DatabaseReference reference = Config_firebase.getBanco_tansportaxi()
                    .child("agenda");

            Query query = reference.orderByChild("idTransportador")
                    .equalTo(UsuarioFirebase.getIdUsuario());

            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    int id = 1;
                    String titulo = "Transportaxi";
                    String texto = "Existe uma novo agendamento esperando por você.";
                    int icone = R.drawable.pedido_icon;

                    Intent intent = new Intent(context, PrincipalActivityTransportador.class);
                    PendingIntent p = getPendingIntent(id, intent, context);

                    NotificationCompat.Builder notificacao = new NotificationCompat.Builder(context);
                    notificacao.setSmallIcon(icone);
                    notificacao.setContentTitle(titulo);
                    notificacao.setContentText(texto);
                    notificacao.setContentIntent(p);

                    NotificationManagerCompat nm = NotificationManagerCompat.from(context);
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

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            // Cria um Calendar para as 0 horas do dia seguinte
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.DAY_OF_MONTH, 10);
            calendar.set(Calendar.HOUR_OF_DAY, 0);

            //PendingIntent para lançar o serviço
            Intent serviceIntent = new Intent(context, PrincipalActivityTransportador.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, serviceIntent, 0);

            //Alarme que se repete todos os dias às 0 horas
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);

        }
    }

    private PendingIntent getPendingIntent(int id, Intent intent, Context context) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(intent.getComponent());
        stackBuilder.addNextIntent(intent);
        PendingIntent p = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);
        return p;
    }
}