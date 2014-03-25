class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		name inbox: "/" {controller = 'message'; action = 'inbox'}
		name view: "/view" {controller = 'message'; action = 'view'}
		name newMessage: "/newMessage" {controller = 'message'; action = [POST:'saveNewMessage', GET:'newMessage']}
		name addressBook: "/addressBook" {controller = 'addressBook'; action = 'book'}
		"500"(view:'/error')
	}
}
