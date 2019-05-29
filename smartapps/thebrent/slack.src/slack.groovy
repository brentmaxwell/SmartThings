/**
 *  Slack
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
  name: "Slack",
  namespace: "thebrent",
  author: "Brent Maxwell",
  description: "Slack integration",
  category: "Fun & Social",
  iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
  iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
  iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")
{
  appSetting "token"
}

preferences {
  page(name: "mainPage", title: "Link your devices to Slack", install: true, uninstall: true)
	page(name: "timeIntervalInput", title: "Only during a certain time") {
		section {
			input "starting", "time", title: "Starting", required: false
			input "ending", "time", title: "Ending", required: false
		}
	}
}

mappings {
  path("/devices") {
    action: [
      GET: "listDevices"
    ]
  }
  path("/device/:id") {
    action: [
      GET: "deviceDetails"
    ]
  }
  path("/device/:id/attribute/:name") {
    action: [
      GET: "deviceGetAttributeValue"
    ]
  }
  path("/device/:id/command/:name") {
    action: [
      POST: "deviceCommand"
    ]
  }
}

def installed() {
  log.debug "Installed with settings: ${settings}"
  subscribeToEvents()
  initialize()
}

def updated() {
  log.debug "Updated with settings: ${settings}"
  unsubscribe()
  unschedule()
  subscribeToEvents()
  initialize()
}

def initialize() {
  // TODO: subscribe to attributes, devices, locations, etc.
}

def listDevices() {
  def resp = []
  devices.each {
    resp << [
        id: it.id,
        label: it.label,
        manufacturerName: it.manufacturerName,
        modelName: it.modelName,
        name: it.name,
        displayName: it.displayName
    ]
  }
  return resp
}

def deviceDetails() {
  def device = getDeviceById(params.id)

  def supportedAttributes = []
  device.supportedAttributes.each {
    supportedAttributes << it.name
  }

  def supportedCommands = []
  device.supportedCommands.each {
    def arguments = []
    it.arguments.each { arg ->
      arguments << "" + arg
    }
    supportedCommands << [
        name: it.name,
        arguments: arguments
    ]
  }

  return [
      id: device.id,
      label: device.label,
      manufacturerName: device.manufacturerName,
      modelName: device.modelName,
      name: device.name,
      displayName: device.displayName,
      supportedAttributes: supportedAttributes,
      supportedCommands: supportedCommands
  ]
}

def deviceGetAttributeValue() {
  def device = getDeviceById(params.id)
  def name = params.name
  def value = device.currentValue(name);
  return [
      value: value
  ]
}

def deviceCommand() {
  def device = getDeviceById(params.id)
  def name = params.name
  def args = params.arg
  if (args == null) {
    args = []
  } else if (args instanceof String) {
    args = [args]
  }
  log.debug "device command: ${name} ${args}"
  switch(args.size) {
    case 0:
      device."$name"()
      break;
    case 1:
      device."$name"(args[0])
      break;
    case 2:
      device."$name"(args[0], args[1])
      break;
    default:
      throw new Exception("Unhandled number of args")
  }
}

def getDeviceById(id) {
  return devices.find { it.id == id }
}

def getControlToAttributeMap() {
	[
    "motion": "motion.active",
    "contact": "contact.open",
    "contactClosed": "contact.close",
    "acceleration": "acceleration.active",
    "mySwitch": "switch.on",
    "mySwitchOff": "switch.off",
    "arrivalPresence": "presence.present",
    "departurePresence": "presence.not present",
    "smoke": "smoke.detected",
    "smoke1": "smoke.tested",
    "water": "water.wet",
    "button1": "button.pushed",
    "triggerModes": "mode",
    "timeOfDay": "time",
	]
}

def mainPage() {
  dynamicPage(name: "mainPage") {
		def anythingSet = anythingSet()
    def notificationMessage = defaultNotificationMessage();
    log.debug "set $anythingSet"
		if (anythingSet) {
			section("Show message when"){
				ifSet "motion", "capability.motionSensor", title: "Motion Here", required: false, multiple: true , submitOnChange:true
				ifSet "contact", "capability.contactSensor", title: "Contact Opens", required: false, multiple: true, submitOnChange:true
				ifSet "contactClosed", "capability.contactSensor", title: "Contact Closes", required: false, multiple: true, submitOnChange:true
				ifSet "acceleration", "capability.accelerationSensor", title: "Acceleration Detected", required: false, multiple: true, submitOnChange:true
				ifSet "mySwitch", "capability.switch", title: "Switch Turned On", required: false, multiple: true, submitOnChange:true
				ifSet "mySwitchOff", "capability.switch", title: "Switch Turned Off", required: false, multiple: true, submitOnChange:true
				ifSet "arrivalPresence", "capability.presenceSensor", title: "Arrival Of", required: false, multiple: true, submitOnChange:true
				ifSet "departurePresence", "capability.presenceSensor", title: "Departure Of", required: false, multiple: true, submitOnChange:true
				ifSet "smoke", "capability.smokeDetector", title: "Smoke Detected", required: false, multiple: true, submitOnChange:true
				ifSet "water", "capability.waterSensor", title: "Water Sensor Wet", required: false, multiple: true, submitOnChange:true
				ifSet "button1", "capability.button", title: "Button Press", required:false, multiple:true //remove from production
				ifSet "triggerModes", "mode", title: "System Changes Mode", required: false, multiple: true, submitOnChange:true
				ifSet "timeOfDay", "time", title: "At a Scheduled Time", required: false, submitOnChange:true
			}
		}
		def hideable = anythingSet || app.installationState == "COMPLETE"
		def sectionTitle = anythingSet ? "Select additional triggers" : "Show message when..."

		section(sectionTitle, hideable: hideable, hidden: true){
			ifUnset "motion", "capability.motionSensor", title: "Motion Here", required: false, multiple: true, submitOnChange:true
			ifUnset "contact", "capability.contactSensor", title: "Contact Opens", required: false, multiple: true, submitOnChange:true
			ifUnset "contactClosed", "capability.contactSensor", title: "Contact Closes", required: false, multiple: true, submitOnChange:true
			ifUnset "acceleration", "capability.accelerationSensor", title: "Acceleration Detected", required: false, multiple: true, submitOnChange:true
			ifUnset "mySwitch", "capability.switch", title: "Switch Turned On", required: false, multiple: true, submitOnChange:true
			ifUnset "mySwitchOff", "capability.switch", title: "Switch Turned Off", required: false, multiple: true, submitOnChange:true
			ifUnset "arrivalPresence", "capability.presenceSensor", title: "Arrival Of", required: false, multiple: true, submitOnChange:true
			ifUnset "departurePresence", "capability.presenceSensor", title: "Departure Of", required: false, multiple: true, submitOnChange:true
			ifUnset "smoke", "capability.smokeDetector", title: "Smoke Detected", required: false, multiple: true, submitOnChange:true
			ifUnset "water", "capability.waterSensor", title: "Water Sensor Wet", required: false, multiple: true, submitOnChange:true
			ifUnset "button1", "capability.button", title: "Button Press", required:false, multiple:true //remove from production
			ifUnset "triggerModes", "mode", title: "System Changes Mode", description: "Select mode(s)", required: false, multiple: true, submitOnChange:true
			ifUnset "timeOfDay", "time", title: "At a Scheduled Time", required: false, submitOnChange:true
		}

		section (title:"Select Channel"){
			input "selectedChannels", "text", title: "Channel", required: true, multiple:true, submitOnChange:true
		}
    section (title: "Configure message"){
      input "defaultMessage", "bool", title: "Use Default Text:\n\"$notificationMessage\"", required: false, defaultValue: true, submitOnChange:true
		  def showMessageInput = (settings["defaultMessage"] == null || settings["defaultMessage"] == true) ? false : true;
			if (showMessageInput) {
        input "customMessage","text",title:"Use Custom Text", defaultValue:"", required:true, multiple: false
      }
    }
		section("More options", hideable: true, hidden: true) {
			href "timeIntervalInput", title: "Only during a certain time", description: timeLabel ?: "Tap to set", state: timeLabel ? "complete" : "incomplete"
			input "days", "enum", title: "Only on certain days of the week", multiple: true, required: false, options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
		}
		section([mobileOnly:true]) {
			label title: "Assign a name", required: false
		}
	}
}

private anythingSet() {
	for (it in controlToAttributeMap) {
    log.debug ("key ${it.key} value ${settings[it.key]} ${settings[it.key]?true:false}")
    if (settings[it.key]) {
      log.debug constructMessageFor(it.value, settings[it.key])
			return true
		}
	}
	return false
}

def defaultNotificationMessage(){
	def message = "";
	for (it in controlToAttributeMap)  {
		if (settings[it.key]) {
      message = constructMessageFor(it.value, settings[it.key])
      break;
		}
	}
	return message;
}

def constructMessageFor(group, device) {
	log.debug ("$group $device")
	def message = "";
  def firstDevice;
  if (device instanceof List) {
    firstDevice = device[0];
  } else {
    firstDevice = device;
  }
  switch(group) {
    case "motion.active":
      message = "Motion detected by $firstDevice.displayName at $location.name"
      break;
    case "contact.open":
      message = "Openning detected by $firstDevice.displayName at $location.name"
      break;
    case "contact.closed":
      message = "Closing detected by $firstDevice.displayName at $location.name"
      break;
    case "acceleration.active":
      message = "Acceleration detected by $firstDevice.displayName at $location.name"
      break;
    case "switch.on":
      message = "$firstDevice.displayName turned on at $location.name"
      break;
    case "switch.off":
      message = "$firstDevice.displayName turned off at $location.name"
      break;
    case "presence.present":
      message = "$firstDevice.displayName detected arrival at $location.name"
      break;
    case "presence.not present":
      message = "$firstDevice.displayName detected departure at $location.name"
      break;
    case "smoke.detected":
      message = "Smoke detected by $firstDevice.displayName at $location.name"
        break;
    case "smoke.tested":
      message = "Smoke tested by $firstDevice.displayName at $location.name"
      break;
    case "water.wet":
      message = "Dampness detected by $firstDevice.displayName at $location.name"
      break;
    case "button.pushed":
      message = "$firstDevice.displayName pushed at $location.name"
      break;
    case "time":
    case "time.":
      message = "Scheduled notification"
      break;
    case "mode":
      message = "Mode changed at $location.name"
      break;
  }
  
  for (mode in location.modes) {
    if ("mode.$mode" == group) {
      message = "Mode changed to $location.currentMode at $location.name";
      break;
    }
  }
  return message;
}

private ifUnset(Map options, String name, String capability) {
	if (!settings[name]) {
		input(options, name, capability)
	}
}

private ifSet(Map options, String name, String capability) {
	if (settings[name]) {
		input(options, name, capability)
	}
}

def subscribeToEvents() {
	log.trace "subscribe to events"
  log.debug "${contact} ${contactClosed} ${mySwitch} ${mySwitchOff} ${acceleration}${arrivalPresence} ${button1}"
	subscribe(app, appTouchHandler)
	subscribe(contact, "contact.open", eventHandler)
	subscribe(contactClosed, "contact.closed", eventHandler)
	subscribe(acceleration, "acceleration.active", eventHandler)
	subscribe(motion, "motion.active", eventHandler)
	subscribe(mySwitch, "switch.on", eventHandler)
	subscribe(mySwitchOff, "switch.off", eventHandler)
	subscribe(arrivalPresence, "presence.present", eventHandler)
	subscribe(departurePresence, "presence.not present", eventHandler)
	subscribe(smoke, "smoke.detected", eventHandler)
	subscribe(smoke, "smoke.tested", eventHandler)
	subscribe(smoke, "carbonMonoxide.detected", eventHandler)
	subscribe(water, "water.wet", eventHandler)
	subscribe(button1, "button.pushed", eventHandler)

	if (triggerModes) {
		subscribe(location, modeChangeHandler)
	}

	if (timeOfDay) {
		schedule(timeOfDay, scheduledTimeHandler)
	}
}

def eventHandler(evt) {
	log.trace "eventHandler(${evt?.name}: ${evt?.value})"
  def name = evt?.name;
  def value = evt?.value;
  takeAction(evt)
}

def modeChangeHandler(evt) {
	log.trace "modeChangeHandler $evt.name: $evt.value ($triggerModes)"
	if (evt?.value in triggerModes) {
		eventHandler(evt)
	}
}

def scheduledTimeHandler() {
  def evt = [name:"time", value:"", device : ""];
	eventHandler(evt)
}

def appTouchHandler(evt) {
	takeAction(evt)
}

def slackConversationsListUrl() { "https://slack.com/api/conversations.list" }
def slackChatPostMessageUrl() { "https://slack.com/api/chat.postMessage" }

def getChannels() {
  httpGet([
    uri: slackChannelsListUrl(),
  ])
}

def takeAction(evt) {
  if(evt == null) {
    log.debug "NPE in takeAction"
    return;
  }
	def messageToShow
  if (defaultMessage) {
    messageToShow = constructMessageFor("${evt.name}.${evt.value}", evt.device);
  } else {
    messageToShow = customMessage;
  }
	if (messageToShow) {
    log.debug "text ${messageToShow}"
    def notification = [
      token: appSettings.token,
      channel: channel,
      text: messageToShow,
    ];
    httpPost([
      uri: slackChatPostMessageUrl(),
      contentType: "application/json",
      body: notification
    ])
  } else {
    log.debug "No message to show"
  }
	log.trace "Exiting takeAction()"
}