package com.messages

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.messages.image.ImageProcessing;

/**
 * Service for the Thread messages
 * @author Raghu Gorur
 */
class ThreadMessageService {
	
	def grailsApplication
	
    /**
     * Send a message from an user to another
     * @param fromId Id of the user that send the message
     * @param toId Id of the user that receives the message
     * @param fromName Name the user that send the message, for sorting purposes
     * @param toName Name of the user that receives the message, for sorting purposes
     * @param text The text of the message
     * @param subject The subject of the message
     * @param Attached multipart file
     * @param messageType Type of message like custom, refer patient, follow up, practice group etc
     * @param forwardMsg Forwarding message
     * @param grpUserIds Users in the group chat 
     * @return a Message
     */
    Message sendThreadMessage(long fromId, long toId, String fromName, String toName, MessageArtifact msgArtifact) {
        def reply = false
        def subject = msgArtifact.subject?.trim()
		def text = msgArtifact.text?.trim()
		def file = msgArtifact.file
		def grpUserIds = msgArtifact.grpUserIds
		def forwardMsg = msgArtifact.forwardMsg
		def messageType = msgArtifact.messageType
		def priorityLevel = msgArtifact.priorityLevel

        Message m = new Message(fromId:fromId, toId: toId, fromName:fromName, toName:toName, text: text, last:true, lastOnThread:true, subject: subject)

        if (subject) {
            //Find messages between those users with same subject
            def messagesOnThread = findAllMessagesOnThread(m)
            if (messagesOnThread) {
                m.reply = true
                m.numberOfMessagesOnThread = messagesOnThread.size() +1
            }
			// Attachments
			if (!file.empty) {
				m.fileType = file.contentType
				m.fileName = file.originalFilename
				def newFileName = fromId + "_" + m.dateCreated.getTime() + "_" + m.fileName
				m.storePath = grailsApplication.config.attchmentUploadFolder + newFileName
				
				def okcontents = grailsApplication.config.imageOkContents
				if (okcontents.contains(file.contentType) && file.size > grailsApplication.config.maxAttachFileSize) {
					def tempFile = new File(grailsApplication.config.tempAttachmentFolder + newFileName)
					file.transferTo(tempFile)
					ImageProcessing.reduceImageQuality(grailsApplication.config.maxAttachFileSize, grailsApplication.config.tempAttachmentFolder + newFileName, m.storePath, m.fileType)
					tempFile.delete()					
					
				} else { 
					file.transferTo(new File(m.storePath))
				}
				 
			}
			// Group Msg and communication is between more than 2 people
			if (grpUserIds && grpUserIds.size() > 2) {
				m.isGroupMessage = true
				for (userId in grpUserIds) {
					m.groupMessageUser += userId
				}
				if (m.subject.indexOf("Grp") != 0) { // New Group message
					m.subject = "Grp: " + m.subject
				}
			}
			// Forward Msg
			if (forwardMsg) {
				if (m.forwardMessage == null) {
					m.forwardMessage = []
				}
				for (msg in forwardMsg) {
					m.forwardMessage += msg.id
				}
				if (!m.isGroupMessage) {
					m.subject = "Fwd: " + m.subject
				}
			}
			// Set message Type
			if (messageType) {
				m.messageType = messageType
			} else {
				m.messageType("custom")
			}
			// Set priority Level(1: High, 2: Medium, 3: Low)
			if (priorityLevel < 1) {
				m.priorityLevel = 3 // Low priority
			} else {
				m.priorityLevel = priorityLevel
			}
            if (m.save()){
                messagesOnThread.each{
                     it.numberOfMessagesOnThread = messagesOnThread.size() +1
                     it.lastOnThread = false
                     it.save()
                 }
				
                return m
            }
        }
        return null
    }


