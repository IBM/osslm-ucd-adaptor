### wraps UCDP REst API
### IBM, 2017, Jochen Kappel

#######################################
###  imports
#######################################
import urllib3
import base64
import json
import sys
import ConfigParser

class UcdpRestApi():
    ### wraps UCD REST API ###
    http = urllib3.PoolManager()
    config = ConfigParser.RawConfigParser()

    def __init__(self, configFile ):
        self.config.read( configFile )
        self.ucdpBaseUrl = self.config.get("UCDP","ucdpUrl")
        self.ucdpCredentials = self.config.get("UCDP","ucdpCredentials")
        ucdpEpass = base64.b64encode( self.ucdpCredentials )
        self.ucdpHeaders = {'Accept':'application/json',  'Content-Type':'application/json','Authorization':'Basic '+ucdpEpass }
        urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

    def __ucdpRestCall( self, url, method, jsonPayload, headers ):
        encoded_payload = json.dumps(jsonPayload).encode('utf-8')
        r = self.http.request( method, url, headers=headers, body=encoded_payload )
        print ("Calling " + url + ' with status ' +str(r.status) )
        if hasattr( r, 'data') and ( r.data ):
            return json.loads( r.data )
        else:
            return ''

    def getProjects( self ):
        print('get Projects')
        url = self.ucdpBaseUrl + '/landscaper/rest/project'
        respBody = self.__ucdpRestCall( url, 'GET', '', self.ucdpHeaders )
        return respBody

    def createBlueprint( self, jsonPayload ):
        print('create new blueprint')
        url = self.ucdpBaseUrl + '/landscaper/rest/blueprint/'
        respBody = self.__ucdpRestCall( url, 'POST', jsonPayload, self.ucdpHeaders )
        return respBody

    def updateBlueprint( self, bpName, locHeader, jsonPayload ):
        print('update blueprint with HOT file')
        url = self.ucdpBaseUrl + '/landscaper/rest/blueprint/' + bpName
        bpHeaders = self.ucdpHeaders
        bpHeaders['Location'] = locHeader
        respBody = self.__ucdpRestCall( url, 'PUT', jsonPayload, bpHeaders )
        return respBody
