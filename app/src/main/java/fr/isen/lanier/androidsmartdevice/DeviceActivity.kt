package fr.isen.lanier.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.lanier.androidsmartdevice.services.ServiceBLE
import fr.isen.lanier.androidsmartdevice.ui.theme.AndroidsmartdeviceTheme
import fr.isen.lanier.androidsmartdevice.view.component.ShowDevice
import fr.isen.lanier.androidsmartdevice.view.component.headerBar

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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { headerBar() }
                ) { innerPadding ->

                    if(isConnected == true){
//                        LazyColumn(
//                            Modifier.fillMaxSize(),
//                            verticalArrangement = Arrangement.Center,
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            items(servicesList){
//                                Text(it.uuid.toString())
//                            }
//                        }

                        displayAction(device , Modifier.padding(innerPadding))
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


@SuppressLint("MissingPermission")
@Composable
fun displayAction(device : ScanResult, modifier: Modifier){
    var checked by remember { mutableStateOf(false) }
    var ledStates = remember { mutableStateListOf(false, false, false) }
    Column(
        modifier = modifier.padding(30.dp)
    ) {

        Text(
            device.device.name,
            Modifier.fillMaxWidth(),
            fontSize = 30.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(50.dp))

        Text("Affichage des differentes led")
        LazyRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(3){

                IconButton(
                    onClick = {
                        ledStates[it] = !ledStates[it]
                        Log.i("LEDSTATE", "displayAction: ${ledStates[it]}")
                    }
                ) {
                    if(!ledStates[it]){
                        Icon(Icons.Outlined.CheckCircle, "LedIconOFF")
                    }
                    else{
                        Icon(Icons.Filled.CheckCircle, "LedIconOn")
                    }
                }
            }
        }

        Spacer(Modifier.height(30.dp))

        Row {
            Text("Abonnez-vous pour recevoir le nombre d'incrementation")
            Checkbox(
                checked = checked,
                onCheckedChange = { checked = it }
            )
        }

        Spacer(Modifier.height(20.dp))

        Row {
            Text("Nombre")
        }
    }

}

