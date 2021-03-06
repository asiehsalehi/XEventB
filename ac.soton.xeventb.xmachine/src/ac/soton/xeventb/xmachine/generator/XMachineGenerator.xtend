/*******************************************************************************
* Copyright (c) 2016,2018 University of Southampton.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*     University of Southampton - initial API and implementation
*******************************************************************************/
package ac.soton.xeventb.xmachine.generator

import ac.soton.emf.translator.TranslatorFactory
import ac.soton.eventb.emf.containment.Containment
import ac.soton.eventb.statemachines.Statemachine
import org.eclipse.core.commands.ExecutionException
import org.eclipse.core.resources.IProject
import org.eclipse.core.resources.IWorkspaceRunnable
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.CoreException
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.core.runtime.Status
import org.eclipse.core.runtime.jobs.ISchedulingRule
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.transaction.TransactionalEditingDomain
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext
import org.eventb.emf.core.CoreFactory
import org.eventb.emf.core.machine.Machine
import org.eventb.emf.persistence.SaveResourcesCommand
import org.rodinp.core.RodinCore

//import ac.soton.eventb.records.Record

/**
 * <p>
 * Generating Rodin Context from the XContext.
 * </p>
 *
 * @author htson
 * @author Dana
 * * @author asiehsalehi
 * @version 1.0
 * @since 0.1
 */
class XMachineGenerator extends AbstractGenerator {

	/* @htson Automatically compile to Rodin files */

	// Dana: In 0.0.6 generator is updated to extend AbstractGenerator
	// Save is added after calling the translator 
	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {

		var mch = resource.contents.get(0) as Machine
		
		var uriString = resource.URI.toString
		uriString = uriString.substring(0, uriString.lastIndexOf('bumx'))
		uriString = uriString + "bum"
		var uri = URI.createURI(uriString)

		// @htson: Set the source machine (from XText) as the content of the Rodin machine.
		var editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain()
		var rodinResource = editingDomain.resourceSet.createResource(uri)
		rodinResource.eSetDeliver(false)
		rodinResource.contents.add(0, mch)
		// Set modified resource to be true (otherwise, saving might ignore this).
		rodinResource.modified = true
		rodinResource.eSetDeliver(true)
		
		if (!mch.extensions.empty) {
			val extensions = mch.extensions
			val factory = TranslatorFactory.getFactory() as TranslatorFactory

			// First deal with containments
			var containmentCommandId = 'ac.soton.eventb.emf.diagrams.generator.translateToEventB'
			for (ex : extensions) {
				//containment
				if (ex instanceof Containment) {
					val ctmt = ex as Containment;
					val diagram = ctmt.getExtension();
					
					// If the diagrams is an instance of Statemachine then set the translation target annotation.
					if (diagram instanceof Statemachine) {
						val stm = diagram as Statemachine
						var annotations = stm.annotations
						val annot = CoreFactory.eINSTANCE.createAnnotation()
						annot.setSource("ac.soton.diagrams.translationTarget")
						annot.references.add(mch)
						annotations.add(annot)
					}
					
					// Translate the diagram using the translate factory
					if (factory.canTranslate(containmentCommandId, diagram.eClass())) {
						var monitor = new NullProgressMonitor as IProgressMonitor;
						factory.translate(editingDomain, diagram, containmentCommandId, monitor)
					}
				}
			}

			// @Dana: Now deal with inclusion
			var commandId = 'ac.soton.eventb.emf.inclusion.commands.include'

			if (factory.canTranslate(commandId, mch.eClass())) {
				val monitor = new NullProgressMonitor;
				factory.translate(editingDomain, mch, commandId, monitor)
			}
			
			//record
			var recordCommandId = "ac.soton.eventb.records.commands.record"
			
			if (factory.canTranslate(recordCommandId, mch.eClass())) {
				val monitor = new NullProgressMonitor;
				factory.translate(editingDomain, mch, recordCommandId, monitor)
			}

		}

		// --------------
		// save all resources that have been modified	
		val saveCommand = new SaveResourcesCommand(editingDomain)
		val wsRunnable = new IWorkspaceRunnable() {
			override void run(IProgressMonitor monitor) {
				try {
					saveCommand.execute(monitor, null);
				} catch (ExecutionException e) {
					val status = new Status(IStatus.ERROR, "ac.soton.xeventb.xmachine", "Nothing", e);
					throw new CoreException(status);
				}
			}
		}
		val monitor = new NullProgressMonitor
		// TODO [--> cfs] Why this can not be execute (i.e. no resource change initially?)
		if (saveCommand.canExecute()) {
			val Resource[] emptyResource = #[]

			RodinCore.run(wsRunnable,
				getSchedulingRule(editingDomain.getResourceSet().getResources().toArray(emptyResource)), monitor);
		}
		monitor.done();
			// ------------
	}

	def private ISchedulingRule getSchedulingRule(Resource[] resources) {
		if (resources.length==0){	
			return null;
		}else if (resources.length==1){
			return WorkspaceSynchronizer.getFile(resources.get(0));
		}else {
			val project = getProject(resources.get(0));
			for (Resource resource : resources) {
				if (project != getProject(resource)){
					return  ResourcesPlugin.getWorkspace().getRoot();
				}
			}
			return project;
		}
	}
	
	def private IProject getProject(Resource resource) {
		val file = WorkspaceSynchronizer.getFile(resource);
		return file?.getProject()?:null;
	}
}
