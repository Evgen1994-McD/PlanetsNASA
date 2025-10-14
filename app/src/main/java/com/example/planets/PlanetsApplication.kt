package com.example.planets

import android.app.Application
import com.example.planets.data.database.ApodDatabase

class PlanetsApplication : Application() {
    
    val database: ApodDatabase by lazy {
        ApodDatabase.getDatabase(this)
    }
}
