define(['webjars!knockout.js'], function(ko) {
    function User(item) {
    	var self = this;
    	self.firstName = item.firstName;
    	self.lastName = item.lastName;
    	self.email = item.email;
    }
	
	return function() {
		var self = this;
		self.users = ko.observableArray([]);
		self.newEmail = ko.observable();
		
		self.submitInvite = function() {
			$('#inviteAlertSuccess').hide();
			$('#inviteAlertError').hide();
			if(self.newEmail() == null || self.newEmail() == '') {
				$('#inviteAlertError').show();
			} else {
				$.post('/signup', $('#inviteForm').serialize()).done(function(data) {
	        		$('#inviteAlertSuccess').show();
	        		self.newEmail(null);
	        	}).fail(function(jqXHR, textStatus, errorThrown) {
	        		$('#inviteAlertError').show();
	        		//var result = JSON.parse(jqXHR.responseText);
	        		//$.map(result, function(item, key) {
	        		//	$('#' + capitaliseFirstLetter(key)).next().text(item);
					//});
	        		console.log(jqXHR.responseText);
	        	});
			}
		}
		
		getUsers();
		
		function getUsers() {
			$.getJSON('/api/users', function(data) {
				var mappedUsers = $.map(data, function(item) { 
					return new User(item); 
				});
				self.users(mappedUsers);
			});
		}
    };
});