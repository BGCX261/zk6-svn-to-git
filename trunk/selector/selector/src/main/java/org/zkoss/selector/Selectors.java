/**
 * 
 */
package org.zkoss.selector;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.zkoss.selector.annotation.Wire;
import org.zkoss.selector.annotation.Listen;
import org.zkoss.selector.impl.ComponentIterator;
import org.zkoss.selector.impl.util.Reflections;
import org.zkoss.selector.impl.util.Reflections.FieldRunner;
import org.zkoss.selector.impl.util.Reflections.MethodRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.sys.ExecutionCtrl;

/**
 * A collection of selector related utilities. 
 * @author simonpai
 */
public class Selectors {
	
	// TODO: wrap iterable
	
	/**
	 * Returns an iterator that iterates through all Components matched by the
	 * selector. 
	 * @param page the reference page for selector
	 * @param selector the selector string
	 * @return an Iterator of Component
	 */
	public static Iterator<Component> iterator(Page page, String selector) {
		return new ComponentIterator(page, selector);
	}
	
	/**
	 * Returns an iterator that iterates through all Components matched by the
	 * selector. 
	 * @param root the reference component for selector
	 * @param selector the selector string
	 * @return an Iterator of Component
	 */
	public static Iterator<Component> iterator(Component root, String selector){
		return new ComponentIterator(root, selector);
	}
	
	/**
	 * Returns a list of Components that match the selector.
	 * @param page the reference page for selector
	 * @param selector the selector string
	 * @return a List of Component
	 */
	public static List<Component> find(Page page, String selector) {
		return toList(iterator(page, selector));
	}
	
	/**
	 * Returns a list of Components that match the selector.
	 * @param root the reference component for selector
	 * @param selector the selector string
	 * @return a List of Component
	 */
	public static List<Component> find(Component root, String selector) {
		return toList(iterator(root, selector));
	}
	
	/**
	 * Returns the ith component that matches the selector
	 * @param page the reference page for selector
	 * @param selector the selector string
	 * @param index 1-based index (1 means the first component found)
	 * @return Component, null if not found
	 */
	public static Component find(Page page, String selector, int index) {
		return getIthItem(new ComponentIterator(page, selector), index);
	}
	
	/**
	 * Returns the ith component that matches the selector
	 * @param root root the reference component for selector
	 * @param selector selector the selector string
	 * @param index 1-based index (1 means the first component found)
	 * @return Component, null if not found
	 */
	public static Component find(Component root, String selector, int index) {
		return getIthItem(new ComponentIterator(root, selector), index);
	}
	
	/**
	 * Wire variables to controller, including components from the page, 
	 * implicit variables, ZScript variables and XEL variables.
	 * @param page the reference page for selector
	 * @param controller the controller object to be injected with variables
	 */
	public static void wireVariables(Page page, Object controller){
		new Wirer(controller).wireVariables(new PageFunctor(page));
	}
	
	/**
	 * Wire variables to controller, including components from the page, 
	 * implicit variables, ZScript variables and XEL variables depending on the
	 * boolean flags.
	 * @param page the reference page for selector
	 * @param controller the controller object to be injected with variables
	 * @param ignoreZScript if true, ZScript variables are not wired
	 * @param ignoreXel if true, XEL variables are not wired
	 */
	public static void wireVariables(Page page, Object controller, 
			boolean ignoreZScript, boolean ignoreXel) {
		new Wirer(controller, ignoreZScript, ignoreXel)
				.wireVariables(new PageFunctor(page));
	}
	
	/**
	 * Wire variables to controller, including components, implicit variables, 
	 * ZScript variables and XEL variables.
	 * @param component the reference component for selector
	 * @param controller the controller object to be injected with variables
	 */
	public static void wireVariables(Component component, Object controller){
		new Wirer(controller).wireVariables(new ComponentFunctor(component));
	}
	
	/**
	 * Wire variables to controller, including components, implicit variables, 
	 * ZScript variables and XEL variables depending on the boolean flags.
	 * @param component the reference component for selector
	 * @param controller the controller object to be injected with variables
	 * @param ignoreZScript if true, ZScript variables are not wired
	 * @param ignoreXel if true, XEL variables are not wired
	 */
	public static void wireVariables(Component component, Object controller, 
			boolean ignoreZScript, boolean ignoreXel) {
		new Wirer(controller, ignoreZScript, ignoreXel)
				.wireVariables(new ComponentFunctor(component));
	}
	
