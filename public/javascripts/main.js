require.config({
    shim: {
        'webjars!bootswatch.js': {deps: ['webjars!jquery.js', 'webjars!bootstrap.js']},
        'knockout-amd-helpers': {deps: ['knockout']},
        'davis': {deps: ['webjars!jquery.js']}
        
    },
    paths: {
        'knockout-amd-helpers': '/assets/javascripts/knockout-amd-helpers.min',
        'text': '/assets/javascripts/text',
        'davis': '/assets/javascripts/davis.min'
    }
});

define('knockout', ['webjars!knockout.js'], function(ko) {
	return ko;
});

require(['knockout', 'modules/app', 'knockout-amd-helpers', 'text', 'webjars!jquery.js', 'webjars!bootswatch.js', 'davis'], function(ko, app) {
	var theApp = new app();
	
	var routes = Davis(function () {
		this.configure(function(settings) {
	        settings.generateRequestOnPageLoad = true;
	    });
		this.get('/', function (req) {
			req.redirect('/#!/instances');
			//theApp.doNavigate('instances');
	    });
        this.get('/#!/:module', function (req) {
        	theApp.doNavigate(req.params['module']);
        });
      });

	//routes.start();
	
	$(function() {
		ko.bindingHandlers.module.baseDir = 'modules';
		ko.applyBindings(theApp);
    });
});