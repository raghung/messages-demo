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
			def contacts = addressBookService.getArray(params.contacts)
			flash.message = addressBookService.saveContacts(currentUser.id, contacts)
		}
		redirect action:"book"
	}
	
	def saveCircles() {
		def currentUser = springSecurityService.currentUser

		def circleName = params.circlename
		if (!circleName.empty) {
			def currentContact = addressBookService.getArray(params.currentContact)
			def currentCircle = addressBookService.getArray(params.currentCircle)
			flash.message = addressBookService.saveCircles(currentUser.id, circleName, currentContact, currentCircle)
		}
		
		redirect action:"book"
	}
	
	def editCircle() {
		def currentUser = springSecurityService.currentUser
		def result = [:]

		if (params.circleId) {
			result = addressBookService.editCircle(currentUser.id, params.circleId)
		}
		
		render view:"edit", model:[user: currentUser, editCircle: result.editCircle, contactList: result.contactList, 
									circleList: result.circleList, addContactList: result.addContactList, addCircleList: result.addCircleList]
	}
	
	def updateCircle() {
		def currentUser = springSecurityService.currentUser
		
		if (params.circleId && params.circlename) {
			def contacts = addressBookService.getArray(params.currentContact)
			def circles = addressBookService.getArray(params.currentCircle)
			def addContacts = addressBookService.getArray(params.addContact)
			def addCircles = addressBookService.getArray(params.addCircle)
			flash.message = addressBookService.updateCircle(params.circleId, params.circlename, contacts, circles, addContacts, addCircles)
		}
		
		redirect action:"book"
	}
	
	def removeContacts() {
		def currentUser = springSecurityService.currentUser
		if (params.contacts) {
			def contacts = addressBookService.getArray(params.contacts)
			flash.message = addressBookService.removeContacts(currentUser.id, contacts)
		}
		redirect action:"book"
	}
	
	def removeCircles() {
		def currentUser = springSecurityService.currentUser
		if (params.circles) {
			def circles = addressBookService.getArray(params.circles)
			flash.message = addressBookService.removeCircles(currentUser.id, circles)
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
