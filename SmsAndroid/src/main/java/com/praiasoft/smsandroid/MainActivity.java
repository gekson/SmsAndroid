package com.praiasoft.smsandroid;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    Button btnStart;
    Button btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Inicia a apliação, abaixo os parametros necessários para envio com o método: sendMultipartTextMessage
     * Número que irá receber o SMS;
     *"Centro de serviço" usado, use null;
     *A mensagem a ser enviada;
     *Um PendingIntent que será "broadcastada" quando o SMS for enviado;
     *Um PendingIntent que será "broadcastada" quando o SMS for recebido;
     * @param view
     */
    public void iniciarAplicacao(View view) {
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);

        /*try {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendMultipartTextMessage ("91893007", null, smsManager.divideMessage("Teste de envio SMS, pela aplicação Sms Android"), null, null);
        Toast.makeText(getApplicationContext(), "SMS Enviado com sucesso!",
                Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Tente novamente!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }*/

        // Inicia o serviço a partir de INICIAR_SERVICO definido no
        // androidManifest.xml
        startService(new Intent("INICIAR_SERVICO"));

    }

    public void pararAplicacao(View view) {
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);

    }
}
