package com.messages

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

class MailMessagingService {
	def threadMessageService
	def grailsApplication
	
	static final okcontents = ['image/png', 'image/jpeg', 'image/gif']
	
	Map getAllMessages(userId, offset, itemsByPage, sort, order) {
		def result = threadMessageService.getAllByThread(userId, offset, itemsByPage, sort, order)

		// Set the other user who is in conversation with current user		
		for (message in result.messages) {
			def otherUser = message.fromId == userId? User.get(message.toId) : User.get(message.fromId)
			message.otherName = otherUser.firstname + ' ' + otherUser.lastname
		}
		
		return result
	}
		
	String sendMessage(User from, User to, String text, String subject, MultipartFile file) {
		
		if (threadMessageService.sendThreadMessage(from.id, to.id, from.firstname+' '+from.lastname, to.firstname+' '+to.lastname, text, subject, file))
			return 'Message sent successfully'
	}
	
	List getUsersList(currUser) {
		def query = User.where {
			username != currUser
		}
		def userList = query.list(sort: "firstname")
		
		return userList
	}
	
	String writeFile(Message msg, OutputStream outputStream) {
		def file = new File(msg.storePath)
		def fileInputStream = new FileInputStream(file)

		byte[] buffer = new byte[4096];
		int len;
		while ((len = fileInputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, len);
		}

		outputStream.flush()
		outputStream.close()
		fileInputStream.close()
		
		return "Successful"
	} 
}
