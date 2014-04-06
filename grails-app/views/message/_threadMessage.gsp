<%@ page import="com.messages.User" %>
<g:each in="${messages}" var="entry">
	<%--<g:if test="${entry.forwardContent}">
		<g:render template="threadMessage" model="[messages: entry.forwardContent]"/>
	</g:if>--%>
	<g:if test="${entry.isGroupMessage}">
		<div>${User.get(entry.fromId).firstname} at <g:formatDate format="yyyy-MM-dd HH:mm" date="${entry.dateCreated}"/></div>
	</g:if>
	<g:else>
		<div>${User.get(entry.fromId).firstname} to ${User.get(entry.toId).firstname} at <g:formatDate format="yyyy-MM-dd HH:mm" date="${entry.dateCreated}"/></div>
	</g:else>
  	
  	<div><b><i>${entry.text}</i></b></div>
  	<g:if test="${entry.fileName}">
		<img style="width: 12.0em;" src="${createLink(controller:'message', action:'showImage', id: entry.ident())}" />
		${entry.fileName}
		<g:link action="download" id="${entry.id}">Download</g:link>
	</g:if>
	<div><hr></div>
</g:each>
