package de.joz.appcommander.ui.misc

import de.joz.appcommander.domain.script.ScriptsRepository
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UiHelperTest {
	@Test
	fun `should return always true when first flag is true`() {
		ScriptsRepository.Platform.entries.forEach {
			assertTrue(UiHelper.isScriptExecutableByUi(true, it))
		}
	}

	@Test
	fun `should return always true when platform is Desktop`() {
		ScriptsRepository.Platform.entries.forEach {
			if (it == ScriptsRepository.Platform.DESKTOP) {
				assertTrue(UiHelper.isScriptExecutableByUi(true, it))
				assertTrue(UiHelper.isScriptExecutableByUi(false, it))
			} else {
				assertTrue(UiHelper.isScriptExecutableByUi(true, it))
				assertFalse(UiHelper.isScriptExecutableByUi(false, it))
			}
		}
	}
}
