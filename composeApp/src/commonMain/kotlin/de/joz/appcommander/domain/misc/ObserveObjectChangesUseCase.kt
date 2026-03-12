package de.joz.appcommander.domain.misc

import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class ObserveObjectChangesUseCase {
	private var objectToObserve: Any? = null

	var wasObjectChanged: Boolean = false
		private set

	suspend operator fun invoke(flowToObserve: Flow<Any>) {
		flowToObserve.collect {
			if (objectToObserve == null) {
				objectToObserve = it
			}

			wasObjectChanged = objectToObserve != it
		}
	}
}
