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
	
	Map getContactsToAdd(Long userId) {
		def addressBook = AddressBook.findByUserId(userId)
		
		def result = [:]
		result.currentList = User.findAllByIdInList(addressBook.contactIds)
		result.circleList = AddressCircle.findAllByIdInList(addressBook.circleIds)
		result.contactList = User.findAll() - User.findAllByIdInList(addressBook.contactIds + userId)
		
		return result 
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
	
	String saveContacts(Long userId, List contacts) {
		if (contacts) {
			def addressBook = AddressBook.findByUserId(userId)?: new AddressBook(userId: userId) 
			addressBook.contactIds += contacts
			addressBook.contactIds = addressBook.contactIds.unique()
			addressBook.save(flush:true, failOnError: true)
			
			return "Contacts updated"
		}
		
		return ""
	}
	
	String saveCircles(Long userId, String circleName, String[] currentContact, String[] currentCircle) {
		if (!circleName.empty) {
			
			def addressCircle = AddressCircle.findByUserIdAndCirclename(userId, circleName)?: new AddressCircle(userId: userId, circlename: circleName)
			for (contact in currentContact) {
				addressCircle.contactIds += contact
			}
			addressCircle.contactIds = addressCircle.contactIds.unique() // Remove Duplicates
			
			for (circle in currentCircle) {
				addressCircle.otherCircleIds += circle
			}
			addressCircle.otherCircleIds = addressCircle.otherCircleIds.unique() // Remove Duplicates
			addressCircle.save(flush:true, failOnError: true)
			
			def addressBook = AddressBook.findByUserId(userId)
			if (!(addressCircle.id in addressBook.circleIds)) {
				addressBook.circleIds += addressCircle.id
				addressBook.save(flush:true, failOnError: true)
			}
			
			return "Circle updated"
		}
		
		return ""
	}
}
