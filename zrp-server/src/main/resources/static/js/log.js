/**
 * @Description
 * @Author zhangjun
 * @Data 2020/7/24
 * @Time 17:07
 * */

window.onload=function () {

    function getLogs() {
        var xhr = new XMLHttpRequest();
        xhr.open("GET", "/api/log/get", true);
        xhr.send();
        xhr.onreadystatechange = function (ev) {
            if (xhr.readyState === 4 && xhr.status === 200) {
                var logs = JSON.parse(xhr.response);
                //创建节点，填充数据
                var container = document.body;
                var table = document.createElement("table");
                table.classList.add("table", "table-hover");
                var tbody = document.createElement("tbody");
                var titleTr = document.createElement("tr");
                var nameTh = document.createElement("th");
                var clientKeyTh = document.createElement("th");
                var portTh = document.createElement("th");
                var flowTh = document.createElement("th");
                var dateTh = document.createElement("th");
                nameTh.innerText = "用户";
                clientKeyTh.innerText = "授权码";
                portTh.innerText = "流量端口";
                flowTh.innerText = "当日流量总计/M";
                dateTh.innerText = "日期";
                titleTr.appendChild(nameTh);
                titleTr.appendChild(clientKeyTh);
                titleTr.appendChild(portTh);
                titleTr.appendChild(flowTh);
                titleTr.appendChild(dateTh);
                tbody.appendChild(titleTr);
                for (var i = 0; i < logs.length; i++) {
                    var logTr = document.createElement("tr");
                    var nameTd = document.createElement("td");
                    var clientKeyTd = document.createElement("td");
                    var portTd = document.createElement("td");
                    var flowTd = document.createElement("td");
                    var dateTd = document.createElement("td");
                    nameTd.innerText = logs[i].clientName;
                    clientKeyTd.innerText = logs[i].clientKey;
                    portTd.innerText = logs[i].port;
                    flowTd.innerText = (logs[i].flow / 1024).toFixed(2);
                    dateTd.innerText = logs[i].date;
                    logTr.appendChild(nameTd);
                    logTr.appendChild(clientKeyTd);
                    logTr.appendChild(portTd);
                    logTr.appendChild(flowTd);
                    logTr.appendChild(dateTd);
                    tbody.appendChild(logTr);
                }
                table.appendChild(tbody);
                container.appendChild(table);
            }
        }
    }

    getLogs();

}