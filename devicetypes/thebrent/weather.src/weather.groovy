/**
 *  Weather
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
  definition (name: "Weather", namespace: "thebrent", author: "Brent Maxwell", cstHandler: true) {
    capability "Temperature Measurement"
    capability "Relative Humidity Measurement"
    capability "Atmospheric Pressure Measurement"
    //capability "Air Quality Sensor"
    //capability "Carbon Dioxide Measurement"
    //capability "Dust Sensor"
    //capability "Fine Dust Sensor"
    //capability "Ultraviolet Index"
    //capability "Very Fine Dust Sensor"
    capability "Geolocation"
    capability "Refresh"
    capability "Polling"

    attribute "city", "string"
    attribute "state", "string"
    attribute "countyId", "string"
    attribute "stationId", "string"
    attribute "zoneId", "string"
    attribute "radarStationId", "string"
    attribute "gridPointX", "number"
    attribute "gridPointY", "number"
  }

  simulator {
    // TODO: define status and reply messages here
  }

  tiles(scale: 2) {
    multiAttributeTile(name:"main", type:"generic", width:6, height:4) {
      tileAttribute("temperature", key: "PRIMARY_CONTROL") {
        attributeState "temperature",label:'${currentValue}°', icon:"st.Weather.weather2", backgroundColors:[
          [value: 32, color: "#153591"],
          [value: 44, color: "#1e9cbb"],
          [value: 59, color: "#90d2a7"],
          [value: 74, color: "#44b621"],
          [value: 84, color: "#f1d801"],
          [value: 92, color: "#d04e00"],
          [value: 98, color: "#bc2323"]
        ]}
      tileAttribute ("humidity", key: "SECONDARY_CONTROL") {
        attributeState "humidity", label:'Humidity: ${currentValue}%'
      }
    } 
    valueTile("temperature", "device.temperature") {
      state("temperature", label: '${currentValue}°', icon:"st.Weather.weather2", backgroundColors: [
        [value: 31, color: "#153591"],
        [value: 44, color: "#1e9cbb"],
        [value: 59, color: "#90d2a7"],
        [value: 74, color: "#44b621"],
        [value: 84, color: "#f1d801"],
        [value: 95, color: "#d04e00"],
        [value: 96, color: "#bc2323"]
      ])
    }
    valueTile("lastupdate", "lastupdate", width: 4, height: 1, inactiveLabel: false) { 			
      state "default", label:"Last updated: " + '${currentValue}' 		
    }
    standardTile("refresh", "device.refresh", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
 			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
 		}
    valueTile("latitude", "device.latitude", height: 1, width: 1) {
      state "default", label:'${currentValue}'
    }
    valueTile("longitude", "device.longitude", height:1, width: 1) {
      state "default", label: '${currentValue}'
    }
    main("main")
    details(["main","lastupdate","refresh"])
  }
}

// parse events into attributes
def parse(String description) {
  log.debug "Parsing '${description}'"
  // TODO: handle 'airQuality' attribute
  // TODO: handle 'atmosphericPressure' attribute
  // TODO: handle 'carbonDioxide' attribute
  // TODO: handle 'dustLevel' attribute
  // TODO: handle 'fineDustLevel' attribute
  // TODO: handle 'fineDustLevel' attribute
  // TODO: handle 'latitude' attribute
  // TODO: handle 'longitude' attribute
  // TODO: handle 'method' attribute
  // TODO: handle 'accuracy' attribute
  // TODO: handle 'altitudeAccuracy' attribute
  // TODO: handle 'heading' attribute
  // TODO: handle 'speed' attribute
  // TODO: handle 'lastUpdateTime' attribute
  // TODO: handle 'humidity' attribute
  // TODO: handle 'temperature' attribute
  // TODO: handle 'ultravioletIndex' attribute
  // TODO: handle 'veryFineDustLevel' attributes
}

def poll() {
  log.debug "poll"
  parent.poll()
}
def refresh() {
  log.debug "refresh"
  parent.refresh();
}