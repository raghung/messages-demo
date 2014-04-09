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
	                <b>Message To:</b><br>
	                <g:each in="${userList}" var="user">
	                <p>&nbsp; <g:checkBox name="contacts" value="${user.id}" checked="false"/> ${user.firstname} ${user.lastname}</p>
	                </g:each>
	                <g:each in="${circleList}" var="circle">
	                <p>&nbsp; <g:checkBox name="circles" value="${circle.id}" checked="false"/> Circle(${circle.circlename})</p>
	                </g:each>
            	</div>
            	<div><b>Priority Level:</b> <g:select name="priorityLevel" from="[1:'High', 2:'Medium', 3:'Low']" 
            									optionKey="key" optionValue="value" value="3"/>
            			&nbsp;<g:checkBox name="groupChat" value="1" checked="false"/> <b>Group Chat</b></div>
                <div><b>Sub:</b> <g:select name="messageType" optionKey="key" optionValue="value"
								  from="['custom':'custom', 
									 	 'refer-patient':'Refer Patient', 
										 'follow-up':'Follow up',
										 'practice-group':'Practice Group',
										 'instant-messaging':'Instant Messaging']"/>
						&nbsp;<g:textField name="subject" size="60"/></div>
                <br />
                <div></div>
                <div><textarea id="text" style="width:700px" name="text" maxlength="5000""></textarea></div>
                <label for="photo"><b>Attachment:</b> </label>
			    <input type="file" name="file" id="file"/>
			    <input type="submit" class="buttons" value="Send" />
            </g:uploadForm>
        </div>


    </body>
</html>
