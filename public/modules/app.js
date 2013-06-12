define(['webjars!knockout.js', 'webjars!jquery.js'], function(ko) {
    return function() {
		var self = this;
		self.currentModule = ko.observable('instances');
		
		var request;
		
		function showLogin() {
			$('#loginModal').modal('show');
		}
		
		function camelize(str) {
			return str.replace(/(?:^\w|[A-Z]|\b\w|\s+)/g, function(match, index) {
				if (+match === 0) return ''; // or if (/\s+/.test(match)) for white spaces
			   	return index == 0 ? match.toLowerCase() : match.toUpperCase();
			});
		}
		
		self.doLogin = function() {
			$.post('/authenticate/userpass', $('#loginForm').serialize(), function(data, textStatus, jqXHR) {
				$.ajax(request);
				$('#loginModal').modal('hide');
			});
		};
		
		self.doNavigate = function(module) {
			self.currentModule(camelize(module));
		}
		
		$.ajaxSetup({
			cache: false,
			error: function (jqXHR, textStatus, errorThrown) {
				if(jqXHR.status == 401) {
					request = this;
					showLogin();
				}
			} 
		});
    };
});