/**
 *  IP Address
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
 */
metadata {
	definition (name: "IP Address", namespace: "thebrent", author: "Brent Maxwell", cstHandler: true) {
		capability "Execute"
    capability "Polling"

    attribute "ipAddress", "String"
    attribute "lastUpdated", "String"
    
    command "getIpAddress"
	}

  simulator {
		// TODO: define status and reply messages here
	}

	tiles {
    valueTile("ipAddress", "device.ipAddress", inactiveLabel: false, decoration: "flat", columns:2) {
      state "default", label:'${currentValue}', unit:"Public IP"
    }
    standardTile("updateIpAddress", "device.ipAddress", width: 4, height: 2, decoration: "flat") {
      state "default", label: "${currentValue}", action: "getIpAddress", icon: "st.Home.home30", backgroundColor: "#ffffff"
    }
    main(["ipAddress"])
    details(["updateIpAddress"])
	}
}

def ipApiUrl() { "https://api.myip.com" }

def poll() {
	updateDevice()
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'data' attribute
}

// handle commands
def execute() {
	log.debug "Executing 'execute'"
	// TODO: handle 'execute' command
}

def getIpAddress() {
  def json;
  httpGet(ipApiUrl()) { response ->
    log.debug "Received ${response.data} from server."
    json = new groovy.json.JsonSlurper().parse(response.data)
  }
  log.debug "Ip Address is ${json.ip}"
  return json.ip
}

def updateDevice() {
  def ipAddress = getIpAddress();
  def currentTimeStamp = new Date();
  sendEvent(name: "ipAddress", value: location, isStateChange: true)
  sendEvent(name: "lastUpdated", value: currentTimeStamp, isStateChange: true)
}
