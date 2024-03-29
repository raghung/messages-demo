package com.messages

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import grails.plugin.springsecurity.annotation.Secured;

@Secured(['ROLE_USER', 'ROLE_DOCTOR', 'ROLE_STAFF'])
class MessageController {

	transient springSecurityService
	
	def mailMessagingService
	def threadMessageService
	def addressBookService
	def elasticSearchService
	def elasticSearchAdminService
	
	static Integer ITEMS_BY_PAGE = 20
	
    def inbox() {
		def currentUser = springSecurityService.currentUser
		
		def offset = 0
		def sort = 'readed'
		def order = 'desc'


		if (params.sort == 'fromName') {
			sort = 'fromName'
		} else if (params.sort == 'subject') {
			sort = 'subject'
		} else if (params.sort == 'dateCreated') {
			sort = 'dateCreated'
		}
		
		if (params.order == 'asc') {
			order = 'asc'
		}

		if (params.offset) {
			try {
				offset = Integer.parseInt(params.offset)
			} catch (Exception e) {
				offset = 0
			}
		}
		
		int priorityLevel = 0
		if (User.isDoctor(currentUser)) {
			priorityLevel = 1
		}
		
		def result = mailMessagingService.getAllMessages(currentUser.id, offset, ITEMS_BY_PAGE, sort, order, priorityLevel)
		
		render view:"inbox", model:[user:currentUser, priorityLevel: priorityLevel, result: result]//totalNum:result.totalNum, unreadedNum:result.unreadedNum, max:ITEMS_BY_PAGE, sort:sort, order:order]
	}
	
	def messagePriority() {
		def currentUser = springSecurityService.currentUser
		def offset = 0
		def sort = 'readed'
		def order = 'desc'
		def priorityLevel = 0
		
		if (params.priorityLevel) {
			try {
				priorityLevel = Integer.parseInt(params.priorityLevel)
			} catch (Exception e) {
				priorityLevel = 0
			}
		}
		
		def result = mailMessagingService.getAllMessages(currentUser.id, offset, ITEMS_BY_PAGE, sort, order, priorityLevel)
		
		render view:"inbox", model:[user:currentUser, priorityLevel: priorityLevel, result: result]
	}
	
	def addTask() {
		
		def currentUser = springSecurityService.currentUser
		
		def subject = params.newSubject
		def dueDate = params.newDueDate
		def messageId = params.messageId
		
		def toDo = new ToDo(userId: currentUser.id, subject: subject, messageId: messageId, dueDate: dueDate)
		try {
			toDo.save(flush:true, failOnError: true)
			flash.message = "Task added to list"
		} catch(Exception e) {
			flash.message = "Error saving Task"
		}
		
		redirect action:"view", params: [messageId: messageId]
	}
	
	def indexAll() {
		elasticSearchService.index()
		elasticSearchAdminService.refresh()
		flash.message = "Indexing done.."
		redirect mapping:"inbox"
	}
	
	def searchName() {
		def currentUser = springSecurityService.currentUser
		
		List messages = mailMessagingService.searchName(currentUser.id, params.searchName)
		//Map map = mailMessagingService.searchAll(currentUser.id, params.searchName)
		
		render view:"inbox", model:[user:currentUser, messages: messages, totalNum:messages.size()]
	}
	
	def searchAll() {
		def currentUser = springSecurityService.currentUser
		
		Map results = mailMessagingService.searchAll(currentUser.id, params.searchText)
		
		render view:"inbox", model:[user:currentUser, result: results]
	}
	
	def view() { 
		def currentUser = springSecurityService.currentUser
		def message = Message.get(params.messageId)
		//If the message exists and the user can view the message
		if ((message) &&
			((message.fromId == currentUser.id) || (message.toId == currentUser.id))) {
				def result = mailMessagingService.getAllThreadMessages(currentUser.id, message)
				
				render view:'thread', model:[user:currentUser, messages:result.messages, subject:result.subject, 
											otherUser:result.otherUser, contactList: result.contactList, circleList: result.circleList,
											lastMessageId: result.lastMessageId, groupList: result.groupList]
		} else {
			redirect mapping: 'inbox'
		}
	}
	
