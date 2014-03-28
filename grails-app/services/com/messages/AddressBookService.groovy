package com.messages

import java.util.List;

class AddressBookService {
	
	Map getAddressBook(long userId) {
		def addressBook = AddressBook.findByUserId(userId)?: new AddressBook(userId: userId).save(flush: true, failOnError: true) 
		def addressList = User.findAllByIdInList(addressBook.contactIds)
		def circlesList = AddressCircle.findAllByIdInList(addressBook.circleIds)
		for (circle in circlesList) {
			def otherIds = []
			for(otherCircleId in circle.otherCircleIds) {
				def otherCircle = AddressCircle.findById(otherCircleId) 
				if (otherCircle) {
					otherIds += otherCircle.contactIds
				}
			}
			circle['userList'] = User.findAllByIdInList(circle.contactIds + otherIds)
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
	
	String saveCircles(Long userId, String circleName, currentContact, currentCircle) {
		if (!circleName.empty) {
			
			def addressCircle = AddressCircle.findByUserIdAndCirclename(userId, circleName)?: new AddressCircle(userId: userId, circlename: circleName)
			if (currentContact) {
				def className = currentContact.getClass().getName()
				if (className == 'java.lang.String') {
					addressCircle.contactIds += currentContact
				} else if (className == '[Ljava.lang.String;') {
					for (contact in currentContact) {
						addressCircle.contactIds += contact
					}
				}
				addressCircle.contactIds = addressCircle.contactIds.unique() // Remove Duplicates
			}
			if (currentCircle) {
				def className = currentCircle.getClass().getName()
				if (className == 'java.lang.String') {
					addressCircle.otherCircleIds += currentCircle
				} else if (className == '[Ljava.lang.String;') {
					for (circle in currentCircle) {
						addressCircle.otherCircleIds += circle
					}
				}
				addressCircle.otherCircleIds = addressCircle.otherCircleIds.unique() // Remove Duplicates
			}
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
