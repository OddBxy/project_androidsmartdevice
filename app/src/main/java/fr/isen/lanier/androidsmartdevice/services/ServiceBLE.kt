package fr.isen.lanier.androidsmartdevice.services

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import fr.isen.lanier.androidsmartdevice.ScanActivity

object ServiceBLE {

    var scanResults = mutableStateListOf<ScanResult>()
    private val scanCallback = object : ScanCallback() {

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onScanResult(callbackType: Int, result: ScanResult?) {

            if(result != null && result.device.name != null){
                with(result.device){
                    val indexQuery = scanResults.indexOfFirst { it.device.address == address }
                    if(indexQuery == -1){   //device not found in the list so we can add it
                        Log.i("SCAN", "device found: $name, address : $address")
                        scanResults.add(result)
                    }
                }
            }
        }
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.i("SCANPB", "problem encoutered while scanning ")
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



}