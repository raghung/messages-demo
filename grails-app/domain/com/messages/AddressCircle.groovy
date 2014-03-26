package com.messages

/**
 * Address Circle containing contacts for a user
 * Ids are used instead of objects, in order have independent plugin from the project
 * @author Raghu Gorur
 */
class AddressCircle {
	/**
	 * MongoDb mapping
	 */
	static mapWith = "mongo"
	
	/**
	 * Id generated for Address Circle
	 */
	String id
	
	/**
	 * Circle belonging to the user
	 */
	Long userId
	
	/**
	 * Name of the circle containing contacts
	 */
	String circlename
	
	/**
	 * Contacts in the circles
	 */
	List contactIds = []
	
	/**
	 * Circles included in the overall circle
	 */
	List otherCircleIds = []
	
    static constraints = {
		userId blank:false
		circlename blank: false
    }
	
	static mapping = {
		compoundIndex userId:1, circlename:1
	}
	
	String toString() {
		return circlename
	}
}
