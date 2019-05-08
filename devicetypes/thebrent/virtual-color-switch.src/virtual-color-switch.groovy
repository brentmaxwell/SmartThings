/**
 *  Copyright 2017 SmartThings
 *
 *  Provides a virtual switch.
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
    definition (name: "Virtual Color Switch", namespace: "thebrent", author: "brent@thebrent.net", runLocally: true, minHubCoreVersion: '000.021.00001', executeCommandsLocally: true, mnmn: "SmartThings", vid: "generic-switch") {
        capability "Switch Level"
		    capability "Actuator"
		    capability "Color Control"
		    capability "Color Temperature"
		    capability "Switch"
		    capability "Refresh"
		    capability "Sensor"
		    capability "Health Check"
		    capability "Light"

		    command "setAdjustedColor"
        command "reset"
        command "refresh"
    }

    preferences {}

    tiles (scale: 2){
		multiAttributeTile(name:"rich-control", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
			}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"setLevel", range:"(0..100)"
      }
      tileAttribute ("brightnessLabel", key: "SECONDARY_CONTROL") {
        attributeState "Brightness", label: '${name}', defaultState: true
      }
			tileAttribute ("device.color", key: "COLOR_CONTROL") {
				attributeState "color", action:"setAdjustedColor"
			}
		}

    standardTile("explicitOn", "device.switch", width: 2, height: 2, decoration: "flat") {
      state "default", label: "On", action: "switch.on", icon: "st.Home.home30", backgroundColor: "#ffffff"
    }
    standardTile("explicitOff", "device.switch", width: 2, height: 2, decoration: "flat") {
      state "default", label: "Off", action: "switch.off", icon: "st.Home.home30", backgroundColor: "#ffffff"
    }
    controlTile("levelSlider", "device.level", "slider", width: 2, height: 2, inactiveLabel: false, range: "(1..100)") {
      state "physicalLevel", action: "switch level.setLevel"
    }

    controlTile("colorTempSliderControl", "device.colorTemperature", "slider", width: 4, height: 2, inactiveLabel: false, range:"(2000..6500)") {
      state "colorTemperature", action:"setColorTemperature"
    }

    valueTile("colorTemp", "device.colorTemperature", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "colorTemperature", label: 'WHITES'
    }

		standardTile("reset", "device.reset", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:"Reset To White", action:"reset", icon:"st.lights.philips.hue-single"
		}

		standardTile("refresh", "device.refresh", height: 2, width: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:"", action:"refresh", icon:"st.secondary.refresh"
		}

		main(["rich-control"])
		details(["rich-control", "levelSlider", "colorTempSliderControl", "colorTemp", "reset", "refresh"])
	}
}

def parse(description) {
	log.debug "parse() - $description"
	def results = []

	def map = description
	if (description instanceof String)  {
		log.debug "Hue Bulb stringToMap - ${map}"
		map = stringToMap(description)
	}

	if (map?.name && map?.value) {
		results << createEvent(name: "${map?.name}", value: "${map?.value}")
	}
	results
}


def on() {
    sendEvent(name: "switch", value: "on", isStateChange: true)
}

def off() {
    sendEvent(name: "switch", value: "off", isStateChange: true)
}

def setLevel(percent, rate = null) {
    log.trace "Executing setLevel $value"
    Map levelEventMap = buildSetLevelEvent(value)
    if (levelEventMap.value == 0) {
        turnOff()
        // notice that we don't set the level to 0'
    } else {
        implicitOn()
        sendEvent(levelEventMap)
    }
}

private Map buildSetLevelEvent(value) {
    def intValue = value as Integer
    def newLevel = Math.max(Math.min(intValue, 100), 0)
    Map eventMap = [name: "level", value: newLevel, unit: "%", isStateChange: true]
    return eventMap
}

def setLevel(value, duration) {
    log.trace "Executing setLevel $value (ignoring duration)"
    setLevel(value)
}

private implicitOn() {
    if (device.currentValue("switch") != "on") {
        turnOn()
    }
}

private turnOn() {
    sendEvent(name: "switch", value: "on", isStateChange: true)
}

private turnOff() {
    sendEvent(name: "switch", value: "off", isStateChange: true)
}

def setSaturation(percent) {
    log.debug "Executing 'setSaturation'"
    if (verifyPercent(percent)) {
	    sendEvent(name: "saturation", value: percent, isStateChange: true)
    }
}

def setHue(percent) {
    log.debug "Executing 'setHue'"
    if (verifyPercent(percent)) {
	    sendEvent(name: "hue", value: percent, isStateChange: true)
    }
}

def setColor(value) {
    def events = []
    def validValues = [:]

    if (verifyPercent(value.hue)) {
        validValues.hue = value.hue
    }
    if (verifyPercent(value.saturation)) {
        validValues.saturation = value.saturation
    }
    if (value.hex != null) {
        if (value.hex ==~ /^\#([A-Fa-f0-9]){6}$/) {
            validValues.hex = value.hex
        } else {
            log.warn "$value.hex is not a valid color"
        }
    }
    if (verifyPercent(value.level)) {
        validValues.level = value.level
    }
    if (value.switch == "off" || (value.level != null && value.level <= 0)) {
        validValues.switch = "off"
    } else {
        validValues.switch = "on"
    }
    if (!validValues.isEmpty()) {
	    sendEvent(name: "color", value: validValues, isStateChange: true)
    }
}

def reset() {
    log.debug "Executing 'reset'"
	setColorTemperature(4000)
}

def setAdjustedColor(value) {
    if (value) {
        log.trace "setAdjustedColor: ${value}"
        def adjusted = value + [:]
        // Needed because color picker always sends 100
        adjusted.level = null
	    setColor(adjusted)
    } else {
        log.warn "Invalid color input $value"
    }
}

def setColorTemperature(value) {
    if (value) {
        log.trace "setColorTemperature: ${value}k"
	    log.trace parent.setColorTemperature(this, value)
    } else {
        log.warn "Invalid color temperature $value"
    }
}

def refresh() {
    log.debug "Executing 'refresh'"
    parent?.manualRefresh()
}

def verifyPercent(percent) {
    if (percent == null)
        return false
    else if (percent >= 0 && percent <= 100) {
        return true
    } else {
        log.warn "$percent is not 0-100"
        return false
    }
}

def installed() {
    on()
}
