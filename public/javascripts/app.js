$(function() {
	$("form input:checkbox").click(function() {
		$(this).parent().parent().submit();
	});
});