package com.example.giovanetti.speechtotext;

import android.annotation.TargetApi;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {

    TextView resultTEXT;
    TextView mainText;
    private Bluetooth bt;
    public final String TAG = "Main";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTEXT= (TextView)findViewById(R.id.TVresult);
        mainText= (TextView)findViewById(R.id.mainText);
        bt = new Bluetooth(App.getContext(), mHandler);
        connectService();
        App.setContext(this);


    }



    public void onButtonClick(View v) {

        if(v.getId()== R.id.imageButton){
            promptSpeechInput();
        }
    }



    public void promptSpeechInput(){
        Intent i= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        String myLanguage = "it"; //or, Locale.Italian.toString()
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, myLanguage);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, myLanguage);
        i.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, myLanguage);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try{
            startActivityForResult(i, 100);
        }
        catch (ActivityNotFoundException a){
            Toast.makeText(MainActivity.this, "Sorry! Your device doesn't support speech Language!", Toast.LENGTH_LONG).show();

        }

    }

    public void onActivityResult(int request_code, int result_code,Intent i){
        super.onActivityResult(100, result_code, i);

        switch (request_code){
            case 100: if(request_code== 100 && i!=null){
                ArrayList<String> result = i.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                resultTEXT.setText(result.get(0));
                String msg= result.get(0).toString();
                bt.sendMessage(msg);
                //resultTEXT.setText("hello");
            }
            break;
        }
    }

    public void connectService(){

        try {
            mainText.setText("Connecting...");
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                bt.start();
                bt.connectDevice("HC-06");
                Log.d(TAG, "Btservice started - listening");
                mainText.setText("Connected");
            } else {
                Log.w(TAG, "Btservice started - bluetooth is not enabled");
                mainText.setText("Bluetooth Not enabled");
            }
        } catch(Exception e){
            Log.e(TAG, "Unable to start bt ",e);
            mainText.setText("Unable to connect " +e);
        }
    }





  private final android.os.Handler mHandler = new android.os.Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    break;
                case Bluetooth.MESSAGE_WRITE:
                    Log.d(TAG, "MESSAGE_WRITE ");
                    break;
                case Bluetooth.MESSAGE_READ:
                    Log.d(TAG, "MESSAGE_READ ");
                    break;
                case Bluetooth.MESSAGE_DEVICE_NAME:
                    Log.d(TAG, "MESSAGE_DEVICE_NAME "+msg);
                    break;
                case Bluetooth.MESSAGE_TOAST:
                    Log.d(TAG, "MESSAGE_TOAST "+msg);
                    break;
            }
        }
    };

}

 class App extends Application {

    private static Context mContext;


    public static Context getContext() {
        return mContext;
    }
     public static void setContext(Context Context1) {
         mContext = Context1;
     }

}
