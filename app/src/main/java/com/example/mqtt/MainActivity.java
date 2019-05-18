package com.example.mqtt;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.io.UnsupportedEncodingException;



/*

source code

                    https://www.hivemq.com/blog/mqtt-client-library-enyclopedia-paho-android-service/
 */

public class MainActivity extends AppCompatActivity {


    String topic = "elsekily/feeds/led";  //topic name
    MqttAndroidClient client;  //MQTT client


    TextView mqtt_subscribe; //textview
    ToggleButton toggle; ///button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mqtt_subscribe = (TextView) findViewById(R.id.text);
        toggle = (ToggleButton) findViewById(R.id.toggBtn);

        //init button function
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startPublish("1");//call this function to publish 1

                } else {
                    startPublish("0");//call this function to publish 0
                }
            }
        });

        String clientId = MqttClient.generateClientId();  //new clientid every start
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://io.adafruit.com:1883", clientId);  //create the mqtt client
        // Remove if there is no additional options
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("elsekily");
        options.setPassword("4109b6d2b091498bb87d98831f4441ba".toCharArray());

        try {
            IMqttToken token = client.connect(options);//remove options word if no options
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {//case connection to server Succeeded
                    // We are connected
                    startSubscribe();  //call this method after the connection to server Succeeded not before and it will subscribe forever
                    Toast.makeText(MainActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
                    //Log.d(TAG, "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    //Log.d(TAG, "onFailure");
                    Toast.makeText(MainActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            Toast.makeText(MainActivity.this, "cannot connect", Toast.LENGTH_SHORT).show();
        }

        client.setCallback(new MqttCallback() {  /// callback method of mqtt subscribe
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                mqtt_subscribe.setText(message.toString());   // when message received it writes text view with its value

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }


    public void startPublish(String s)  //when button clicked call this method with string s value to publish it
    {
        String payload = s;
        byte[] encodedPayload = new byte[0];  //encoded payload as bytes
        try {
            encodedPayload = payload.getBytes("UTF-8");  //
            MqttMessage message = new MqttMessage(encodedPayload);

            //message.setRetained(true);   /// to retain message in mqtt broker
            client.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            Toast.makeText(MainActivity.this, "Cannot Publish", Toast.LENGTH_SHORT).show();
        }

    }


    public void startSubscribe()  // call after connect successfully to the broker
    {
        try {
            IMqttToken subToken = client.subscribe(topic, 1);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "subscribed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {

                    Toast.makeText(MainActivity.this, "cannot subscribe", Toast.LENGTH_SHORT).show();

                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}



