package com.messages

class AddressCircle {
	/**
	 * MongoDb mapping
	 */
	static mapWith = "mongo"
	
	String id
	Long userId
	String circlename
	List<Long> contacts
	List<Long> otherCircles
	
    static constraints = {
		userId unique: true
		circlename blank: false, unique: true
    }
	
	String toString() {
		return circlename
	}
}
