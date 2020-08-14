/**
 * @Description
 * @Author zhangjun
 * @Data 2020/7/24
 * @Time 17:08
 * */

window.onload=function () {

    //登录
    function login() {
        var username = document.getElementById("username").value;
        var password = document.getElementById("password").value;
        var placeholder = document.getElementById("placeholder");
        if (username == null || password == null || username.length <= 0 || password.length <= 0) {
            placeholder.innerText = "用户名或密码不能为空";
        }else {
            var xhr = new XMLHttpRequest();
            xhr.open("GET","/api/admin/login?username="+username+"&password="+password,true);
            xhr.send();
            xhr.onreadystatechange=function (ev) {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    var res = JSON.parse(xhr.responseText);
                    if (res) {
                        // 登录成功，刷新父页面
                        parent.window.location.href = "index";
                    }else {
                        placeholder.innerText = "用户名或密码错误";
                    }
                }
            };
        }
    }

    //绑定登录事件
    function bindLogin() {
        var loginBtn = document.getElementById("loginBtn");
        loginBtn.addEventListener("click",login);
    }
    bindLogin();

}