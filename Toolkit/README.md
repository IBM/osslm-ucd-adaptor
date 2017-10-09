# ALM UrbanCode Deploy Artefacts

This project contains artefacts that are required to enable IBM UrbanCode Deploy (UCD) to seamlessly work with the ALM 1.1 ucd-driver and act as resource manager for ALM.

It contains
- Templates
- a UCD Toolkit
- a UCD VNF on-boarding process

NOTE: you **MUST** install the templates for the ucd-driver to work with UCD.  
The toolkit as well as the generic process are **OPTIONAL**. They are just provided to save you a lot of manual setup steps to load your VNF Component Package into UCD/UCDP.

### Pre-Requisites
- IBM UrbanCode Deploy >= 6.2.4  
- IBM UrbanCode Blueprint Designer >= 6.2.4  

---

## Templates
There are two templates contained in this project

- almApplication - exposing the northbound APIs to the UCD-driver
- almComponent - implementing a generic call to the VNF lifecycle operations

### Pre-Requisites
None

### Installation
1. download this project
2. the templates are in the *ucdTemplates* folder
3. login to your target UCD  

#### Install the Component Template  
1. Click on *Components -> Templates -> Import Template*
2. Use settings:  
    Upgrade Template: *No*  
    Generic Process Upgrade Type: *Use Existing Process*  
    Browse: *select the ucdTemplates/almComponent.json file*  
3. Click *Submit*

Verify that the template is loaded.

#### Install the Application Template
1. Click on *Application -> Templates -> Import Application Template*
2. Use settings:  
    Upgrade Application Template: *No*  
    Generic Process Upgrade Type: *Use Existing Process*  
    Generic Resource Template Upgrade Type: *Use Existing Resource Template*      
    Browse: *select the ucdTemplates/almApplication.json file*  
3. Click *Submit*

Verify that the template is loaded.  

---

## VNF Onboarding Toolkit
Before installing the toolkit you need to provide your UCD credentials. The toolkit uses the UCD REST API which requires authentication.  

### Pre-Requisites
None

### Before you Install
1. download this project
2. the templates are in the *ucdToolkit* folder
1. open the file *ucdToolkit/config.properties* in your preferred text editor
2. Validate the ports for UCD and UCDP urls. Modify if required
3. Replace "*admin:passw0rd*" with the credentials of UCD and UCDP users that have access rights to use the REST API in your environment
4. Save the config.properties file
5. Add all files in the ucdTookit folder to a zip-archive

### Loading the toolkit  
3. login to your target UCD
2. Click on *Settings -> Automation Plugins -> Load Plugin*
3. Browse to the zip-archive you have created in step 7.
4. Click *Submit*
5. Verify that the plugin *VNF Onboarding* is listed
---
## The Generic Process
This generic UCD process onboards a VNF component package into UCD.
In the current implementation it does **NOT** validate the contents of the VNF component package. This will be subject of a future CI pipeline (e.g. using Jenkins with UCD) approach, which may replace this process.

This process executes the following tasks
- create a UCD application for the VNF component package
- create a UCD component for the VNF component package
- create a tag and tag application and component
- load the contents of the package lifecycle and operations folders as a first version of the component
- create component processes based on the descriptors in the operations folder

### Pre-Requisites
- the application template
- the component template
- the VNF Onboarding toolkit
- a folder *"/var/vnf"* on your UCD server

### Installation
1. download this project
2. the process is in the *ucdProcesses* folder
1. login to your target UCD
2. Click on *Processes -> Import Process*
3. Browse to the process descriptor *ucdProcesses/onboard+VNF+Package.json*
4. Click *Submit*
3. Browse to the process descriptor *ucdProcesses/remove+VNF+Package.json*
4. Click *Submit*
5. Verify that these generic processes are listed:  
  - onboard VNF Package
  - remove VNF Package
