<%@ page import="com.messages.User" %>
<html>
	<head>
		<title>To Do List</title>
		<meta name="layout" content="main">
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
	</head>
	<body>
		<g:if test="${flash.message}"><div style="color:red">${flash.message}</div></g:if>
		||&nbsp;<g:link mapping="inbox">Inbox</g:link>&nbsp;||
		<g:link mapping="addressBook">Address Book</g:link>&nbsp;||&nbsp;
		<g:link mapping="toDo">To Do</g:link>&nbsp;||&nbsp;
		<g:link mapping="addressBook">Basic Info</g:link>&nbsp;||&nbsp;
		
		<g:form controller="toDo">
		<table>
			<tr><td><h2>ToDo List</h2></td></tr>
			<tr>
				<td><b>Show </b> <g:select name="showLevel" from="[0:'All', 1:'Incomplete', 2:'Completed']" 
        									optionKey="key" optionValue="value" value="${showLevel}" onchange="getTasks()"/>&nbsp;
        									<input type="button" value="New Task" onclick="openTask()"></td>
			</tr>
		</table>
		<table id="tblAddTask" style="display:none">
			<tr>
				<td><g:textField name="newSubject" placeholder="Enter Task.." size="60"/></td>
				<td><g:datePicker name="newDueDate" default="${new Date()}"/></td>
				<td><g:actionSubmit value="Add" action="addTask"/></td>
			</tr>
		</table>
		<table id="tblTasks">
			<tr>
				<td><b>&#10004;</b></td>
				<td><b>Task</b></td>
				<td><b>Due Time</b></td>
				<td><b>Del / Updt</b></td>
			</tr>
			<g:each in="${tasks}" var="task" status="i">
			<g:if test="${task}">
			<tr>
				<g:hiddenField name="taskId" value="${task.id}"/>
				<td align="center">&nbsp;<g:checkBox name="completed" value="${task.completed}" onclick="setCompleted(this, '${task.id}')"/>
					<g:if test="${task.messageId}">
						<g:link mapping="view" params="[messageId:task.messageId]">&spades;</g:link>
					</g:if>
				</td>
				<td width="40%"><g:textField name="subject" value="${task.subject}" size="40"/></td>
				<td width="40%"><g:datePicker name="dueDate" value="${task.dueDate}"/></td>
				<td><input type="button" value="&#10008;" onclick="removeTask(${i})">&nbsp;<%--<input type="button" value="&#10004;" onclick="updateTask(${i})"></td>--%>
					<g:actionSubmit value="&uarr;&darr;" action="updateTask" onclick="return setUpdateIndex(${i})"/>
			</tr>
			</g:if>
			</g:each>
			<g:hiddenField name="updateIndex"/>
			<g:hiddenField name="totalTasks" value="${tasks.size()}"/>
		</table>
		</g:form>

		<g:javascript>
			function getTasks() {
				location.href = "${createLink(controller: 'toDo', action: 'tasks')}?showLevel=" + $("#showLevel").val(); 
			}
			function openTask() {
				document.getElementById('tblAddTask').style.display = ''
			}
			function setCompleted(obj, taskId) {
				location.href = "${createLink(controller: 'toDo', action: 'completeTask')}?taskId=" + taskId + "&value=" + obj.checked;
			}
			function removeTask(val) {
				var arrTask = document.getElementsByName('taskId');
				location.href = "${createLink(controller: 'toDo', action: 'removeTask')}?taskId=" + arrTask[val].value;  
			}
			function setUpdateIndex(val) {
				$("#updateIndex").val(val)
			}
		</g:javascript>
	</body>
</html>