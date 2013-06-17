define(['webjars!knockout.js', 'listItem'], function(ko, listItem) {
    return function() {
        var self = this;
        
        self.images = ko.observableArray([]);
        self.keys = ko.observableArray([]);
        self.securityGroups = ko.observableArray([]);
        self.types = ko.observableArray([]);
        self.newInstanceName = ko.observable();
        self.powerSaveMode = ko.observable(true);
        
        self.key = ko.observable('DevOps');
        self.type = ko.observable('t1.micro');
        self.group = ko.observable('HTTP-Default');
        self.newInstanceName = ko.observable();
        
        getListItems('/api/types', function(mappedItems) {
        	self.types(mappedItems);
        });
        
        getListItems('/api/images', function(mappedItems) {
        	self.images(mappedItems);
        });
        
        getListItems('/api/keys', function(mappedItems) {
        	self.keys(mappedItems);
        });
        
        getListItems('/api/securityGroups', function(mappedItems) {
        	self.securityGroups(mappedItems);
        });
       
        function getListItems(url, callback) {
        	$.getJSON(url, function(data) {
				var mappedItems = $.map(data, function(item, key) {
					return new listItem(key, item);
				});
				callback(mappedItems);
			});
        }
        
        self.doCreateInstance = function() {
        	$('#instanceAlertSuccess').hide();
        	$('#instanceAlertError').hide();
        	$.ajax({
        		url: '/api/instances/new',
        		type: 'PUT',
        		data: $('#createInstanceForm').serialize()
        	}).done(function(data) {
        		$('#instanceAlertSuccess').show();
        	}).fail(function(jqXHR, textStatus, errorThrown) {
        		$('#instanceAlertError').show();
        		var result = JSON.parse(jqXHR.responseText);
        		$.map(result, function(item, key) {
        			$('#' + capitaliseFirstLetter(key)).next().text(item);
				});
        	});
        };
        
        function capitaliseFirstLetter(string)
        {
            return string.charAt(0).toUpperCase() + string.slice(1);
        }
    }
});