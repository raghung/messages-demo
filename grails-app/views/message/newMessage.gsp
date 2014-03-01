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
	                <p><input type="radio" name="toId" value="${user.id}"> ${user.firstname} ${user.lastname}</p>
	                </g:each>
            	</div>
                <div>Sub: <input id="subject" class="subject" type="text" name="subject"/></div>
                <br />
                <div><textarea id="text" style="width:700px" name="text" maxlength="5000""></textarea></div>
                <label for="photo">Upload Picture: </label>
			    <input type="file" name="photo" id="photo"/>
			    <input type="submit" class="buttons" value="Send" />
            </g:uploadForm>
        </div>


    </body>
</html>
