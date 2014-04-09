package com.messages

import java.util.Date;

/**
 * Direct message sent between two users
 * Uses ids of the user instead of objects, in order have independent plugin from the project user system
 * @author Raghu Gorur
 */
class Message {
	
	static final MAX_FILE_SIZE = 1024 * 1024 * 4//4194304 - 4 MB
	
	/**
	 * MongoDb mapping
	 */
	static mapWith = "mongo"
	
	/**
	 *  Elastic search mapping
	 */
	static searchable = true/*{
		only = ['fromName', 'toName', 'text', 'subject', 'fileName']
	}*/
	
	/**
	 * Generated id for message
	 */
	String id
	
    /**
     * Id of the user that generates the message
     */
    Long fromId

    /**
     * Id of the user that receives the message
     */
    Long toId

    /**
     * Message text
     */
    String text

    /**
     * Is this the last message between those users?
     */
    Boolean last

    /**
     * The message has been readed
     */
    Boolean readed = false

    /**
     * Date when the message was created
     */
    Date dateCreated = new Date()


    ///////////////////////////////////////////
    // Fields for threaded messages
    ///////////////////////////////////////////

    /**
     * Subject of the message. Optional, only for "mail" type messages
     */
    String subject

    /**
     * This message is a reply. Useful for "mail" type messages
     */
    Boolean reply = false

    /**
     * Is this the last message between those users on this subject?. Useful for "mail" type messages
     */
    Boolean lastOnThread = false

    /**
     * The number of messages between those same users
     * With the same subject
     */
    Integer numberOfMessagesOnThread = 1

    /**
     * Logical deletion. This message becomes invisible for the 'from' user
     */
    Boolean fromDeletedOnThread = false

    /**
     * Logical deletion. This message becomes invisible for the 'to' user
     */
    Boolean toDeletedOnThread = false

    /**
     * Name of the user that generates the message, for sorting
     */
     String fromName = ''

     /**
     * Name of the user that receives the message, for sorting
     */
     String toName = ''

	 /**
	  * Name of the other user in conversation with logged in user
	  */
	 String otherName = ''
	 
	 /**
	  * File name uploaded with message
	  */
	 String fileName
	 
	 /**
	  * File type uploaded with message
	  */
	 String fileType
	 
	 /**
	  * Folder path where file is stored
	  */
	 String storePath
	 
	 /**
	  * Map containing forwarded messages 
	  */
	 List forwardMessage = []
	 
	 /**
	  * Indicated Group message
	  */
	 Boolean isGroupMessage = false
	 
	 /**
	  * Users in a Group message
	  */
	 List groupMessageUser = []
	 
	 /**
	  * Message Type
	  */
	 String messageType
	 
	 /**
	  * Priority level
	  */
	 Integer priorityLevel = 0
	  
	 /**
	  * Id of the user that message is assigned(Staff to Physician or Vice versa)
	  */
	 //Long assignToId
	 
	 /**
	  * Id of the user sending the message(Staff on behalf of Physician)
	  */
	 //Long sentById
	 
	 //static embedded = ['forwardMessage']
	 
    static constraints = {
        subject nullable: true, blank: true
        reply nullable: true
        lastOnThread nullable: true
        numberOfMessagesOnThread nullable: true
        fromDeletedOnThread nullable: true
        toDeletedOnThread nullable: true
        fromName nullable: true, blank: true
        toName nullable: true, blank: true
		otherName nullable: true, blank: true
		fileName nullable: true, blank: true
		fileType nullable:true, blank: true
		storePath nullable:true, blank: true 
    }

    static mapping = {
        table "directmessages_message"
        text type:"text"
        subject type:"text", index: 'directmessages_message_subject_idx'
    }

    boolean isDeletedForUser(long idUser){
        return ((this.fromId == idUser && this.fromDeletedOnThread) || (this.toId == idUser && this.toDeletedOnThread))
    }

	String toString() {
		return "${fromName} to ${toName} at ${dateCreated}"
	}

}
