package com.github.mqtran01.brightmood_android;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;




public class FitbitActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private Button fitbitButton1;
    private BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
    private Button sendButton;
    private SeekBar facebookSlider;
    private SeekBar fitbitSlider;

    private Button speechBtn;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    private boolean mScanning;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;

    private ScanFilter mScanFilter;
    private ScanSettings mScanSettings;


    private final BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                String  id = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);

                Toast.makeText(getApplicationContext(),"id " + id + " RSSI: " + rssi + "dBm", Toast.LENGTH_SHORT).show();
            }
        }
    };


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);

                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    Intent speechIntent = new Intent(FitbitActivity.this, SpeechActivity.class);
                    speechIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    startActivity(speechIntent);
                    FitbitActivity.this.finish();
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    Intent btIntent = new Intent(FitbitActivity.this, BluetoothActivity.class);
                    btIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                    startActivity(btIntent);
                    FitbitActivity.this.finish();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitbit);

        mTextMessage = (TextView) findViewById(R.id.message);

        facebookSlider = (SeekBar) findViewById(R.id.seekBar);
        fitbitSlider = (SeekBar) findViewById(R.id.seekBar2);

        sendButton = (Button) findViewById(R.id.button10);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextMessage.setText("Sending HTTP");
                LogicFitbit fitbit = new LogicFitbit();
                StrictMode.ThreadPolicy tp = StrictMode.ThreadPolicy.LAX;
                StrictMode.setThreadPolicy(tp);
                String msg = fitbit.logicFitbit(mTextMessage);
//                if(msg == null) {
//                    mTextMessage.setText("I am null");
//                } else {
//                    mTextMessage.setText(msg);
//
//                }
                mTextMessage.setText(msg);
//                GatewayHandler handler = new GatewayHandler();
                //handler.execute("http://192.168.1.32/SetDyNet.cgi?a=2&p=2");

//                        facebookSlider.getProgress() + "&c=0&l=" + fitbitSlider.getProgress() * 20
//new String[]{//"http://httpbin.org/get?facebook=" +
            // facebookSlider.getProgress() + "&fitbit=" + fitbitSlider.getProgress()});

        }
        });

        speechBtn = (Button) findViewById(R.id.speechBtn);
        speechBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechToText();
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        BTAdapter.startDiscovery();
        final BluetoothManager BTManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (BTAdapter == null || !BTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

//        setScanFilter();
//        setScanSettings();


    }

    private final int SPEECH_RECOGNITION_CODE = 1;

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback for speech recognition activity
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    chooseLighting(text);
                }
                break;
            }

        }
    }

    private void chooseLighting(String text) {
        ArrayList<String> words = new ArrayList<>(Arrays.asList(text.split(" ")));
        GatewayHandler handler = new GatewayHandler(mTextMessage);
        if (words.contains("white")) {
            handler.execute("http://192.168.1.32/SetDyNet.cgi?a=2&p=1");
            mTextMessage.setText("white detected");
        } else if (words.contains("blue")) {
            handler.execute("http://192.168.1.32/SetDyNet.cgi?a=2&p=5");
            mTextMessage.setText("blue detected");
        } else if (words.contains("red")) {
            handler.execute("http://192.168.1.32/SetDyNet.cgi?a=2&p=3");
            mTextMessage.setText("red detected");
        } else if (words.contains("green")) {
            handler.execute("http://192.168.1.32/SetDyNet.cgi?a=2&p=2");
            mTextMessage.setText("green detected");
        } else if (words.contains("dim")) {
            handler.execute("http://192.168.1.32/SetDyNet.cgi?a=2&l=50&f=1");
            mTextMessage.setText("dim detected");
        } else if (words.contains("off")) {
            handler.execute("http://192.168.1.32/SetDyNet.cgi?a=2&p=4");
            mTextMessage.setText("off detected");
        } else if (words.contains("on")) {
            handler.execute("http://192.168.1.32/SetDyNet.cgi?a=2&p=1");
            mTextMessage.setText("on detected");
        } else if (words.contains("applause")) {
            handler.execute("http://192.168.1.32/SetDyNet.cgi?a=10&p=3");
            mTextMessage.setText("applause detected");
        } else if (words.contains("party")) {
            handler.execute("http://192.168.1.32/SetDyNet.cgi?a=10&p=4");
            mTextMessage.setText("party detected");
        } else if (words.contains("relax")) {
            handler.execute("http://192.168.1.32/SetDyNet.cgi?a=10&p=5");
            mTextMessage.setText("relax detected");
        } else {
            mTextMessage.setText(text);
        }
    }





    // NOT FUNCTIONAL

    protected ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            ScanRecord mScanRecord = result.getScanRecord();
            byte[] manufacturerData = mScanRecord.getManufacturerSpecificData(224);
            int mRssi = result.getRssi();
        }
    };



//    private void scanLeDevice(final boolean enable) {
//        if (enable) {
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mScanning = false;
////                    BTAdapter.stopLeScan(mLeScanCallback);
//                }
//            }, SCAN_PERIOD);
//
//            mScanning = true;
////            BTAdapter.startLeScan()
//        }
//    }
//
//
//
//    private void setScanFilter() {
//        ScanFilter.Builder mBuilder = new ScanFilter.Builder();
//        ByteBuffer mManufacturerData = ByteBuffer.allocate(23);
//        ByteBuffer mManufacturerDataMask = ByteBuffer.allocate(24);
//        byte[] uuid = getIdAsByte(UUID.fromString("0CF052C297CA407C84F8B62AAC4E9020"));
//        mManufacturerData.put(0, (byte)0xBE);
//        mManufacturerData.put(1, (byte)0xAC);
//        for (int i=2; i<=17; i++) {
//            mManufacturerData.put(i, uuid[i-2]);
//        }
//        for (int i=0; i<=17; i++) {
//            mManufacturerDataMask.put((byte)0x01);
//        }
//        mBuilder.setManufacturerData(224, mManufacturerData.array(), mManufacturerDataMask.array());
//        mScanFilter = mBuilder.build();
//    }
//
//    public byte[] getIdAsByte(UUID uuid)
//    {
//        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
//        bb.putLong(uuid.getMostSignificantBits());
//        bb.putLong(uuid.getLeastSignificantBits());
//        return bb.array();
//    }
//
//    private void setScanSettings() {
//        ScanSettings.Builder mBuilder = new ScanSettings.Builder();
//        mBuilder.setReportDelay(0);
//        mBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
//        mScanSettings = mBuilder.build();
//    }
//
//    public double calculateDistance(int txPower, double rssi) {
//        if (rssi == 0) {
//            return -1.0; // if we cannot determine accuracy, return -1.
//        }
//        double ratio = rssi*1.0/txPower;
//        if (ratio < 1.0) {
//            return Math.pow(ratio,10);
//        }
//        else {
//            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
//            return accuracy;
//        }
//    }
//
//    private String getDistance(double accuracy) {
//        if (accuracy == -1.0) {
//            return "Unknown";
//        } else if (accuracy < 1) {
//            return "Immediate";
//        } else if (accuracy < 3) {
//            return "Near";
//        } else {
//            return "Far";
//        }
//    }

}
