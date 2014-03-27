package com.messages

import com.sun.org.apache.xpath.internal.operations.String;

import grails.plugin.springsecurity.annotation.Secured;

@Secured(['ROLE_USER'])
class AddressBookController {
	def springSecurityService
	def addressBookService
	
	def book() {
		def currentUser = springSecurityService.currentUser
		def result = addressBookService.getAddressBook(currentUser.id)
		
		render view:"book", model:[user: currentUser, addressList: result.addressList, circlesList: result.circlesList]
	}
	
	def addContacts() {
		def currentUser = springSecurityService.currentUser
		def result = addressBookService.getContactsToAdd(currentUser.id) 
				
		render view:"addContacts", model:[user: currentUser, contactList: result.contactList, currentList: result.currentList, circleList: result.circleList]	
	}
	
	def saveContacts() {
		def currentUser = springSecurityService.currentUser

		if (params.contacts) {
			flash.message = addressBookService.saveContacts(currentUser.id, params.contacts.toList())
		}
		redirect action:"book"
	}
	
	def saveCircles() {
		def currentUser = springSecurityService.currentUser

		def circleName = params.circlename
		if (!circleName.empty) {
			flash.message = addressBookService.saveCircles(currentUser.id, circleName, params.currentContact, params.currentCircle)
		}
		
		redirect action:"book"
	}
	
	def editCircle() {
		def currentUser = springSecurityService.currentUser
		println params.circleId
		
		redirect action:"book"
	}
	
	def removeContacts() {
		def currentUser = springSecurityService.currentUser
		if (params.contacts) {
			flash.message = addressBookService.removeContacts(currentUser.id, params.contacts.toList())
		}
		redirect action:"book"
	}
	
	
	def save() {
		
		def currentUser = springSecurityService.currentUser
		def raghu = User.get(currentUser.id)
		def sundar = User.findByFirstname("Sundar")
		def yamini = User.findByFirstname("Yamini")
		def circle = AddressCircle.findByCirclename("Circle Admin")?:new AddressCircle(userId: raghu.id, circlename: 'Circle Admin')
		circle.addToContactIds(yamini.id).save(flush:true, failOnError:true)
		/*def raghuBook = AddressBook.findByUserId(raghu.id)
		raghuBook.addToCircleIds(circle.id).save(flush:true, failOnError:true)*/
		def raghuBook = AddressBook.findByUserId(raghu.id)?: new AddressBook(userId: raghu.id).addToContactIds(sundar.id)
																					.addToContactIds(yamini.id)
																					.addToCircleIds(circle.id)
																					.save(flush:true, failOnError: true)
																					 
	}

}
