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
	
	def save() {
		
		def currentUser = springSecurityService.currentUser
		def raghu = User.get(currentUser.id)
		def sundar = User.findByFirstname("Sundar")
		def yamini = User.findByFirstname("Yamini")
		def circle = AddressCircle.findByCirclename("Circle Admin")?:new AddressCircle(userId: raghu.id, circlename: 'Circle Admin')
		circle.addToContactIds(yamini.id).save(flush:true, failOnError:true)
		def raghuBook = AddressBook.findByUserId(raghu.id)
		raghuBook.addToCircleIds(circle.id).save(flush:true, failOnError:true)
		/*def raghuBook = AddressBook.findByUserId(raghu.id)?: new AddressBook(userId: raghu.id).addToContactIds(sundar.id)
																					.addToContactIds(yamini.id)
																					.addToCircleIds(circle.id)
																					.save(flush:true, failOnError: true)*/
																					 
	}

}
