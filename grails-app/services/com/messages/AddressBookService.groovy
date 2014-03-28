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
	
	Map editCircle(Long userId, String circleId) {
		def addressCircle = AddressCircle.findById(circleId)
		def result = [:]
		if (addressCircle) {
			def currentContactIds = []
			result.editCircle = addressCircle
			result.contactList = User.findAllByIdInList(addressCircle.contactIds)
			currentContactIds += addressCircle.contactIds
			
			def circles = AddressCircle.findAllByIdInList(addressCircle.otherCircleIds)
			for (circle in circles) {
				circle['contactList'] = User.findAllByIdInList(circle.contactIds)
				currentContactIds += circle.contactIds
			}
			result.circleList = circles
			
			currentContactIds = currentContactIds.unique()
			def addressBook = AddressBook.findByUserId(userId)
			result.addContactList = User.findAllByIdInList(addressBook.contactIds - currentContactIds)
			
			def lstCircle = AddressCircle.findAllByUserId(userId)
			def circleIds = []
			for (circle in lstCircle) {
				circleIds += circle.id
			}
			circleIds -= circleId
			circleIds -= addressCircle.otherCircleIds
			result.addCircleList = AddressCircle.findAllByIdInList(circleIds) 
		}
		
		return result
	}
	
	String removeContacts(Long userId, String[] delContacts) {
		if (delContacts) {
			def addressBook = AddressBook.findByUserId(userId)

			// Remove contacts
			for (contact in delContacts) {
				addressBook.contactIds -= contact
			}
			addressBook.save(flush:true, failOnError: true)
			
			// Remove contacts in circles
			for (circleId in addressBook.circleIds) {
				def addressCircle = AddressCircle.findById(circleId)
				for (contact in delContacts) {
					addressCircle.contactIds -= contact
				}
				addressCircle.save(flush:true, failOnError: true)
			}
			
			return "Contacts removed"
		}
		
		return ""
	}
	
	String removeCircles(Long userId, String[] delCircles) {
		if (delCircles) {
			def addressBook = AddressBook.findByUserId(userId)

			// Delete circles and remove from AddressBook 
			for (circleId in delCircles) {
				def addressCircle = AddressCircle.findById(circleId)
				addressCircle.delete(flush: true, failOnError: true)
				
				addressBook.circleIds -= circleId
			}
			addressBook.save(flush: true, failOnError: true)
			
			// Remove circles from otherCircleIds
			def lstCircles = AddressCircle.findAllByUserId(userId)
			for(circle in lstCircles) {
				for (circleId in delCircles) {
					circle.otherCircleIds -= circleId
				}
				circle.save(flush: true, failOnError: true)
			}
			
			return "Circles removed"
		}
		
		return ""
	}
	
	String saveContacts(Long userId, String[] contacts) {
		if (contacts) {
			def addressBook = AddressBook.findByUserId(userId)?: new AddressBook(userId: userId) 

			for (contact in contacts) {
				addressBook.contactIds += contact
			}
			addressBook.contactIds = addressBook.contactIds.unique()
			addressBook.save(flush:true, failOnError: true)
			
			return "Contacts updated"
		}
		
		return ""
	}
	
	String[] getArray(value) {
		def arrayVal = value
		if (arrayVal) {
			def className = arrayVal.getClass().getName()
			if (className == 'java.lang.String') {
				arrayVal = new String[1]
				arrayVal[0] = value 
			}
		}
		
		return arrayVal
	}
	
	String saveCircles(Long userId, String circleName, String[] currentContact, String[] currentCircle) {
		if (!circleName.empty) {
			
			def addressCircle = AddressCircle.findByUserIdAndCirclename(userId, circleName)?: new AddressCircle(userId: userId, circlename: circleName)
			if (currentContact) {
				for (contact in currentContact) {
					addressCircle.contactIds += contact
				}
				addressCircle.contactIds = addressCircle.contactIds.unique() // Remove Duplicates
			}
			if (currentCircle) {
				for (circle in currentCircle) {
					addressCircle.otherCircleIds += circle
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
	
	String updateCircle(String circleId, String circlename, String[] contacts, String[] circles, String[] addContacts, String[] addCircles) {
		def addressCircle = AddressCircle.findById(circleId)
		
		if (addressCircle) {
			addressCircle.circlename = circlename
			
			for (contact in contacts) {
				addressCircle.contactIds -= contact
			}
			
			for (circle in circles) {
				addressCircle.otherCircleIds -= circle
			}
			
			for (contact in addContacts) {
				addressCircle.contactIds += contact
			}
			
			for (circle in addCircles) {
				addressCircle.otherCircleIds += circle
			}
			
			addressCircle.contactIds = addressCircle.contactIds.unique()
			addressCircle.otherCircleIds = addressCircle.otherCircleIds.unique()
			addressCircle.save(flush: true, failOnError: true)
			
			return "${circlename} updated"
		}
		
		return ""
	}
}
