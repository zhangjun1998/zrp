/**
 * @Description
 * @Author zhangjun
 * @Data 2020/7/24
 * @Time 17:05
 * */

window.onload=function () {

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

    function getClients() {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", "/api/client/get", true);
        xhr.send();
        xhr.onreadystatechange = function (ev) {
            if (xhr.readyState === 4 && xhr.status === 200) {
                var clients = JSON.parse(xhr.response);
                //创建节点，填充数据
                var container = document.body;
                var addBtn = document.createElement("button");
                addBtn.innerText = "添加授权";
                addBtn.classList.add("btn", "btn-success");
                addBtn.addEventListener("click", showClientModal);
                container.appendChild(addBtn);
                var table = document.createElement("table");
                table.classList.add("table", "table-hover");
                var tbody = document.createElement("tbody");
                var titleTr = document.createElement("tr");
                var idTh = document.createElement("th");
                var nameTh = document.createElement("th");
                var clientKeyTh = document.createElement("th");
                var flowTh = document.createElement("th");
                var statusTh = document.createElement("th");
                var startTimeTh = document.createElement("th");
                var stopTimeTh = document.createElement("th");
                var commentTh = document.createElement("th");
                var deleteTh = document.createElement("th");
                idTh.innerText = "id";
                nameTh.innerText = "客户名";
                clientKeyTh.innerText = "授权码";
                flowTh.innerText = "累计流量";
                statusTh.innerText = "授权状态";
                startTimeTh.innerText = "授权时间";
                stopTimeTh.innerText = "到期时间";
                commentTh.innerText = "备注";
                deleteTh.innerText = "删除";
                titleTr.appendChild(idTh);
                titleTr.appendChild(nameTh);
                titleTr.appendChild(clientKeyTh);
                titleTr.appendChild(flowTh);
                titleTr.appendChild(statusTh);
                titleTr.appendChild(startTimeTh);
                titleTr.appendChild(stopTimeTh);
                titleTr.appendChild(commentTh);
                titleTr.appendChild(deleteTh);
                tbody.appendChild(titleTr);
                for (var i = 0; i < clients.length; i++) {
                    var clientTr = document.createElement("tr");
                    var stopTime = new Date(clients[i].stopTime);
                    var now = new Date();
                    //判断授权到期时间，改变单元格颜色
                    if (stopTime.getTime() > now.getTime()) {
                        clientTr.classList.add("success");
                    } else {
                        clientTr.classList.add("danger");
                    }
                    var idTd = document.createElement("td");
                    var nameTd = document.createElement("td");
                    var clientKeyTd = document.createElement("td");
                    var flowTd = document.createElement("td");
                    var statusTd = document.createElement("td");
                    var startTimeTd = document.createElement("td");
                    var stopTimeTd = document.createElement("td");
                    var commentTd = document.createElement("td");
                    var deleteTd = document.createElement("td");
                    var button = document.createElement("button");
                    idTd.innerText = clients[i].id;
                    nameTd.innerText = clients[i].name;
                    clientKeyTd.innerText = clients[i].clientKey;
                    flowTd.innerText = (clients[i].flow/1024).toFixed(2) + " M";
                    statusTd.innerText = clients[i].status;
                    startTimeTd.innerText = clients[i].startTime;
                    stopTimeTd.innerText = clients[i].stopTime;
                    commentTd.innerText = clients[i].comment;
                    button.innerText = "删除";
                    button.classList.add("btn", "btn-danger", "btn-xs");
                    button.setAttribute("clientid", clients[i].id);
                    button.addEventListener("click", deleteClient);
                    deleteTd.appendChild(button);
                    clientTr.appendChild(idTd);
                    clientTr.appendChild(nameTd);
                    clientTr.appendChild(clientKeyTd);
                    clientTr.appendChild(flowTd);
                    clientTr.appendChild(statusTd);
                    clientTr.appendChild(startTimeTd);
                    clientTr.appendChild(stopTimeTd);
                    clientTr.appendChild(commentTd);
                    clientTr.appendChild(deleteTd);
                    tbody.appendChild(clientTr);
                }
                table.appendChild(tbody);
                container.appendChild(table);
            }
        }
    }

    getClients();


    //展示添加客户弹框
    function showClientModal() {
        var clientKeyModal = document.getElementById("clientKeyModal");
        clientKeyModal.classList.add("show");
    }

    //隐藏添加客户弹框
    function hiddenClientModal() {
        var clientKeyModal = document.getElementById("clientKeyModal");
        clientKeyModal.classList.remove("show");
    }

    //绑定隐藏添加客户弹框
    function bindHiddenClientModal() {
        var cancel = document.getElementById("cancel");
        cancel.addEventListener("click", hiddenClientModal);
    }

    bindHiddenClientModal();

    //删除客户
    function deleteClient(e) {
        var clientId = e.currentTarget.getAttribute("clientid");
        var xhr = new XMLHttpRequest();
        xhr.open("GET", "/api/client/delete?clientId=" + clientId, true);
        xhr.send();
        xhr.onreadystatechange = function (ev) {
            if (xhr.readyState === 4 && xhr.status === 200) {
                var res = JSON.parse(xhr.response);
                if (res) {
                    //删除成功，刷新页面
                    window.location.reload();
                } else {
                    alert("删除失败");
                }
            }
        }
    }

    //添加授权
    function addClient() {
        var username = document.getElementById("username").value;
        var clientKey = document.getElementById("clientKey").value;
        var stopTime = document.getElementById("stopTime").value.trim();
        var comment = document.getElementById("comment").value;

        if (username == null || clientKey == null || stopTime == null ||
            username.length <= 0 || clientKey.length <= 0 || stopTime.length <= 0) {
            alert("请完善信息后再添加");
        } else {
            //判断有效期是否合法
            if(isNaN(stopTime)){
                alert("请输入合法有效期");
                return;
            }
            if (comment == null || comment.length <= 0) {
                comment = "无";
            }
            var client = {
                "name": username,
                "clientKey": clientKey,
                "stopTime": getDay(parseInt(stopTime)),
                "comment": comment
            };
            var clientJson = JSON.stringify(client);
            var xhr = new XMLHttpRequest();
            xhr.open("POST", "/api/client/add", true);
            xhr.setRequestHeader("Content-type", "application/json;charset=UTF-8");
            xhr.send(clientJson);
            xhr.onreadystatechange = function (ev) {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    var res = JSON.parse(xhr.response);
                    if (res) {
                        //添加授权成功，刷新页面
                        window.location.reload();
                    } else {
                        hiddenClientModal();
                        alert("添加授权失败");
                    }
                }
            }

        }

    }

    // 绑定添加客户事件
    function bindAddClient() {
        var submit = document.getElementById("submit");
        submit.addEventListener("click", addClient);
    }

    bindAddClient();

}