<%@ page import="com.messages.User" %>
<html>
	<head>
		<title>Add Contacts</title>
		<meta name="layout" content="main">
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
	</head>
	<body>
		<g:if test="${flash.message}"><div style="color:red">${flash.message}</div></g:if>
		&nbsp;<g:link mapping="addressBook">Address Book</g:link> &gt; Add Contacts 
		
		<g:form controller="addressBook" action="saveContacts">
		<table>
			<tr>
			<g:each in="${contactList}" var="contact">
			<g:if test="${contact}">
				<td><g:checkBox name="contacts" value="${contact.id}" checked="false"/>&nbsp;|${contact.firstname} ${contact.lastname}|</td>
			</g:if>
			</g:each>
			</tr>
			<tr><td><input type="submit" value="Add Contacts"></td></tr>
		</table>
		</g:form>
		<hr>
		<g:form controller="addressBook" action="saveCircles">
		<table>
			<tr><td><input type="text" name="circlename" placeholder="Enter Circle Name"></td></tr>
			<tr>
			<g:each in="${currentList}" var="contact">
			<g:if test="${contact}">
				<td><g:checkBox name="currentContact" value="${contact.id}" checked="false"/>&nbsp;|${contact.firstname} ${contact.lastname}|</td>
			</g:if>
			</g:each>
			</tr>
			<tr>
			<g:each in="${circleList}" var="circle">
			<g:if test="${circle}">
				<td><g:checkBox name="currentCircle" value="${circle.id}" checked="false"/>&nbsp;|${circle.circlename}|</td>
			</g:if>
			</g:each>
			</tr>
			<tr><td><input type="submit" value="Add Circle"></td></tr>
		</table>
		</g:form>
	</body>
</html>