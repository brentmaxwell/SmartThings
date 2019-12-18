/**
 *  Virtual Device Creator
 *
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
        name: "API Passthrough",
        namespace: "thebrent",
        author: "Brent Maxwell",
        description: "Run API commands on your local network",
        iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
        iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
        iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
        singleInstance: true,
        pausable: false
)


preferences {
    page name: "mainPage", title: "Click save to create a new virtual device.", install: true, uninstall: true
}

mappings {
  path("/send") {
    action: [
        GET: "sendGet",
        POST: "sendPost"
        PUT: "sendPut"
    ]
  }
}

def mainPage() {
  dynamicPage(name: "mainPage") {
    section("Endpoint") {
      paragraph "${state.endpoint}"
    }
    remove("Remove (Includes Devices)", "This will remove all virtual devices created through this app.")
  }
}

def installed() {
    log.debug "Installed with settings: ${settings}"
}

def uninstalled() {
}

def updated() {
    initialize()
}

def initialize() {
    setupEndpoint();
}

def setupEndpoint() {
  if(!state.endpoint) {
    def accessToken = createAccessToken()
    if (accessToken) {
      state.endpoint = apiServerUrl("/api/token/${accessToken}/smartapps/installations/${app.id}/")				
    }
  }
}

def sendGet() {
  send()
}

def sendPost() {

}

def sendPut() {
}

def send(host, port, path, method, headers, query, body) {
  def params = [
    path: path,
    method: method,
    headers: headers,
    query: query,
    body: body
  ]
  def options = [
    callback: sendCallback
  ]
  sendHubCommand(new HubAction(params,null,options))
}

def sendCallback(hubResponse) {
  return hubResponse;
}