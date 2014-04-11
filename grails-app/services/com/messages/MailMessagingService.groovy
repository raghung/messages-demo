package com.messages

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

class MailMessagingService {
	def threadMessageService
	def grailsApplication
	
	final int MAX_INBOX_TEXT = 30
	
	Map getAllMessages(Long userId, int offset, Integer itemsByPage, String sort, String order, Integer priorityLevel = 0) {
		
		def result = threadMessageService.getAllByThread(userId, offset, itemsByPage, sort, order, priorityLevel)

		return groupMessages(userId, result.messages)
	}
	
	Map getAllThreadMessages(Long currUserId, Message message) {
		def messageIds = []
		def messages = threadMessageService.findAllMessagesOnThread(message)
		
		//Mark as read
		messages.each {
			if (it.toId == currUserId) {
				it.readed = true
				it.save()
			}
			messageIds += messages.id
		}
		
		// Set the forward content message
		messageIds = getForwardMessageIds(messages, messageIds)
		messages = Message.findAllByIdInList(messageIds, [sort:'dateCreated', order: 'asc'])

		def otherUser = message.fromId == currUserId?User.get(message.toId):User.get(message.fromId)
		def addressBook = AddressBook.findByUserId(currUserId)
		def contactList = User.findAllByIdInList(addressBook.contactIds - otherUser.id.toString())
		def circleList = AddressCircle.findAllByIdInList(addressBook.circleIds)
		def groupList
		if (message.isGroupMessage) {
			groupList = User.findAllByIdInList(message.groupMessageUser - currUserId.toString())
		} 
		
		def result = [:]
		result.subject = messages[messages.size()-1].subject
		result.lastMessageId = messages[messages.size()-1].id
		result.messages = messages
		result.otherUser = otherUser
		result.contactList = contactList
		result.circleList = circleList
		result.groupList = groupList
		
		return result
	}
	
	List getForwardMessageIds(List messages, List messageIds) {
		if (messages) {
			for (msg in messages) {
				if (msg.forwardMessage) {
					messageIds += getForwardMessageIds(Message.findAllByIdInList(msg.forwardMessage), messageIds)
				}
				messageIds += msg.id
			}
		}
		
		return messageIds
	}
	
	/*List<Message> findForwardMessages(List messageIds, String orderby='desc'){
		return Message.createCriteria().list{
			'in' ('id', messageIds)
			order 'dateCreated', orderby
		}
		
	}*/
		
	String sendMessage(User from, String[] contacts, String[] circles, String text, String subject, MultipartFile file, String messageType, Integer priorityLevel, boolean isGroupChat=false,  List forwardMsg = []) {

		if (sendThreadMessage(from, contacts, circles, text, subject, file, messageType, priorityLevel, isGroupChat, forwardMsg)) {
			return 'Message sent successfully'
		}
		
		return 'Message sending error'
	}
	
	/*String forwardMessage(User from, String[] contacts, String[] circles, String lastMsgId, String text, String subject, MultipartFile file, Integer priorityLevel, boolean isGroupChat=false) {
		
		def lastMessage = Message.findById(lastMsgId)
		def forwardMsg = threadMessageService.findAllMessagesOnThread(lastMessage)
		
		if (sendThreadMessage(from, contacts, circles, text, subject, file, isGroupChat, lastMessage.messageType, priorityLevel, forwardMsg)) {
			return 'Message sent successfully'
		}

		return 'Message sending error'
	}*/
		
