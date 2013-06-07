$(function() {
	$.getJSON('/api/instances', function(data) {
		var items = [];
		 
		$.each(data, function(index, item) {
			items.push('<li>' + item.name + '</li>');
		});
	 
		$('<ul/>', {
			'data-role': 'listview',
			html: items.join('')
		}).appendTo('#content').listview();
	});
});