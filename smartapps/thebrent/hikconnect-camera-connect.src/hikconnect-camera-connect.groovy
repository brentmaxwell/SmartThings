/**
 *  HikConnect Camera Connect
 *
 *  Copyright 2019 Brent Maxwell
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
    name: "HikConnect Camera Connect",
    namespace: "thebrent",
    author: "Brent Maxwell",
    description: "HikVision Camera Connect",
    category: "SmartThings Labs",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png") {
}


preferences {
	section("Title") {
    	input "label", "text", title: "Name", required: true
		input "address", "text", title: "Server address", required: true, description:"IP:Port. ex)192.168.0.100:80"
        input "username", "text", title: "Username", required: true
        input "password", "password", title: "Password", required: true
	}
}

def installed() {
	addChildDevice("thebrent", "HikConnect Camera", address, null, [
      "label": name,
      "data": [
      	"address": address,
      	"username": username,
      	"password": password
      ]
    ])
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
}

// TODO: implement event handlers