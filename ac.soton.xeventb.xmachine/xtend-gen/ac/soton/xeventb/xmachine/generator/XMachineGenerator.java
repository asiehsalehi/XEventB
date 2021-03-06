/**
 * Copyright (c) 2016,2018 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     University of Southampton - initial API and implementation
 */
package ac.soton.xeventb.xmachine.generator;

import ac.soton.emf.translator.TranslatorFactory;
import ac.soton.eventb.emf.containment.Containment;
import ac.soton.eventb.emf.diagrams.Diagram;
import ac.soton.eventb.statemachines.Statemachine;
import com.google.common.base.Objects;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eventb.emf.core.AbstractExtension;
import org.eventb.emf.core.Annotation;
import org.eventb.emf.core.CoreFactory;
import org.eventb.emf.core.machine.Machine;
import org.eventb.emf.persistence.SaveResourcesCommand;
import org.rodinp.core.RodinCore;

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
@SuppressWarnings("all")
public class XMachineGenerator extends AbstractGenerator {
  /**
   * @htson Automatically compile to Rodin files
   */
  @Override
  public void doGenerate(final Resource resource, final IFileSystemAccess2 fsa, final IGeneratorContext context) {
    try {
      EObject _get = resource.getContents().get(0);
      Machine mch = ((Machine) _get);
      String uriString = resource.getURI().toString();
      uriString = uriString.substring(0, uriString.lastIndexOf("bumx"));
      uriString = (uriString + "bum");
      URI uri = URI.createURI(uriString);
      TransactionalEditingDomain editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
      Resource rodinResource = editingDomain.getResourceSet().createResource(uri);
      rodinResource.eSetDeliver(false);
      rodinResource.getContents().add(0, mch);
      rodinResource.setModified(true);
      rodinResource.eSetDeliver(true);
      boolean _isEmpty = mch.getExtensions().isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        final EList<AbstractExtension> extensions = mch.getExtensions();
        TranslatorFactory _factory = TranslatorFactory.getFactory();
        final TranslatorFactory factory = ((TranslatorFactory) _factory);
        String containmentCommandId = "ac.soton.eventb.emf.diagrams.generator.translateToEventB";
        for (final AbstractExtension ex : extensions) {
          if ((ex instanceof Containment)) {
            final Containment ctmt = ((Containment) ex);
            final Diagram diagram = ctmt.getExtension();
            if ((diagram instanceof Statemachine)) {
              final Statemachine stm = ((Statemachine) diagram);
              EList<Annotation> annotations = stm.getAnnotations();
              final Annotation annot = CoreFactory.eINSTANCE.createAnnotation();
              annot.setSource("ac.soton.diagrams.translationTarget");
              annot.getReferences().add(mch);
              annotations.add(annot);
            }
            boolean _canTranslate = factory.canTranslate(containmentCommandId, diagram.eClass());
            if (_canTranslate) {
              NullProgressMonitor _nullProgressMonitor = new NullProgressMonitor();
              IProgressMonitor monitor = ((IProgressMonitor) _nullProgressMonitor);
              factory.translate(editingDomain, diagram, containmentCommandId, monitor);
            }
          }
        }
        String commandId = "ac.soton.eventb.emf.inclusion.commands.include";
        boolean _canTranslate_1 = factory.canTranslate(commandId, mch.eClass());
        if (_canTranslate_1) {
          final NullProgressMonitor monitor_1 = new NullProgressMonitor();
          factory.translate(editingDomain, mch, commandId, monitor_1);
        }
        String recordCommandId = "ac.soton.eventb.records.commands.record";
        boolean _canTranslate_2 = factory.canTranslate(recordCommandId, mch.eClass());
        if (_canTranslate_2) {
          final NullProgressMonitor monitor_2 = new NullProgressMonitor();
          factory.translate(editingDomain, mch, recordCommandId, monitor_2);
        }
      }
      final SaveResourcesCommand saveCommand = new SaveResourcesCommand(editingDomain);
      final IWorkspaceRunnable wsRunnable = new IWorkspaceRunnable() {
        @Override
        public void run(final IProgressMonitor monitor) {
          try {
            try {
              saveCommand.execute(monitor, null);
            } catch (final Throwable _t) {
              if (_t instanceof ExecutionException) {
                final ExecutionException e = (ExecutionException)_t;
                final Status status = new Status(IStatus.ERROR, "ac.soton.xeventb.xmachine", "Nothing", e);
                throw new CoreException(status);
              } else {
                throw Exceptions.sneakyThrow(_t);
              }
            }
          } catch (Throwable _e) {
            throw Exceptions.sneakyThrow(_e);
          }
        }
      };
      final NullProgressMonitor monitor_3 = new NullProgressMonitor();
      boolean _canExecute = saveCommand.canExecute();
      if (_canExecute) {
        final Resource[] emptyResource = {};
        RodinCore.run(wsRunnable, 
          this.getSchedulingRule(editingDomain.getResourceSet().getResources().<Resource>toArray(emptyResource)), monitor_3);
      }
      monitor_3.done();
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private ISchedulingRule getSchedulingRule(final Resource[] resources) {
    int _length = resources.length;
    boolean _equals = (_length == 0);
    if (_equals) {
      return null;
    } else {
      int _length_1 = resources.length;
      boolean _equals_1 = (_length_1 == 1);
      if (_equals_1) {
        return WorkspaceSynchronizer.getFile(resources[0]);
      } else {
        final IProject project = this.getProject(resources[0]);
        for (final Resource resource : resources) {
          IProject _project = this.getProject(resource);
          boolean _notEquals = (!Objects.equal(project, _project));
          if (_notEquals) {
            return ResourcesPlugin.getWorkspace().getRoot();
          }
        }
        return project;
      }
    }
  }
  
  private IProject getProject(final Resource resource) {
    final IFile file = WorkspaceSynchronizer.getFile(resource);
    IProject _elvis = null;
    IProject _project = null;
    if (file!=null) {
      _project=file.getProject();
    }
    if (_project != null) {
      _elvis = _project;
    } else {
      _elvis = null;
    }
    return _elvis;
  }
}
