package com.messages

/**
 * Address Book containing contacts for a user
 * Ids are used instead of objects, in order have independent plugin from the project
 * @author Raghu Gorur
 */
class AddressBook {
	/**
	 * MongoDb mapping
	 */
	static mapWith = "mongo"
	
	/**
	 * Id generated for Address Book
	 */
	String id
	
	/**
	 * Address book belonging to the User
	 */
	Long userId
	
	/**
	 * Contacts list
	 */
	List contactIds = []
	
	/**
	 * Circles list containing contacts 
	 */
	List circleIds = []
	
	static constraints = {
		userId unique: true
    }

	static mapping = {
		userId index:true
	}
}
