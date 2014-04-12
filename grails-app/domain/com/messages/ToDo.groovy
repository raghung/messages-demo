package com.messages

import java.util.Date;

class ToDo {
	
	/**
	 * MongoDb mapping
	 */
	static mapWith = "mongo"

	String id
	
	Long userId
	
	String messageId
	
	String subject
	
	Date dueDate = new Date()
	
	Boolean completed = false
	
	Date dateCreated = new Date()
	
	Date dateUpdated = new Date()
	
    static constraints = {
		userId blank:false
		subject blank:false
		dueDate blank:false
    }
}
