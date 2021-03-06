/**
 *  GeTHR
 *
 *  Copyright 2014 Darshak Thakore
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "GeTHR",
    namespace: "com.cablelabs.smartthings",
    author: "Darshak Thakore",
    description: "This app tracks various huddle rooms and conference rooms and updates the system when they become empty or occupied",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    oauth: true
)


preferences {
	section("Title") {
		// TODO: put inputs here
	}
    section("When movement is detected...") {
		input "motionSensor", "capability.motionSensor", title: "Pick the room"
	}
    section("Turn on a light...") {
		input "switch1", "capability.switch"
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
    subscribe(motionSensor, "motion.inactive", roomAvailableHandler)
    subscribe(motionSensor, "motion.active", roomOccupiedHandler)
}


// TODO: implement event handlers

def roomAvailableHandler(evt) {
	log.debug "Event = $evt"
    switch1.on()
    
    // The CableLabs Web Service to receive room status
	def params = [
        uri: "http://cablelabs.ws:8383",
        path: "/rooms/",
        headers: ['Cache-Control': 'no-cache', 'Content-Type': 'application/x-www-form-urlencoded'],
        body: [id: '203', s: 'available']
	]
    
    httpPost(params) {response ->
    
    	if(method != null) {
        	api(method, args, success)
      	}
        return result
    }
}

def roomOccupiedHandler(evt) {
	log.debug "Event = $evt"
    switch1.off()
    
    // The CableLabs Web Service to receive room status
	def params = [
        uri: "http://cablelabs.ws:8383",
        path: "/rooms/",
        headers: ['Cache-Control': 'no-cache', 'Content-Type': 'application/x-www-form-urlencoded'],
        body: ['id': '203', 's': 'occupied']
	]
    
    httpPost(params) {response ->
    
    	if(method != null) {
        	api(method, args, success)
      	}
        return result
    }
}