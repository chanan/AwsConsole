define(['webjars!knockout.js', 'webjars!jquery.js', 'webjars!bootswatch.js', '/routes.js'], function(ko) {
	function Instance(item) {
		var self = this;
		self.apiController = routes.controllers.Api;
		self.instanceId = item.instanceId;
		self.name = item.name;
		self.type = item.type;
		self.zone = item.zone;
		self.key = item.key;
		self.securityGroup = item.securityGroup;
		self.lastLaunchTime = ko.observable(item.lastLaunchTime);
		self.dns = ko.observable(item.dns);
		self.state = ko.observable(item.state);
		self.powerSaveMode = ko.observable(item.powerSaveMode);
		self.disallowPowerSave = ko.observable(item.disallowPowerSave);
		self.timeout = '';
		
		self.start = function() {;
			self.apiController.startInstance(self.instanceId).ajax();
			self.state('pending');
			self.timeout = window.setInterval(function() {
				self.apiController.instance(self.instanceId).ajax().done(function(item) {
					self.state(item.state);
					self.dns(item.dns);
					self.lastLaunchTime(item.lastLaunchTime);
					if(item.state == 'running') window.clearInterval(self.timeout);
				});
			}, 1000);
		};
		
		self.stop = function() {
			self.apiController.stopInstance(self.instanceId).ajax();
			self.state('stopping');
			self.timeout = window.setInterval(function() {
				self.apiController.instance(self.instanceId).ajax().done(function(item) {
					self.state(item.state);
					self.dns(item.dns);
					if(item.state == 'stopped') window.clearInterval(self.timeout);
				});
			}, 1000);
		};
		
		self.checkPowerSave = function() {
			self.apiController.changePowerSave(self.instanceId).ajax();
			return true;
		};
	}
	
	return function() {
		var self = this;
		self.apiController = routes.controllers.Api;
		self.instances = ko.observableArray([]);
		
		getInstances();
		
		function getInstances() {
			self.apiController.instances().ajax().done(function(data){
				var mappedInstances = $.map(data, function(item) { 
					return new Instance(item); 
				});
				self.instances(mappedInstances);
			});
		}
	}
});