define(["webjars!knockout.js"], function(ko) {
    return function() {
        console.log('in app');
        this.articlePath = "articles";
        this.currentArticle = ko.observable("one");
        this.currentArticle.full = ko.computed(function() {
            var current = this.currentArticle();
            return current && this.articlePath + "/" + current;
        }, this);
    };
});