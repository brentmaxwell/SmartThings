/**
 *  FiOS Quantum Router
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
	definition (name: "FiOS Quantum Router", namespace: "thebrent", author: "Brent Maxwell", cstHandler: true) {
		capability "Alarm"
		capability "Demand Response Load Control"
		capability "Energy Meter"
		capability "Execute"
		capability "Geolocation"
		capability "Language Setting"
		capability "Location Mode"
		capability "Lock"
		capability "Lock Codes"
		capability "Network Meter"
		capability "Notification"
		capability "Presence Sensor"
		capability "Remote Control Status"
		capability "Signal Strength"
		capability "Wifi Mesh Router"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		// TODO: define your main and details tiles here
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'alarm' attribute
	// TODO: handle 'drlcStatus' attribute
	// TODO: handle 'energy' attribute
	// TODO: handle 'data' attribute
	// TODO: handle 'latitude' attribute
	// TODO: handle 'longitude' attribute
	// TODO: handle 'method' attribute
	// TODO: handle 'accuracy' attribute
	// TODO: handle 'altitudeAccuracy' attribute
	// TODO: handle 'heading' attribute
	// TODO: handle 'speed' attribute
	// TODO: handle 'lastUpdateTime' attribute
	// TODO: handle 'supportedLanguages' attribute
	// TODO: handle 'language' attribute
	// TODO: handle 'mode' attribute
	// TODO: handle 'lock' attribute
	// TODO: handle 'lock' attribute
	// TODO: handle 'codeChanged' attribute
	// TODO: handle 'lockCodes' attribute
	// TODO: handle 'scanCodes' attribute
	// TODO: handle 'codeLength' attribute
	// TODO: handle 'maxCodes' attribute
	// TODO: handle 'maxCodeLength' attribute
	// TODO: handle 'minCodeLength' attribute
	// TODO: handle 'codeReport' attribute
	// TODO: handle 'uplinkSpeed' attribute
	// TODO: handle 'downlinkSpeed' attribute
	// TODO: handle 'presence' attribute
	// TODO: handle 'remoteControlEnabled' attribute
	// TODO: handle 'lqi' attribute
	// TODO: handle 'rssi' attribute
	// TODO: handle 'wifiNetworkName' attribute
	// TODO: handle 'wifiGuestNetworkName' attribute
	// TODO: handle 'connectedRouterCount' attribute
	// TODO: handle 'disconnectedRouterCount' attribute
	// TODO: handle 'connectedDeviceCount' attribute
	// TODO: handle 'wifiNetworkStatus' attribute
	// TODO: handle 'wifiGuestNetworkStatus' attribute

}

// handle commands
def off() {
	log.debug "Executing 'off'"
	// TODO: handle 'off' command
}

def strobe() {
	log.debug "Executing 'strobe'"
	// TODO: handle 'strobe' command
}

def siren() {
	log.debug "Executing 'siren'"
	// TODO: handle 'siren' command
}

def both() {
	log.debug "Executing 'both'"
	// TODO: handle 'both' command
}

def requestDrlcAction() {
	log.debug "Executing 'requestDrlcAction'"
	// TODO: handle 'requestDrlcAction' command
}

def overrideDrlcAction() {
	log.debug "Executing 'overrideDrlcAction'"
	// TODO: handle 'overrideDrlcAction' command
}

def execute() {
	log.debug "Executing 'execute'"
	// TODO: handle 'execute' command
}

def setLanguage() {
	log.debug "Executing 'setLanguage'"
	// TODO: handle 'setLanguage' command
}

def setMode() {
	log.debug "Executing 'setMode'"
	// TODO: handle 'setMode' command
}

def lock() {
	log.debug "Executing 'lock'"
	// TODO: handle 'lock' command
}

def unlock() {
	log.debug "Executing 'unlock'"
	// TODO: handle 'unlock' command
}

def setCode() {
	log.debug "Executing 'setCode'"
	// TODO: handle 'setCode' command
}

def deleteCode() {
	log.debug "Executing 'deleteCode'"
	// TODO: handle 'deleteCode' command
}

def requestCode() {
	log.debug "Executing 'requestCode'"
	// TODO: handle 'requestCode' command
}

def reloadAllCodes() {
	log.debug "Executing 'reloadAllCodes'"
	// TODO: handle 'reloadAllCodes' command
}

def unlockWithTimeout() {
	log.debug "Executing 'unlockWithTimeout'"
	// TODO: handle 'unlockWithTimeout' command
}

def setCodeLength() {
	log.debug "Executing 'setCodeLength'"
	// TODO: handle 'setCodeLength' command
}

def nameSlot() {
	log.debug "Executing 'nameSlot'"
	// TODO: handle 'nameSlot' command
}

def updateCodes() {
	log.debug "Executing 'updateCodes'"
	// TODO: handle 'updateCodes' command
}

def deviceNotification() {
	log.debug "Executing 'deviceNotification'"
	// TODO: handle 'deviceNotification' command
}

def enableWifiNetwork() {
	log.debug "Executing 'enableWifiNetwork'"
	// TODO: handle 'enableWifiNetwork' command
}

def disableWifiNetwork() {
	log.debug "Executing 'disableWifiNetwork'"
	// TODO: handle 'disableWifiNetwork' command
}

def enableWifiGuestNetwork() {
	log.debug "Executing 'enableWifiGuestNetwork'"
	// TODO: handle 'enableWifiGuestNetwork' command
}

def disableWifiGuestNetwork() {
	log.debug "Executing 'disableWifiGuestNetwork'"
	// TODO: handle 'disableWifiGuestNetwork' command
}