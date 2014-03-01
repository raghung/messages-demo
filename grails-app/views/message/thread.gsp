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
        <div><h2>Conversation with ${otherUser.firstname} ${otherUser.lastname}</h2></div>
        <div><b>Sub: <i>${messages[0].subject}</i></b></div>
        <div><hr></div>
        <div>
        	<g:each in="${messages}" status="i" var="entry">
        		<div>${User.get(entry.fromId).firstname} at <g:formatDate format="yyyy-MM-dd HH:mm" date="${entry.dateCreated}"/></div>
        		<div><b><i>${entry.text}</i></b></div>
        		<g:if test="${entry.photo}">
  					<img style="width: 6.0em; height: 7.5em;" src="${createLink(controller:'message', action:'showImage', id: entry.ident())}" />
				</g:if>
        		<div><hr></div>
        	</g:each>
        </div>
        <div>
        	<g:uploadForm mapping="newMessage">
	        	<div>	
	        		<input id="subject" type="hidden" name="subject" value="${messages[0].subject}" />
	                <input type="hidden" name="toId" value="${otherUser.id}" />
	        		<textarea id="text" name="text" style="width:700px"></textarea>
	        	</div>	
			    <label for="photo">Upload Picture: </label>
			    <input type="file" name="photo" id="photo"/>
			    <input type="submit" class="buttons" value="Send" />
		    </g:uploadForm>
        </div>
	</body>
</html>