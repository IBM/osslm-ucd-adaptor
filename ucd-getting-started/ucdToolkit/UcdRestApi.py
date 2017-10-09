### wraps UCD REst API
### IBM, 2017, Jochen Kappel

#######################################
###  imports
#######################################
import urllib3
import base64
import json
import sys
import ConfigParser
from urllib import urlencode
import subprocess


class UcdRestApi():
    ### wraps UCD REST API ###
    http = urllib3.PoolManager()
    config = ConfigParser.RawConfigParser()

    def __init__(self, configFile ):
        self.config.read( configFile )
        self.ucdBaseUrl = self.config.get("UCD","ucdUrl")
        self.ucdCredentials = self.config.get("UCD","ucdCredentials")
        ucdEpass = base64.b64encode( self.ucdCredentials )
        self.ucdHeaders = {'Accept':'application/json',  'Content-Type':'application/json','Authorization':'Basic '+ucdEpass }
        urllib3.disable_warnings()

    def __ucdRestCall( self, url, method, jsonPayload ):
        if (jsonPayload != ''):
            encoded_payload = json.dumps(jsonPayload).encode('utf-8')
            r = self.http.request( method, url, headers=self.ucdHeaders, body=encoded_payload )
        else:
            r = self.http.request( method, url, headers=self.ucdHeaders)
        print ("Calling " + url + ' with status ' +str(r.status) )
        # print(r)
        if hasattr( r, 'data') and ( r.data ) and ( r.status == 200):
            return json.loads( r.data ), r.status
        else:
            return '', r.status

    def createApplicationProcess( self, jsonPayload ):
        print('create new application process')
        url = self.ucdBaseUrl + '/cli/applicationProcess/create'
        return self.__ucdRestCall( url, 'PUT', jsonPayload )

    def createComponentProcess( self, jsonPayload ):
        print('create new component process')
        url = self.ucdBaseUrl + '/cli/componentProcess/create'
        return  self.__ucdRestCall( url, 'PUT', jsonPayload )

    def addComponentProcessProperties( self, jsonPayload, cpId ):
        print('add component process properties')
        url = self.ucdBaseUrl + '/rest/deploy/componentProcess/' + cpId + '/savePropDef'
        return self.__ucdRestCall( url, 'PUT', jsonPayload )


    def createComponent( self, jsonPayload ):
        print('create component')
        url = self.ucdBaseUrl + '/cli/component/create'
        return self.__ucdRestCall( url, 'PUT', jsonPayload )

    def getComponent( self, cpId ):
        print('get component')
        url = self.ucdBaseUrl + '/cli/component/info?component='+cpId
        return self.__ucdRestCall( url, 'GET', '' )

    def getResource( self, rId ):
        print('get resource')
        url = self.ucdBaseUrl + '/cli/resource/info?resource='+rId
        return self.__ucdRestCall( url, 'GET', '' )

    def addComponentToResource( self, jsonPayload ):
        print('add component to resource')
        url = self.ucdBaseUrl + '/rest/resource/resource'
        return self.__ucdRestCall( url, 'PUT', jsonPayload )

    def tagComponent( self, cpId, tag ):
        print('create component tag '+tag+' for '+cpId)
        url = self.ucdBaseUrl + '/cli/component/tag?component='+cpId+'&tag='+tag
        # ok return 204
        return self.__ucdRestCall( url, 'PUT','' )

    def assignComponent( self, cpId, appId, ):
        print('add component '+cpId+' to '+appId )
        url = self.ucdBaseUrl + '/cli/application/addComponentToApp?component='+cpId+'&application='+appId
        return self.__ucdRestCall( url, 'PUT' ,'' )

    def runVersionImport( self, jsonPayload ):
        print('running version import')
        url = self.ucdBaseUrl + '/cli/component/integrate'
        return self.__ucdRestCall( url, 'PUT',jsonPayload )

    def getComponentVersions( self, cpId ):
        print('getting version')
        url = self.ucdBaseUrl + '/cli/component/versions?component='+cpId+'&numResults=1'
        return self.__ucdRestCall( url, 'GET', '' )

    def runApplicationProcess( self, jsonPayload ):
        print('running an application process')
        url = self.ucdBaseUrl + '/cli/applicationProcessRequest/request'
        return self.__ucdRestCall( url, 'PUT',jsonPayload )

    def createEnvironment( self, appName, envName ):
        print('creating an environment')
        #  there sees to be a bug, calling with curl works, no way with urllib3...

        #encoded_args = urlencode({'application': appId, 'name': envName, 'color':'%23ff0000' })
        #url = self.ucdBaseUrl + '/cli/environment/createEnvironment?' + encoded_args
        #return self.__ucdRestCall( url, 'PUT', '' )
        bash_com = 'curl -k -u '+self.ucdCredentials+'  -X PUT "'+self.ucdBaseUrl+'/cli/environment/createEnvironment?application='+appName+'&name='+envName+'&color=%23ff0000"'
        subprocess.Popen(bash_com, shell=True)

    def addBaseResource( self, appId, envName, resPath ):
        print('add resrouce to environment')
        url = self.ucdBaseUrl + '/cli/environment/addBaseResource?application='+appId+'&environment='+envName+'&resource='+resPath
        return self.__ucdRestCall( url, 'PUT', '' )
