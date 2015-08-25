package org.zkoss.modeltable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.lang.Classes;
import org.zkoss.lang.Objects;
import org.zkoss.modeltable.test.MyModeltableRenderer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;

public class Modeltable extends HtmlBasedComponent {

	private static final String ATTR_ON_INIT_RENDER_POSTED = "org.zkoss.zul.onInitLaterPosted";

	private static final String COMPONENT_FLAG = "z$wgt";

	private MyModeltableRenderer _render;

	private ListModel _model;

	private RowRendererImpl renderhead = new RowRendererImpl(this);

	private RowRendererImpl rendercontent = new RowRendererImpl(this);

	private transient ListDataNotifyListener _dataListener;

	public MyModeltableRenderer getRender() {
		return _render;
	}

	public void setRenderer(MyModeltableRenderer render) {
		this._render = render;
	}

	public void setModel(ListModel model) {
		if (model != null) {
			if (_model != model) {
				// ListDataNotifyListener
				if (_model != null) {
					_model.removeListDataListener(_dataListener);
				}
				_model = model;
				_dataListener = new ListDataNotifyListener(this);
				_model.addListDataListener(_dataListener);
				_dataListener.notifyTarget();
			}
		} else if (_model != null) {
			_model.removeListDataListener(_dataListener);
			_model = null;
			getChildren().clear();
			invalidate();
		}
	}

	public void onInitRender(Event e) {
		removeAttribute(ATTR_ON_INIT_RENDER_POSTED);
		getChildren().clear();
		renderhead.clear();
		rendercontent.clear();
		evaluteRenderHeader(this, getRender(), renderhead);
		evaluteRenderItem(this, getRender(), _model, rendercontent);
		invalidate();
	}

	// super//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "headers", renderhead.getContents());
		render(renderer, "items", rendercontent.getContents());

	}

	public Object getRenderer() {
		return _render == null ? _defRend : _render;
	}

	public void setRenderer(String clsnm) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			InstantiationException, InvocationTargetException {
		if (clsnm != null)
			setRenderer((MyModeltableRenderer) Classes.newInstanceByThread(clsnm));
	}

	private static final MyModeltableRenderer _defRend = new MyModeltableRenderer() {

		public void renderHeader(RowRenderer renderer, ListModel model) {

		}

		public void renderItem(RowRenderer renderer, Object model, int index) {
			renderer.appendHtml("<td>" + Objects.toString(model) + "</td>");
		}
	};

	public Object getDefaultRenderer() {
		return _defRend;
	}

	private void evaluteRenderHeader(Component target, ModeltableRenderer renderer, RowRendererImpl rw) {
		if (_model == null)
			return;

		renderer.renderHeader(rw, _model);

		List comps = rw.getComponents();
		for (int i = 0; i < comps.size(); ++i) {
			appendChild((Component) comps.get(i));
		}
	}

	private void evaluteRenderItem(Component target, ModeltableRenderer renderer, ListModel _model, RowRendererImpl rw) {
		if (_model == null) {
			return;
		}

		for (int i = 0; i < _model.getSize(); ++i) {
			renderer.renderItem(rw, _model.getElementAt(i), i);
		}
		List comps = rw.getComponents();
		for (int i = 0; i < comps.size(); ++i) {
			appendChild((Component) comps.get(i));
		}
	}

	private class ListDataNotifyListener implements ListDataListener {

		private Component _target;

		public ListDataNotifyListener(Component target) {
			_target = target;
		}

		public void notifyTarget() {
			if (_target.getAttribute(ATTR_ON_INIT_RENDER_POSTED) == null) {
				_target.setAttribute(ATTR_ON_INIT_RENDER_POSTED, Boolean.TRUE);
				Events.postEvent("onInitRender", _target, null);
			}
		}

		public void onChange(ListDataEvent event) {
			notifyTarget();
		}
	}

	private class RowRendererImpl implements RowRenderer {

		private Modeltable _modeltable;

		private List _htmls;

		private List _components;

		public RowRendererImpl(Modeltable modeltable) {
			_modeltable = modeltable;
			_htmls = new ArrayList();
			_components = new ArrayList();
		}

		public Modeltable getTable() {
			return _modeltable;
		}

		public void appendHtml(String html) {
			_htmls.add(html);
		}

		public void appendComponent(Component comp) {
			_htmls.add(COMPONENT_FLAG);
			_components.add(comp);
		}

		public void clear() {
			_htmls.clear();
			_components.clear();
		}

		public List getContents() {
			return _htmls;
		}

		public List getComponents() {
			return _components;
		}
	};
}
