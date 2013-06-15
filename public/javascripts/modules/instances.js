define(['webjars!knockout.js', 'webjars!jquery.js', 'webjars!bootswatch.js'], function(ko) {
	function Instance(item) {
		var self = this;
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
		
		self.start = function() {
			$.post('/api/instances/' + self.instanceId + '/start');
			self.state('pending');
			self.timeout = window.setInterval(function() {
				$.getJSON('/api/instances/' + self.instanceId, function(item) {
					self.state(item.state);
					self.dns(item.dns);
					self.lastLaunchTime(item.lastLaunchTime);
					if(item.state == 'running') window.clearInterval(self.timeout);
				});
			}, 1000);
		};
		
		self.stop = function() {
			$.post('/api/instances/' + self.instanceId +'/stop');
			self.state('stopping');
			self.timeout = window.setInterval(function() {
				$.getJSON('/api/instances/' + self.instanceId, function(item) {
					self.state(item.state);
					self.dns(item.dns);
					if(item.state == 'stopped') window.clearInterval(self.timeout);
				});
			}, 1000);
		};
		
		self.checkPowerSave = function() {
			$.post('/api/instances/' + self.instanceId + '/changePowerSave');
			return true;
		};
		
		//ko.track(this);
	}
	
	return function() {
		var self = this;
		self.instances = ko.observableArray([]);
		
		//ko.track(this);
		
		getInstances();
		
		function getInstances() {
			$.getJSON('/api/instances', function(data) {
				var mappedInstances = $.map(data, function(item) { 
					return new Instance(item); 
				});
				self.instances(mappedInstances);
			});
		}
	}
});