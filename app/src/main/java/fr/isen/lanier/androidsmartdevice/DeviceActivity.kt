package fr.isen.lanier.androidsmartdevice

import android.Manifest
import android.bluetooth.le.ScanResult
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import fr.isen.lanier.androidsmartdevice.models.ServiceBLEFactory
import fr.isen.lanier.androidsmartdevice.ui.theme.AndroidsmartdeviceTheme
import fr.isen.lanier.androidsmartdevice.view.DeviceView
import fr.isen.lanier.androidsmartdevice.view.component.headerBar

class DeviceActivity() : ComponentActivity() {

    private val instanceBLE = ServiceBLEFactory.getServiceBLEInstance()

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val device = intent.getParcelableExtra<ScanResult>("device")
        enableEdgeToEdge()
        setContent {

            //InstanceBLE.instance.connect(device!!, LocalContext.current)
            instanceBLE.connect(device!!, LocalContext.current)
            AndroidsmartdeviceTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { headerBar() }
                ) { innerPadding ->

                    DeviceView(instanceBLE,device, Modifier.padding(innerPadding))

                }
            }
        }
    }


    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onDestroy() {
        instanceBLE.disconnectDevice()
        super.onDestroy()
    }
}
