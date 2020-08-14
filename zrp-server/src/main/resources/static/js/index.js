/**
 * @Description
 * @Author zhangjun
 * @Data 2020/7/24
 * @Time 17:06
 * */

window.onload=function () {


    //全局用户名
    var usernameGlobal = null;

    //取消其它导航栏按钮的active属性
    function cancelActive(current) {
        var lis = document.getElementsByClassName("changePage");
        for (var i = 0; i < lis.length; i++) {
            lis[i].classList.remove("active");
        }
        current.classList.add("active");
    }

    // 根据点击导航栏元素的id切换iframe页面
    function changePage(e) {
        var iframe = document.getElementById("pageChoice");
        var current = e.currentTarget;
        cancelActive(current);
        iframe.src = current.id;
    }

    //绑定点击导航栏切换iframe页面事件
    function bindChangePage() {
        var changePages = document.getElementsByClassName("changePage");
        for (var i = 0; i < changePages.length; i++) {
            changePages[i].addEventListener("click", changePage);
        }
    }

    //检查登录状态
    function checkLogin() {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", "/api/admin/isLogin", false);
        xhr.send();
        if (xhr.readyState === 4 && xhr.status === 200) {
            var user = xhr.responseText;
            if (user !== "null") {  //已经登录
                usernameGlobal = user;
                //展示管理模块
                document.getElementById("pageChoice").src = "admin";
                //允许点击切换模块
                bindChangePage();
            } else {  //没有登录
                //展示登录模块
                document.getElementById("pageChoice").src = "login";
            }
        }
    }

    checkLogin();

    //格式化日期，是否补0
    function formatNumber(value){
        return (value < 10 ? '0' : '') + value;
    }

    //获取指定count天后的日期yyyy-MM-dd HH:mm:ss
    function getDay(count) {
        var curr_date = new Date();
        console.log("当前时间："+curr_date);
        curr_date.setDate(curr_date.getDate() + count);
        var strYear = curr_date.getFullYear();
        var strMonth = curr_date.getMonth() + 1;
        var strDay = curr_date.getDate();
        var strHours = curr_date.getHours();
        var strMinutes = curr_date.getMinutes();
        var strSeconds = curr_date.getSeconds();

        var datastr = strYear + '-' + formatNumber(strMonth) + '-'
            + formatNumber(strDay) + ' '
            + formatNumber(strHours) + ':' + formatNumber(strMinutes) + ':'
            + formatNumber(strSeconds) +' GMT+0800';
        console.log(datastr);
        return datastr;
    }
    getDay(30);

}