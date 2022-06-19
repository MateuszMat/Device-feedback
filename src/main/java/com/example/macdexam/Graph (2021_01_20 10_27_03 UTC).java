package com.example.macdexam;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.nio.charset.Charset;
import java.util.Random;

public class Graph extends AppCompatActivity implements MqttCallback {

    public static final String TAG = "MQTT";
    private static final String BROKER = "tcp://m24.cloudmqtt.com:16370";
    private String clientId;
    private MemoryPersistence persistence = new MemoryPersistence();
    private MqttClient mqttClient;
    private static final String TEMP= "Temp";//Topic
    private static final String HUM= "Hum";
    public static FragmentManager fman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Button button = findViewById(R.id.temp);
        fman= getSupportFragmentManager();

        clientId = generateClientID();
        mqttConnect();

        if(findViewById(R.id.fholder) !=null){

            if(savedInstanceState!=null){
                return;
            }////////

            FragmentTransaction ft = fman.beginTransaction();
            Tempgraph temp_f= new Tempgraph();
            ft.add(R.id.fholder,temp_f, null);
            ft.commit();
        }
    }///
    private String generateClientID() {

        byte[] array = new byte[7];
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));

        return "AndroidClient_" + generatedString;
    }// end generate client id

    private void mqttConnect() {
        try {
            mqttClient = new MqttClient(BROKER, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            connOpts.setUserName("eengfasv");
            connOpts.setPassword("n4DHSg6G0ZS0".toCharArray());
            connOpts.setAutomaticReconnect(true);
            connOpts.setCleanSession(false);
            connOpts.setKeepAliveInterval(10);
            mqttClient.setCallback(this);
            Log.d(TAG, "Connecting to broker: " + BROKER);
            mqttClient.connect(connOpts);
            Log.d(TAG, "Connected");
            mqttClient.subscribe(TEMP);
            mqttClient.subscribe(HUM);

        } catch (MqttException me) {
            Log.e(TAG, "msg " + me.getMessage());
            me.printStackTrace();
        }
    }//end mqttconnect



    public void connectionLost(Throwable cause) {
        Log.d(TAG, cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message)  {
        final String incomingMessage = new String(message.getPayload());
        Log.d(TAG, incomingMessage);
        if (topic.equals(TEMP)) {
                Tempgraph.temperaturegauge.setValue(Float.parseFloat(incomingMessage));
                Bothgraph.temperature2gauge.setValue(Float.parseFloat(incomingMessage));
        }
        if (topic.equals(HUM)) {
                Humgraph.humiditygauge.setValue(Float.parseFloat(incomingMessage));
                Bothgraph.humidity2gauge.setValue(Float.parseFloat(incomingMessage));
        }
    }///


    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}

    @Override
    protected void onPause() {
        super.onPause();

        try {
            mqttClient.disconnect();
            mqttClient = null;
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mqttClient == null) {
            mqttConnect();
        }
    } // end housekeepiing


    public void main(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void message(View view) {
        Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
        startActivity(intent);
    }

    public void graph(View view) {
        Intent intent = new Intent(getApplicationContext(), Graph.class);
        startActivity(intent);
    }

    public void open_tfrag(View view) {
        fman.beginTransaction().replace(R.id.fholder,new Tempgraph(), null).commit();
    }

    public void openhfrag(View view) {
        fman.beginTransaction().replace(R.id.fholder,new Humgraph(), null).commit();
    }

    public void openbothfrags(View view) {
        fman.beginTransaction().replace(R.id.fholder,new Bothgraph(), null).commit();
    }
}
