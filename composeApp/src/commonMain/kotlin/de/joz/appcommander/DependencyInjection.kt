package de.joz.appcommander

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import de.joz.appcommander.data.getPreferenceFileStorePath
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okio.Path.Companion.toPath
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import java.io.File

@Module(includes = [])
@ComponentScan
class DependencyInjection {
	@Factory
	@MainDispatcher
	fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

	@Factory
	@IODispatcher
	fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

	@Factory
	fun provideProcessBuilder() = ProcessBuilder()

	@Factory
	fun provideWorkingDirectory() = File(".")

	@Factory
	fun provideDatastore(): DataStore<Preferences> =
		PreferenceDataStoreFactory.createWithPath(
			corruptionHandler = null,
			migrations = emptyList(),
			produceFile = {
				getPreferenceFileStorePath(fileName = "userprefs.preferences_pb").toPath()
			},
		)
}

@Named
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class MainDispatcher

@Named
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class IODispatcher
