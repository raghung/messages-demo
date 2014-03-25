package com.messages

class AddressBook {
	/**
	 * MongoDb mapping
	 */
	static mapWith = "mongo"
	
	String id
	Long userId
	List contactIds = []
	List circleIds = []
	
	static constraints = {
		userId unique: true
    }

}
