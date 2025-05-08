package com.example.transferdata.presentation

import android.app.Application
import com.google.android.gms.wearable.Wearable

class SampleApplication : Application() {

    val capabilityClient by lazy { Wearable.getCapabilityClient(this) }
}