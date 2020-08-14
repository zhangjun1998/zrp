/**
 * @Description
 * @Author zhangjun
 * @Data 2020/7/24
 * @Time 17:04
 * */

window.onload=function () {

    var usernameGlobal = null;

    //检查登录状态
    function checkLogin() {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", "/api/admin/isLogin", false);
        xhr.send();
        if (xhr.readyState === 4 && xhr.status === 200) {
            var placeHolder = document.getElementById("placeHolder");
            var user = xhr.responseText;
            if (user !== "null") {  //已经登录
                usernameGlobal = user;
                document.getElementById("adminName").innerText = ("欢迎您，"+usernameGlobal);
                //绑定修改密码事件
                var changeBtn = document.getElementById("changeBtn");
                changeBtn.addEventListener("click", changePassword);
            } else {  //没有登录
                //不允许修改密码
                placeHolder.style.color = "red";
                placeHolder.innerText = "请先登录";
            }
        }
    }
    checkLogin();

    //修改密码
    function changePassword() {
        var oldPassword = document.getElementById("oldPassword").value;
        var newPassword = document.getElementById("newPassword").value;
        var repeatPassword = document.getElementById("repeatPassword").value;
        var placeHolder = document.getElementById("placeHolder");
        if (oldPassword == null || newPassword == null || repeatPassword == null ||
            oldPassword.length <= 0 || newPassword.length <= 0 || repeatPassword.length <= 0) {
            placeHolder.style.color = "red";
            placeHolder.innerText = "请完善信息后再修改";
        } else {
            if (newPassword !== repeatPassword) {
                placeHolder.style.color = "red";
                placeHolder.innerText = "两次密码不一致";
            } else {
                var xhr = new XMLHttpRequest();
                xhr.open("GET", "/api/admin/changePass?username=" + usernameGlobal + "&oldPass=" + oldPassword + "&newPass=" + newPassword, true);
                xhr.send();
                placeHolder.style.color = "#d0df3c";
                placeHolder.innerText = "正在修改，请等待...";
                xhr.onreadystatechange = function (ev) {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        var res = JSON.parse(xhr.responseText);
                        if (res) {
                            // 修改成功
                            placeHolder.innerText = "修改成功";
                            placeHolder.style.color = "#1ab120";
                        } else {
                            placeHolder.style.color = "red";
                            placeHolder.innerText = "原密码错误或与新的密码重复";
                        }
                    }
                };
            }
        }
    }

}