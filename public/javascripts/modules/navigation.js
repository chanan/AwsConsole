define(['webjars!knockout.js'], function(ko) {
	function navlink (link, name) {
		var self = this;
		self.link = link;
		self.name = name;
	}
	
    return function() {
    	var self = this;
    	
    	self.modules = ko.observableArray([]);
    	
    	var instances = new navlink('#!/instances', 'Instances');
    	var createInstance = new navlink('#!/createInstance', 'Create Instance');
    	var users = new navlink('#!/users', 'Users');
    	
    	self.modules.push(instances);
    	self.modules.push(createInstance);
    	self.modules.push(users);
    }
});