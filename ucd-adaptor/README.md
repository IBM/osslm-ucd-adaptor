# README for UrbanCode Deploy Resource Manager Driver #

### Building the driver ###

##### Prerequisites #####

A Java 1.8 JDK is required to build this driver. An internet connection is also required to download any dependencies.

##### Build Instructions #####

In the root folder of the project, run the following command to build the software.

`mvnw clean package`

If this is successful, a jar file called `ucd-driver-1.0.0.jar` (or similar) will be created in the `target/` directory.

### Running the driver ###

##### Prerequisites #####

A Java 1.8 JRE is required to run the driver.

##### Instructions #####

* Navigate to the directory with the jar file
* To configure the driver, create an `application.yml` file and add any overrides (see below for an example)
* Run to following command

`java -jar ucd-driver-1.0.0.jar`

### Configuration Properties Example (application.yml) ###

```yaml
urbancodedeploy:
    allow-self-signed-certificates: true
    environments:
        admin@local:
            ucd-server:
                url: https://192.168.122.25:8443
                username: admin
                password: admin
            ucd-patterns:
                url: https://192.168.122.26:8443
                username: ucdpadmin
                password: ucdpadmin
```