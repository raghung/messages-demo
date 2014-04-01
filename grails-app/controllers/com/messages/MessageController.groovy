package com.messages

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import grails.plugin.springsecurity.annotation.Secured;

@Secured(['ROLE_USER'])
class MessageController {
	def threadMessageService
	def mailMessagingService
	def addressBookService
	def springSecurityService
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
		
		def result = mailMessagingService.getAllMessages(currentUser.id, offset, ITEMS_BY_PAGE, sort, order)
		
		render view:"inbox", model:[user:currentUser, messages:result.messages, totalNum:result.totalNum, unreadedNum:result.unreadedNum, max:ITEMS_BY_PAGE, sort:sort, order:order]
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
		
		render view:"inbox", model:[user:currentUser, messages: results.searchResults, totalNum: results.total]
	}
	
	def view() { 
		def currentUser = springSecurityService.currentUser
		def message = Message.get(params.messageId)
		//If the message exists and the user can view the message
		if ((message) &&
			((message.fromId == currentUser.id) || (message.toId == currentUser.id))) {
				def result = mailMessagingService.getAllThreadMessages(currentUser.id, message)
				
				render view:'thread', model:[user:currentUser, messages:result.messages, subject:message.subject, 
											otherUser:result.otherUser, contactList: result.contactList, circleList: result.circleList]
		} else {
			redirect mapping: 'inbox'
		}
	}
	
	def newMessage() {
		def type = params.messageType // patient(normal, follow up), physician(normal, practice group)
		def currentUser = springSecurityService.currentUser
		//def userList = mailMessagingService.getUsersList(currentUser.username)
		def result = addressBookService.getAddressBook(currentUser.id)
		
		render view: 'newMessage', model:[user: currentUser, userList: result.addressList, circleList: result.circlesList]
		
		/*def otherUser = User.get(params.toId)
		if (otherUser) {
			render view:'newMessage', model:[user:currentUser, otherUser:otherUser]
		} else {
			redirect mapping: 'inbox'
		}*/
	}
    def saveNewMessage() {
        def currentUser = springSecurityService.currentUser
        //def toUser = User.get(params.toId)
		def file = request.getFile("file")

        if (params.contacts || params.circles) {
			def contacts = addressBookService.getArray(params.contacts)
			def circles = addressBookService.getArray(params.circles)
			
			flash.message = mailMessagingService.sendMessage(currentUser, contacts, circles, params.text, params.subject, file)//message(code: 'thread.success')
			elasticSearchService.index(class:Message)
			elasticSearchAdminService.refresh()
        }
        redirect mapping: 'inbox'
    }
	
	def saveThreadMessage() {
		def currentUser = springSecurityService.currentUser
		def toUser = User.get(params.toId)
		def file = request.getFile("file")

		if (params.contacts || params.circles) { // Forward Message
			def contacts = addressBookService.getArray(params.contacts)
			def circles = addressBookService.getArray(params.circles)
			flash.message = mailMessagingService.forwardMessage(currentUser, contacts, circles, params.messageId, params.text, params.subject, file)//message(code: 'thread.success')
			elasticSearchService.index(class:Message)
			elasticSearchAdminService.refresh()
			
		} else if (params.toId) { // Normal reply
			
			def contacts = addressBookService.getArray(params.toId)
			flash.message = mailMessagingService.sendMessage(currentUser, contacts, null, params.text, params.subject, file)//message(code: 'thread.success')
			elasticSearchService.index(class:Message)
			elasticSearchAdminService.refresh()
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
	
	def download(long id) {
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
	
	def forwardMessage() { }
	
	def todo() { }

}
