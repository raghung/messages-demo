package com.messages

class AddressCircle {
	/**
	 * MongoDb mapping
	 */
	static mapWith = "mongo"
	
	String id
	Long userId
	String circlename
	List contactIds = []
	List otherCircleIds = []
	
    static constraints = {
		userId blank:false
		circlename blank: false
    }
	
	static mapping = {
		circlename index:true
	}
	
	String toString() {
		return circlename
	}
}
