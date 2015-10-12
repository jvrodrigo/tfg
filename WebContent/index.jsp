<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="css/style.css" rel="stylesheet" type="text/css">
<title>Welcome to WEBRTC</title>
</head>
<body>
	<s:form action="welcome" class="org.web.actions.Welcome" namespace="/">
		<s:textfield name="userName" label="Nombre de usuario" />
		<s:submit />
	</s:form>
</body>
</html>
