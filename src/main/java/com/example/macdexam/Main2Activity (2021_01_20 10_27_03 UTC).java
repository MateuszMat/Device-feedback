package com.example.macdexam;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.nio.charset.Charset;
import java.util.Random;

public class Main2Activity extends AppCompatActivity implements MqttCallback{
    public static final String TAG = "MQTT";
    private static final String BROKER = "tcp://m24.cloudmqtt.com:16370";
    private String clientId;
    private MemoryPersistence persistence = new MemoryPersistence();
    private MqttClient mqttClient;
    private static final String ToSenseHat = "ToSenseHat";
    private TextView publishmsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        publishmsg= findViewById(R.id.msg);
        clientId = generateClientID();
        mqttConnect();
    }

    private String generateClientID() {

        byte[] array = new byte[7];
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));

        return "AndroidClient_" + generatedString;
    }

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
        } catch (MqttException me) {
            Log.e(TAG, "msg " + me.getMessage());
            me.printStackTrace();
        }
    }

    private void publish(String topic, String stringMessage) {
        if(mqttClient != null){
            if (!mqttClient.isConnected()) {
                Log.d(TAG, "client is not connected !!! ");
                return;
            }

            try {
                Log.d(TAG, "Publishing message: " + stringMessage);
                MqttMessage message = new MqttMessage(stringMessage.getBytes());
                message.setQos(2);
                mqttClient.publish(topic, message);
                Log.d(TAG, "Message published");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
    public void connectionLost(Throwable cause) {
        Log.d(TAG, cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message)  {
        final String incomingMessage = new String(message.getPayload());
        Log.d(TAG, incomingMessage);
    }
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
    }

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
    public void tosensehat(View view) {
        System.out.println("publishing to the senseHat");
        publish(ToSenseHat, String.valueOf(publishmsg.getText()));
    }
}
