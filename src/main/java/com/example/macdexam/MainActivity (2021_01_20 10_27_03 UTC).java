package com.example.macdexam;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.nio.charset.Charset;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements MqttCallback,SensorEventListener {
    public static final String TAG = "MQTT";
    private static final String BROKER = "tcp://m24.cloudmqtt.com:16370";
    private String clientId;
    private MemoryPersistence persistence = new MemoryPersistence();
    private MqttClient mqttClient;;
    private static final String ToESP = "ToESP";
    private SensorManager mSensor;
    private Sensor aSensor;
    private TextView meter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        meter = (TextView) findViewById(R.id.acc);
        mSensor = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        aSensor= mSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
        System.out.println("connection lost :(");
    }

    @Override
    public void messageArrived(String topic, MqttMessage msg)  {
        final String incomingMessage = new String(msg.getPayload());
        Log.d(TAG, incomingMessage);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}

    protected void onResume() {
        super.onResume();
        mSensor.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (mqttClient == null) {
            mqttConnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensor.unregisterListener(this);
        try {
            mqttClient.disconnect();
            mqttClient = null;
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        float y = event.values[1];
        meter.setText(y+"");
        if(y>5){
            publish(ToESP, String.valueOf(1));
        }else if(y<-5){
        }else{
            publish(ToESP, String.valueOf(0));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
    public void toesp(View view) {

    }
}
