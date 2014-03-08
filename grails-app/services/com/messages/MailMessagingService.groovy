package com.messages

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

class MailMessagingService {
	def threadMessageService
	def grailsApplication
	
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
		
		def msg = validateMessage(subject, text, file)
		if (msg.empty && threadMessageService.sendThreadMessage(from.id, to.id, from.firstname+' '+from.lastname, to.firstname+' '+to.lastname, text, subject, file)) {
			msg = 'Message sent successfully'
		}

		return msg 
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
	
	private String validateMessage(String subject, String text, MultipartFile file) {
		def msg = ""
		def imageOkContents = grailsApplication.config.imageOkContents
		def attachmentNotOk = grailsApplication.config.attachementNotOk
		def maxTextSize = grailsApplication.config.maxMessageTextSize

		if (subject && (text || !file.empty)) {
			
			if (text && text.size() > maxTextSize) {
				msg = "Message text cannot be more than ${maxTextSize} characters"
			}
				
			if (!file.empty) {
				def fileExt = file.originalFilename.substring(file.originalFilename.indexOf(".")).toUpperCase()
				if (attachmentNotOk.contains(fileExt)) {
					
					msg = "File with extension ${fileExt} cannot be attached"
				} else if (!imageOkContents.contains(file.contentType) && file.bytes.size() > grailsApplication.config.maxAttachFileSize) {
					
					msg = "File cannot be more than " + grailsApplication.config.error.maxAttachFileSize
				} 
			}
			
		} else {
			msg = 'Error sending message'//message(code: 'thread.error')
		}
		
		return msg
	}
	
}
