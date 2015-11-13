<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="css/style.css" rel="stylesheet" type="text/css">
<title>Bienvenido a WEBRTC</title>
</head>
<body onload="acceptField()">
<div id="content">
<h1>Bienvenido a WEBRTC</h1>
<h2>Introduce un nombre de usuario para conectacte a la Sala Principal</h2>
	<s:form  action="welcome" class="org.web.actions.Welcome" namespace="/">
		<s:textfield onkeyup="acceptField()" name="userName" label="Nombre de usuario" />
		<s:submit disabled="disabled"/>
	</s:form>
	<p id="alert-message">El nombre de usuario debe de tener más de 2 carácteres</p>
	</div>
</body>
<script type="text/javascript">
var userName = document.getElementById("welcome_userName");
function acceptField(){
	if(userName.value.length < 3){
		document.getElementById("alert-message").style.display = "block";
		document.getElementById("welcome_0").disabled = true;
		document.getElementsByClassName('org.web.actions.Welcome')[1].style.border = "1px solid #F00";
	}else{
		document.getElementById("alert-message").style.display = "none";
		document.getElementById("welcome_0").disabled = false;
		document.getElementsByClassName('org.web.actions.Welcome')[1].style.border = "1px solid #666";
	}
	
};
</script>
</html>
