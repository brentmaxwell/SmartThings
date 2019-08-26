/**
 *  Weather (Connect)
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
  name: "Weather (Connect)",
  namespace: "thebrent",
  author: "Brent Maxwell",
  description: "Weather status",
  category: "SmartThings Labs",
  iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
  iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
  iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")
  {
    appSetting "apiKey"
  }

preferences {
  page name: "mainPage", title: "Click save to create a new virtual device.", install: true, uninstall: true
}

import groovy.json.JsonSlurper

def geocodingUrl() { "https://maps.googleapis.com/maps/api/geocode/json" }

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
	httpGet([
      uri: "${geocodingUrl()}?address=${address}&key=${appSettings.apiKey}",
    ]) { resp ->
      def data = resp.data.results[0];
      def location = data.geometry.location;
      def deviceId = "weather-${data.place_id}"
      def childDevice = getChildDevice(deviceId)
      if(!childDevice){
        childDevice = addChildDevice("thebrent", "Weather", deviceId, null, [label: "Weather: ${data.formatted_address}"])
      }
      childDevice.sendEvent(name: "latitude", value: location.lat)
      childDevice.sendEvent(name: "longitude", value: location.lng)
      getLocationData(childDevice)
    }
  runEvery30Minutes("poll")
}

def mainPage() {
  dynamicPage(name: "mainPage") {
    section("Location") {
      input "address", title: "Enter", required: true
    }
    section("Devices Created") {
      paragraph "${getAllChildDevices().inject("") {result, i -> result + (i.label + "\n")} ?: ""}"
    }
    remove("Remove (Includes Devices)", "This will remove all virtual devices created through this app.")
    }
}

def poll() {
  refresh()
}

def refresh() {
  def children = getChildDevices()
  for(device in children){
    getCurrentObservations(device)
    getAlerts(device)
    device.sendEvent(name: 'lastUpdated', value: new Date().format("yyyy/MM/dd HH:mm:ss"))
  }
}

def weatherApiEndpoint() { "https://api.weather.gov" }

def getLocationData(childDevice) {
  def lat = childDevice.currentState("latitude").value
  def lng = childDevice.currentState("longitude").value
  def params = [
    uri: "${weatherApiEndpoint()}/points/${lat},${lng}",
    contentType: "application/ld+json"
  ]
  httpGet(params) { resp ->
    def data = new JsonSlurper().parseText(bytesToString(resp.data))
    childDevice.sendEvent(name: "city", value: data.city.toString())
    childDevice.sendEvent(name: "state", value: data.state.toString())
    childDevice.sendEvent(name: "countyId", value: data.county.toString().replace("https://api.weather.gov/zones/county/",""))
    childDevice.sendEvent(name: "stationId", value: data.cwa.toString())
    childDevice.sendEvent(name: "zoneId", value: data.forecastZone.toString().replace("https://api.weather.gov/zones/forecast/",""))
    childDevice.sendEvent(name: "radarStationId", value: data.radarStation.toString())
    childDevice.sendEvent(name: "gridPointX", value: data.gridX.toString())
    childDevice.sendEvent(name: "gridPointY", value: data.gridY.toString())
  }
}

def getCurrentObservations(childDevice){
  def zoneId = childDevice.currentState("zoneId").value
  def params = [
    uri: "${weatherApiEndpoint()}/zones/forecast/${zoneId}/observations?limit=1",
    contentType: "application/geo+json"
  ]
  httpGet(params) { resp ->
  	def data = new JsonSlurper().parseText(bytesToString(resp.data)).features[0].properties
    log.debug data
    childDevice.sendEvent(name: "humidity", value: data.relativeHumidity.value)
    childDevice.sendEvent(name: "temperature", value: cToPref(data.temperature.value), unit: getTemperatureScale())
  }
}

def getAlerts(childDevice) {
  def zoneId = childDevice.currentState("zoneId").value
  def params = [
    uri: "${weatherApiEndpoint()}/alerts/active/zone/${zoneId}",
    contentType: "application/geo+json"
  ]
  httpGet(params) { resp ->
    def data = new JsonSlurper().parseText(bytesToString(resp.data)).features;
    log.debug data
    childDevice.sendEvent(name: "data", value: data)
  }
}

def bytesToString(data){
  int n = data.available();
  byte[] bytes = new byte[n];
  data.read(bytes, 0, n);
  String s = new String(bytes)
  return s
}

def cToPref(temp) {
  if(getTemperatureScale() == 'C') {
    return temp
  } else {
    return temp * 1.8 + 32
  }
}
