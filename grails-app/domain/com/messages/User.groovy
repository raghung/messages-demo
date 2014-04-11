package com.messages

class User {

	transient springSecurityService

	/**
	 * MongoDb mapping
	 */
	static mapWith = "mongo"
	
	String username
	String password
	String firstname
	String lastname
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired
	
	static transients = ['springSecurityService']

	static constraints = {
		username email: true, blank: false, unique: true
		password blank: false
	}

	static mapping = {
		password column: '`password`'
		compoundIndex firstname:1, lastname:1
	}
	
	String toString() {
		return "${firstname} ${lastname}"
	}

	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role } as Set
	}
	
	static boolean isDoctor(User user) {
		def role = Role.findByAuthority("ROLE_DOCTOR")
		if (role && UserRole.get(user.id, role.id))
			return true
		return false
	}
	static boolean isPatient(User user) {
		def role = Role.findByAuthority("ROLE_USER")
		if (role && UserRole.get(user.id, role.id))
			return true
		return false
	}
	BasicInfo getBasicInfo() {
		BasicInfo.findByUserId(this.id)
	}
	
	Set<User> getPhysicians() {
		def basicInfo = getBasicInfo()
		if (basicInfo.physicianIds)
			return User.findAllByIdInList(getBasicInfo.physicianIds) as Set
		return null
	}
	
	Set<User> getPatients() {
		def basicInfo = getBasicInfo()
		if (basicInfo.patientIds)
			return User.findAllByIdInList(getBasicInfo.patientIds) as Set
		return null
	}
	
	Set<User> getProfessionals() {
		def basicInfo = getBasicInfo()
		if (basicInfo.professionalIds)
			return User.findAllByIdInList(getBasicInfo.professionalIds) as Set
		return null
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService.encodePassword(password)
	}
}
