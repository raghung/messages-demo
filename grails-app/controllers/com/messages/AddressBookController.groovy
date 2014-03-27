package com.messages

import com.sun.org.apache.xpath.internal.operations.String;

import grails.plugin.springsecurity.annotation.Secured;

@Secured(['ROLE_USER'])
class AddressBookController {
	def springSecurityService
	def addressBookService
	
	def book() {
		def currentUser = springSecurityService.currentUser
		//save()
		def result = addressBookService.getAddressBook(currentUser.id)
		def userList = addressBookService.getUsersList(currentUser.id)
		
		render view:"book", model:[user: currentUser, addressList: result.addressList, circlesList: result.circlesList, userList: userList]
	}
	
	def addContacts() {
		def currentUser = springSecurityService.currentUser
		def addressBook = AddressBook.findByUserId(currentUser.id)
		addressBook.addToContactIds(currentUser.id)
		
		def result = User.findAll()
		result -= User.findAllByIdInList(addressBook.contactIds) 
				
		render view:"addContacts", model:[user: currentUser, contactList: result]	
	}
	
	def saveContacts() {
		def currentUser = springSecurityService.currentUser

		def addressBook = AddressBook.findByUserId(currentUser.id)
		addressBook.contactIds += params.contacts.toList()
		addressBook.save(flush:true, failOnError: true)
		
		flash.message = "Contacts added"
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