    /**
     * Get a list of the messages by the user, grouping by thread, that is, 'last' messages of every thread.
     * Those messages could be sent or received messages
     * @param id Id of the user
     * @param offset Number of messages to skip (for pagination)
     * @param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
     * @param sort Field to order by. Can be one of 'fromId', 'toId', 'subject' or 'dateCreated'
     * @param order 'asc' or 'desc' for ascendig or descending order.
     * @return a map with: messages (the list of Messages), totalNum (the total num of messages, for the pagination), unreadedNum (the number of unreaded messages)
     */
    Map getAllByThread(long id, int offset = 0, int itemsByPage = -1, String sort='dateCreated', String order='asc'){
        def result = [:]
        def resultMessages = getThreads (id, true, true)
        result.totalNum = resultMessages.size()
        result.unreadedNum = resultMessages.count{ it.readed == false }
        result.messages = messagesSortAndPagination (resultMessages, offset, itemsByPage, sort, order)
        return result
    }

    /**
     * Get a list of the messages received by the user, grouping by thread, that is, 'last' received messages of every thread.
     * @param id Id of the user
     * @param offset Number of messages to skip (for pagination)
     * @param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
     * @param sort Field to order by. Can be one of 'fromId', 'toId', 'subject' or 'dateCreated'
     * @param order 'asc' or 'desc' for ascendig or descending order.
     * @return a map with: messages (the list of Messages), totalNum (the total num of messages, for the pagination), unreadedNum (the number of unreaded messages)
     */
    Map getReceivedByThread(long id, int offset = 0, int itemsByPage = -1, String sort='dateCreated', String order='asc'){
        def result = [:]
        def resultMessages = getThreads (id, true, false)
        result.totalNum = resultMessages.size()
        result.unreadedNum = resultMessages.count{ it.readed == false }
        result.messages = messagesSortAndPagination (resultMessages, offset, itemsByPage, sort, order)
        return result
    }

    /**
     * Get a list of the messages sent by the user, grouping by thread, that is, 'last' sent messages of every thread.
     * @param id Id of the user
     * @param offset Number of messages to skip (for pagination)
     * @param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
     * @param sort Field to order by. Can be one of 'fromId', 'toId', 'subject' or 'dateCreated'
     * @param order 'asc' or 'desc' for ascendig or descending order.
     * @return a map with: messages (the list of Messages), totalNum (the total num of messages, for the pagination)
     */
    Map getSentByThread(long id, int offset = 0, int itemsByPage = -1, String sort='dateCreated', String order='asc'){
        def result = [:]
        def resultMessages = getThreads (id, false, true)
        result.totalNum = resultMessages.size()
        result.messages = messagesSortAndPagination (resultMessages, offset, itemsByPage, sort, order)
        return result
    }

    /**
     * Find all the messages on this thread (between those same users with the same subject)
     * @param message any of the messages on the thread
     * @return a list of Messages
     */
     List<Message> findAllMessagesOnThread(Message message, String orderby='asc'){
         return Message.createCriteria().list{
             or{
                 and {
                     eq 'fromId', message.fromId
                     eq 'toId', message.toId
                 }
                 and {
                     eq 'fromId', message.toId
                     eq 'toId', message.fromId
                 }
             }
             eq 'subject', message.subject
			 order 'dateCreated', orderby
         }
		 
     }
	 
	 /**
	  * Find all the group messages on this thread (between those group users with the same subject)
	  * @param message any of the messages on the thread
	  * @return a list of Messages
	  */
	  List<Message> findAllGroupMessagesOnThread(Message message, String orderby='asc'){
		  return Message.createCriteria().list{
			  eq 'subject', message.subject
			  order 'dateCreated', orderby
		  }
		  
	  }

     /**
      * Delete messages on a thread from the point of view of an user.
      * On a thread between Alice and Bob, if Alice delete the thread, it is only deleted
      * from Alice's point of view. From Bob's point of view the thread isn't deleted.
      * @param userId the id of the user that wants to delete the thread
      * @param message any of the messages on the thread
      */
      void deleteMessagesOnThread(long userId, Message message){
          def messages = findAllMessagesOnThread(message)
          messages.each {
              if (it.fromId == userId) {
                  it.fromDeletedOnThread = true
                  it.save()
              } else if (it.toId == userId) {
                  it.toDeletedOnThread = true
                  it.save()
              }
          }
      }




