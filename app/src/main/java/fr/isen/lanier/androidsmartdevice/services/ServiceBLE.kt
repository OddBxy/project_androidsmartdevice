package fr.isen.lanier.androidsmartdevice.services

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import java.util.UUID

class ServiceBLE {

    var scanResults = mutableStateListOf<ScanResult>()
    var services = mutableStateListOf<BluetoothGattService>()
    var isConnected = mutableStateOf<Boolean?>(null)
    var characteristicValues = mutableStateMapOf<UUID, ByteArray?>()
    private var bluetoothGatt: BluetoothGatt? = null

    private val scanCallback = object : ScanCallback() {

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onScanResult(callbackType: Int, result: ScanResult?) {

            if(result != null && result.device.name != null){
                with(result.device){
                    val indexQuery = scanResults.indexOfFirst { it.device.address == address }
                    if(indexQuery == -1){   //device not found in the list so we can add it
                        Log.i("SCANBLE_OK", "device found: $name, address : $address")
                        scanResults.add(result)
                    }
                }
            }
        }
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.i("SCANBLE_PB", "problem encoutered while scanning ")
        }
    }


    @OptIn(ExperimentalStdlibApi::class)
    private val connectCallback = object : BluetoothGattCallback(){
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            if (status != BluetoothGatt.GATT_SUCCESS) {
                isConnected.value = false
                Log.i("CONNECTBLE_PB", "enable to connect to device")
                return
            }

            if (newState == BluetoothGatt.STATE_CONNECTED) {
                isConnected.value = true
                Log.i("CONNECTBLE_OK", "onConnectionStateChange: ")
                Handler(Looper.getMainLooper()).post {
                    gatt?.discoverServices()
                }
            }
        }


        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            Log.i("SERVICES", "${gatt?.services}")
            services.clear()
            gatt?.services?.let {
                services.addAll(it)
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            Log.i("WRITE", "Write status: $status")
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)

            characteristicValues[characteristic.uuid] = characteristic.value
            Log.i("NOTIFY CHANGE", "New value: ${characteristic.value.toHexString()}")
        }


        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            super.onDescriptorWrite(gatt, descriptor, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("NOTIFIED_OK", "Descriptor ${descriptor?.uuid} value : ${descriptor?.value}")
            }
            else {
                Log.i("NOTIFIED_PB", "Descriptor ${descriptor?.uuid} : write fail (status=$status)")
            }
        }

    }



    val ALL_BLE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
    }
    else {
        arrayOf(
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }




    val bluetoothScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    public fun startScan(){
        bluetoothScanner.startScan(scanCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    public fun stopScan(){
        bluetoothScanner.stopScan(scanCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public fun connect(device : ScanResult, context: Context){
        isConnected.value = null
        bluetoothGatt = device.device.connectGatt(context, false, connectCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public fun writeCharacteristic(characteristic: BluetoothGattCharacteristic?, newValue : ByteArray){
        if(characteristic != null){
            Log.i("WRITE UUID", "UUID : ${characteristic.uuid}, VALUE : $newValue")
            characteristic.setValue(newValue)
            bluetoothGatt?.writeCharacteristic(characteristic)
        }
    }


    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public fun enableNotify(characteristic: BluetoothGattCharacteristic?){
        if( (characteristic != null) && (characteristic.isNotifiable()) ){
            bluetoothGatt?.setCharacteristicNotification(characteristic, true)

            characteristic.descriptors[0].setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            bluetoothGatt?.writeDescriptor(characteristic.descriptors[0])

            Log.i("DESCRIPTOR", "${characteristic.descriptors.get(0)}")

        }
    }


    fun BluetoothGattCharacteristic.isNotifiable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_NOTIFY)

    fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean =
        properties and property != 0

}