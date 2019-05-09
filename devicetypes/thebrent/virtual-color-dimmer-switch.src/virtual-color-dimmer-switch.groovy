/**
 *  Virtual Color Dimmer Switch
 *
 *  Copyright 2019 Andrew Sayre
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
	definition (name: "Virtual Color Dimmer Switch", namespace: "thebrent", author: "Andrew Sayre") {
		capability "Color Control"
		capability "Color Temperature"
        capability "Switch"
        capability "Switch Level"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 1, height: 1, canChangeIcon: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
				attributeState("on", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#00a0dc", nextState:"turningOff")
				attributeState("off", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn")
				attributeState("turningOn", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#00a0dc", nextState:"turningOff")
				attributeState("turningOff", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn")
			}

			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}

			tileAttribute ("device.color", key: "COLOR_CONTROL") {
				attributeState "color", action:"color control.setColor"
			}
		}
	}

	controlTile("colorTempSliderControl", "device.colorTemperature", "slider", width: 4, height: 2, inactiveLabel: false, range:"(2700..6500)") {
		state "colorTemperature", action:"color temperature.setColorTemperature"
	}

	main(["switch"])
	details(["switch", "levelSliderControl", "rgbSelector", "colorTempSliderControl"])

}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

def setHue(value) {
	log.debug "Executing 'setHue': ${value}"
    def color_map = [
    	'saturation': device.currentValue("saturation"),
        'hue': value
    ]
    setColor(color_map)
}

def setSaturation(value) {
	log.debug "Executing 'setSaturation': ${value}"
    def color_map = [
    	'saturation': value,
        'hue': device.currentValue("hue")
    ]
    setColor(color_map)
}

def setColor(value) {
	log.debug "Executing 'setColor': ${value}"
	if (value.hex) {
        def hsv = colorUtil.hexToHsv(value.hex)
        sendEvent(name: "color", value: value.hex, isStateChange: true)
        sendEvent(name: "hue", value: hsv[0], isStateChange: true)
        sendEvent(name: "saturation", value: hsv[1], isStateChange: true)
	} else {
    	def color = colorUtil.hsvToHex(Math.round(value.hue) as int, Math.round(value.saturation) as int)
        sendEvent(name: "color", value: color, isStateChange: true)
        sendEvent(name: "hue", value: value.hue, isStateChange: true)
        sendEvent(name: "saturation", value: value.saturation, isStateChange: true)
	}
}

def setColorTemperature(value) {
	log.debug "Executing 'setColorTemperature': ${value}"
    sendEvent(name: "colorTemperature", value: value, isStateChange: true)
}

def on() {
    log.trace "Executing 'on'"
    turnOn()
}

def off() {
    log.trace "Executing 'off'"
    turnOff()
}

def setLevel(value, rate=0) {
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

private turnOn() {
    sendEvent(name: "switch", value: "on", isStateChange: true)
}

private turnOff() {
    sendEvent(name: "switch", value: "off", isStateChange: true)
}

private Map buildSetLevelEvent(value) {
    def intValue = value as Integer
    def newLevel = Math.max(Math.min(intValue, 100), 0)
    Map eventMap = [name: "level", value: newLevel, unit: "%", isStateChange: true]
    return eventMap
}

private implicitOn() {
    if (device.currentValue("switch") != "on") {
        turnOn()
    }
}