	def newMessage() {
		def type = params.messageType // patient(normal, follow up), physician(normal, practice group)
		def currentUser = springSecurityService.currentUser
		def result = addressBookService.getAddressBook(currentUser.id)
		
		render view: 'newMessage', model:[user: currentUser, userList: result.addressList, circleList: result.circlesList]
	}
    def saveNewMessage() {
        def currentUser = springSecurityService.currentUser
        //def toUser = User.get(params.toId)
		def file = request.getFile("file")
		def isGroupChat = params.groupChat? true:false
		def priorityLevel = params.priorityLevel? new Integer(params.priorityLevel) : 3

        if (params.contacts || params.circles) { // Normal or Group Message
			def contacts = addressBookService.getArray(params.contacts)
			def circles = addressBookService.getArray(params.circles)
			
			println "${currentUser}, ${contacts}, ${circles}, ${params.text}, ${params.subject}, ${file}, ${params.messageType}, ${priorityLevel}, ${isGroupChat}"
			flash.message = mailMessagingService.sendMessage(currentUser, contacts, circles, params.text, params.subject, 
																file, params.messageType, priorityLevel, isGroupChat)//message(code: 'thread.success')
			elasticSearchService.index(class:Message)
			elasticSearchAdminService.refresh()
        }
        redirect mapping: 'inbox'
    }
	
	def saveThreadMessage() {
		def currentUser = springSecurityService.currentUser
		def toUser = User.get(params.toId)
		def file = request.getFile("file")
		def lastMessage = Message.get(params.messageId)
		def priorityLevel = params.priorityLevel? new Integer(params.priorityLevel) : 3
		def isGroupChat = params.groupChat? true:false
		def toId = params.toId
		
		def contacts = addressBookService.getArray(params.contacts)
		def circles = addressBookService.getArray(params.circles)
		
		// Check for forward or group message
		def forwardMsg = []
		if (contacts || circles || params.subject != lastMessage.subject) {
			forwardMsg = threadMessageService.findAllMessagesOnThread(lastMessage)	
		}
		
		if (toId) {
			if (contacts && isGroupChat) { // Adding for group chat
				contacts += toId
			} else if (!contacts) { // Adding single user reply
				contacts = addressBookService.getArray(toId)
			}
		}
		
		println "${currentUser}, ${contacts}, ${circles}, ${params.text}, ${params.subject}, ${file}, ${params.messageType}, ${priorityLevel}, ${isGroupChat}, ${forwardMsg}"
		flash.message = mailMessagingService.sendMessage(currentUser, (String[])contacts, circles, params.text, params.subject, file, params.messageType, priorityLevel, isGroupChat, forwardMsg)
		
		elasticSearchService.index(class:Message)
		elasticSearchAdminService.refresh()

		redirect mapping: 'inbox'
	}
	
	def savePriority() {
		def priorityLevel = params.priorityLevel
		if (priorityLevel && params.messageId) {
			def messages = threadMessageService.findAllMessagesOnThread(Message.get(params.messageId))
			def level = new Integer(priorityLevel)
			messages.each {
				it.priorityLevel = level
				it.save(flush:true)
			}

			flash.message = "Priority Level saved"
		}
		
		redirect mapping: 'inbox'
	}
	
	def showImage() {
		def message = Message.get(params.id)
		OutputStream out
		
		if (message && message.fileName) {
			response.contentType = message.fileType
			mailMessagingService.writeFile(message, response.getOutputStream())
		}
	}
	
	def download(String id) {
		Message documentInstance = Message.get(id)
		
		if ( documentInstance == null) {
			flash.message = "Message not found."
			redirect (action:'inbox')
		} else {
			response.setContentType("APPLICATION/OCTET-STREAM")
			response.setHeader("Content-Disposition", "Attachment;Filename=\"${documentInstance.fileName}\"")

			mailMessagingService.writeFile(documentInstance, response.getOutputStream())
		}
	}
	
	def addressBook() {
		redirect mapping: 'addressBook'
	}
	
	def todo() { }

}
