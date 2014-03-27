package com.messages

import java.util.List;

class AddressBookService {
	
	Map getAddressBook(long userId) {
		def addressBook = AddressBook.findByUserId(userId)?: new AddressBook(userId: userId).save(flush: true, failOnError: true) 
		def addressList = User.findAllByIdInList(addressBook.contactIds)
		def circlesList = AddressCircle.findAllByIdInList(addressBook.circleIds)
		for (circle in circlesList) {
			circle['userList'] = User.findAllByIdInList(circle.contactIds)
		}
		
		def result = [:]
		result.addressList = addressList
		result.circlesList = circlesList
		
		return result
	}
	
	List getUsersList(Long currUserId) {
		def addressBook = AddressBook.findByUserId(currUserId)
		addressBook.addToContactIds(currUserId)
		def userList = User.findAllByIdNotInList(addressBook.contactIds)
		
		return userList
	}
	
	String removeContacts(Long userId, List delContacts) {
		if (delContacts) {
			def addressBook = AddressBook.findByUserId(userId)
			// Remove contacts
			for(contact in delContacts)
				addressBook.contactIds -= contact
				
			addressBook.save(flush:true, failOnError: true)
			return "Contacts removed"
		}
		
		return ""
	}
}
