package fr.isen.lanier.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattService
import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import fr.isen.lanier.androidsmartdevice.services.InstanceBLE
import fr.isen.lanier.androidsmartdevice.services.ServiceBLE
import fr.isen.lanier.androidsmartdevice.ui.theme.AndroidsmartdeviceTheme
import fr.isen.lanier.androidsmartdevice.view.component.ShowDevice
import fr.isen.lanier.androidsmartdevice.view.component.headerBar
import kotlinx.coroutines.selects.select

class DeviceActivity() : ComponentActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val device = intent.getParcelableExtra<ScanResult>("device")
        enableEdgeToEdge()
        setContent {
            val servicesList = remember { InstanceBLE.instance.services }
            val isConnected by InstanceBLE.instance.isConnected
            InstanceBLE.instance.connect(device!!, LocalContext.current)
            AndroidsmartdeviceTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { headerBar() }
                ) { innerPadding ->

                    if(isConnected == true){
                        displayAction(device, servicesList, Modifier.padding(innerPadding))
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





@OptIn(ExperimentalStdlibApi::class)
@SuppressLint("MissingPermission")
@Composable
fun displayAction(device : ScanResult, servicesList : MutableList<BluetoothGattService>,modifier: Modifier){
    var checked by remember { mutableStateOf(false) }
    val characteristicData by remember {
        derivedStateOf {
            InstanceBLE.instance.characteristicValues[servicesList.get(2).characteristics.get(1).uuid]
        }
    }

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

        radioButtons()

        Spacer(Modifier.height(30.dp))

        Row {
            Text("Abonnez-vous pour recevoir le nombre d'incrementation")
            Checkbox(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    if(!servicesList.isEmpty()) {
                        InstanceBLE.instance.enableNotify(servicesList.get(2).characteristics.get(1))
                    }
                }
            )
        }

        Spacer(Modifier.height(20.dp))

        Row {
            if(!servicesList.isEmpty()) {
                Text("Nombre : ${characteristicData?.toHexString()}")
            }
            else {
                Text("Nombre : None")
            }
        }
    }

}



@SuppressLint("MissingPermission")
@Composable
fun radioButtons(){

    var selectedOption by remember { mutableStateOf(0) }
    var leds = listOf(0x01, 0x02, 0x03)

    Text("Affichage des differentes led")
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leds.forEach{ value ->
            Card(
                onClick = {
                    selectedOption = value
                    InstanceBLE.instance.writeCharacteristic(InstanceBLE.instance.services.get(2).characteristics.get(0), byteArrayOf(value.toByte()))
                    Log.i("LEDSTATE", "displayAction: $value")
                },


                ) {
                if(selectedOption != value){
                    Icon(Icons.Outlined.CheckCircle, "LedIconOFF")
                }
                else{
                    Icon(Icons.Filled.CheckCircle, "LedIconOn")
                }
            }
        }
    }

}
