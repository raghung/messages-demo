package com.messages

import grails.plugin.springsecurity.annotation.Secured;

@Secured(['ROLE_USER', 'ROLE_DOCTOR', 'ROLE_STAFF'])
class ToDoController {
	transient springSecurityService
	final int INCOMPLETE = 1
	final int COMPLETE = 2

	def tasks() {
		def currentUser = springSecurityService.currentUser
		def showLevel = params.showLevel? Integer.parseInt(params.showLevel) : INCOMPLETE
		def tasks
		if (showLevel == INCOMPLETE) {
			tasks = ToDo.findAllNotCompletedByUserId(currentUser.id, [sort: "dueDate", order: "asc"])
		} else if (showLevel == COMPLETE) {
			tasks = ToDo.findAllCompletedByUserId(currentUser.id, [sort: "dueDate", order: "asc"])
		} else {
			tasks = ToDo.findAllByUserId(currentUser.id, [sort: "dueDate", order: "asc"])
		}
		
		render view:"toDoList", model:[user: currentUser, tasks: tasks, showLevel: showLevel	]
	}
	
	def addTask() {
		def currentUser = springSecurityService.currentUser
		
		def subject = params.newSubject
		def dueDate = params.newDueDate
		def messageId = params.messageId? params.messageId : ""
		
		def toDo = new ToDo(userId: currentUser.id, subject: subject, messageId: messageId, dueDate: dueDate)
		try {
			toDo.save(flush:true, failOnError: true)
			flash.message = "Task added to list"
		} catch(Exception e) {
			flash.message = "Error saving Task"
		}
		
		redirect action:"tasks"
	}
	
	def removeTask() {
		def currentUser = springSecurityService.currentUser
		
		def taskId = params.taskId
		def toDo = ToDo.get(taskId)
		try {
			toDo.delete(flush:true)
			flash.message = "Task removed from the list"
		} catch(Exception e) {
			flash.message = "Error removing the Task"
		}

		redirect action:"tasks"
	}
	
	def completeTask() {
		def currentUser = springSecurityService.currentUser
		
		def taskId = params.taskId
		def completed = params.value == "true"? true : false
		def toDo = ToDo.get(taskId)
		try {
			toDo.completed = completed
			toDo.dateUpdated = new Date()
			toDo.save(flush:true, failOnError: true)
			flash.message = "Task updated"
		} catch(Exception e) {
			flash.message = "Error updating the Task"
		}

		redirect action:"tasks"
	}
	
	def updateTask() {
		def currentUser = springSecurityService.currentUser
		
		try {
			def index = Integer.parseInt(params.updateIndex)
			def totalTasks = Integer.parseInt(params.totalTasks)
			
			def taskId = params.taskId[index]
			def subject = params.subject[index]
			def year = Integer.parseInt(params.dueDate_year[index])
			def month = Integer.parseInt(params.dueDate_month[index])
			def day = Integer.parseInt(params.dueDate_day[index])
			def hour = Integer.parseInt(params.dueDate_hour[index])
			def minute = Integer.parseInt(params.dueDate_minute[index])
			
			if (totalTasks == 1) {
				taskId = params.taskId
				subject = params.subject
				year = Integer.parseInt(params.dueDate_year)
				month = Integer.parseInt(params.dueDate_month)
				day = Integer.parseInt(params.dueDate_day)
				hour = Integer.parseInt(params.dueDate_hour)
				minute = Integer.parseInt(params.dueDate_minute)
			}
			
			def date = new GregorianCalendar(year, month-1, day, hour, minute).getTime()
		
			def toDo = ToDo.get(taskId)
			if (toDo) {
				toDo.subject = subject
				toDo.dueDate = date
				toDo.dateUpdated = new Date() 
				toDo.save(flush:true, failOnError: true)
			}
			
			flash.message = "Task updated"
		} catch(Exception e) {
			flash.message = "Error updating Task"
		}

		redirect action:"tasks"
	}
	
}