	/**
	 * Add a reference of controller in the attributes of component. If &#064;Wire
	 * is present on the controller class with a nonempty value, the value will 
	 * be the attribute name. Otherwise the old convention is used.
	 * @param component the component to inject reference
	 * @param controller the controller to be referred to
	 */
	public static void wireController(Component component, Object controller){
		Wire anno = controller.getClass().getAnnotation(Wire.class);
		if(anno == null || anno.value().length() == 0) { 
			Components.wireController(component, controller);
			return;
		}
		
		String compKey = anno.value();
		if (!component.hasAttributeOrFellow(compKey, false))
			component.setAttribute(compKey, controller);
	}
	
	/**
	 * Add event listeners to components based on the controller.
	 * @param component the reference component for selector 
	 * @param controller the controller of event listening methods
	 */
	public static void wireEventListeners(final Component component, 
			final Object controller){
		Reflections.forMethods(controller.getClass(), Listen.class, 
				new MethodRunner<Listen>(){
			public void onMethod(Class<?> clazz, Method method, Listen anno) {
				// check method signature
				if((method.getModifiers() & Modifier.STATIC) != 0) 
					throw new UiException("Cannot add forward to static method: " + 
							method.getName());
				// method should have 0 or 1 parameter
				if(method.getParameterTypes().length > 1) 
					throw new UiException("Event handler method should have " + 
							"at most one parameter: " + method.getName());
				
				for(String[] strs : splitListenAnnotationValues(anno.value())){
					String name = strs[0];
					if(name == null) name = "onClick";
					Iterator<Component> iter = iterator(component, strs[1]);
					// no forwarding, just add to event listener
					while(iter.hasNext())
						iter.next().addEventListener(name, 
								new ComposerEventListener(method, controller));
				}
			}
		});
	}
	
	
	
	// helper //
	private static String[][] splitListenAnnotationValues(String str){
		List<String[]> result = new ArrayList<String[]>();
		int len = str.length();
		boolean inSqBracket = false;
		boolean inQuote = false;
		boolean escaped = false;
		String evtName = null;
		int i = 0;
		
		for(int j = 0; j < len; j++) {
			char c = str.charAt(j);
			
			if(!escaped)
				switch(c){
				case '[': inSqBracket = true; break;
				case ']': inSqBracket = false; break;
				case '"':
				case '\'':
					inQuote = !inQuote;
					break;
				case '=':
					if(inSqBracket || inQuote) break;
					if(evtName != null)
						throw new UiException("Illegal value of @Listen: " + str);
					evtName = str.substring(i, j).trim();
					// check event name: onX
					if(evtName.length() < 3 || !evtName.startsWith("on") || 
							!Character.isUpperCase(evtName.charAt(2)))
						throw new UiException("Illegal value of @Listen: " + str);
					i = j + 1;
					break;
				case ';':
					if(inQuote) break;
					String target = str.substring(i, j).trim();
					// check selector string: nonempty
					if(target.length() == 0)
						throw new UiException("Illegal value of @Listen: " + str);
					result.add(new String[]{evtName, target});
					i = j + 1;
					evtName = null;
					break;
				default:
					// do nothing
				}
			
			escaped = !escaped && c == '\\'; 
		}
		
		// flush last chunk if any
		if(i < len) {
			String last = str.substring(i).trim();
			if(last.length() > 0)
				result.add(new String[]{evtName, last});
		}
		return result.toArray(new String[0][0]);
	}
	
	private static <T> List<T> toList(Iterator<T> iterator){
		List<T> result = new ArrayList<T>();
		while(iterator.hasNext()) result.add(iterator.next());
		return result;
	}
	
	// helper: auto wire //
	private static class Wirer {
		
		private final Object _controller;
		private final boolean _ignoreXel;
		private final boolean _ignoreZScript;
		
		private Wirer(Object controller){
			this(controller, true, true);
		}
		
		private Wirer(Object controller, boolean ignoreZScript, boolean ignoreXel){
			_controller = controller;
			_ignoreZScript = ignoreZScript;
			_ignoreXel = ignoreXel;
		}
		
