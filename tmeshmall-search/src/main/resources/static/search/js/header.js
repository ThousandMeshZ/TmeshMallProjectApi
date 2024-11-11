$("#area_name").ready(function(){
	// 页面渲染完成后的操作
	console.log("area_name 加载完成")
	var area_name = $.cookie('area_name');
	if (area_name && area_name != "" && area_name != {}) {
		$(".area_name").text(area_name)
		var aList = $(".header_head_p_cs").find("a");
		aList.each(function () {
			$(this).removeAttr("style")
			$(this).removeClass("select_area")
			var value = $(this).text()
			if (value == area_name) {
				$(this).addClass("select_area")
			}
		})
	} else {
		$(".area_name").text('北京')
	}
});

//地理位置下拉
$("#area").hover(function() {
	$(this).children(".header_head_p_cs").stop(true).show(100)
	$(this).css({
		background: "#fff"
	})
}, function() {
	$(this).css({
		background: "#E3E4E5"
	})
	$(this).children(".header_head_p_cs").stop(true).hide(100)
})

$(".header_head_p_cs a").click(function(){
	console.log("click: ", $(this).text())
	// $(obj).style = "background: #C81623;color: #fff;"
	var aList = $(".header_head_p_cs").find("a");
	aList.each(function () {
		$(this).removeAttr("style")
		$(this).removeClass("select_area")
	})
	// for (var i = 0; i < aList.length; i++) {
	//   $(aList[i]).removeAttr("style")
	// }
	$(this).css({
		'background': '#C81623',
		'color': '#fff',
	});
	$(this).addClass("select_area")
	$(".area_name").text($(this).text())
	$.cookie("area_name", $(this).text(), { expires: 7, path: '/' })
})
$(".header_head_p_cs a").hover(function(){
	console.log("hover: ", $(this).text())
	$(this).css({background:"#f0f0f0"})
	$(".select_area").css({background:"#c81623"})
},function(){
	console.log("fnOut: ", $(this).text())
	$(this).css({background:"#fff"})
	$(".select_area").css({background:"#c81623"})
})