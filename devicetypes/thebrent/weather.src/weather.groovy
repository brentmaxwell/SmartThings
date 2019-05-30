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
		capability "Air Quality Sensor"
		capability "Atmospheric Pressure Measurement"
		capability "Carbon Dioxide Measurement"
		capability "Dust Sensor"
		capability "Fine Dust Sensor"
		capability "Geolocation"
		capability "Relative Humidity Measurement"
		capability "Temperature Measurement"
		capability "Ultraviolet Index"
		capability "Very Fine Dust Sensor"
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
	// TODO: handle 'veryFineDustLevel' attribute

}