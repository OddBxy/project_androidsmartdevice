package fr.isen.lanier.androidsmartdevice.view

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.lanier.androidsmartdevice.models.ServiceBLE

@Composable
fun DeviceView(instanceBLE : ServiceBLE, device : ScanResult, modifier: Modifier){
    if(instanceBLE.isConnected.value == true){
        displayAction(instanceBLE, device, modifier)
    }
    else if(instanceBLE.isConnected.value == false){
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



@SuppressLint("MissingPermission")
@Composable
fun displayAction(instanceBLE : ServiceBLE, device : ScanResult, modifier: Modifier){

    var services : SnapshotStateList<BluetoothGattService> = instanceBLE.services

    if(!services.isEmpty()){

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

            radioButtons(instanceBLE, services[2].characteristics[0])

            Spacer(Modifier.height(30.dp))


            notificationPanel(instanceBLE, services[2].characteristics?.get(1))
            Spacer(Modifier.height(30.dp))
            notificationPanel(instanceBLE, services[3].characteristics?.get(0))
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
            Text("Loading services ......")
        }

    }


}



@SuppressLint("MissingPermission")
@Composable
fun radioButtons(instanceBLE : ServiceBLE, characteristic: BluetoothGattCharacteristic?){

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
                    instanceBLE.writeCharacteristic(characteristic, byteArrayOf(value.toByte()))
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


@OptIn(ExperimentalStdlibApi::class)
@SuppressLint("MissingPermission")
@Composable
fun notificationPanel(instanceBLE : ServiceBLE, characteristic: BluetoothGattCharacteristic?){

    var checked by remember { mutableStateOf(false) }
    val characteristicData by remember {
        derivedStateOf {
            instanceBLE.characteristicValues[
                characteristic?.uuid
            ]
        }
    }


    Row {
        Text("Abonnez-vous pour recevoir le nombre d'incrementation")
        Checkbox(
            checked = checked,
            onCheckedChange = {
                checked = it
                characteristic?.let {
                    if(checked != false) {
                        instanceBLE.enableNotify(characteristic)
                    }
                    else if(checked == false){
                        instanceBLE.disableNotify(characteristic)
                    }
                }
            }
        )
    }

    Spacer(Modifier.height(20.dp))

    Row {
        Text("Nombre : ${characteristicData?.toHexString()}")
    }
}

