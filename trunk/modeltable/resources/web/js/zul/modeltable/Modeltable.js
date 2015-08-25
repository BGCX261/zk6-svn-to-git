zul.modeltable.Modeltable = zk.$extends(zk.Widget, {
	$define: {
		items: function() {
			this.rerender();
		},
		headers: function() {
			this.rerender();
		}
	},
	drawContent: function(out, cnt, ref) {
		
		for (var i = 0, len = cnt.length; i < len; ++i) {
			if (cnt[i] == 'z$wgt') {
				if (ref.child) {
					ref.child.redraw(out);
					ref.child = ref.child.nextSibling;
				}
			} else {
				out.push(cnt[i]);
			}
		}
	},
	getZclass: function() {
		return this._zclass != null ? this._zclass : "z-modeltable";
	}
});
