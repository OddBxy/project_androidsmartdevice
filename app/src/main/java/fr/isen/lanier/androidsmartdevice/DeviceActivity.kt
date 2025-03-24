package fr.isen.lanier.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.lanier.androidsmartdevice.services.ServiceBLE
import fr.isen.lanier.androidsmartdevice.ui.theme.AndroidsmartdeviceTheme
import fr.isen.lanier.androidsmartdevice.view.component.ShowDevice

class DeviceActivity() : ComponentActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val device = intent.getParcelableExtra<ScanResult>("device")
        enableEdgeToEdge()
        setContent {
            val servicesList = remember { ServiceBLE.services }
            val isConnected by ServiceBLE.isConnected
            ServiceBLE.connect(device!!, LocalContext.current)
            AndroidsmartdeviceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    if(isConnected == true){
                        LazyColumn(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(servicesList){
                                Text(it.uuid.toString())
                            }
                        }
                    }
                    else if(isConnected == false){
                        Column(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("A problem has occured while connecting")
                        }
                    }
                    else{
                        Column(
                            Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            CircularProgressIndicator(
                                modifier = Modifier.fillMaxWidth(1/6f),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                            Spacer(Modifier.height(30.dp))
                            Text("Connecting ......")
                        }
                    }

                }
            }
        }
    }
}
