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
		<g:link mapping="addressBook">Address Book</g:link>&nbsp;||&nbsp;
		<g:link mapping="addressBook">To Do</g:link>&nbsp;||&nbsp;
		<g:link mapping="addressBook">Basic Info</g:link>&nbsp;||&nbsp;
		<table>
			<tr>
				<td colspan=2 align="right"><g:link mapping="newMessage">New Message</g:link></td>
				<td colspan=2>
					<g:form controller="message" action="searchAll">
						<g:textField name="searchText" /><input type="submit" value="Search">
					</g:form>
				</td>
			</tr>
			<tr><td>
				<b>Messages Priority:</b> <g:select name="priorityLevel" from="[0:'All', 1:'High', 2:'Medium', 3:'Low']" 
        									optionKey="key" optionValue="value" value="${priorityLevel}" onchange="getPriorityMessages()"/>
				</td></tr>
		</table>
		<table>
			<thead>
				<tr><td colspan="4"><b>Physician Communication</b></td></tr>
				<tr><td colspan="4"><hr></td></tr>
				<tr>
					<td><b>Physician</b></td>
					<td><b>Subject</b></td>
					<td><b>Last Text</b></td>
					<td><b>Updated</b></td>
				</tr>
			</thead>
			<tbody>
				<g:each in="${result.physicianMsgs}" var="entry">
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
				<tr><td colspan="4"><b><i>Practice Group:</i></b></td></tr>
				<g:each in="${result.practiceGrpMsgs}" var="entry">
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
		<table>
			<thead>
				<tr><td colspan="4"><b>Patient Communication</b></td></tr>
				<tr><td colspan="4"><hr></td></tr>
				<tr>
					<td><b>Patient</b></td>
					<td><b>Subject</b></td>
					<td><b>Last Text</b></td>
					<td><b>Updated</b></td>
				</tr>
			</thead>
			<tbody>
				<g:each in="${result.patientMsgs}" var="entry">
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
				<tr><td colspan="4"><b><i>Follow up:</i></b></td></tr>
				<g:each in="${result.followupMsgs}" var="entry">
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
	<g:javascript>
		function getPriorityMessages() {
			var val = document.getElementById('priorityLevel').value
			location.href="${createLink(controller: 'message', action: 'messagePriority', params: [priorityLevel: ''])}" + val;
		}
	</g:javascript>	
	</body>
</html>