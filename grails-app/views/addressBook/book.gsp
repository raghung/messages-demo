<%@ page import="com.messages.User" %>
<html>
	<head>
		<title>Address Book</title>
		<meta name="layout" content="main">
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
	</head>
	<body>
		<g:if test="${flash.message}"><div style="color:red">${flash.message}</div></g:if>
		<table>
			<tr>
				<td><b>Address Book</b></td>
			</tr>
			
			<g:each in="${addressList}" var="address">
			<g:if test="${address}">
			<tr>
				<td>${address.firstname} ${address.lastname}</td>
			</tr>
			</g:if>
			</g:each>
			
			<tr><td><hr></td></tr>
			<tr>
				<td><b>Circles</b></td>
			</tr>
			<g:each in="${circlesList}" var="circle">
			<tr>
				<td>${circle.circlename}</td>
			</tr>
			<g:each in="${circle.userList}" var="user">
			<g:if test="${user}">
			<tr>
				<td>${user.firstname} ${user.lastname}</td>
			</tr>
			</g:if>
			</g:each>
			</g:each>
			
		</table>
	</body>
</html>