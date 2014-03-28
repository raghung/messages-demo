<%@ page import="com.messages.User" %>
<html>
	<head>
		<title>Edit Address Book</title>
		<meta name="layout" content="main">
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
	</head>
	<body>
		<g:if test="${flash.message}"><div style="color:red">${flash.message}</div></g:if>
		&nbsp;<g:link mapping="addressBook">Address Book</g:link> &gt; Edit 
		
		<g:if test="${editCircle}">
		<g:form controller="addressBook" action="updateCircle">
		<input type="hidden" name="circleId" value="${editCircle.id}">
		<table>
			<tr><td>Circle(<input type="text" name="circlename" value="${editCircle.circlename}">)</td></tr>
			<tr><td style="color:blue">** Select checkbox for removing contact or circle</td></tr>
			<tr>
			<g:each in="${contactList}" var="contact">
			<g:if test="${contact}">
				<td><g:checkBox name="currentContact" value="${contact.id}" checked="false"/>&nbsp;|${contact.firstname} ${contact.lastname}|</td>
			</g:if>
			</g:each>
			</tr>
		</table>
		<table>
			<g:each in="${circleList}" var="circle">
			<g:if test="${circle}">
				<tr>
					<td><g:checkBox name="currentCircle" value="${circle.id}" checked="false"/>&nbsp;Circle(${circle.circlename})</td>
				</tr>
				<g:each in="${circle.contactList}" var="contact">
				<g:if test="${contact}">
				<tr>
					<td>|${contact.firstname} ${contact.lastname}|</td>
				</tr>
				</g:if>
				</g:each>	
			</g:if>
			</g:each>
		</table>
		<hr>
		<table>
			<tr><td><b><i>Add Contacts & Circles</i></b></td></tr>
		</table>
		<table>
			<tr>
			<g:each in="${addContactList}" var="contact">
			<g:if test="${contact}">
				<td><g:checkBox name="addContact" value="${contact.id}" checked="false"/>&nbsp;|${contact.firstname} ${contact.lastname}|</td>
			</g:if>
			</g:each>
			</tr>
		</table>
		<table>
			<tr>
			<g:each in="${addCircleList}" var="circle">
			<g:if test="${circle}">
				<td><g:checkBox name="addCircle" value="${circle.id}" checked="false"/>&nbsp;Circle(${circle.circlename})</td>
			</g:if>
			</g:each>
			</tr>
		</table>
		<hr>
		<table>
			<tr><td><input type="submit" value="Update Circle"></td></tr>
		</table>
		</g:form>
		</g:if>
	</body>
</html>