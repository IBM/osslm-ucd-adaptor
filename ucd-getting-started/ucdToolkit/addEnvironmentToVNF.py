### add an environment to a VNF application
### IBM, 2017, Jochen Kappel
### input args:
###   - UCD input properties file  (appName, artDir)
###   - UCD output properties files
###   - config.properties file


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
appTag = inProps.get("root","appTag")
envName = inProps.get("root","envName")
resourcePath = inProps.get("root","resourcePath")
artDir = inProps.get("root","artDir")
workDir = inProps.get("root","workDir")

#######################################
### REST calls to UCD/P
#######################################

def createComponentTag( ucdApi, compName, tag):
    desc = 'Managed by'
    resp, status = ucdApi.tagComponent( compName, tag )
    return status

def addComponentToResource( ucdApi, compName, resourcePath ):
    resp, status = ucdApi.getComponent( compName )
    roleId = resp['resourceRole']['id']
    resp, status = ucdApi.getResource( resourcePath)
    resourceId = resp['id']
    pl = {}
    pl['name'] = compName
    pl['roleId'] = roleId
    pl['parentId'] = resourceId
    resp, status = ucdApi.addComponentToResource( pl )


def addBaseResource( ucdApi, appName, envName, resPath ):
    # create environment
    time.sleep(5)
    rd, status = ucdApi.addBaseResource( appName, envName, resPath)

def runAppProcess( ucdApi, appName, compName, envName, artDir, workDir ):
    # create payload
    payload = {}
    payload['application'] = appName
    payload['description'] = 'Download artefacts to '+appName
    payload['applicationProcess'] = '__pushArtefacts'
    payload['environment'] = envName
    payload['onlyChanged'] = "false"
    payload['properties'] = {}
    payload['properties']['artDir'] = artDir
    payload['properties']['workDir'] = workDir
    payload['versions'] = []

    # previous version import takes while.
    time.sleep(5)
    # now poll if done
    for i in range(30):
        rd, status = ucdApi.getComponentVersions( compName )
        print('checking if import is done '+str(i))
        if (rd):
            print('finally got version :' + rd[0]['name'])

            aversion = {}
            aversion['version'] = rd[0]['name']
            aversion['component'] = compName

            payload['versions'].append( aversion )

            rd, status = ucdApi.runApplicationProcess( payload )
            break
        time.sleep(2)




#######################################
### MAIN DOIT NOW !
#######################################

### get UCD API
ucdApi = UcdRestApi( sys.argv[3] )

# create the environemtn
ucdApi.createEnvironment( appName, envName )
# add resource to environment
addBaseResource( ucdApi, appName, envName, resourcePath)

# add this component to the environemnt of the manageing app, this is reuired to push artefacts down
addComponentToResource(ucdApi, compName, resourcePath )

# add a tag if the artefacts are used by other external apps
createComponentTag( ucdApi, compName, appTag )

#now start the above process to download artefacts to the managing app
#
runAppProcess( ucdApi, appName, compName, envName, artDir, workDir )
