/*
 * generated by Xtext 2.14.0
 */
package ac.soton.xeventb.camillex.tests

import ac.soton.xeventb.internal.camillex.tests.AssertContextExtensions
import ac.soton.xeventb.internal.camillex.tests.AssertExtensions
import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper
import org.eventb.emf.core.EventBNamedCommentedComponentElement
import org.eventb.emf.core.context.Context
import org.junit.Assert
import org.junit.Before
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith

@ExtendWith(InjectionExtension)
@InjectWith(EventBComponentInjectorProvider)
class ContextParsingTest {
	@Inject
	extension ParseHelper<EventBNamedCommentedComponentElement> parseHelper
	
	extension AssertExtensions = new AssertExtensions()
	extension AssertContextExtensions = new AssertContextExtensions()
	
	/**
	 * Manually register any EPackage required for running the tests.
	 */
	@Before
	def void registerEPackages() {
		registerContextEPackage
	}
	
	@Test
	def void testContextClauseSuccessful() {
		val testInput = 
		'''
			context c0
			end
		'''
		val result = testInput.parse // Parse the text input
		Assert.assertNotNull(result) // The result must not be null
		
		val errors = result.eResource.errors
		errors.assertEmpty // There should be no errors.
		
		Assert.assertTrue(result instanceof Context) // The result is a Context
		
		var ctx = result as Context
		ctx.assertContext("c0", null) // 
		ctx.assertContextExtendsNames()
		ctx.assertContextSets()
		ctx.assertContextConstants()
		ctx.assertContextAxioms()
	}

	@Test
	def void testContextClauseSuccessful_ML_COMMENT() {
		val testInput = 
		'''
			/* 
			 * Multi-line
			 * comments
			 */
			context c0
			end
		'''
		val result = testInput.parse
		Assert.assertNotNull(result)
		val errors = result.eResource.errors
		errors.assertEmpty
		Assert.assertTrue(result instanceof Context)
		var ctx = result as Context
		ctx.assertContext("c0", null) // Comments are ignored
		ctx.assertContextExtendsNames()
		ctx.assertContextSets()
		ctx.assertContextConstants()
		ctx.assertContextAxioms()
	}

	@Test
	def void testContextClauseSuccessful_SL_COMMENT() {
		val testInput = 
		'''
			// Single-line comment
			context c0
			end
		'''
		val result = testInput.parse
		Assert.assertNotNull(result)
		val errors = result.eResource.errors
		errors.assertEmpty
		Assert.assertTrue(result instanceof Context)
		var ctx = result as Context
		ctx.assertContext("c0", null) // Comments are ignored
		ctx.assertContextExtendsNames()
		ctx.assertContextSets()
		ctx.assertContextConstants()
		ctx.assertContextAxioms()
	}

	@Test
	def void testContextClauseFailed_ErrornousName() {
		val testInput = 
		'''
			context 0c
			end
		'''
		val result = testInput.parse()
		Assert.assertNotNull(result)
		val errors = result.eResource.errors
		errors.assertNotEmpty
		errors.assertLength(1)
		errors.get(0).assertErrorDetails(
			"extraneous input '0' expecting RULE_ID", null, 9, 1)
		Assert.assertTrue(result instanceof Context)
	}

	@Test
	def void testSetsClauseSuccessful_Sets1() {
		val testInput = 
		'''
			context c0
			sets S
			end
		'''
		val result = testInput.parse
		Assert.assertNotNull(result)
		val errors = result.eResource.errors
		errors.assertEmpty
		Assert.assertTrue(result instanceof Context)
		var ctx = result as Context
		ctx.assertContext("c0", null) // Comments are ignored
		ctx.assertContextExtendsNames()
		ctx.assertContextSets("S:")
		ctx.assertContextConstants()
		ctx.assertContextAxioms()
	}

	@Test
	def void testSetsClauseSuccessful_Sets2() {
		val testInput = 
		'''
			context c0
			sets S T
			end
		'''
		val result = testInput.parse
		Assert.assertNotNull(result)
		val errors = result.eResource.errors
		errors.assertEmpty
		Assert.assertTrue(result instanceof Context)
		var ctx = result as Context
		ctx.assertContext("c0", null)
		ctx.assertContextExtendsNames()
		ctx.assertContextSets("S:", "T:")
		ctx.assertContextConstants()
		ctx.assertContextAxioms()
	}

	@Test
	def void testSetsClauseSuccessful_Constants1() {
		val testInput = 
		'''
			context c0
			constants a
			end
		'''
		val result = testInput.parse
		Assert.assertNotNull(result)
		val errors = result.eResource.errors
		errors.assertEmpty
		Assert.assertTrue(result instanceof Context)
		var ctx = result as Context
		ctx.assertContext("c0", null)
		ctx.assertContextExtendsNames()
		ctx.assertContextSets()
		ctx.assertContextConstants("a:")
		ctx.assertContextAxioms()
	}

	@Test
	def void testSetsClauseSuccessful_Constants2() {
		val testInput = 
		'''
			context c0
			constants a b
			end
		'''
		val result = testInput.parse
		Assert.assertNotNull(result)
		val errors = result.eResource.errors
		errors.assertEmpty
		Assert.assertTrue(result instanceof Context)
		var ctx = result as Context
		ctx.assertContext("c0", null)
		ctx.assertContextExtendsNames()
		ctx.assertContextSets()
		ctx.assertContextConstants("a:", "b:")
		ctx.assertContextAxioms()
	}

	@Test
	def void testSetsClauseSuccessful_Axioms1() {
		val testInput = 
		'''
			context c0
			axioms 
				@axm1: a ∈ S
			end
		'''
		val result = testInput.parse
		Assert.assertNotNull(result)
		val errors = result.eResource.errors
		errors.assertEmpty
		Assert.assertTrue(result instanceof Context)
		var ctx = result as Context
		ctx.assertContext("c0", null)
		ctx.assertContextExtendsNames()
		ctx.assertContextSets()
		ctx.assertContextConstants()
		ctx.assertContextAxioms("axm1:a ∈ S:false:")
	}

	@Test
	def void testSetsClauseSuccessful_Axioms2() {
		val testInput = 
		'''
			context c0
			axioms 
				@axm1: a ∈ S
				@axm2: b ∈ T
			end
		'''
		val result = testInput.parse
		Assert.assertNotNull(result)
		val errors = result.eResource.errors
		errors.assertEmpty
		Assert.assertTrue(result instanceof Context)
		var ctx = result as Context
		ctx.assertContext("c0", null)
		ctx.assertContextExtendsNames()
		ctx.assertContextSets()
		ctx.assertContextConstants()
		ctx.assertContextAxioms("axm1:a ∈ S:false:", "axm2:b ∈ T:false:")
	}
}
