$(function(){
    $.getJSON("index/allcatalog.json",function (data) {
        console.log("data: ", data)
        var ul = $(".header_main_left").children().filter("ul")
        data.forEach(function (catalog1, index1, arr1) {
            console.log("catalog1:", catalog1);
            console.log("index1:", index1);
            console.log("arr1:", arr1);
            var li = $("<li class=\"header_li2\"></li>");
            var cata1link = $("<a href=\"http://search.tmesh.cn/list.html?catalog3Id=" + catalog1.catId + "\" class='header_main_left_a'>" + '<b>' + catalog1.name +'</b>' + " </a>");
            li.append(cata1link);
            var div2 = $("<div class=\"header_main_left_main\"></div>d>")
            if (catalog1.cj) {
                var div_cj = $("<div class=\"header_sj\"></div>")
                catalog1.cj.forEach(function (catalog1_cj, cj_index, cj_arr) {
                    var cata1cjlink = $("<a href=\"http://search.tmesh.cn/list.html?catalog3Id=" + catalog1_cj.catId + "\" class='header_sj_a'>" + '<b>' + catalog1_cj.name +'</b>' + " </a>");
                    div_cj.append(cata1cjlink);
                });
                div2.append(div_cj);
            }
            if (catalog1.children) {
                var catalog2_ol = $("<ol class=\"header_ol\"></ol>")
                catalog1.children.forEach(function (catalog2, index2, arr2) {
                    console.log("catalog2:", catalog2);
                    console.log("index2:", index2);
                    console.log("arr2:", arr2);
                    var cata2link = $("<a href=\"http://search.tmesh.cn/list.html?catalog3Id=" + catalog2.catId + "\" style=\"color: #111;\" class='aaa'>" +  catalog2.name + " </a>");
                    catalog2_ol.append(cata2link);
                    if (catalog2.children) {
                        var catalog3_li = $("<li></li>");
                        var len=0;
                        catalog2.children.forEach(function (catalog3, index3, arr3) {
                            console.log("catalog3:", catalog3);
                            console.log("index3:", index3);
                            console.log("arr3:", arr3);
                            var cata3link = $("<a href=\"http://search.tmesh.cn/list.html?catalog3Id=" + catalog3.catId + "\" style=\"color: #999;\" >" + catalog3.name + " </a>");
                            catalog3_li.append(cata3link);
                            len=len+1+ctg3.name.length;
                        });
                        if(len>=46&&len<92){
                            catalog3_li.attr("style","height: 60px;");
                        }else if(len>=92){
                            catalog3_li.attr("style","height: 90px;");
                        }
                        catalog2_ol.append(catalog3_li);
                    }
                });
                div2.append(catalog2_ol);
            }
            li.append(div2);
            ul.append(li);
        });
        div.append(ul);
    });
});