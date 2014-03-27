package com.messages

class User {

	transient springSecurityService

	/**
	 * MongoDb mapping
	 */
	static mapWith = "mongo"
	
	/**
	 *  Elastic search mapping
	 */
	static searchable = {
		only = ['firstname', 'lastname']
	}
	
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
