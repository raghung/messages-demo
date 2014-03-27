<%@ page import="com.messages.User" %>
<html>
	<head>
		<title>Address Book</title>
		<meta name="layout" content="main">
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
	</head>
	<body>
		<g:if test="${flash.message}"><div style="color:red">${flash.message}</div></g:if>
		||&nbsp;<g:link mapping="inbox">Inbox</g:link>&nbsp;||
		&nbsp;<g:link mapping="addressBook">Address Book</g:link>&nbsp;||
		
		<g:form controller="addressBook" action="removeContacts">
		<table>
			<tr>
				<td><b><u>Contacts</u></b>&nbsp;- <g:link controller="addressBook" action="addContacts">Add</g:link></td>
			</tr>
			<tr>
			<g:each in="${addressList}" var="address">
			<g:if test="${address}">
				<td><g:checkBox name="contacts" value="${address.id}" checked="false"/>&nbsp;|${address.firstname} ${address.lastname}|</td>
			</g:if>
			</g:each>
			</tr>
			<tr><td><input type="submit" value="Delete Contacts"></td></tr>
		</table>
		</g:form>
		
		<table>
			<tr>
				<td><b><u>Circles</u></b>&nbsp;- <g:link controller="addressBook" action="addContacts">Add</g:link></td>
			</tr>
			<g:each in="${circlesList}" var="circle">
			<tr>
				<td><b><i>${circle.circlename}</i></b>&nbsp;- <g:link controller="addressBook" action="editCircle" params="[circleId: circle.id]">Edit</g:link></td>
			</tr>
			<tr>
			<g:each in="${circle.userList}" var="user">
			<g:if test="${user}">
				<td><g:checkBox name="cirContacts" value="${circle.id} + '|' + ${user.id}" checked="false"/>&nbsp;|${user.firstname} ${user.lastname}|</td>
			</g:if>
			</g:each>
			</tr>
			</g:each>
			<tr><td><input type="button" value="Delete Circle Contacts"></td></tr>
		</table>
	</body>
</html>