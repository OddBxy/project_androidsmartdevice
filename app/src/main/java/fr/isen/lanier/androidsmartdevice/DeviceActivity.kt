package fr.isen.lanier.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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
            ServiceBLE.connect(device!!, LocalContext.current)
            AndroidsmartdeviceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LazyColumn {
                        items(servicesList){
                            Text(it.uuid.toString())
                        }
                    }
                }
            }
        }
    }
}
