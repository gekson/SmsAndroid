package com.praiasoft.smsandroid;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.*;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.praiasoft.entidade.Mensagem;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by gek on 28/05/13.
 */
public class SmsServico extends Service {
    private SharedPreferences prefs;
    int counter = 0;
    //static final int INTERVALO_ATUALIZACAO = 1000; //1 segundo
    static final int INTERVALO_ATUALIZACAO = 15000; //15 segundos
    private Timer timer = new Timer();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Toast.makeText(this, "Serviço iniciado!", Toast.LENGTH_LONG).show();
        consumirWebService();
        return START_STICKY;
    }

    private void consumirWebService(){
        //Mock
        Mensagem m1 = new Mensagem();
        m1.setId(1);
        m1.setMensagem("Mensagem eviada pelo aplicativo SmsAndroid");
        m1.setTelefone("91893007");

        Mensagem m2 = new Mensagem();
        m2.setId(2);
        m2.setMensagem("Mensagem eviada pelo aplicativo SmsAndroid");
        m2.setTelefone("1893007");

        List<Mensagem> lista = new ArrayList<Mensagem>();
        lista.add(m1);
        lista.add(m2);

        try {
            for(Mensagem m : lista){
                enviarSms(m);
                Toast.makeText(this, "Status: " + m.getStatus(), Toast.LENGTH_LONG).show();
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //Fim mock
    /*    prefs = getSharedPreferences(PreferenciasGateway.NOME_ARQUIVO, MODE_PRIVATE);
        final PreferenciasGateway p = new PreferenciasGatewayDAO().getPreferenciasGateway(prefs);
        timer.scheduleAtFixedRate(new TimerTask() {
            @SuppressWarnings({ "deprecation" })
            @Override
            public void run() {
                SoapObject request = new SoapObject(p.getNamespace(), MetodosWebService.PEGAR_CONFIGURACOES_PENDENTES);
                PropertyInfo propInfo1 = new PropertyInfo();
                PropertyInfo propInfo2 = new PropertyInfo();

                propInfo1.name = MetodosWebService.PEGAR_CONFIGURACOES_PENDENTES__ID_GATEWAY;
                propInfo1.type = PropertyInfo.STRING_CLASS;
                request.addProperty(propInfo1, p.getIdGateway());

                propInfo2.name = MetodosWebService.PEGAR_CONFIGURACOES_PENDENTES__SENHA_WS;
                propInfo2.type = PropertyInfo.STRING_CLASS;
                request.addProperty(propInfo2, p.getSenhaWebService());

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(p.getUrl());
                try {
                    androidHttpTransport.call(p.getNamespace()+p.getSoapAction()+"/"+MetodosWebService.PEGAR_CONFIGURACOES_PENDENTES+MetodosWebService.REQUISICAO, envelope);
                    SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
                    List<ConfiguracaoPendente> listaConfiguracoes = new ConfiguracaoPendente().listaConfiguracoesPendentes(result.toString());

                    for(ConfiguracaoPendente c : listaConfiguracoes){
                        enviarSms(c);
                        Thread.sleep(5000);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            } //FIM run()
        }, 0, INTERVALO_ATUALIZACAO);*/
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(timer != null)
            timer.cancel();

        Toast.makeText(this, "Serviço parado!", Toast.LENGTH_LONG).show();
    }

    public void enviarSms(final Mensagem m){
        PendingIntent enviadoPI = PendingIntent.getBroadcast(this, 0, new Intent(""+m.getId()), 0); //Cria um ID que será usado como parâmetro pelo escutador que será criado mais a frente
        PendingIntent entreguePI = PendingIntent.getBroadcast(this, 0, new Intent(""+m.getId()), 0); //Cria um ID que será usado como parâmetro pelo escutador que será criado mais a frente

        //--quando o sms tiver sido enviado--
        registerReceiver(new BroadcastReceiver() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceive(Context context, Intent intent) {
                /*final PreferenciasGateway p = new PreferenciasGatewayDAO().getPreferenciasGateway(prefs);
                SoapObject request2 = new SoapObject(p.getNamespace(), MetodosWebService.ATUALIZAR_ST_ENVIO);
                PropertyInfo propInfo21 = new PropertyInfo();
                PropertyInfo propInfo22 = new PropertyInfo();
                PropertyInfo propInfo23 = new PropertyInfo();
                PropertyInfo propInfo24 = new PropertyInfo();

                SoapSerializationEnvelope envelope2 = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                HttpTransportSE androidHttpTransport2 = new HttpTransportSE(p.getUrl());
                */
                switch(getResultCode()){
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        /*propInfo21.name = MetodosWebService.ATUALIZAR_ST_ENVIO__SENHA_WS;
                        propInfo21.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo21, p.getSenhaWebService());

                        propInfo22.name = MetodosWebService.ATUALIZAR_ST_ENVIO__ID_CONFIGURACAO_RASTREADOR;
                        propInfo22.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo22, cp.getIdConfiguracaoRastreador());

                        propInfo23.name = MetodosWebService.ATUALIZAR_ST_ENVIO__ST_ENVIO;
                        propInfo23.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo23, StEnvioTP.FALHA_GENERICA);

                        propInfo24.name = MetodosWebService.ATUALIZAR_ST_ENVIO__NR_TENTATIVA;
                        propInfo24.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo24, cp.getNrTentativa()+1);


                        envelope2.setOutputSoapObject(request2);
                        try {
                            androidHttpTransport2.call(p.getNamespace()+p.getSoapAction()+"/"+MetodosWebService.ATUALIZAR_ST_ENVIO+MetodosWebService.REQUISICAO, envelope2);
                            envelope2.getResponse();
                        }catch(Exception e){
                            e.printStackTrace();
                        }*/
                        m.setStatus(SmsManager.RESULT_ERROR_GENERIC_FAILURE);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        m.setStatus(SmsManager.RESULT_ERROR_NO_SERVICE);
                        /*propInfo21.name = MetodosWebService.ATUALIZAR_ST_ENVIO__SENHA_WS;
                        propInfo21.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo21, p.getSenhaWebService());

                        propInfo22.name = MetodosWebService.ATUALIZAR_ST_ENVIO__ID_CONFIGURACAO_RASTREADOR;
                        propInfo22.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo22, cp.getIdConfiguracaoRastreador());

                        propInfo23.name = MetodosWebService.ATUALIZAR_ST_ENVIO__ST_ENVIO;
                        propInfo23.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo23, StEnvioTP.SEM_SERVICO);

                        propInfo24.name = MetodosWebService.ATUALIZAR_ST_ENVIO__NR_TENTATIVA;
                        propInfo24.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo24, cp.getNrTentativa()+1);


                        envelope2.setOutputSoapObject(request2);
                        try {
                            androidHttpTransport2.call(p.getNamespace()+p.getSoapAction()+"/"+MetodosWebService.ATUALIZAR_ST_ENVIO+MetodosWebService.REQUISICAO, envelope2);
                            envelope2.getResponse();
                        }catch(Exception e){
                            e.printStackTrace();
                        }*/
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        m.setStatus(SmsManager.RESULT_ERROR_NULL_PDU);
                        /*propInfo21.name = MetodosWebService.ATUALIZAR_ST_ENVIO__SENHA_WS;
                        propInfo21.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo21, p.getSenhaWebService());

                        propInfo22.name = MetodosWebService.ATUALIZAR_ST_ENVIO__ID_CONFIGURACAO_RASTREADOR;
                        propInfo22.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo22, cp.getIdConfiguracaoRastreador());

                        propInfo23.name = MetodosWebService.ATUALIZAR_ST_ENVIO__ST_ENVIO;
                        propInfo23.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo23, StEnvioTP.PDU_NULO);

                        propInfo24.name = MetodosWebService.ATUALIZAR_ST_ENVIO__NR_TENTATIVA;
                        propInfo24.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo24, cp.getNrTentativa()+1);


                        envelope2.setOutputSoapObject(request2);
                        try {
                            androidHttpTransport2.call(p.getNamespace()+p.getSoapAction()+"/"+MetodosWebService.ATUALIZAR_ST_ENVIO+MetodosWebService.REQUISICAO, envelope2);
                            envelope2.getResponse();
                        }catch(Exception e){
                            e.printStackTrace();
                        }*/
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        m.setStatus(SmsManager.RESULT_ERROR_RADIO_OFF);
                        /*propInfo21.name = MetodosWebService.ATUALIZAR_ST_ENVIO__SENHA_WS;
                        propInfo21.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo21, p.getSenhaWebService());

                        propInfo22.name = MetodosWebService.ATUALIZAR_ST_ENVIO__ID_CONFIGURACAO_RASTREADOR;
                        propInfo22.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo22, cp.getIdConfiguracaoRastreador());

                        propInfo23.name = MetodosWebService.ATUALIZAR_ST_ENVIO__ST_ENVIO;
                        propInfo23.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo23, StEnvioTP.RADIO_OFF);

                        propInfo24.name = MetodosWebService.ATUALIZAR_ST_ENVIO__NR_TENTATIVA;
                        propInfo24.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo24, cp.getNrTentativa()+1);


                        envelope2.setOutputSoapObject(request2);
                        try {
                            androidHttpTransport2.call(p.getNamespace()+p.getSoapAction()+"/"+MetodosWebService.ATUALIZAR_ST_ENVIO+MetodosWebService.REQUISICAO, envelope2);
                            envelope2.getResponse();
                        }catch(Exception e){
                            e.printStackTrace();
                        }*/
                        break;
                    default:
                        m.setStatus(SmsManager.STATUS_ON_ICC_SENT);
                        Log.d("Status sent","intent="+intent);
                        /*propInfo21.name = MetodosWebService.ATUALIZAR_ST_ENVIO__SENHA_WS;
                        propInfo21.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo21, p.getSenhaWebService());

                        propInfo22.name = MetodosWebService.ATUALIZAR_ST_ENVIO__ID_CONFIGURACAO_RASTREADOR;
                        propInfo22.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo22, cp.getIdConfiguracaoRastreador());

                        propInfo23.name = MetodosWebService.ATUALIZAR_ST_ENVIO__ST_ENVIO;
                        propInfo23.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo23, StEnvioTP.ENVIADO);

                        propInfo24.name = MetodosWebService.ATUALIZAR_ST_ENVIO__NR_TENTATIVA;
                        propInfo24.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo24, cp.getNrTentativa()+1);

                        envelope2.setOutputSoapObject(request2);
                        try {
                            androidHttpTransport2.call(p.getNamespace()+p.getSoapAction()+"/"+MetodosWebService.ATUALIZAR_ST_ENVIO+MetodosWebService.REQUISICAO, envelope2);
                            envelope2.getResponse();
                        }catch(Exception e){
                            e.printStackTrace();
                        }*/
                        break;
                }
            }
        }, new IntentFilter(""+m.getId())); //Usa o mesmo ID do PendingIntent e cria um escutador somente daquela intenção

        //--quando o sms tiver sido entregue--
        registerReceiver(new BroadcastReceiver() {
            @SuppressWarnings("deprecation")
            @Override
            public void onReceive(Context context, Intent intent) {
                /*final PreferenciasGateway p = new PreferenciasGatewayDAO().getPreferenciasGateway(prefs);
                SoapObject request2 = new SoapObject(p.getNamespace(), MetodosWebService.ATUALIZAR_ST_ENTREGA);
                PropertyInfo propInfo21 = new PropertyInfo();
                PropertyInfo propInfo22 = new PropertyInfo();
                PropertyInfo propInfo23 = new PropertyInfo();

                SoapSerializationEnvelope envelope2 = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                HttpTransportSE androidHttpTransport2 = new HttpTransportSE(p.getUrl());
                */
                switch(getResultCode()){
                    case Activity.RESULT_OK:
                        m.setStatus(Activity.RESULT_OK);
                        Log.d("Status ok","intent="+intent);
                        /*propInfo21.name = MetodosWebService.ATUALIZAR_ST_ENTREGA__SENHA_WS;
                        propInfo21.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo21, p.getSenhaWebService());

                        propInfo22.name = MetodosWebService.ATUALIZAR_ST_ENTREGA__ID_CONFIGURACAO_RASTREADOR;
                        propInfo22.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo22, cp.getIdConfiguracaoRastreador());

                        propInfo23.name = MetodosWebService.ATUALIZAR_ST_ENTREGA__ST_ENTREGA;
                        propInfo23.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo23, StEntregaTP.ENTREGUE);


                        envelope2.setOutputSoapObject(request2);
                        try {
                            androidHttpTransport2.call(p.getNamespace()+p.getSoapAction()+"/"+MetodosWebService.ATUALIZAR_ST_ENTREGA+MetodosWebService.REQUISICAO, envelope2);
                            envelope2.getResponse();
                        }catch(Exception e){
                            e.printStackTrace();
                        }*/
                        break;
                    case Activity.RESULT_CANCELED:
                        m.setStatus(Activity.RESULT_CANCELED);
                        /*propInfo21.name = MetodosWebService.ATUALIZAR_ST_ENTREGA__SENHA_WS;
                        propInfo21.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo21, p.getSenhaWebService());

                        propInfo22.name = MetodosWebService.ATUALIZAR_ST_ENTREGA__ID_CONFIGURACAO_RASTREADOR;
                        propInfo22.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo22, cp.getIdConfiguracaoRastreador());

                        propInfo23.name = MetodosWebService.ATUALIZAR_ST_ENTREGA__ST_ENTREGA;
                        propInfo23.type = PropertyInfo.STRING_CLASS;
                        request2.addProperty(propInfo23, StEntregaTP.NAO_ENTREGUE);


                        envelope2.setOutputSoapObject(request2);
                        try {
                            androidHttpTransport2.call(p.getNamespace()+p.getSoapAction()+"/"+MetodosWebService.ATUALIZAR_ST_ENTREGA+MetodosWebService.REQUISICAO, envelope2);
                            envelope2.getResponse();
                        }catch(Exception e){
                            e.printStackTrace();
                        }*/
                        break;
                }
            }
        }, new IntentFilter(""+m.getId()));  //Usa o mesmo ID do PendingIntent e cria um escutador somente daquela intenção

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(m.getTelefone(), null, m.getMensagem() + m.getStatus(), enviadoPI, entreguePI);
    }

}