    ///////////////////////////
    // Private methods
    ///////////////////////////



    /**
     * This method is intended to be used only privately
     * Get a list of the messages sent or received by the user, grouping by subject, that is, 'last' messages of every subject.
     * @param id Id of the user
     * @param offset Number of messages to skip (for pagination)
     * @param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
     * @param sort Field to order by. Can be one of 'fromId', 'toId', 'subject' or 'dateCreated'
     * @param order 'asc' or 'desc' for ascending or descending order.
     * @return a list of Messages
     */
    List<Message> getThreads(long id, boolean received, boolean sent){
        def resultMessages = []
        def messages = []

        if (received && sent) {
            messages = Message.createCriteria().list{
                or{
                    and {
                        eq 'fromId', id
                        eq 'fromDeletedOnThread', false
                    }
                    and {
                        eq 'toId', id
                        eq 'toDeletedOnThread', false
                    }
                }	
            }
        } else if (received) {
            messages = Message.findAllByToIdAndToDeletedOnThread(id, false)
        } else if (sent) {
            messages = Message.findAllByFromIdAndFromDeletedOnThread(id, false)
        }

        while (messages) {
            def message = messages[0]
            def subjectGroup
            if (received && sent) {
				if (message.isGroupMessage) {
					subjectGroup = messages.findAll{it.subject == message.subject}.sort{it.dateCreated}
				} else {
	                subjectGroup = messages.findAll{
	                    it.subject == message.subject &&
	                    ((it.fromId == message.fromId && it.toId == message.toId) ||
	                    (it.fromId == message.toId && it.toId == message.fromId))
	                }.sort{it.dateCreated}
				}
            } else if (received) {
                subjectGroup = messages.findAll{it.fromId == message.fromId && it.subject == message.subject}.sort{it.dateCreated}
            } else {
                subjectGroup = messages.findAll{it.toId == message.toId && it.subject == message.subject}.sort{it.dateCreated}
            }
            resultMessages << subjectGroup.last()
            messages = messages - subjectGroup
        }

        return resultMessages

    }



    /**
     * This method is intended to be used only privately
     * Sorts and paginates in memory a list of messages
     * @param messages the list of messages to sort and paginate
     * @param offset Number of messages to skip (for pagination)
     * @param itemsByPage Number of messages to return (for pagination). -1 will return all messages.
     * @param sort Field to order by. Can be one of 'fromId', 'toId', 'subject' or 'dateCreated'
     * @param order 'asc' or 'desc' for ascending or descending order.
     * @return a list of Messages
     **/
    List<Message> messagesSortAndPagination(List<Message> messages, int offset = 0, int itemsByPage = -1, String sort='dateCreated', String order='asc'){

        //Sort
        if (sort == 'fromId') {
            messages = messages.sort{it.fromId}
        } else if (sort == 'toId') {
            messages = messages.sort{it.toId}
        } else if (sort == 'fromName') {
            messages = messages.sort{it.fromName}
        } else if (sort == 'toName') {
            messages = messages.sort{it.toName}
        } else if (sort == 'subject') {
            messages = messages.sort{it.subject}
        } else if (sort == 'dateCreated') {
            messages = messages.sort{it.dateCreated}
        } else {
            //If the user hadn't ask for a sort, make a special sort with the unreaded messages first
            messages = messages.sort{a,b->
                if (a.readed) {
                    if (b.readed) {
                        return a.dateCreated < b.dateCreated?-1:1
                    } else {
                        return -1
                    }
                } else {
                    if (b.readed) {
                        return 1
                    } else {
                        return a.dateCreated < b.dateCreated?-1:1
                    }
                }
            }
        }


        if (order == 'desc') {
            messages = messages.reverse()
        }

        //Pagination
        if (itemsByPage != -1) {
            if (offset < messages.size()) {
                def max = Math.min(offset+itemsByPage, messages.size()) - 1

                messages = messages[offset..max]
            } else {
                messages = []
            }
        }

        return messages

    }
}
