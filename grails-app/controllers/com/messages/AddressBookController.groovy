package com.messages

import grails.plugin.springsecurity.annotation.Secured;

@Secured(['ROLE_USER'])
class AddressBookController {
	def springSecurityService
	def addressBookService
	
	def book() {
		def currentUser = springSecurityService.currentUser
		//save()
		def addressBook = AddressBook.findByUserId(currentUser.id)
		def addressList = User.findAllByIdInList(addressBook.contacts)
		def circlesList = AddressCircle.findAllByIdInList(addressBook.circles)
		for (circle in circlesList) {
			circle['userList'] = User.findAllByIdInList(circle.contacts)
		}
		
		render view:"book", model:[user: currentUser, addressList: addressList, circlesList: circlesList]
	}
	
	def save() {
		
		// Adding to address book
		//def devCircle = AddressCircle.findByUserAndCirclename(raghu, "Circle Dev")?: new AddressCircle(user:raghu, circlename: "Circle Dev")
		
		def currentUser = springSecurityService.currentUser
		def raghu = User.get(currentUser.id)
		def sundar = User.findByFirstname("Sundar")
		def yamini = User.findByFirstname("Yamini")
		def devCircle = AddressCircle.findByCirclename("Circle Dev")?:new AddressCircle(userId: raghu.id, 
																						circlename: 'Circle Dev', 
																						contacts:[sundar.id, yamini.id]).save(failOnError: true)
									 
		def raghuBook = AddressBook.findByUserId(raghu.id)?: new AddressBook(userId: raghu.id).addToContacts(sundar.id)
																					.addToContacts(yamini.id)
																					.addToCircles(devCircle.id)
																					.save(failOnError: true)
																					 
	}

}
