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
	definition (name: "Router", namespace: "thebrent", author: "Brent Maxwell", cstHandler: true) {
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
        capability "Refresh"
        
        attribute "externalIpAddress", "string"
        attribute "lastUpdated", "string"
        attribute "refreshStatus", "enum", ["loading", "idle"]
    	attribute "dynamicDnsResponse", "enum", ["good","nochg","nohost","badauth","notfqdn","badagent","911"]
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
        multiAttributeTile(name:"main", type:"generic", width:6, height:2) {
			tileAttribute("device.externalIpAddress", key: "PRIMARY_CONTROL") {
        		attributeState "default", label:'${currentValue}', icon:"st.secondary.smartapps-tile"
      		}
      		tileAttribute("device.lastUpdated", key: "SECONDARY_CONTROL") {
        		attributeState "default", label:'${currentValue}'
      		}
        }
    	standardTile("refresh", "device.refreshStatus", decoration: "flat", width: 6, height: 2) {
      		state "idle", icon:"st.secondary.refresh", action:"refresh.refresh"
      		state "loading", icon: "st.motion.motion.active", action:"refresh.refresh"
    	}
    	standardTile("dynamicDnsStatus", "device.dynamicDnsResponse", decoration: "flat", width: 6, height: 2) {
      		state "good", label: "Good", color: "st.colors.green"
      		state "nochg", label: "Good", color: "st.colors.green"
      		state "default", label: "Problem!", color: "st.colors.red"
    	}
        main(["main"])
    	details(["main", "dynamicDnsStatus", "refresh"])
	}
    preferences {
    	input name: "enableDynDns", type: "bool", title: "Enable", description: "Enable dynamic DNS"
    	input name: "hostname", type: "string", title: "Host Name", description: "The hostname to update"
    	input name: "username", type: "string", title: "Username"
    	input name: "password", type: "password", title: "Password"

  }
}

// parse events into attributes
def parse(String description) {
    def msg = parseLanMessage(description)
    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    def json = msg.json              // => any JSON included in response body, as a data structure of lists and maps
    def xml = msg.xml                // => any XML included in response body, as a document tree structure
    def data = msg.data              // => either JSON or XML in response body (whichever is specified by content-type header in response)
    if( body.contains("GetExternalIPAddressResponse")) {
    	sendEvent(name: 'externalIpAddress', value: data)
        sendEvent(name: 'lastUpdated', value: new Date().format("yyyy/MM/dd HH:mm:ss"))
        updateDns(data)
	}
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
	sendEvent(name: 'refreshStatus', value: 'idle')
}

def getExternalIpAddress() {
	return doSoapAction("GetExternalIPAddress", "WANIPConnection:1", "/ctl/IPConn")
}

def refresh() {
  state.enableDynDns = enableDynDns
  state.hostname = hostname
  state.username = username
  state.password = password
  sendEvent(name: 'refreshStatus', value: "loading")
  getExternalIpAddress()
  
}

def updateDns(ipAddress) {
  def url = "https://${state.username}:${state.password}@domains.google.com/nic/update?hostname=${state.hostname}&myip=${ipAddress}"
  httpGet(url, { response -> 
    def result = response.data.getText();
    log.debug result
    def stringResult = result.split(" ")[0]
    log.debug stringResult
    sendEvent(name: 'dynamicDnsResponse', value: stringResult);
  })
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

def sync(ip, port) {
    def existingIp = getDataValue("ip")
    def existingPort = getDataValue("port")
    if (ip && ip != existingIp) {
        updateDataValue("ip", ip)
    }
    if (port && port != existingPort) {
        updateDataValue("port", port)
    }
}

def doSoapAction(action, service, path, Map body = [InstanceID:0, Speed:1]) {
    def result = new physicalgraph.device.HubSoapAction(
        path:    path,
        urn:     "urn:schemas-upnp-org:service:$service:1",
        action:  action,
        body:    body,
        headers: [Host:getHostAddress(), CONNECTION: "close"]
    )
    return result
}

// gets the address of the Hub
private getCallBackAddress() {
    return device.hub.getDataValue("localIP") + ":" + device.hub.getDataValue("localSrvPortTCP")
}

// gets the address of the device
private getHostAddress() {
    def ip = getDataValue("ip")
    def port = getDataValue("port")

    if (!ip || !port) {
        def parts = device.deviceNetworkId.split(":")
        if (parts.length == 2) {
            ip = parts[0]
            port = parts[1]
        } else {
            log.warn "Can't figure out ip and port for device: ${device.id}"
        }
    }

    log.debug "Using IP: $ip and port: $port for device: ${device.id}"
    return convertHexToIP(ip) + ":" + convertHexToInt(port)
}

private Integer convertHexToInt(hex) {
    return Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
    return [convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}