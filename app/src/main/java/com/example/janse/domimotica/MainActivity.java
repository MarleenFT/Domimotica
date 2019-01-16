package com.example.janse.domimotica;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Adapter;


import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    // Create components
    private TextView home_currently_connected_name_box;
    private TextView home_currently_connected_amount_name_box;
    private TextView dashboard_name_box;
    private RadioButton dashboard_on_button;
    private RadioButton dashboard_off_button;
    private Spinner dashboard_node_select;

    private HashMap<String, BTLE_Device> mBTDevicesHashMap;
    private ArrayList<BTLE_Device> mBTDevicesArrayList;
    private ListAdapter_BTLE_Devices adapter;

    private Button btn_Scan;
    private Context mContext;

    public static final int REQUEST_ENABLE_BT = 1;

    private BroadcastReceiver_BTState mBTStateUpdateReceiver;
    private Scanner_BLTE mBTLeScanner;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    homeSetup();
                    return true;
                case R.id.navigation_dashboard:
                    dashboardSetup();
                    return true;
                case R.id.navigation_config:
                    configSetup();
                    return true;
                case R.id.navigation_node_list:
                    configSetup();
                    return true;
            }
            return false;
        }
    };

    private Spinner.OnItemSelectedListener spinnerListener
            = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String node = dashboard_node_select.getSelectedItem().toString();
            dashboard_name_box.setText(node);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    /**
     * Called when the scan button is clicked.
     * @param v The view that was clicked
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_scan:
                Utils.toast(mContext, "Scan Button Pressed");

                if(!mBTLeScanner.isScanning()){
                    startScan();
                }
                else{
                    stopScan();
                }

                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Utils.toast(getApplicationContext(), "BLE not supported");
            finish();
        }

        mContext = this;

        mBTStateUpdateReceiver = new BroadcastReceiver_BTState(getApplicationContext());
        mBTLeScanner = new Scanner_BLTE(this, 7500,-200);

        mBTDevicesHashMap = new HashMap<>();
        mBTDevicesArrayList = new ArrayList<>();

        adapter = new ListAdapter_BTLE_Devices(this, R.layout.activity_main, mBTDevicesArrayList);

        ListView listView = new ListView(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        ((ScrollView) findViewById(R.id.scrollView)).addView(listView);

        btn_Scan = (Button) findViewById(R.id.btn_scan);
        findViewById(R.id.btn_scan).setOnClickListener(this);

        // Init home screen components
        home_currently_connected_name_box = findViewById(
                R.id.home_currently_connected_name_box);
        home_currently_connected_amount_name_box = findViewById(
                R.id.home_currently_connected_amount_name_box);

        // Init dashboard screen components
        dashboard_name_box = findViewById(R.id.dashboard_name_box);
        dashboard_on_button = findViewById(R.id.dashboard_on_button);
        dashboard_off_button = findViewById(R.id.dashboard_off_button);
        dashboard_node_select = findViewById(R.id.dashboard_node_sellect);

        // Create the array adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dashboard_spinner_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        dashboard_node_select.setAdapter(adapter);

        // Set startup visibility values
        listView.setVisibility(View.VISIBLE);
        btn_Scan.setVisibility(View.VISIBLE);

        home_currently_connected_name_box.setVisibility(View.INVISIBLE);
        home_currently_connected_amount_name_box.setVisibility(View.INVISIBLE);
        dashboard_name_box.setVisibility(View.INVISIBLE);
        dashboard_on_button.setVisibility(View.INVISIBLE);
        dashboard_off_button.setVisibility(View.INVISIBLE);
        dashboard_node_select.setVisibility(View.INVISIBLE);

        // Init navigation bar
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        dashboard_node_select.setOnItemSelectedListener(spinnerListener);
    }

    // This function sets the visibility for the home screen
    private void homeSetup(){
        home_currently_connected_name_box.setVisibility(View.VISIBLE);
        home_currently_connected_amount_name_box.setVisibility(View.VISIBLE);

        dashboard_name_box.setVisibility(View.INVISIBLE);
        dashboard_on_button.setVisibility(View.INVISIBLE);
        dashboard_off_button.setVisibility(View.INVISIBLE);
        dashboard_node_select.setVisibility(View.INVISIBLE);
    }

    // This function sets the visibility for the dashboard screen
    public void dashboardSetup(){
        home_currently_connected_name_box.setVisibility(View.INVISIBLE);
        home_currently_connected_amount_name_box.setVisibility(View.INVISIBLE);

        String node = dashboard_node_select.getSelectedItem().toString();
        dashboard_name_box.setText(node);

        dashboard_name_box.setVisibility(View.VISIBLE);
        dashboard_on_button.setVisibility(View.VISIBLE);
        dashboard_off_button.setVisibility(View.VISIBLE);
        dashboard_node_select.setVisibility(View.VISIBLE);
    }

    // This function sets the visibility for the config screen
    public void configSetup(){
        home_currently_connected_name_box.setVisibility(View.INVISIBLE);
        home_currently_connected_amount_name_box.setVisibility(View.INVISIBLE);

        dashboard_name_box.setVisibility(View.INVISIBLE);
        dashboard_on_button.setVisibility(View.INVISIBLE);
        dashboard_off_button.setVisibility(View.INVISIBLE);
        dashboard_node_select.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver(mBTStateUpdateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

        stopScan();
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mBTStateUpdateReceiver);
        stopScan();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                 Utils.toast(getApplicationContext(), "Thank you for turning on Bluetooth");
            }
            else if (resultCode == RESULT_CANCELED) {
                Utils.toast(getApplicationContext(), "Please turn on Bluetooth");
            }
        }
    }

    /**
     * Called when an item in the ListView is clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Used in future BLE tutorials
    }

    public void addDevice(BluetoothDevice bluetoothDevice, int new_rssi) {
        String address = bluetoothDevice.getAddress();

        if(!mBTDevicesHashMap.containsKey(address)){
            BTLE_Device btle_device = new BTLE_Device(bluetoothDevice);
            btle_device.setRSSI(new_rssi);

            mBTDevicesHashMap.put(address, btle_device);
            mBTDevicesArrayList.add(btle_device);
        }
        else {
            mBTDevicesHashMap.get(address).setRSSI(new_rssi);
        }


        //adapter.notifyDataSetChanged();
    }

    public void startScan(){
        btn_Scan.setText("Scanning...");

        mBTDevicesArrayList.clear();
        mBTDevicesHashMap.clear();

        adapter.notifyDataSetChanged();

        mBTLeScanner.start();

        adapter.notifyDataSetChanged();
    }

    public void stopScan() {
        btn_Scan.setText("Scan Again");

        mBTLeScanner.stop();
    }
}