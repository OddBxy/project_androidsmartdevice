package fr.isen.lanier.androidsmartdevice.services

object InstanceBLE {
    val instance: ServiceBLE by lazy {
        ServiceBLE()
    }
}