### create UCDP blueprint
### IBM, 2017, Jochen Kappel
### input args:
###   - UCD input properties file  (appName, artDir)
###   - UCD output properties files
###   - config.properties file


#######################################
###  imports
#######################################
import urllib3
import base64
import json
import sys
import ConfigParser
import glob
import StringIO
from UcdpRestApi import UcdpRestApi

#######################################
###  getting config and templates
#######################################

### read input parameters from input properties file
ini_str = '[root]\n' + open(sys.argv[1], 'r').read()
ini_fp = StringIO.StringIO(ini_str)
inProps = ConfigParser.RawConfigParser()
inProps.readfp( ini_fp )
### input properties:
appId = inProps.get("root","appName")
compId = appId
#compId = appId + '_component'
bpName = inProps.get("root","bpName")
appTag = inProps.get("root","appTag")
artDir =  inProps.get("root","artDir")
print('creating blueprint '+bpName+' for '+ appId)

#######################################
### REST calls to UCD/P
#######################################

def getFolder( appTag ):
    print('get Folders')
    resp = ucdpApi.getProjects()

    for f in resp[0]['folders']:
        if ( f['name'] == appTag + '/'):
            return f['location']

    return ""


def createBlueprint( bpName, appName, folderLoc ):
    # create payload
    payload = {}
    payload['name'] = appName
    payload['description'] = "HEAT template for application "+ appName

    if ( folderLoc != "" ):
        payload['folderLocation'] = folderLoc
        payload['location'] = folderLoc

    resp = ucdpApi.createBlueprint( payload  )
    return resp['location']

def loadBlueprintFile( appName, artDir, bpName, bpLoc, hot  ):
    print('update blueprint with HEAT file')
    # create payload
    payload = {}
    payload['document'] = hot

    resp = ucdpApi.updateBlueprint( bpName, bpLoc, payload )
    return


#######################################
### Helpers
#######################################

def readHotFile( artDir, appId, bpName ):
    hf = open( artDir + '/' + compId + '/HEAT/'+ bpName +'.yaml'  ,'r')
    hfs = ''
    #hf = open( bpName + '.yaml' ,'r')
    hfs = hf.read()
    return hfs

#######################################
### MAIN DOIT NOW !
#######################################
### get UCD API
ucdpApi = UcdpRestApi( sys.argv[3] )

hotFile = readHotFile( artDir, appId, bpName )
if ( hotFile == '' ):
    print('no file found ! ' + bpName)
else:
    # folderLoc = getFolder( appTag ) folder a re not supported by the ucd-driver for NOW
    folderLoc = getFolder( 'default')
    bpLoc = createBlueprint( bpName, appId, folderLoc )
    loadBlueprintFile( appId, artDir, bpName, bpLoc, hotFile )
