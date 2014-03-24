package com.messages

class AddressBook {
	/**
	 * MongoDb mapping
	 */
	static mapWith = "mongo"
	
	String id
	Long userId
	List<Long> contacts
	List<Long> circles
	
	static constraints = {
		userId unique: true
    }
	static embedded = {
		'circles'
	}
}
