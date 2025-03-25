package fr.isen.lanier.androidsmartdevice.view

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.lanier.androidsmartdevice.DeviceActivity
import fr.isen.lanier.androidsmartdevice.models.ServiceBLE
import fr.isen.lanier.androidsmartdevice.view.component.ShowDevice



@SuppressLint("MissingPermission")
@Composable
fun ScanView(instaceBLE : ServiceBLE , devices : MutableList<ScanResult>, context: Context){

    var loading by remember { mutableStateOf(false) }
    var intent = Intent(context, DeviceActivity::class.java)

    Column(
        Modifier.padding(horizontal = 20.dp)
    ) {
        TextButton (
            onClick =  {
                loading = !loading
                if(loading){
                    instaceBLE.startScan()
                }
                else{
                    instaceBLE.stopScan()
                }
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


        Spacer(Modifier.height(20.dp))

        LazyColumn {
            items(devices){
                Column(
                    Modifier.fillMaxWidth().clickable {
                        instaceBLE.stopScan()
                        loading = false
                        intent.putExtra("device", it)
                        context.startActivity(intent)
                    }
                ) {
                    ShowDevice(it)
                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )
                }
            }
        }
    }
}