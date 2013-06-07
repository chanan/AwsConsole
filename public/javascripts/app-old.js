$(function() {
	
	
	$("form input:checkbox").click(function() {
		$(this).parent().parent().submit();
	});
	
	$('#instanceTable').on('click', 'button[data-action="start"]', function(event) {
		var instanceId = $(this).attr('data-instanceId');
		$.post("/api/instances/" + instanceId + "/start");
	});
	
	$('#instanceTable').on('click', 'button[data-action="stop"]', function(event) {
		var instanceId = $(this).attr('data-instanceId');
		$.post("/api/instances/" + instanceId + "/stop");
	});
	
	$.getJSON('/api/instances', function(data) {
		var items = [];
		 
		$.each(data, function(index, item) {
			var row = "<tr>";
			var stateForm = "";
			row = row + "<td>" + item.instanceId + "</td>";
			row = row + "<td>" + item.name + "</td>";
			row = row + "<td>" + item.type + "</td>";
			row = row + "<td>" + item.zone + "</td>";
			row = row + "<td>" + item.key + "</td>";
			row = row + "<td>" + item.securityGroup + "</td>";
			row = row + "<td>" + item.lastLaunchTime + "</td>";
			row = row + "<td>" + item.dns + "</td>";
			if (item.state == "stopped") {
				stateForm += '<button class="btn btn-primary btn-mini" title="Start Instance" data-instanceId="' + item.instanceId + '" data-action="start">';
				stateForm += '<i class="icon-play"></i>';
				stateForm += '</button>';
			}
			
			if (item.state == "running") {
				stateForm += '<button class="btn btn-primary btn-mini" title="Stop Instance" data-instanceId="' + item.instanceId + '" data-action="stop">';
				stateForm += '<i class="icon-stop"></i>';
				stateForm += '</button>';
			}
			
			if (item.state == "pending") {
				stateForm = '<i class="icon-spinner icon-spin icon-large"></i>';
			}
			
			if (item.state == "stopping") {
				stateForm = '<i class="icon-spinner icon-spin icon-large"></i>';
			}
			row = row + '<td id="state-' + item.instanceId + '">' + item.state + stateForm + "</td>";
			if(item.disallowPowerSave) {
				row = row + "<td>disabled</td>";
			} else {
				var powerSaveForm = "<form>";
				powerSaveForm += '<input type="hidden" name="instanceId" value="' + item.instanceId + '" />';
				powerSaveForm += '<label class="checkbox">';
				powerSaveForm += '<input type="checkbox" name="powerSave"';
				if(item.powerSaveMode) {
					powerSaveForm += ' checked="checked"';	
				}
				powerSaveForm += '> Power save';
				powerSaveForm += '</label>';
				powerSaveForm += "</form>";
				row = row + "<td>" + powerSaveForm + "</td>";
			}
			row = row + "</tr>";
			items.push(row);
		});
	 
		$('<tbody/>', {
			html: items.join('')
		}).appendTo('#instanceTable');
	});
});