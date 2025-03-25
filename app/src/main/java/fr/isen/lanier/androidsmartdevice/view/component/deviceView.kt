package fr.isen.lanier.androidsmartdevice.view.component

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.lanier.androidsmartdevice.services.InstanceBLE

@Composable
fun DeviceView(device : ScanResult, modifier: Modifier){
    if(InstanceBLE.instance.isConnected.value == true){
        displayAction(device, modifier)
    }
    else if(InstanceBLE.instance.isConnected.value == false){
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





@OptIn(ExperimentalStdlibApi::class)
@SuppressLint("MissingPermission")
@Composable
fun displayAction(device : ScanResult, modifier: Modifier){

    var services = InstanceBLE.instance.services
    var checked by remember { mutableStateOf(false) }
    val characteristicData by remember {
        derivedStateOf {
            InstanceBLE.instance.characteristicValues[
                services.get(2).characteristics.get(1).uuid
            ]
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
                    if(!services.isEmpty()) {
                        InstanceBLE.instance.enableNotify(services.get(2).characteristics.get(1))
                    }
                }
            )
        }

        Spacer(Modifier.height(20.dp))

        Row {
            if(!services.isEmpty()) {
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
    var services = InstanceBLE.instance.services

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
                    InstanceBLE.instance.writeCharacteristic(services.get(2).characteristics.get(0), byteArrayOf(value.toByte()))
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
