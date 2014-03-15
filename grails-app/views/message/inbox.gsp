<%@ page import="com.messages.User" %>
<html>
	<head>
		<title>Inbox</title>
		<meta name="layout" content="main">
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
	</head>
	<body>
		<g:if test="${flash.message}"><div style="color:red">${flash.message}</div></g:if>
		||&nbsp;<g:link mapping="inbox">Inbox</g:link>&nbsp;||&nbsp;<%--<g:link controller="message" action="indexAll">Index All</g:link>--%>
		<table>
			<thead>
				<tr>
					<td colspan=2 align="right"><g:link mapping="newMessage">New Message</g:link></td>
					<td colspan=2>
						<g:form controller="message" action="searchAll">
							<g:textField name="searchText" /><input type="submit" value="Search">
						</g:form>
					</td>
				</tr>
				<tr>
					<td><b>User</b></td>
					<td><b>Subject</b></td>
					<td><b>Last Text</b></td>
					<td><b>Updated</b></td>
				</tr>
			</thead>
			<tbody>
				<g:each in="${messages}" var="entry">
				<tr>
					<td>${entry.otherName}</td>
					<td><g:link mapping="view" params="[messageId:entry.id]">
                             ${entry.subject}
                         </g:link>
                    </td>
					<td>${entry.text}</td>
					<td><g:formatDate format="yyyy-MM-dd HH:mm" date="${entry.dateCreated}"/></td>
				</tr>
				</g:each>
			</tbody>
		</table>
	</body>
</html>