package android.kaviles.bletutorial;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.UUID;


public class GATT_Services {

    private final static UUID CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private final static UUID NOTIFY_SERVICE_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    private final static UUID NOTIFY_CHARACTERISTIC_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");

    BluetoothGatt mBluetoothGatt;
    Context mContext;
    String TAG = "GATT_Services";

    public GATT_Services(Context context){
        mContext = context;
    }

    public void connectToGatt(BluetoothDevice device){
        if(mBluetoothGatt == null){
            mBluetoothGatt = device.connectGatt(mContext, false, gattCallback, BluetoothDevice.TRANSPORT_LE);
        }
    }

    public void setCharacteristicNotify(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, Boolean enable) {
        Log.i(TAG, "BLE setCharacteristicNotify");
        gatt.setCharacteristicNotification(characteristic, enable);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR_UUID);
        if(enable) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }
        else{
            descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
            gatt.close();
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i(TAG, "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e(TAG, "STATE_DISCONNECTED");
                    mBluetoothGatt = null;
                    break;
                default:
                    Log.e(TAG, "STATE_OTHER");
                    mBluetoothGatt = null;
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i(TAG, "Services: " + services.toString());
            List<BluetoothGattCharacteristic> characteristics = services.get(3).getCharacteristics();
            Log.i(TAG, "Characteristics: " + characteristics.toString());

            setCharacteristicNotify(gatt, characteristics.get(0), true);
           // gatt.readCharacteristic(characteristics.get(0));
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.e(TAG, "descriptor service UUID: " + descriptor.getCharacteristic().getService().getUuid().toString());
            Log.e(TAG, "status: " + status);
            final UUID characteristicUuid = descriptor.getCharacteristic().getUuid();
            if(status == BluetoothGatt.GATT_SUCCESS){
                BluetoothGattCharacteristic characteristic =
                        gatt.getService(NOTIFY_SERVICE_UUID)
                            .getCharacteristic(NOTIFY_CHARACTERISTIC_UUID);

                characteristic.setValue(new byte[] {1,1});
                gatt.writeCharacteristic(characteristic);
            }
            else {
                gatt.close();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            final byte[] dataInput = characteristic.getValue();
            if(status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Read SUCCES");
                StringBuilder sb = new StringBuilder();
                for (byte b : characteristic.getValue()) {
                    sb.append(String.format("%02x", b));
                }

                Log.d(TAG, "HEX %02x: " + sb);
                Log.d(TAG, "String value: " + characteristic.getStringValue(0));
                Log.d(TAG, "getIntValue(FORMAT_UINT8,0) " + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0));
                Log.d(TAG, "getIntValue(FORMAT_UINT8,1) " + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1));
            }
            gatt.close();
        }
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "Read SUCCES");
            StringBuilder sb = new StringBuilder();
            for (byte b : characteristic.getValue()) {
                sb.append(String.format("%02x", b));
            }

            Log.d(TAG, "HEX %02x: " + sb);
            Log.d(TAG, "String value: " + characteristic.getStringValue(0));
            Log.d(TAG, "getIntValue(FORMAT_UINT8,0) " + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0));
            Log.d(TAG, "getIntValue(FORMAT_UINT8,1) " + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1));
        }
    };

}
