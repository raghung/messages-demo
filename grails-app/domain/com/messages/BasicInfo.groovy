package com.messages

class BasicInfo {

	static mapWith = "mongo"
	
	String id
	Long userId
    
	List physicianIds
	List patientIds
	List professionalIds 
	
	static constraints = {
		userId unique: true, blank: false
    }
}
