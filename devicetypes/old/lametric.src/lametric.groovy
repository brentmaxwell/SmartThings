/**
 *  LaMetric v2
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
  definition (name: "LaMetric", namespace: "thebrent", author: "Brent Maxwell", cstHandler: true) {
    //capability "Alarm"
    capability "Audio Mute"
    capability "Audio Volume"
    capability "Button"
    capability "Execute"
    capability "Notification"
    capability "Signal Strength"
    capability "Switch Level"
    capability "Polling"
    capability "Refresh"
    capability "Health Check"

    attribute "id", "string"
    attribute "name", "string"
    attribute "serialNumber", "string"
    attribute "osVersion", "string"
    attribute "model", "string"
    attribute "scroll_mode", "enum", ["auto", "manual", "kiosk"]
    attribute "currentIP", "string"
    attribute "apiKey", "string"
    attribute "bluetoothState", "enum", ["off", "active", "discoverable", "pairable"]
    attribute "mode", "enum", ["offline","online"]
    attribute "refreshStatus", "enum", ["loading", "idle"]
    
    command "bluetoothOn"
    command "bluetoothOff"
  }


  simulator {
    // TODO: define status and reply messages here
  }

  tiles(scale: 2) {
    controlTile("volume", "device.volume", "slider", height: 2, width: 2) {
      state "default", action:"audio volume.setVolume"
    }
    standardTile("mute", "device.mute", width: 2, height: 2) {
      state "muted", icon: "st.custom.sonos.muted", backgroundColor: "#ffffff", action: "audio mute.unmute"
      state "unmuted", icon: "st.custom.sonos.unmuted", backgroundColor: "#00a0dc", action: "audio mute.mute"
    }
    controlTile("brightness", "device.level", "slider", height: 2, width: 2) {
      state "default", action:"switch level.setLevel"
    }
    
    standardTile("refresh", "device.refreshStatus", decoration: "flat", width: 2, height: 2) {
      state "idle", icon: "st.secondary.refresh", action: "refresh.refresh"
      state "loading", icon: "st.motion.motion.active", action: "refresh.refresh"
      state "error", icon: "st.secondary.refresh", action: "refresh.refresh",  backgroundColor: "#e86d13"
    }
    standardTile("bluetooth", "device.bluetoothState", width:2, height: 2) {
     	state "off", label: '${currentValue}', action: "bluetoothOn"
      state "active", label: '${currentValue}', action: "bluetoothOff"
      state "discoverable", label: '${currentValue}', action: "bluetoothOff"
      state "pairable", label: '${currentValue}', action: "bluetoothOff"
    }
    // valueTile("serialNumber", "device.serialNumber", decoration: "flat", height: 1, width: 2, inactiveLabel: false) {
		// 	state "default", label:'SN: ${currentValue}'
		// }
		valueTile("networkAddress", "device.currentIP", decoration: "flat", height: 1, width: 2, inactiveLabel: false) {
			state "default", label:'${currentValue}', height: 1, width: 2, inactiveLabel: false
		}
    valueTile("signalStrength", "device.lqi", width:1, height:1) {
      state "default", label: '${currentValue}'
    }
		main ([])
		details(["volume", "mute", "brightness", "bluetooth", "refresh", "serialNumber","networkAddress", "signalStrength"])
	}
  preferences {
    input name: "defaultNotificationSound", type: "enum", title: "Default Notification Sound", description: "Default Notification Sound", options: ["car", "bicycle", "cash", "cat", "dog", "dog2", "energy", "knock-knock", "letter_email", "lose1", "lose2", "negative1", "negative2", "negative3", "negative4", "negative5", "notification", "notification2", "notification3", "notification4", "open_door", "positive1", "positive2", "positive3", "positive4", "positive5", "positive6", "statistic", "thunder", "water1", "water2", "win", "win2", "wind", "wind_short", "alarm1", "alarm2","alarm3", "alarm4", "alarm5", "alarm6", "alarm7", "alarm8", "alarm9", "alarm10", "alarm11", "alarm12", "alarm13"]
    input name: "defaultNotificationIcon", type: "enum", title: "Default Notification Icon", description: "Default Notification Icon", options: ["none", "info", "alert"]
    input name: "defaultNotificationPriority", type: "enum", title: "Default Notification Priority", description: "Default Notification Priority", options: ["info", "warning", "critical"]
  }
}

// parse events into attributes
def parse(String description) {
  log.debug "Parsing '${description}'"
  // TODO: handle 'mute' attribute
  // TODO: handle 'volume' attribute
  // TODO: handle 'button' attribute
  // TODO: handle 'numberOfButtons' attribute
  // TODO: handle 'supportedButtonValues' attribute
  // TODO: handle 'chime' attribute
  // TODO: handle 'data' attribute
  // TODO: handle 'lqi' attribute
  // TODO: handle 'rssi' attribute
  // TODO: handle 'wirelessOperatingMode' attribute
}

def refresh() {
  log.trace "refresh()"
  parent.requestRefreshDeviceInfo(device.deviceNetworkId)
  state.previousVolume = volume
}

// Alarm

// def off() {
//   log.trace "Executing 'off'"
//   // TODO: handle 'off' command
// }

// def strobe() {
//   log.trace "Executing 'strobe'"
//   // TODO: handle 'strobe' command
// }

// def siren() {
//   log.trace "Executing 'siren'"
//   // TODO: handle 'siren' command
// }

// def both() {
//   log.trace "Executing 'both'"
//   // TODO: handle 'both' command
// }

// Audio Mute

def setMute(value) {
  log.trace "setMute(${value})"
  if(device.currentValue("mute") == "muted") {
    unmute()
  }
  else {
    mute()
  }
  log.trace ("result ${result}"); 
}

def mute() {
  log.trace "mute()"
  state.previousVolume = device.currentValue("volume")
  def result = setVolume(0);
  sendEvent(name: "mute", value: "muted")
  return result
}

def unmute() {
  log.trace "unmute()"
  def previousVolume = state.previousVolume
  log.debug previousVolume
  def result = setVolume(previousVolume)
  sendEvent(name: "mute", value: "unmuted")
  return result
  
}

// Audio Volume

def setVolume(volume) {
  log.trace "setVolume(${volume})"
  def result = parent.sendApiCallToDevice(device.deviceNetworkId, "PUT", '/api/v2/device/audio', [ volume: volume ])
  return result
}

def volumeUp() {
  log.trace "volumeUp()"
  def newVolume = device.currentValue("volume") + 1
  def result = setVolume(newVolume)
  return result
}

def volumeDown() {
  log.trace "volumeDown()"
  def newVolume = device.currentValue("volume") -1
  def result = setVolume(newVolume)
  return result
}

// Execute

def execute() {
  log.trace "execute()"
  // TODO: handle 'execute' command
}

// Notification
def deviceNotification(notificationText) {
  log.trace "deviceNotification()"
  def notificationObject = [
    model: [
      frames: [
        [
          text: notificationText
        ],
      ],
      sound: [
        category: "notifications",
        id: "notification",
      ],
    ]
  ]
  def result = parent.sendApiCallToDevice(device.deviceNetworkId, "POST", '/api/v2/device/notifications', notificationObject)
}

// Switch Level
def setLevel(level) {
  log.trace "setLevel(${level})"
  def result = parent.sendApiCallToDevice(device.deviceNetworkId, "PUT", '/api/v2/device/display', [ brightness: level ])
  return result
}

def bluetoothOn() {
  return setBluetoothState(true);
}

def bluetoothOff() {
  return setBluetoothState(false);
}

def setBluetoothState(state) {
  log.trace "setBluetoothState(${state})"
  def result = parent.sendApiCallToDevice(device.deviceNetworkId, "PUT", '/api/v2/device/bluetooth', [ active: state ])
  refresh()
  return result
}