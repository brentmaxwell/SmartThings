/**
 *  Tile
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
	definition (name: "Tile", namespace: "thebrent", author: "Brent Maxwell", cstHandler: true) {
		capability "Audio Mute"
		capability "Audio Notification"
		capability "Audio Volume"
		capability "Battery"
		capability "Button"
		capability "Chime"
		capability "Execute"
		capability "Geolocation"
		capability "Momentary"
		capability "Notification"
		capability "Object Detection"
		capability "Stateless Audio Mute Button"
		capability "Stateless Audio Volume Button"
		capability "Tone"
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
	// TODO: handle 'mute' attribute
	// TODO: handle 'volume' attribute
	// TODO: handle 'battery' attribute
	// TODO: handle 'button' attribute
	// TODO: handle 'numberOfButtons' attribute
	// TODO: handle 'supportedButtonValues' attribute
	// TODO: handle 'chime' attribute
	// TODO: handle 'data' attribute
	// TODO: handle 'latitude' attribute
	// TODO: handle 'longitude' attribute
	// TODO: handle 'method' attribute
	// TODO: handle 'accuracy' attribute
	// TODO: handle 'altitudeAccuracy' attribute
	// TODO: handle 'heading' attribute
	// TODO: handle 'speed' attribute
	// TODO: handle 'lastUpdateTime' attribute
	// TODO: handle 'supportedValues' attribute
	// TODO: handle 'detected' attribute
	// TODO: handle 'availableAudioMuteButtons' attribute
	// TODO: handle 'availableAudioVolumeButtons' attribute

}

// handle commands
def setMute() {
	log.debug "Executing 'setMute'"
	// TODO: handle 'setMute' command
}

def mute() {
	log.debug "Executing 'mute'"
	// TODO: handle 'mute' command
}

def unmute() {
	log.debug "Executing 'unmute'"
	// TODO: handle 'unmute' command
}

def playTrack() {
	log.debug "Executing 'playTrack'"
	// TODO: handle 'playTrack' command
}

def playTrackAndResume() {
	log.debug "Executing 'playTrackAndResume'"
	// TODO: handle 'playTrackAndResume' command
}

def playTrackAndRestore() {
	log.debug "Executing 'playTrackAndRestore'"
	// TODO: handle 'playTrackAndRestore' command
}

def setVolume() {
	log.debug "Executing 'setVolume'"
	// TODO: handle 'setVolume' command
}

def volumeUp() {
	log.debug "Executing 'volumeUp'"
	// TODO: handle 'volumeUp' command
}

def volumeDown() {
	log.debug "Executing 'volumeDown'"
	// TODO: handle 'volumeDown' command
}

def chime() {
	log.debug "Executing 'chime'"
	// TODO: handle 'chime' command
}

def off() {
	log.debug "Executing 'off'"
	// TODO: handle 'off' command
}

def execute() {
	log.debug "Executing 'execute'"
	// TODO: handle 'execute' command
}

def push() {
	log.debug "Executing 'push'"
	// TODO: handle 'push' command
}

def deviceNotification() {
	log.debug "Executing 'deviceNotification'"
	// TODO: handle 'deviceNotification' command
}

def setButton() {
	log.debug "Executing 'setButton'"
	// TODO: handle 'setButton' command
}





def beep() {
	log.debug "Executing 'beep'"
	// TODO: handle 'beep' command
}