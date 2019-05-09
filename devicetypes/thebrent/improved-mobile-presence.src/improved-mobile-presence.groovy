/**
 *  Improved Mobile Presence
 *
 *  Copyright 2018 John Callahan
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
 
metadata {
	definition (name: "Advanced Mobile Presence", namespace: "thebrent", author: "John Callahan") {
		capability "Presence Sensor"
    capability "Occupancy Sensor"
    capability "Sensor"
    capability "Battery"
    capability "Power Source"
        
    attribute "currentLocation", "String"
    attribute "previousLocation", "String"
        
    command "checkPresence", ["boolean"]
    command "setPresence",["boolean","string"]
    command "setBattery",["number","boolean"]
  }

	tiles {
    standardTile("presence", "device.presence", width: 2, height: 2, canChangeBackground: true) {
      state "present", labelIcon:"st.presence.tile.present", backgroundColor:"#00a0dc"
      state "not present", labelIcon:"st.presence.tile.not-present", backgroundColor:"#ffffff"
    }
    valueTile("location", "device.currentLocation", width: 2, height: 1) {
      state "Home", label: '${currentValue}', backgroundColor:"#00a0dc"
      state "default", label: '${currentValue}', backgroundColor:"#ffffff"
		}
    valueTile("battery", "device.battery", width: 1, height: 1){
      state "default", label: '${currentValue}%'
    }
    valueTile("charging", "device.powerSource", width: 1, height: 1){
      state "battery", label: 'Not Charging'
      state "dc", label: 'Charging', backgroundColor:"#00a0dc"
      state "default", label: '${currentValue}'
    }
    main "location"
    details(["presence", "location", "battery", "charging"])
  }
    
  preferences {
    input "timeout", "number", title: "Presence timeout", description: "Time before leaving a location is reported (minutes)"
  }
}

// parse events into attributes
def parse(String description) {
	def value = parseValue(description)

	switch(value) {
		case "not present":
      if (device.currentState("occupancy") != "unoccupied") sendEvent(generateEvent("occupancy: 0"))
      break
    case "occupied":
      if (device.currentState("presence") != "present") sendEvent(generateEvent("presence: 1"))
      break
	}

  sendEvent(generateEvent(description))
}

private generateEvent(String description) {
	log.debug "description: $description"
	def value = parseValue(description)
	def name = parseName(description)
	def linkText = getLinkText(device)
	def descriptionText = parseDescriptionText(linkText, value, description)
	def handlerName = getState(value)
	def isStateChange = isStateChange(device, name, value)

	def results = [
    translatable: true,
		name: name,
		value: value,
		unit: null,
		linkText: linkText,
		descriptionText: descriptionText,
		handlerName: handlerName,
		isStateChange: isStateChange,
		displayed: displayed(description, isStateChange)
	]
	log.debug "GenerateEvent returned $results.descriptionText"

	return results
}

private String parseName(String description) {
	if (description?.startsWith("presence: ")) {
		return "presence"
	} else if (description?.startsWith("occupancy: ")) {
		return "occupancy"
	}
	null
}

private String parseValue(String description) {
	switch(description) {
		case "presence: 1": return "present"
		case "presence: 0": return "not present"
		case "occupancy: 1": return "occupied"
		case "occupancy: 0": return "unoccupied"
		default: return description
	}
}

private parseDescriptionText(String linkText, String value, String description) {
	switch(value) {
		case "present": return "{{ linkText }} has arrived"
		case "not present": return "{{ linkText }} has left"
		case "occupied": return "{{ linkText }} is inside"
		case "unoccupied": return "{{ linkText }} is away"
		default: return value
	}
}

private getState(String value) {
	switch(value) {
		case "present": return "arrived"
		case "not present": return "left"
		case "occupied": return "inside"
		case "unoccupied": return "away"
		default: return value
	}
}

def installed() {
	configure()
}

def updated() {
	configure()
}

def configure() {
	log.debug "Running config with settings: ${settings}"
	runEvery15Minutes(checkPresence);
    checkPresence(false);
}

def checkPresence(){
	checkPresence(false);
}

// Used after timeout is complete
def forceUpdate(){
	checkPresence(true);
}

def checkPresence(boolean force){
	log.debug "Checking presence"
    def params = [
    	uri: "https://st.callahtech.com/detailedpresence?id=${device.getDeviceNetworkId()}"
	]
	try {
    httpGet(params) { resp ->
      log.debug "Recieved ${resp.data} from server"
      log.debug "Current Location: " + device.currentValue("currentLocation");
      log.debug "Previous Location: " + device.currentValue("previousLocation");
      log.debug "response data: ${resp.data}"
      if(resp.data.error) {
        log.error('Error checking presence - ' + resp.data.message);
      } 
      else {
        setPresence(resp.data.present, resp.data.location, force);
        if(resp.data.battery && resp.data.charging != null) {
          setBattery(resp.data.battery, resp.data.charging);
        }
      }
    }
	} catch (e) {
	    log.error "Error getting presence: $e"
	}
}

def setBattery(int percent, boolean charging){
	if(percent != device.currentValue("battery")) {
		sendEvent(name: "battery", value: percent, isStateChange: true, displayed: false);
  }
  if(charging != device.currentValue("charging")) {
    sendEvent(name: "powerSource", value: (charging ? "dc":"battery"), isStateChange: true, displayed: false);
  }
}

def setPresence(boolean present, String location){
	setPresence(present, location, false);
}

def setPresence(boolean present, String location, boolean force) {
	log.debug "setPresence(" + present + ")"
  if(location != device.currentValue("currentLocation") || (present ? "present":"not present") != device.currentValue("presence")) {
    if(timeout && timeout > 0 && location == "Away" && !force) {
      log.debug("Delaying update by ${timeout} minute(s)");
      // Schedule update for time defined in timeout
      runIn(timeout * 60, forceUpdate);
      return;
    } else if(timeout && timeout > 0) {
      // Ensure there are no pending events if a timeout is set
      unschedule();
    }
		if(present) {
      sendEvent(displayed: true,  isStateChange: true, name: "presence", value: "present", descriptionText: "$device.displayName has arrived at " + location)
    } else {
      if(location == "Away") {
        sendEvent(displayed: true,  isStateChange: true, name: "presence", value: "not present", descriptionText: "$device.displayName has left " + device.currentValue("currentLocation"))
      } else {
          sendEvent(displayed: true,  isStateChange: true, name: "presence", value: "not present", descriptionText: "$device.displayName has arrived at " + location)
      }
    }
    setPresenceLocation(location);
    log.debug "Presence set"
	}
}

def setPresenceLocation(String location){
	if(location != device.currentValue("currentLocation")) {
		log.debug "Setting location to: " + location
		sendEvent(name: "previousLocation", value: device.currentValue("currentLocation"), isStateChange: true, displayed: false)
		sendEvent(name: "currentLocation", value: location, isStateChange: true, displayed: false)
		log.debug "Current location: " + device.currentValue("currentLoaction")
		log.debug "Previous location: " + device.currentValue("previousLocation")
  }
}