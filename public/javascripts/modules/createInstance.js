define(['webjars!knockout.js', 'listItem', '/routes.js'], function(ko, listItem) {
    return function() {
        var self = this;
        self.apiController = routes.controllers.Api;
        
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
        
        getListItems(self.apiController.types(), function(mappedItems) {
        	self.types(mappedItems);
        	self.type('t1.micro');
        });
        
        getListItems(self.apiController.images(), function(mappedItems) {
        	self.images(mappedItems);
        });
        
        getListItems(self.apiController.keys(), function(mappedItems) {
        	self.keys(mappedItems);
        	self.key('DevOps');
        });
        
        getListItems(self.apiController.securityGroups(), function(mappedItems) {
        	self.securityGroups(mappedItems);
        	self.group('HTTP-Default');
        });
       
        function getListItems(url, callback) {
        	$.ajax(url).done(function(data) {
        		var mappedItems = $.map(data, function(item, key) {
					return new listItem(key, item);
				});
				callback(mappedItems);
        	});
        }
        
        self.doCreateInstance = function() {
        	$('#instanceAlertSuccess').hide();
        	$('#instanceAlertError').hide();
        	$.ajax($.extend(self.apiController.createInstance(), {data: $('#createInstanceForm').serialize() })).done(function(data) {
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