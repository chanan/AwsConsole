require.config({
    shim: {
        'webjars!bootswatch.js': {deps: ['webjars!jquery.js', 'webjars!bootstrap.js']},
        'knockout-amd-helpers': {deps: ['knockout']}
    },
    paths: {
        "knockout-amd-helpers": "/assets/javascripts/knockout-amd-helpers.min",
        "text": "/assets/javascripts/text"
    }
});

define('knockout', ['webjars!knockout.js'], function(ko) {
	return ko;
});

require(['knockout', "modules/app", 'knockout-amd-helpers', 'text', 'webjars!jquery.js'], function(ko, app) {
	$(function() {
		ko.bindingHandlers.module.baseDir = "modules";
		ko.applyBindings(new app());
    });
});