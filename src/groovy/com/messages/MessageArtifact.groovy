/**
 * 
 */
package com.messages

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Raghu Gorur
 *
 */
class MessageArtifact {

	String messageType
	
	Integer priorityLevel
	
	String text 
	
	String subject
	
	MultipartFile file
	
	List forwardMsg = []
	
	List grpUserIds = []
}
