/**
 * Copyright (c) 2016-2017 University of Southampton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * University of Southampton - initial API and implementation
 */
package ac.soton.xeventb.generator;

import ac.soton.emf.translator.TranslatorFactory;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eventb.emf.core.machine.Machine;
import org.eventb.emf.persistence.EMFRodinDB;

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
@SuppressWarnings("all")
public class XMachineGenerator implements IGenerator {
  /**
   * @htson Automatically compile to Rodin files
   */
  @Override
  public void doGenerate(final Resource resource, final IFileSystemAccess fsa) {
    try {
      EObject _get = resource.getContents().get(0);
      Machine mch = ((Machine) _get);
      boolean _isEmpty = mch.getExtensions().isEmpty();
      if (_isEmpty) {
        EMFRodinDB emfRodinDB = new EMFRodinDB();
        String uriString = resource.getURI().toString();
        uriString = uriString.substring(0, uriString.lastIndexOf("bumx"));
        uriString = (uriString + "bum");
        URI uri = URI.createURI(uriString);
        emfRodinDB.saveResource(uri, mch);
      } else {
        String commandId = "ac.soton.eventb.emf.inclusion.commands.include";
        TranslatorFactory _factory = TranslatorFactory.getFactory();
        TranslatorFactory factory = ((TranslatorFactory) _factory);
        if (((factory != null) && factory.canTranslate(commandId, mch.eClass()))) {
          NullProgressMonitor _nullProgressMonitor = new NullProgressMonitor();
          factory.translate(mch, commandId, _nullProgressMonitor);
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
