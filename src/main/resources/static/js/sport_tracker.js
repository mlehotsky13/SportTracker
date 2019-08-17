$(document).ready(function () {
  $("#navbarSupportedContent").css("top", $("#sideNav").outerHeight());

  $("body").click(function (e) {
    if ($(window).width() < 756) {
      var targetObj = $(e.target);

      if ($(e.target) !== $("#navbarSupportedContent") && $("#navbarSupportedContent").has(targetObj).length === 0 && $(".navbar-toggler").has(targetObj).length === 0) {
        $("#navbarSupportedContent").removeClass('show');
      }
    }
  });

  $("body").css("padding-top", $("#sideNav").outerHeight());

  $("#navbarSupportedContent").css("padding-bottom", $("#sideNav").outerHeight());

  if ($(window).width() >= 756) {
    $("#navbarSupportedContent").addClass("show");
  }
});