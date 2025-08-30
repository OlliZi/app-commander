package de.joz.appcommander

import android.app.Application
import de.joz.appcommander.data.DataStoreAndroidImpl
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.ksp.generated.*

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // REMOVE
        DataStoreAndroidImpl.dummycontext = this

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MainApplication)
            modules(DependencyInjection().module)
        }
    }
}