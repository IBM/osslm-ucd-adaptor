### pull VNF archives from s FTP to local directory
### (c) Copyright,IBM, 2017, Jochen Kappel
### input args:
###   - UCD input properties file  (appName, artDir)
###   - UCD output properties files
###   - config.properties file


#######################################
###  imports
#######################################
import sys
import ConfigParser
import StringIO
import os
from ftplib import FTP
import zipfile

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
#compId = appId + '_component'
compId = appId
print('pulling archive for '+appId )
ftpDir = inProps.get("root","ftpDir")
print('searching archives in '+ftpDir)
artDir = inProps.get("root", "artDir")

class VNFarchive():
    config = ConfigParser.RawConfigParser()

    def __init__(self, configFile, ftpDir, appName ):
        self.config.read( configFile )
        self.ftpServer = self.config.get("FTP","Server")
        self.ftpUser = self.config.get("FTP","User")
        self.ftpPwd = self.config.get("FTP","Password")
        self.ftpDir = ftpDir
        if ( appName.find('.csar') >= 0 ):
            self.archiveName = appName
        else:
            self.archiveName = appName + '.csar'
        self.appName = appName

    def connect(self):
        self.ftp = FTP( self.ftpServer,self.ftpUser, self.ftpPwd)

        if self.ftp:
            self.ftp.set_debuglevel( 1 )
            self.ftp.set_pasv (0) # turn off PASV mode
            return True
        else:
            print('cannot connect to FTP server')
            return False

    def close(self):
        self.ftp.quit()

    def doesExist(self):
        self.ftp.cwd( self.ftpDir )

        try:
            for filename in self.ftp.nlst(self.archiveName ):
                print ('found file '+ self.archiveName)
                return True
        except Exception as e:
            print ('no file with name '+self.archiveName )
            return False

    def download( self, artDir):
        # check that the local directory exists
        self.archiveDir = artDir+'/'+self.appName
        self.archivePathName = artDir+'/'+self.appName + '/'+ self.archiveName
        if not os.path.isdir( self.archiveDir ):
            print('directory does not exist '+ self.archiveDir)
            os.makedirs( self.archiveDir )
            print('directory created '+ self.archiveDir)

        # download file
        fh = open( self.archivePathName, 'wb')
        self.ftp.cwd( self.ftpDir )
        print 'Getting ' + self.archiveName + ' and writing to ' + self.archivePathName
        self.ftp.retrbinary('RETR ' + self.archiveName, fh.write)
        fh.close()
        return True

    def unpack( self ):

         try:
             fh = open( self.archivePathName , 'rb')
             z = zipfile.ZipFile(fh)
             z.extractall( self.archiveDir )
             fh.close()
         except Exception as e:
             print(self.archivePathName + ' is not a ZIP archive')





#######################################
### MAIN DOIT NOW !
#######################################

VNFarch = VNFarchive( sys.argv[3], ftpDir, appId)
if VNFarch.connect():
    #####
    if VNFarch.doesExist():
        VNFarch.download( artDir )
        VNFarch.unpack()
    VNFarch.close()
