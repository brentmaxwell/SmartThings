/**
 *  ps_GetPublicIP
 *
 *  Copyright 2014 patrick@patrickstuart.com
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
    definition (name: "IP Address and Dynamic DNS Updater", namespace: "thebrent", author: "brent@thebrent.net") {
    capability "Refresh"

    attribute "publicIpAddress", "string"
    attribute "lastUpdated", "string"
    attribute "status", "enum", ["loading", "idle"]
    attribute "dynamicDnsResponse", "enum", ["good","nochg","nohost","badauth","notfqdn","badagent","911"]
  }

	simulator {
  }

	tiles(scale: 2) {
    valueTile("publicIpAddress", "device.publicIpAddress", decoration: "flat", width: 6, height: 2) {
      state "default", label:'${currentValue}', unit:"Public IP Address"
    }
    valueTile("lastUpdated", "device.lastUpdated", decoration: "flat", width: 6, height: 2) {
      state "default", label:'${currentValue}', unit:"Last Updated"
    }
    standardTile("refresh", "device.status", decoration: "flat", width: 6, height: 2) {
      state "idle", icon:"st.secondary.refresh", action:"refresh.refresh"
      state "loading", icon: "st.motion.motion.active", action:"refresh.refresh"
    }
    standardTile("dynamicDnsStatus", "device.dynamicDnsResponse", decoration: "flat", width: 6, height: 2) {
      state "good", label: "Good"
      state "nochg", label: "Good"
      state "default", label: "Problem!"
    }

    main "publicIpAddress"
    details(["publicIpAddress", "lastUpdated", "dynamicDnsStatus", "refresh"])
	}
  preferences {
    input name: "enableDynDns", type: "bool", title: "Enable", description: "Enable dynamic DNS"
    input name: "hostname", type: "string", title: "Host Name", description: "The hostname to update"
    input name: "username", type: "string", title: "Username"
    input name: "password", type: "password", title: "Password"

  }
}

def getIpServiceAddress() { "216.146.38.70" }


// parse events into attributes
def parse(String description) {
  log.debug "Parsing response"
	def map = stringToMap(description)
  def bodyString = new String(map.body.decodeBase64())
	def body = (new XmlSlurper().parseText(bodyString))
  def publicIp = body.toString().replace("Current IP CheckCurrent IP Address: ","")
  def lastUpdated = new Date().format("yyyy/MM/dd HH:mm:ss")
  log.debug publicIp
  if(state.enableDynDns) {
    updateDns(publicIp);  
  }
  sendEvent(name: 'publicIpAddress', value: publicIp)
  sendEvent(name: 'lastUpdated', value: lastUpdated)
  sendEvent(name: 'status', value: "idle")
}

def refresh() {
  log.debug "Refreshing"
  state.enableDynDns = enableDynDns
  state.hostname = hostname
  state.username = username
  state.password = password
  sendEvent(name: 'status', value: "loading")
  getHubAction()
}

def getHubAction() {
  log.debug "Getting IP address"

  def method = "GET"
  def host = "216.146.38.70"
  def hosthex = convertIPtoHex(host)
  def porthex = convertPortToHex(80)
  device.deviceNetworkId = "$hosthex:$porthex" 
  def headers = [:]
  headers.put("HOST", "$host:80")
  def path = "/"
  def hubAction = new physicalgraph.device.HubAction(
    method: "GET",
    headers: [
      "HOST": "$host:80"
    ]
  )
  log.debug hubAction
  hubAction
}

def updateDns(ipAddress) {
  log.debug "Updating Google Dynamic DNS"
  def url = "https://${state.username}:${state.password}@domains.google.com/nic/update?hostname=${state.hostname}&myip=${ipAddress}"
  httpGet(url, { response -> 
    def result = response.data.getText();
    log.debug result
    def stringResult = result.split(" ")[0]
    log.debug stringResult
    sendEvent(name: 'dynamicDnsResponse', value: stringResult);
  })
}

private String convertIPtoHex(ipAddress) {
  String hex = ipAddress.tokenize( '.' ).collect { String.format( '%02x', it.toInteger() ) }.join()
  log.debug "IP address entered is $ipAddress and the converted hex code is $hex"
  return hex
}

private String convertPortToHex(port) {
  String hexport = port.toString().format( '%04x', port.toInteger() )
  log.debug hexport
  return hexport
}

private Integer convertHexToInt(hex) {
  Integer.parseInt(hex,16)
}

private String convertHexToIP(hex) {
  log.debug("Convert hex to ip: $hex")
  [convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private getHostAddress() {
  def parts = device.deviceNetworkId.split(":")
  log.debug device.deviceNetworkId
  def ip = convertHexToIP(parts[0])
  def port = convertHexToInt(parts[1])
  return ip + ":" + port
}
