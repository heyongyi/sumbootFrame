<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>Insert title here</title>
</head>
<body>
<script type="text/javascript" src="js/jquery.min.js"></script>
<script type="text/javascript" src="js/sockjs.js"></script>
<script type="text/javascript">
    var websocket = null;
    if ('WebSocket' in window) {
        websocket = new WebSocket('ws://localhost:9090/portal/websocket/pushChannel/create.do');
    }
    // else if ('MozWebSocket' in window) {
    //     websocket = new MozWebSocket("ws://192.168.51.75:60801/portal/websocket/pushChannel/socketServer.do");
    // }
    // else {
    //     websocket = new SockJS("http://192.168.51.75:8080/portal/websocket/pushChannel/socketServer.do");
    // }
    websocket.onopen = onOpen;
    websocket.onmessage = onMessage;
    websocket.onerror = onError;
    websocket.onclose = onClose;

    function onOpen(openEvt) {
        //alert(openEvt.Data);
    }

    function onMessage(evt) {
        alert(evt.data);
    }
    function onError() {}
    function onClose() {}

    function doSend() {
        if (websocket.readyState == websocket.OPEN) {
            var msg = $("#inputMsg").val();


            websocket.send("{\"KOAL_CERT_CN\":\""+msg+"\"}");//调用后台handleTextMessage方法
            alert("发送成功!");
        } else {
            alert("连接失败!");
        }
    }

    window.close=function()
    {
        websocket.onclose();
    }
</script>
请输入：<textarea rows="5" cols="10" id="inputMsg" name="inputMsg"></textarea>
<button onclick="doSend();">发送</button>
</body>
</html>