		private void wireVariables(final PsdoCompFunctor functor) {
			Class<?> ctrlClass = _controller.getClass();
			// wire to fields
			Reflections.forFields(ctrlClass, Wire.class, new FieldRunner<Wire>(){
				public void onField(Class<?> clazz, Field field, Wire anno) {
					if((field.getModifiers() & Modifier.STATIC) != 0)
						throw new UiException("Cannot wire variable to " + 
								"static field: " + field.getName());
					
					String selector = anno.value();
					boolean optional = anno.optional();
					if(selector.length() > 0) {
						injectComponent(field, functor.iterator(selector), optional);
						return;
					}
					
					// no selector value, wire implicit object by naming convention
					Object value = 
						getObjectByName(functor, field.getName(), field.getType());
					if(value != null) {
						Reflections.setFieldValue(_controller, field, value);
						return;
					} 
					if(optional) return;
					
					// no matched Object or Component
					String name = field.getName();
					if(name.contains("$")) throw new UiException(
							"GenericAnnotatedComposer does not support " + 
							"syntax with '$'. Please use selector as alternative.");
					throw new UiException("Cannot wire variable to field: " + name);
				}
			});
			// wire by methods
			Reflections.forMethods(ctrlClass, Wire.class, new MethodRunner<Wire>(){
				public void onMethod(Class<?> clazz, Method method, Wire anno) {
					// check method signature
					String name = method.getName();
					if((method.getModifiers() & Modifier.STATIC) != 0) 
						throw new UiException("Cannot wire variable by static" + 
								" method: " + name);
					Class<?>[] paramTypes = method.getParameterTypes();
					if(paramTypes.length != 1) 
						throw new UiException("Setter method should have only" + 
								" one parameter: " + name);
					
					String selector = anno.value();
					// check selector string: nonempty
					if(selector.length() == 0)
						throw new UiException("Selector is empty on method: " + 
								method.getName());
					
					injectComponent(method, functor.iterator(selector), 
							anno.optional());
				}
			});
		}
		
		private void injectComponent(Method method, Iterator<Component> iter, 
				boolean optional) {
			injectComponent(new MethodFunctor(method), iter, optional);
		}
		
		private void injectComponent(Field field, Iterator<Component> iter, 
				boolean optional) {
			injectComponent(new FieldFunctor(field), iter, optional);
		}
		
		@SuppressWarnings("unchecked")
		private void injectComponent(InjectionFunctor injector, 
				Iterator<Component> iter, boolean optional) {
			Class<?> type = injector.getType();
			boolean isField = injector instanceof FieldFunctor;
			// Array
			if(type.isArray()) {
				injector.inject(_controller, 
						generateArray(type.getComponentType(), iter));
				return;
				
			}
			// Collection
			if(Collection.class.isAssignableFrom(type)) {
				
				Collection collection = null;
				if(isField) {
					Field field = ((FieldFunctor) injector).getField();
					try {
						collection = (Collection) field.get(_controller);
					} catch (Exception e) {
						throw new IllegalStateException("Field " + field + 
							" not accessible or not declared by" + _controller);
					}
				}
				
				// try to give an instance if null 
				if(collection == null) {
					collection = getCollectionInstanceIfPossible(type);
					if(collection == null)
						throw new UiException("Cannot initiate collection for "+
								(isField? "field" : "method") + ": " + 
								injector.getName() + " on " + _controller);
					if(isField) injector.inject(_controller, collection);
				}
				
				// try add to collection
				collection.clear();
				Component comp = null;
				while(iter.hasNext()) {
					comp = iter.next();
					if(!Reflections.isAppendableToCollection(
							injector.getGenericType(), comp)) continue;
					collection.add(comp);
				}
				if(!isField) injector.inject(_controller, collection);
				return;
			} 
			// set to field once or invoke method once
			while(iter.hasNext()) {
				Component comp = iter.next();
				if(!type.isInstance(comp)) continue;
				
				injector.inject(_controller, comp);
				return;
			}
			if(!optional)
				// failed to inject, throw exception
				throw new UiException("Failed to inject to field " + 
						injector.getName() + "on controller " + _controller);
			injector.inject(_controller, null); // no match, inject null
		}
		
		private Object getObjectByName(PsdoCompFunctor functor, 
				String name, Class<?> type) {
			Object result = functor.getImplicit(name);
			if(isValidValue(result, type)) return result;
			
			if(!_ignoreZScript) {
				result = functor.getZScriptVariable(name);
				if(isValidValue(result, type)) return result;
			}
			
			result = functor.getAttributeOrFellow(name);
			if(isValidValue(result, type)) return result;
			
			if(!_ignoreXel) {
				result = functor.getXelVariable(name);
				if(isValidValue(result, type)) return result;
			}
			
			result = functor.getFellowIfAny(name);
			return isValidValue(result, type)? result : null;
		}
		
		private interface InjectionFunctor {
			public void inject(Object obj, Object value);
			public String getName();
			public Class<?> getType();
			public Type getGenericType();
		}
		
		private class FieldFunctor implements InjectionFunctor {
			private final Field _field;
			private FieldFunctor(Field field){ _field = field; }
			public void inject(Object obj, Object value) {
				Reflections.setFieldValue(obj, _field, value);
			}
			public String getName() {
				return _field.getName();
			}
			public Class<?> getType() {
				return _field.getType();
			}
			public Field getField(){
				return _field;
			}
			public Type getGenericType() {
				return _field.getGenericType();
			}
		}
		
