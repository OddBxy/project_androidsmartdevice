package fr.isen.lanier.androidsmartdevice

import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import fr.isen.lanier.androidsmartdevice.ui.theme.AndroidsmartdeviceTheme
import fr.isen.lanier.androidsmartdevice.view.component.ShowDevice

class ScanActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val bluetoothScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
            val bluetoothLEAvailable = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)

            AndroidsmartdeviceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->


                    if(bluetoothLEAvailable){
                        if(bluetoothScanner != null){
                            Row(Modifier.padding(innerPadding)) {
                                scanDevices()
                            }
                        }
                        else{
                            Column(
                                Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Please enable bluetooth")
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
}


@Composable
fun scanDevices(){

    var loading by remember { mutableStateOf(false) }

    Column(
        Modifier.padding(horizontal = 20.dp)
    ) {
        TextButton (
            onClick =  { loading = true },
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
                Icon(Icons.Filled.PlayArrow, "EventLogo")
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