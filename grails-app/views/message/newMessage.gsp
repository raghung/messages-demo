<html>
    <head>
        <title>New Message</title>
        <meta name="layout" content="main">
    </head>
    <body>

        &nbsp;<g:link mapping="inbox">Inbox</g:link> &gt; New Message
        <div><hr></div>

        <div class="new_message">
            <g:uploadForm mapping='newMessage'>
                <div>
	                Message To:<br>
	                <g:each in="${userList}" var="user">
	                <p>&nbsp; <g:checkBox name="contacts" value="${user.id}" checked="false"/> ${user.firstname} ${user.lastname}</p>
	                </g:each>
	                <g:each in="${circleList}" var="circle">
	                <p>&nbsp; <g:checkBox name="circles" value="${circle.id}" checked="false"/> Circle(${circle.circlename})</p>
	                </g:each>
            	</div>
                <div>Sub: <select name="messageType">
                			<option value="custom">Custom</option>
                			<option value="refer-patient">Refer Patient</option>
                			<option value="follow-up">Follow up</option>
                			<option value="practice-group">Practice Group</option>
                			<option value="instant-messaging">Instant Messaging</option>
                		  </select>&nbsp;<input id="subject" class="subject" type="text" name="subject"/></div>
                <br />
                <div>&nbsp;<g:checkBox name="groupChat" value="1" checked="false"/> Group Chat</div>
                <div><textarea id="text" style="width:700px" name="text" maxlength="5000""></textarea></div>
                <label for="photo">Attachment: </label>
			    <input type="file" name="file" id="file"/>
			    <input type="submit" class="buttons" value="Send" />
            </g:uploadForm>
        </div>


    </body>
</html>
