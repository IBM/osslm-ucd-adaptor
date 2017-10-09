### create UCD component, tag it and assign to an application
### IBM, 2016, Jochen Kappel
### input args:
###   - UCD input properties file  (appName, artDir)
###   - UCD output properties files
###   - config.properties file
###   - component.json template file


#######################################
###  imports
#######################################
import json
import sys
import ConfigParser
import glob
import StringIO
import time
from UcdRestApi import UcdRestApi

#######################################
###  getting config and templates
#######################################

### read input parameters from input properties file
ini_str = '[root]\n' + open(sys.argv[1], 'r').read()
ini_fp = StringIO.StringIO(ini_str)
inProps = ConfigParser.RawConfigParser()
inProps.readfp( ini_fp )
### input properties:
appName = inProps.get("root","appName")
compName = inProps.get("root","compName")
print('generating component for '+appName+': '+compName)
template = inProps.get("root","templateName")
appTag = inProps.get("root","appTag")
artDir = inProps.get("root","artDir")


# load component json
with open(sys.argv[4]) as json_data:
    cD = json.load(json_data)
    print ('component template read')



#######################################
### REST calls to UCD/P
#######################################

def createComponent( ucdApi, cD, appName, compName, template, artDir ):
    # create payload
    payload = cD
    payload['name'] = compName
    payload['description'] = 'Component to hold artefacts for '+ appName
    payload['properties']['FileSystemComponentProperties/basePath'] = artDir
    if template:
        payload['templateName'] = template
        payload['templateVersion'] = ""
    resp, status = ucdApi.createComponent( payload )

    return status, resp['id']

def createComponentTag( ucdApi, compName, tag):
    desc = 'Managed by'
    resp, status = ucdApi.tagComponent( compName, tag )
    return status

def addComponentToApp( ucdApi, compName, appName):
    resp, status = ucdApi.assignComponent( compName, appName )
    return status

def importVersion( ucdApi, compId ):
    # create payload
    payload = {}
    payload['component'] = compId

    rd, status = ucdApi.runVersionImport( payload )

#######################################
### MAIN DOIT NOW !
#######################################

### get UCD API
ucdApi = UcdRestApi( sys.argv[3] )

#create the base component
status, cpId = createComponent( ucdApi, cD,appName, compName, template, artDir )
print (cpId)
if (status < 300 ):
    # all fine start the artefact upload to the component (version = 1)
    importVersion( ucdApi, cpId )

    # lets tag the component with the app that owns the artefacts
    createComponentTag( ucdApi, compName, appTag  )

    # and add the component to this app
    addComponentToApp( ucdApi, compName, appName )
