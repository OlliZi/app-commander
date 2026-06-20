package de.joz.appcommander.helper

import org.junit.Rule

abstract class TestRuleApplier {
	@JvmField
	@Rule
	val mainDispatcherRule = MainDispatcherRule()
}
