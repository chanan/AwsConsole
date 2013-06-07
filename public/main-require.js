require.config({
    paths: {
        "knockout-amd-helpers": "/assets/javascripts/knockout-amd-helpers.min",
        "text": "/assets/javascripts/text"
    }
});

define("knockout", ["webjars!knockout.js"], function(ko) {return ko});

require(["webjars!knockout.js", "modules/app", "knockout-amd-helpers", "text"], function(ko, App) {
    console.log('in main');
    ko.bindingHandlers.module.baseDir = "modules";
    ko.applyBindings(new App());
});
