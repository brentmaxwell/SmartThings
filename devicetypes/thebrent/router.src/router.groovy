/**
*  Router Device
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
    capability "Sensor"
    capability "Execute"
    capability "Geolocation"
    capability "Network Meter"
    capability "Notification"
    capability "Remote Control Status"
    capability "Signal Strength"
    capability "Wifi Mesh Router"
    capability "Refresh"

    attribute "externalIpAddress", "string"
    attribute "lastUpdated", "string"
    attribute "refreshStatus", "enum", ["loading", "idle"]
    attribute "dynamicDnsResponse", "enum", ["good","nochg","nohost","badauth","notfqdn","badagent","911"]

    command "getExternalIpAddress"
    command "updateDns", ["string"]
  }

  simulator {
    // TODO: define status and reply messages here
  }

  tiles(scale: 2) {
    standardTile("ipAddress", "device.externalIpAddress", decoration: "flat", canChangeIcon: true, width:6, height:2) {
      state "default", label:'${currentValue}', icon:"st.secondary.smartapps-tile"
    }
    standardTile("lastUpdated", "device.lastUpdated", decoration: "flat", width:6, height:2) {
      state "default", label:'${currentValue}'
    }
    standardTile("refresh", "device.refreshStatus", decoration: "flat", width: 3, height: 2) {
      state "idle", icon: "st.secondary.refresh", action: "refresh.refresh"
      state "loading", icon: "st.motion.motion.active", action: "refresh.refresh"
      state "error", icon: "st.secondary.refresh", action: "refresh.refresh",  backgroundColor: "#e86d13"
    }
    standardTile("dynamicDnsStatus", "device.dynamicDnsResponse", decoration: "flat", width: 3, height: 2) {
      state "good", label: "Good", backgroundColor: "#44B621"
      state "nochg", label: "Good", backgroundColor: "#44B621"
      state "nohost", label: "Error", backgroundColor: "#e86d13"
      state "badauth", label: "Authentication error", backgroundColor: "#e86d13"
      state "notfqdn", label: "Error", backgroundColor: "#e86d13"
      state "badagent", label: "Bad request", backgroundColor: "#e86d13"
      state "abuse", label: "Blocked", backgroundColor: "#e86d13"
      state "911", label: "Problem!", backgroundColor: "#e86d13"
    }
    main(["ipAddress"])
    details(["ipAddress", "lastUpdated", "dynamicDnsStatus", "refresh"])
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
    log.trace "IP Address: ${data}"
    sendEvent(name: 'externalIpAddress', value: data)
    sendEvent(name: 'lastUpdated', value: new Date().format("yyyy/MM/dd HH:mm:ss"))
    updateDns(data)
  }
  sendEvent(name: 'refreshStatus', value: 'idle')
}

def getExternalIpAddress() {
  log.trace "getExternalIpAddress"
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
  if(state.enableDynDns) {
    log.trace "Update DNS: ${ipAddress}"
    def url = "https://${state.username}:${state.password}@domains.google.com/nic/update?hostname=${state.hostname}&myip=${ipAddress}"
    httpGet(url, { response -> 
      def result = response.data.getText();
      log.trace result
      def stringResult = result.split(" ")[0]
      sendEvent(name: 'dynamicDnsResponse', value: stringResult);
    })
  }
}

// handle commands
def execute() {
  log.debug "Executing 'execute'"
  // TODO: handle 'execute' command
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

def subscribeToExternalIpChanges() {
    subscribeToAction("/evt/IPConn/ExternalIPAddress")
}

private subscribeToAction(path, callbackPath="") {
    log.trace "subscribe($path, $callbackPath)"
    def address = getCallBackAddress()
    def ip = getHostAddress()

    def result = new physicalgraph.device.HubAction(
        method: "SUBSCRIBE",
        path: path,
        headers: [
            HOST: ip,
            CALLBACK: "<http://${address}/notify$callbackPath>",
            NT: "upnp:event",
            TIMEOUT: "Second-28800"
        ]
    )

    log.trace "SUBSCRIBE $path"

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
