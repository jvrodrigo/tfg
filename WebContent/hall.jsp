<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Bienvenido a la Sala Principal</title>
</head>
<body>

	<s:push value="model">
		<ul>
			<li>Bienvenido usuario:</li>
			<li id="myName"><s:property value="name" /></li>
			<li id="myToken"><s:property value="token" /></li>
		</ul>
	</s:push>
	<p>Estas en la Sala Principal donde puedes contactar con los demas
		usuarios</p>
	<ul id="userList">


	</ul>
</body>
<script type="text/javascript">
	var host = "http://localhost";
	var port = 8080;
	var wsPort = 8081;
	var myToken = document.getElementById("myToken");
	console.log(myToken.innerHTML);

	var myName = document.getElementById("myName");
	console.log(myName.innerHTML);

	function initialize() {
		//resetStatus();
		openChannel();
	};
	function openChannel() {
		console.log("Abriendo el canal.");
		//var location = "ws://{server_name}:8081/";
		///var location = "ws://{server_name}:8000/webrtc/?r=" + roomKey;
		var location = "ws://localhost:8081/tfg";
		console.log(location);
		channel = new WebSocket(location);
		channel.onopen = onChannelOpened;

		channel.onmessage = onChannelMessage;
		channel.onclose = onChannelClosed;
		channel.onerror = onChannelError;
	}
	function onChannelOpened() {
		console.log('Conectado a la app');
		channel.send('[{"username":"' + myName.innerHTML + '","token":"' + myToken.innerHTML + '"}]');
		//sendMessage(token);
		//channel.send('user:' + myName.innerHTML + ":" + myToken.innerHTML);
	}
	function onChannelMessage(message) {

		console.log('S -> C: ' + message.data);
		processSignalingMessage(message.data);

	}
	function onChannelError() {

		console.log('Error del canal');
	}
	function onChannelClosed() {

		console.log('Canal cerrado para el usuario');
		alert('Canal cerrado para el usuario');
		//channel = null;
	}
	/*function sendMessage(message) {
		try {
			var msgString = JSON.stringify(message);
			console.log('C -> S: ' + msgString);
			path = '/tfg/hallmessage';
			var xhr = new XMLHttpRequest();
			xhr.open('POST', path, true);
			xhr.send(msgString);
		} catch (error) {
			console.log(error);
		};
	}*/
	function processSignalingMessage(message) {
		console.log(message);
		
		if (message) {
			var msg = JSON.parse(message);
			//console.log("Processing signaling message:\n Msg type: " + msg.type); 
			console.log(msg);
			//var msg = JSON.parse(message);
			//console.log(msg);
			var userList = document.getElementById("userList");
			var user = document.createElement("li");
			var userToken = document.createTextNode(message);
			var hiperLink = document.createElement("a");
			hiperLink.setAttribute('href', host + ":" + port + "/webrtc/?r=" + msg.usertoken);
			
			hiperLink.appendChild(userToken);
			user.appendChild(hiperLink);
			
			userList.appendChild(user);

		}
	}
	initialize();
</script>
</html>
