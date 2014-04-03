<%@ page import="com.messages.User" %>
<html>
	<head>
		<title>Thread</title>
		<meta name="layout" content="main">
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
	</head>
	<body>
		&nbsp;<g:link mapping="inbox">Inbox</g:link> &gt; Messages
		<div><hr></div>
		<g:if test="${groupList}">
        	<div><h2>Group Conversation (
        	<g:each in="${groupList}" var="gpUser">
        		|${gpUser.firstname} ${gpUser.lastname}|
        	</g:each>
        	)
        	</h2></div>
        </g:if>
        <g:else>
        	<div><h2>Conversation with ${otherUser.firstname} ${otherUser.lastname}</h2></div>
        </g:else>
        <div><input type="button" value="Forward/Group Users >>" onclick="openForward()"></div>
        
        <g:uploadForm controller="message" action="saveThreadMessage">
        
        <div id="divForward" style="display:none">
        	<g:each in="${groupList}" var="grpUser">
        		<p>&nbsp; <g:checkBox name="contacts" value="${grpUser.id}" /> ${grpUser.firstname} ${grpUser.lastname}</p>
        	</g:each>
        	<g:each in="${contactList}" var="user">
	            <p>&nbsp; <g:checkBox name="contacts" value="${user.id}" checked="false"/> ${user.firstname} ${user.lastname}</p>
	        </g:each>
	        <g:each in="${circleList}" var="circle">
	            <p>&nbsp; <g:checkBox name="circles" value="${circle.id}" checked="false"/> Circle(${circle.circlename})</p>
            </g:each>
        </div>
		<div><hr></div>
		
		<div>&nbsp;<g:checkBox name="groupChat" value="${groupList}"/> Group Chat</div>
		
		<div><hr></div>
        <div><b>Sub: <i>${subject}</i></b><input type="hidden" name="messageId" value="${lastMessageId}"></div>
        <div><hr></div>

        <div id="thread-message">
        	<g:render template="threadMessage" model="[messages: messages, groupList: groupList]"/>
        </div><!-- End of Thread message -->
       	
       	<div>	
       		<input id="subject" type="hidden" name="subject" value="${subject}" />
            <input type="hidden" name="toId" value="${otherUser.id}" />
       		<textarea id="text" name="text" style="width:700px"></textarea>
       	</div>	
	    <label for="photo">Attachments: </label>
	    <input type="file" name="file" id="upload-file"/>
	    <input type="submit" class="buttons" value="Send" />
	    
	    </g:uploadForm>
	    
        <g:javascript>
        $('#upload-file').bind('change', function() {
            alert('This file size is: ' + this.files[0].size/1024/1024 + "MB");
        });
        function openForward() {
        	var ele = document.getElementById('divForward')
        	ele.style.display = ''
        }
        </g:javascript>
	</body>
</html>