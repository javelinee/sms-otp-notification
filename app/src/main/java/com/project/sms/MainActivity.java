package com.project.sms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    EditText messages;
    Button btnSend, btnVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestSendReceiveSMS();
        init();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messages.getText().toString();
                SmsManager.getDefault().sendTextMessage("5554", null, message, null, null);
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = messages.getText().toString();

                String channelId = "sms_channel";
                CharSequence channelName = "channel";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;

                if(otp.equals("5478")){
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);


                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, channelId)
                                .setContentTitle("Verifikasi Berhasil")
                                .setContentText("Nomor handphone berhasil diverifikasi")
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setChannelId(channelId);

                        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        Objects.requireNonNull(notifManager).createNotificationChannel(notificationChannel);

                        //cara menampilkan notification manager
                        notifManager.notify(0, builder.build());
//                    Toast.makeText(MainActivity.this, "Terverifikasi", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //untuk mengambil kode OTP dr sms
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br, intentFilter);
    }

    private void init() {
        messages = findViewById(R.id.etMessages);
        btnSend = findViewById(R.id.btnSend);
        btnVerify = findViewById(R.id.btnVerify);
    }

    private void requestSendReceiveSMS() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS}, 0);
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] smsMessages = null;

            if (!bundle.isEmpty()) {
                //pdu --> format sms
                Object[] pdus = (Object[]) bundle.get("pdus");
                smsMessages = new SmsMessage[pdus.length];

                for (int i = 0; i < pdus.length; i++) {
                    smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    String msg = smsMessages[i].getMessageBody();
                    String from = smsMessages[i].getOriginatingAddress();

                    if(from.equals("8888") && msg.equals("5478"))
                        messages.setText(msg);
                }
            }
        }
    };
}