	Message sendThreadMessage(User from, String[] contacts, String[] circles, String text, String subject, 
								MultipartFile file, String messageType, Integer priorityLevel, boolean isGroupChat=false, List forwardMsg = []) {
		def message = null
		def msg = validateMessage(subject, text, file)
		if (msg.empty) {
			
			def toIds = []
			for (contactId in contacts) {
				toIds += contactId
			}
			
			for (circleId in circles) {
				def addressCircle = AddressCircle.findById(circleId)
				if (addressCircle) {
					for (contactId in addressCircle.contactIds) {
						toIds += contactId
					}
					
					// other circles ids
					for (otherCircleId in addressCircle.otherCircleIds) {
						def otherCircle = AddressCircle.findById(otherCircleId)
						if (otherCircle) {
							for (contactId in otherCircle.contactIds) {
								toIds += contactId
							}
						}
					}
				}
			}
			
			toIds = toIds.unique()
			
			def grpUserIds = []
			if (toIds.size() > 1 && isGroupChat) {
				for (toId in toIds) {
					grpUserIds += toId
				}
				grpUserIds += from.id.toString()
			}
			
			for (toId in toIds) {
				def toUser = User.findById(toId)
				if (toUser) { // If To User exists
					def msgArtifact = new MessageArtifact(messageType: messageType, priorityLevel: priorityLevel, text: text, subject: subject, 
														file: file, forwardMsg: forwardMsg, grpUserIds: grpUserIds)
					message = threadMessageService.sendThreadMessage(from.id, toUser.id, from.firstname+' '+from.lastname, toUser.firstname+' '+toUser.lastname, 
																	msgArtifact)
				}
			}
		}
		
		return message
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
		def maxTextSize = grailsApplication.config.maxMessageTextSize

		if (subject) {
			
			if (text && text.size() > maxTextSize) {
				msg = "Message text cannot be more than ${maxTextSize} characters"
			}
				
			if (!file.empty) {
				def imageOkContents = grailsApplication.config.imageOkContents
				def attachmentNotOk = grailsApplication.config.attachementNotOk
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
	
	List searchName(long userId, String searchText) {
		def messages = Message.findAllByFromNameOrToNameIlike('%'+searchText +'%', '%'+searchText+'%', [sort:'dateCreated', order:'desc'])
		
		return filterMessages(userId, messages) 
	}
	
	Map searchAll(long userId, String searchText) {
		
		def result = Message.search(searchText, [sort:'dateCreated', order:'desc'])
		def messages = result.searchResults
		
		def resultMessages = []
		while (messages) {
			def message = messages[0]
			def subjectGroup
			if (message.isGroupMessage) {
				subjectGroup = messages.findAll{it.subject == message.subject}.sort{it.dateCreated}
			} else {
				subjectGroup = messages.findAll{
					it.subject == message.subject &&
					((it.fromId == message.fromId && it.toId == message.toId) ||
					(it.fromId == message.toId && it.toId == message.fromId))
				}.sort{it.dateCreated}
			}
			resultMessages << subjectGroup.last()
			messages = messages - subjectGroup
		}
		
		return groupMessages(userId, resultMessages)
	}

	
	private Map groupMessages(long userId, List messages) {
		def result = [:]
		def doctorRole = Role.findByAuthority("ROLE_DOCTOR")
		def patientRole = Role.findByAuthority("ROLE_USER")
		def staffRole = Role.findByAuthority("ROLE_STAFF")

		/*def currUser = User.get(userId)
		def basicInfo = currUser.getBasicInfo()
		def physicianIds = basicInfo? basicInfo.physicianIds : []
		def patientIds = basicInfo? basicInfo.patientIds : []*/

		def physicianMsgs = []
		def practiceGrpMsgs = []
		def followupMsgs = []
		def patientMsgs = []
		
		// Set the other user who is in conversation with current user
		for (message in messages) {
			def otherUser = message.fromId == userId? User.get(message.toId) : User.get(message.fromId)
			
			if (message.isGroupMessage) {
				message.otherName = "Group("+ otherUser.firstname + ' ' + otherUser.lastname +")"
			} else {
				message.otherName = otherUser.firstname + ' ' + otherUser.lastname
			}
			
			if (message.text.length() > MAX_INBOX_TEXT) {
				message.text = message.text.substring(0, MAX_INBOX_TEXT) + ".."
			}
			
			// Physician message -- use physicianIds.contains(otherUser.id.toString()) when needed
			if (otherUser) {
				if (User.isDoctor(otherUser)) {
					if (message.messageType == "practice-group") {
						practiceGrpMsgs += message
					} else {
						physicianMsgs += message
					}
				} else if (User.isPatient(otherUser)) { // Patients Message
					if (message.messageType == "follow-up") {
						followupMsgs += message
					} else {
						patientMsgs += message
					}
				}
			}
		}
		//result.messages = result.messages.sort{it.dateCreated}.reverse()
		result.physicianMsgs = physicianMsgs.sort{it.dateCreated}.reverse()
		result.practiceGrpMsgs = practiceGrpMsgs.sort{it.dateCreated}.reverse()
		result.patientMsgs = patientMsgs.sort{it.dateCreated}.reverse()
		result.followupMsgs = followupMsgs.sort{it.dateCreated}.reverse()
		
		return result
	}
	
}
