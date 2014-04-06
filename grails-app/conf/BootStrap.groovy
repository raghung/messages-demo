import com.messages.AddressBook;
import com.messages.AddressCircle;
import com.messages.BasicInfo;
import com.messages.Role;
import com.messages.User;
import com.messages.UserRole;

class BootStrap {
	def mailMessagingService

    def init = { servletContext ->
		
		def draruna = User.findByFirstname("Dr. Aruna")?:new User(firstname:'Dr. Aruna', lastname:'Ramayya', username:'draruna@onehaystack.com', password:'draruna', enabled:true).save(flush: true)
		def doctorRole = Role.findByAuthority("ROLE_DOCTOR")?: new Role(authority: 'ROLE_DOCTOR').save(flush: true)
		//UserRole.create(draruna, doctorRole, true)
		
		if (draruna) {
			new BasicInfo(userId: draruna.id, physicianIds: ["1", "3"], patientIds: ["1", "2", "4", "5"], professionalIds: ["3"]).save(flush: true)
		}
		
		/*def raghu = new User(firstname:'Raghu', lastname:'Gorur', username:'raghu@onehaystack.com', password:'raghu', enabled:true).save(flush: true)
		def sundar = new User(firstname:'Sundar', lastname:'Ramayya', username:'sundar@onehaystack.com', password:'sundar', enabled:true).save(flush: true)
		def dilip = new User(firstname:'Dr. Dilip', lastname:'Parekh', username:'drdilip@onehaystack.com', password:'drdilip', enabled:true).save(flush: true)
		def drraghu = new User(firstname:'Dr. Raghu', lastname:'', username:'drraghu@onehaystack.com', password:'drraghu', enabled:true).save(flush: true)
		def yamini = new User(firstname:'Yamini', lastname:'Suvarna', username:'yamini@onehaystack.com', password:'yamini', enabled:true).save(flush: true)
		
		def userRole = Role.findByAuthority("ROLE_USER")?: new Role(authority: 'ROLE_USER').save(flush: true)
		def doctorRole = Role.findByAuthority("ROLE_DOCTOR")?: new Role(authority: 'ROLE_DOCTOR').save(flush: true)
		def staffRole = Role.findByAuthority("ROLE_STAFF")?: new Role(authority: 'ROLE_STAFF').save(flush: true)
		def adminRole = Role.findByAuthority("ROLE_ADMIN")?: new Role(authority: 'ROLE_ADMIN').save(flush: true)
		
		UserRole.create(raghu, userRole, true)
		UserRole.create(sundar, userRole, true)
		UserRole.create(dilip, doctorRole, true)
		UserRole.create(drraghu, doctorRole, true)
		UserRole.create(yamini, userRole, true)*/
		
		/*def message = mailMessagingService.sendMessage(raghu, sundar, "Hello Sundar", "Testing messages1", null)
		message.readed = true
		message.save();
		message = mailMessagingService.sendMessage(sundar, raghu, "Hello Raghu", "Testing messages2", null)
		message.readed = true
		message.save();
		message = mailMessagingService.sendMessage(raghu, sundar, "Hello Sundar", "Testing messages2", null)
		message.readed = true
		message.save();
		message = mailMessagingService.sendMessage(sundar, raghu, "Lets have a meeting this Sunday", "Testing messages2", null)
		message.readed = true
		message.save();
		message = mailMessagingService.sendMessage(raghu, sundar, "Sure. Please let me know the timings", "Testing messages2", null)
		message.readed = true
		message.save();*/
		
		/*assert User.count() == 5
		assert Role.count() == 2
		assert UserRole.count() == 5*/
    }
    def destroy = {
    }
}
