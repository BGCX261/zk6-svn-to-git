package org.zkoss.modeltable.test;

import org.zkoss.modeltable.ModeltableRenderer;
import org.zkoss.modeltable.RowRenderer;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

public class MyModeltableRenderer implements ModeltableRenderer {

	int columns = 300;

	public void renderHeader(RowRenderer render, ListModel model) {

		render.appendHtml("<tr style='color:red;border:1px solid red'>");
		render.appendHtml("<th style='color:red;text-align:left;' colspan='" + columns + "'>");
		render.appendHtml("Items :" + model.getSize());
		render.appendHtml("</th>");

		render.appendHtml("</tr>");

		render.appendHtml("<tr style='color:red;border:1px solid red'>");
		for (int i = 0; i < columns; ++i) {
			render.appendHtml("<th style='color:red;'>");
			render.appendHtml("Test Header" + i);
			render.appendHtml("</th>");
		}

		render.appendHtml("</tr>");
	}

	public void renderItem(RowRenderer render, Object model, int index) {

		render.appendHtml("<tr class='row" + index + "'>");
		render.appendHtml("<td>" + model.toString() + "</td>");
		render.appendHtml("<td>Hello1</td>");
		render.appendHtml("<td>Hello2</td>");
		render.appendHtml("<td>Hello3</td>");
		render.appendHtml("<td>Hello4</td>");

		render.appendHtml("<td>");
		Textbox tb = new Textbox();
		tb.setConstraint("no empty");
		tb.addEventListener("onChange", new EventListener() {

			public void onEvent(Event event) throws Exception {
				Messagebox.show("hi");
			}
		});
		render.appendComponent(tb);
		render.appendHtml("</td>");

		for (int i = 0; i < columns - 4; ++i) {
			render.appendHtml("<td>");
			render.appendHtml("Hello" + i);
			render.appendHtml("</td>");
		}

		render.appendHtml("</tr>");
	}

}
