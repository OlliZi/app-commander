package de.joz.appcommander

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named

@Module(includes = [])
@ComponentScan()
class DependencyInjection {
	@Factory
	fun provideJson() =
		Json {
			prettyPrint = true
			ignoreUnknownKeys = true
		}

	@Factory
	@MainDispatcher
	fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

	@Factory
	@IODispatcher
	fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@Named
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class MainDispatcher

@Named
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class IODispatcher
