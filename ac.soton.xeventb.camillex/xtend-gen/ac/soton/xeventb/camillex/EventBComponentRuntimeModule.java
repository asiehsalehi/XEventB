/**
 * generated by Xtext 2.14.0
 */
package ac.soton.xeventb.camillex;

import ac.soton.xeventb.camillex.AbstractEventBComponentRuntimeModule;
import ac.soton.xeventb.camillex.EventBComponentTransientValueService;
import ac.soton.xeventb.camillex.EventBComponentValueConverter;
import ac.soton.xeventb.camillex.scoping.EventBComponentScopeProvider;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.parsetree.reconstr.ITransientValueService;
import org.eclipse.xtext.scoping.IScopeProvider;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
@SuppressWarnings("all")
public class EventBComponentRuntimeModule extends AbstractEventBComponentRuntimeModule {
  /**
   * Bind the value converter service for Event-B attributes and elements, e.g.,
   * labels, etc.
   * 
   * @see EventBComponentValueConverter
   */
  @Override
  public Class<? extends IValueConverterService> bindIValueConverterService() {
    return EventBComponentValueConverter.class;
  }
  
  /**
   * Bind the transient value service for XContext, use for serialisation of
   * EMF Event-B to XText.
   * 
   * @see XContextTransientValueService
   */
  @Override
  public Class<? extends ITransientValueService> bindITransientValueService() {
    return EventBComponentTransientValueService.class;
  }
  
  /**
   * Bind the scope provider, use for references for context extensions, etc.
   * 
   * @see XContextScopeProvider
   */
  @Override
  public Class<? extends IScopeProvider> bindIScopeProvider() {
    return EventBComponentScopeProvider.class;
  }
}
