### create UCD processes
### IBM, 2016, Jochen Kappel
### input args:
###   - UCD input properties file  (appName, artDir)
###   - UCD output properties files
###   - config.properties file
###   - processOps template file
###   - applicationOps template file

#######################################
###  imports
#######################################
import json
import sys
import ConfigParser
import glob
import StringIO
from UcdRestApi import UcdRestApi

#######################################
###  getting input properties
#######################################

### read input properties file
ini_str = '[root]\n' + open(sys.argv[1], 'r').read()
ini_fp = StringIO.StringIO(ini_str)
inProps = ConfigParser.RawConfigParser()
inProps.readfp( ini_fp )

### input properties:
appId = inProps.get("root","appName")
#compId = appId + '_component'
compId = appId
print('generating processes for '+appId+' and '+compId)
artDir = inProps.get("root","artDir") +  '/'+ compId + '/operations'
print('searching operations in '+artDir)


with open(sys.argv[4]) as json_data:
    cP = json.load(json_data)
    print ('component Op template read')

with open(sys.argv[5]) as json_data:
    aP = json.load(json_data)
    print ('Application Op templte read')


#######################################
### REST calls to UCD/P
#######################################

def createApplicationOperation( ucdApi, appId, compName, oD, aP, pD, pP ):
    # create payload
    aP['rootActivity']['children'][1]['componentName'] = compName
    aP['rootActivity']['children'][1]['children'][0]['componentProcessName'] = oD['name']
    aP['rootActivity']['children'][1]['children'][0]['componentName'] = compName
    aP['rootActivity']['children'][1]['children'][0]['properties']= pP
    payload = aP
    payload['application'] = appId
    payload['name'] = oD['name']
    payload['description'] = oD['description']
    payload['propDefs'] = pD

    ucdApi.createApplicationProcess( payload )
    return

def createComponentOperation( ucdApi, compId, oD, cP, cmd, cpD ):
    cP['rootActivity']['children'][1]['properties']['scriptBody'] = cmd
    # print( cP['rootActivity']['children'][1]['properties']['scriptBody'] )

    # create payload
    payload = cP
    payload['name'] = oD['name']
    payload['description'] = oD['description']
    payload['component'] = compId

    rd, status = ucdApi.createComponentProcess( payload )

    for i in cpD:
        ucdApi.addComponentProcessProperties(  i , rd['id'])
    return

#######################################
### Helpers
#######################################

def buildProcessPropDef( oD ):
    print('building PropDefs')
    pD = oD['propDefs']
    v = 0
    for i in pD:
        del i['sequence']
        v += 1
        i['componentProcessVersion'] = v
    return pD

def buildCommand( oD ):
    print('building command line')
    pD = oD['propDefs']
    pList = []
    for i in pD:
        pList.insert( i['sequence'], i['name'])
    c = ''
    for i in pList:
        c = c + ' ${p:' + i + '}'
    c = 'sudo ./' + oD['name'] + '.sh ' + c
    return c

def buildProcessProperties( oD ):
    print('building properties')
    pD = oD['propDefs']
    pList = []
    for i in pD:
        pList.insert( i['sequence'], i['name'])
    c = {}
    for i in pList:
        c[i] = '${p:' + i + '}'
    return c

#######################################
### MAIN DOIT NOW !
#######################################

### get UCD API
ucdApi = UcdRestApi( sys.argv[3] )

for file in glob.glob( artDir + '/*.json'):
    # read operation definition
    print ('now working on '+file)
    with open( file) as json_data:
        oD = json.load(json_data)

    command = buildCommand( oD )
    props = buildProcessProperties( oD )
    cpD = buildProcessPropDef( oD )

    createComponentOperation( ucdApi, compId, oD, cP, command, cpD )
    createApplicationOperation( ucdApi, appId , compId, oD, aP, cpD, props  )
