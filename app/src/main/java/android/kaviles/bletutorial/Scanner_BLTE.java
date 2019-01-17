package android.kaviles.bletutorial;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

public class Scanner_BLTE {

    private MainActivity ma;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private boolean mScanning;
    private Handler mHandler;

    private long scanPeriod;
    private int signalStrength;

    public Scanner_BLTE(MainActivity mainActivity, long scanPeriod, int signalStrength){
        ma = mainActivity;

        mHandler = new Handler();

        this.scanPeriod = scanPeriod;
        this.signalStrength = signalStrength;

        final BluetoothManager bluetoothManager=
            (BluetoothManager) ma.getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    public boolean isScanning(){
        return mScanning;
    }

    public void start(){
        if(!Utils.checkBluetooth(mBluetoothAdapter)){
            Utils.requestUserBluetooth(ma);
            ma.stopScan();
        }
        else{
            scanLeDevice(true);
        }
    }

    public void stop(){
        scanLeDevice(false);
    }

    private void scanLeDevice(final boolean enable){
        if (enable && !mScanning) {
            Utils.toast(ma.getApplication(), "Starting BLE Scan...");

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Utils.toast(ma.getApplication(), "Stopping BLE Scan...");

                    mScanning = false;
                    mBluetoothLeScanner.stopScan(mLeScanCallback);

                    ma.stopScan();
                }
            }, scanPeriod);

            mScanning = true;

            mBluetoothLeScanner.startScan(mLeScanCallback);
        }
    }

    private ScanCallback mLeScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    final int rssi = result.getRssi();
                    final BluetoothDevice device = result.getDevice();
                    if(rssi > signalStrength){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ma.addDevice(device, rssi);
                            }
                        });
                    }
                }
            };
}