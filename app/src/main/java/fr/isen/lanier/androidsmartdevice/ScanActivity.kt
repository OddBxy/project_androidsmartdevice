package fr.isen.lanier.androidsmartdevice

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import fr.isen.lanier.androidsmartdevice.ui.theme.AndroidsmartdeviceTheme
import fr.isen.lanier.androidsmartdevice.view.component.ShowDevice

class ScanActivity : ComponentActivity() {

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


    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        GrantPermissions()
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val bluetoothLEAvailable = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)


            AndroidsmartdeviceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    if(bluetoothLEAvailable){
                        if(bluetoothAdapter.isEnabled && isLocationEnabled(context)){
                            Row(Modifier.padding(innerPadding)) {
                                scannedDevices(bluetoothAdapter.bluetoothLeScanner, context)
                            }
                        }
                        else{
                            Column(
                                Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Please enable bluetooth and localisation")
                            }
                        }
                    }
                    else{
                        Column(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Bluetooth low energy doesnt seem to be available")
                        }
                    }

                }
            }
        }

    }



    private fun GrantPermissions() {
        val launcher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.values.all { it }) {
                Log.i("WHOUHOUH", "pesgsiuni")
            } else {
                Log.i("MERDE", "GrantPermissions: ")
            }
        }

        launcher.launch(ALL_BLE_PERMISSIONS)
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

}


@Composable
@RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
fun scannedDevices(bluetoothLeScanner : BluetoothLeScanner, context: Context){

    var loading by remember { mutableStateOf(false) }

    Column(
        Modifier.padding(horizontal = 20.dp)
    ) {
        TextButton (
            onClick =  {
                loading = true
                bluetoothLeScanner.startScan(scanCallback)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp),

        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Start scanning devices", fontSize = 20.sp)
                Icon(Icons.Filled.PlayArrow, "ScanLogo")
            }
        }

        if(loading){
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }

        LazyColumn {
            items(5){
                ShowDevice()
                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }
        }
    }


}




val scanCallback = object : ScanCallback() {

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        super.onScanResult(callbackType, result)
        Log.i("SCAN", "device found: ${result?.device?.name}, address : ${result?.device?.address}")
    }
    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        Log.i("SCANPB", "problem encoutered while scanning ")
    }
}



