function (out) {
	var item,
		header,
		ref = {
			child: this.firstChild
		};	

	out.push('<table ',this.domAttrs_(),'>');
	
	if (header = this._headers ) 
		this.drawContent(out, header, ref);
	
	
	if (item = this._items) {
		this.drawContent(out, item, ref);
	}
	out.push('</table>');

}