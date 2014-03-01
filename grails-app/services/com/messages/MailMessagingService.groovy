package com.messages

import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

class MailMessagingService {
	def threadMessageService
	
	Map getAllMessages(userId, offset, itemsByPage, sort, order) {
		def result = threadMessageService.getAllByThread(userId, offset, itemsByPage, sort, order)

		// Set the other user who is in conversation with current user		
		for (message in result.messages) {
			def otherUser = message.fromId == userId? User.get(message.toId) : User.get(message.fromId)
			message.otherName = otherUser.firstname + ' ' + otherUser.lastname
		}
		
		return result
	}
		
	Message sendMessage(User from, User to, String text, String subject, CommonsMultipartFile photo) {
		return threadMessageService.sendThreadMessage(from.id, to.id, from.firstname+' '+from.lastname, to.firstname+' '+to.lastname, text, subject, photo)
	}
	
	List getUsersList(currUser) {
		def query = User.where {
			username != currUser
		}
		def userList = query.list(sort: "firstname")
		
		return userList
	}
}
