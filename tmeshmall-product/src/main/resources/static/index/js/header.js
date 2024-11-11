$(".header_banner1").hover(function() {
	$(".header_banner1_div").stop(true).animate({
		width:"990px"
	},500)
}, function() {
	$(".header_banner1_div").stop(true).animate({
		width:"0"
	},300)
})
$(".head p").on("click", function() {
	$(".head").fadeOut(500)
})
$(".header_banner1_div p").on("click", function() {
	$(".header_banner1_div").stop(true).animate({
		width:"0"
	},200)
})
$(".header_ol a").hover(function() {
	$(this).css({
		color: "#c81623"
	})
}, function() {
	$(this).css({
		color: "#999"
	})
	$(".aaa").css({
		color: "#111"
	})
})
//轮播图
var swiper1 = new Swiper(".swiper1", {
	loop: true,
	autoplay: 2000,
	effect: 'fade',
	fade: {
		crossFade: false,
	},
	pagination: ".swiper-pagination",
	paginationClickable: true,
	prevButton: '.swiper-button-prev',
	nextButton: '.swiper-button-next',
	autoplayDisableOnInteraction: false,
})

//货品分类
$('.header_main_left>ul>li').hover(function() {
	$(this).css({
		background: "#989898"
	}).find('.header_main_left_main').stop(true).fadeIn(300)
}, function() {
	$(this).css({
		background: "#6e6568"
	}).find(".header_main_left_a").css({
		color: "#fff"
	})
	$(this).find('.header_main_left_main').stop(true).fadeOut(100)
})
$(".header_sj a").hover(function() {
	$(this).css({
		background: "#444"
	})
}, function() {
	$(this).css({
		background: "#6e6568"
	})
})
//购物车下拉
$('.header_ico').hover(function() {
	// console.log("header_ico fnOver", $(this).find('.header_ko'))
	$(this).find('.header_ko').stop(true).fadeIn(100)
}, function() {
	// console.log("header_ico fnOut", $(this).find('.header_ko'))
	$(this).find('.header_ko').stop(true).fadeOut(100)
})
/*$('.header_gw').hover(function() {
	console.log("header_gw fnOver")
	$(this).next('.header_ko').stop(true).fadeIn(100)
}, function() {
	console.log("header_gw fnOut")
	// $(this).next('.header_ko').stop(true).fadeOut(100)
})
$('.header_ko').hover(function() {
	console.log("header_ko fnOver")
	// $(this).stop(true).fadeIn(100)
}, function() {
	console.log("header_ko fnOut")
	$(this).stop(true).fadeOut(100)
})*/

//我的TMesh下拉
$(".header_wdjd").hover(function() {
	$(this).children(".header_wdjd_txt").stop(true).show(100)
	$(this).css({
		background: "#fff"
	})
}, function() {
	$(this).css({
		background: "#E3E4E5"
	})
	$(this).children(".header_wdjd_txt").stop(true).hide(100)
})

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
$(".header_head_p").hover(function() {
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
//客户服务下拉
$(".header_wdjd1").hover(function() {
	$(this).children(".header_wdjd_txt").stop(true).show(100)
	$(this).css({
		background: "#fff"
	})
}, function() {
	$(this).css({
		background: "#E3E4E5"
	})
	$(this).children(".header_wdjd_txt").stop(true).hide(100)
})
//网站导航下拉
$(".header_wzdh").hover(function() {
	$(this).children(".header_wzdh_txt").stop(true).show(100)
	$(this).css({
		background: "#fff"
	})
}, function() {
	$(this).css({
		background: "#E3E4E5"
	})
	$(this).children(".header_wzdh_txt").stop(true).hide(100)
})
//促销公告选项卡
$(".header_new_t p").hover(function() {
	var i = $(this).index()
	$(".header_new_t p").removeClass("active").eq(i).addClass("active")
	$(".header_new_connter_1").hide().eq(i).show()
})
//话费机票
$(".ser_box_aaa_nav li").hover(function() {
	var i = $(this).index()
	$(".ser_box_aaa_nav li").removeClass("active").eq(i).addClass("active")
	$(".ser_ol_li").hide().eq(i).show()
})
$(".guanbi").on("click", function() {
	$(".ser_box_aaa .ser_box_aaa_one").stop(true).animate({
		top: "210px"
	},600)
})
$(".ser_box_item span").hover(function() {
	$(".ser_box_aaa .ser_box_aaa_one").css("display", "block")
	$(".ser_box_aaa .ser_box_aaa_one").stop(true).animate({
		top: "0"
	},600)
}, function() {

})
//右侧侧边栏
$(".header_bar_box ul li").hover(function() {
	$(this).css({
		background: "#7A6E6E",
		borderRadius: 0
	}).children(".div").css({
		display: "block"
	}).stop(true).animate({
		left: "-60px"
	}, 300)
}, function() {
	$(this).css({
		background: "#7A6E6E",
		borderRadius: 5
	}).children(".div").css({
		display: "none"
	}).stop(true).animate({
		left: "0"
	}, 300)
})