		private class MethodFunctor implements InjectionFunctor {
			private final Method _method;
			private MethodFunctor(Method method){ _method = method; }
			public String getName() {
				return _method.getName();
			}
			public void inject(Object obj, Object value) {
				Reflections.invokeMethod(_method, obj, value);
			}
			public Class<?> getType() {
				return _method.getParameterTypes()[0];
			}
			public Type getGenericType() {
				return _method.getGenericParameterTypes()[0];
			}
		}
		
	}
	
	private static boolean isValidValue(Object value, Class<?> clazz){
		return value != null && clazz.isAssignableFrom(value.getClass());
	}
	
	@SuppressWarnings("unchecked")
	private static Collection getCollectionInstanceIfPossible(Class<?> clazz){
		if(clazz.isAssignableFrom(ArrayList.class)) return new ArrayList();
		if(clazz.isAssignableFrom(HashSet.class)) return new HashSet();
		if(clazz.isAssignableFrom(TreeSet.class)) return new TreeSet();
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T[] generateArray(Class<T> clazz, Iterator<Component> iter){
		// add to a temporary ArrayList then set to Array
		ArrayList<T> list = new ArrayList<T>();
		Component comp = null;
		while(iter.hasNext()) {
			comp = iter.next();
			if(clazz.isAssignableFrom(comp.getClass())) list.add((T) comp);
		}
		return list.toArray((T[]) Array.newInstance(clazz, 0));
	}
	
	private static class ComposerEventListener implements EventListener {
		
		private final Method _ctrlMethod;
		private final Object _ctrl;
		
		public ComposerEventListener(Method method, Object controller){
			_ctrlMethod = method;
			_ctrl = controller;
		}
		
		public void onEvent(Event event) throws Exception {
			if(_ctrlMethod.getParameterTypes().length == 0)
				_ctrlMethod.invoke(_ctrl);
			else
				_ctrlMethod.invoke(_ctrl, event);
		}
	}
	
	
	
	// helper: functor //
	private interface PsdoCompFunctor {
		public Iterator<Component> iterator(String selector);
		public Object getImplicit(String name);
		public Object getZScriptVariable(String name);
		public Object getAttributeOrFellow(String name);
		public Object getXelVariable(String name);
		public Component getFellowIfAny(String name);
	}
	
	private static class PageFunctor implements PsdoCompFunctor {
		private final Page _page;
		private PageFunctor(Page page){ _page = page; }
		public Iterator<Component> iterator(String selector) {
			return Selectors.iterator(_page, selector);
		}
		public Object getImplicit(String name) {
			return Components.getImplicit(_page, name);
		}
		public Object getZScriptVariable(String name) {
			return _page.getZScriptVariable(name);
		}
		public Object getXelVariable(String name) {
			return _page.getXelVariable(null, null, name, true);
		}
		public Object getAttributeOrFellow(String name) {
			return _page.getAttributeOrFellow(name, true);
		}
		public Component getFellowIfAny(String name) {
			return _page.getFellowIfAny(name);
		}
	}
	
	private static class ComponentFunctor implements PsdoCompFunctor {
		private final Component _comp;
		private ComponentFunctor(Component comp){ _comp = comp; }
		public Iterator<Component> iterator(String selector) {
			IdSpace spaceOwner = _comp.getSpaceOwner();
			return spaceOwner instanceof Component ?
					Selectors.iterator((Component) spaceOwner, selector) :
					Selectors.iterator((Page) spaceOwner, selector);
		}
		public Object getImplicit(String name) {
			return Components.getImplicit(_comp, name);
		}
		public Object getZScriptVariable(String name) {
			return getPage().getZScriptVariable(name);
		}
		public Object getXelVariable(String name) {
			return getPage().getXelVariable(null, null, name, true);
		}
		public Object getAttributeOrFellow(String name) {
			return _comp.getAttributeOrFellow(name, true);
		}
		private Page getPage() {
			Page page = _comp.getPage();
			if (page != null) return page;
			final Execution exec = Executions.getCurrent();
			return exec != null ? ((ExecutionCtrl)exec).getCurrentPage(): null;
		}
		public Component getFellowIfAny(String name) {
			return _comp.getFellowIfAny(name);
		}
	}
	
	private static <T> T getIthItem(Iterator<T> iter, int index){
		// shift (index - 1) times
		for(int i = 1; i < index; i++) {
			if(!iter.hasNext())
				return null;
			iter.next();
		}
		return iter.hasNext() ? iter.next() : null;
	}
	
}
