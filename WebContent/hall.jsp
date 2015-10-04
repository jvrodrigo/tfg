<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
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
			<s:set name="myToken" value="token"/>
		</ul>
	</s:push>
	<p>Estas en la Sala Principal donde puedes contactar con los demas
		usuarios</p>
	<ul id="userList">

		<s:iterator value="userList" status="userStatus">
			<s:iterator value="key">
				<s:iterator value="value">
				<s:set name="token" value="%{token}" />
				<s:if test="%{#myToken != #token}"> 
					<li onclick="calling(this)" id="<s:property value="%{token}" />"><s:url id="callUrl" action="call">
							<s:param value="%{token}" />
						</s:url> <s:a href="%{callUrl}">
						Usuario: <s:property value="name" />
						</s:a></li>
					</s:if>
				</s:iterator>
			</s:iterator>
		</s:iterator>
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
		channel.send('{"username":"' + myName.innerHTML + '","token":"'
				+ myToken.innerHTML + '"}');
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
	var userList = document.getElementById("userList");
	function processSignalingMessage(message) {
		console.log(message);

		if (message) {
			var msg = JSON.parse(message);
			//console.log("Processing signaling message:\n Msg type: " + msg.type); 
			console.log(msg);
			if (msg.type == "newuser") {
				//var msg = JSON.parse(message);
				//console.log(msg);

				var user = document.createElement("li");
				user.setAttribute("id", msg.usertoken);
				var userToken = document.createTextNode("Usuario: "
						+ msg.username);
				var hiperLink = document.createElement("a");
				hiperLink.setAttribute('href', host + ":" + port
						+ "/tfg/call.action?r=" + msg.usertoken);
				hiperLink.setAttribute('title', "Pulse para llamar al usuario "
						+ msg.username);
				hiperLink.appendChild(userToken);
				user.appendChild(hiperLink);

				userList.appendChild(user);
			}
			if(msg.type == "deleteuser"){
				console.log(msg.usertoken);
				console.log(document.getElementById(msg.usertoken));
				userList.removeChild(document.getElementById(msg.usertoken));
				
			}

		}
	}
	function calling(toUser){
		console.log("User ->" + toUser.id);
		debugger;
		channel.send('{"calling":"'+ myToken.innerHTML +'", "to":"'+ toUser.id +'"}');
	}
	initialize();
</script>
</html>
