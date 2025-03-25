package fr.isen.lanier.androidsmartdevice.models

class ServiceBLEFactory {
    companion object {
        private var serviceBLEInstance: ServiceBLE? = null

        fun getServiceBLEInstance(): ServiceBLE {
            if (serviceBLEInstance == null) {
                serviceBLEInstance = ServiceBLE() // Create instance if not already created
            }
            return serviceBLEInstance!!
        }
    }
}