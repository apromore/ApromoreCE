![apromore](http://apromore.org/wp-content/uploads/2019/11/Apromore-banner_narrow.png "apromore")

# Apromore Community Edition

This repository contains source code of [Apromore Community Edition](https://apromore.org/platform/editions/). This edition includes all the experimental plugins developed by the open-source community on top of Apromore Core. You can choose to use the H2 or MySQL database, the plugins you want to install, configure LDAP access and Apromore Portal’s URL, etc. Below you can find instructions to build this edition locally. Alternatively, you can run it from our public node in [Estonia](http://apromore-ce.cloud.ut.ee) or download a contenarized image in [Docker](https://github.com/apromore/ApromoreDocker/releases). Note: the Docker image is currently available for an older version of Apromore CE (version 7.12). 


## System Requirements
* Windows 7 or newer or Mac OSX 10.8 or newer (other users - check out our [Docker-based version](https://github.com/apromore/ApromoreDocker))
* Java SE 8 ["Server JRE"](https://www.oracle.com/technetwork/java/javase/downloads/server-jre8-downloads-2133154.html) or ["JDK"](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) Edition 1.8. Note that newer versions, including Java SE 11, are currently not supported
* [Apache Maven](https://maven.apache.org/download.cgi) 3.5.2 or newer
* [Apache Ant](https://ant.apache.org/bindownload.cgi) 1.10.1 or newer
* [Lessc](http://lesscss.org/usage/) 3.9.0 or newer
* (optional) [MySQL server](https://dev.mysql.com/downloads/mysql/5.7.html) 5.6 or 5.7. Note that version 8.0 is currently not supported.


## Installation instructions
* Check out the source code using git: `git clone https://github.com/apromore/ApromoreCE.git`
* Open command prompt/terminal and change to the root of the project `cd ApromoreCE`
* Execute the following commands: `git submodule init` and `git submodule update`.  This populates the ApromoreCore subdirectory.
* Given that currently you are on the 'ApromoreCE' directory, go to the 'ApromoreCore' directory 'cd ApromoreCore'
* Checkout and pull the v7.15 branch of ApromoreCore 'git checkout v7.15' and 'git pull'. 
* Run the maven command `mvn clean install`.  This will build the Apromore manager, portal and editor and all the extra plugins.
* Create an empty H2 database `ant create-h2`.  Only do this once, unless you just want to reset to a blank database later on.
* Run the ant command `ant start-virgo-community`.  This will install, configure and start Eclipse Virgo, and deploy Apromore. Only do this once. Later, You can start the server by running the 'startup.sh' script from the '/ApromoreCE/ApromoreCore/Apromore-Assembly/virgo-tomcat-server-3.6.4.RELEASE/bin/' directory.
* Open a web browser to [http://localhost:9000](http://localhost:9000). Use "admin”/“password” to access as administrator, or create a new account.
* Keep the prompt/terminal window open.  Ctrl-C on the window will shut the server down.


## Configuration
Many of the configuration options are common to all editions of Apromore.
These are described in the README document of the [Apromore Core repository](https://github.com/apromore/ApromoreCore).

Configuration options specific to the community edition follow.

### Share file to all users (optional)

* By default Apromore does not allow you to share a file with all users (i.e. the "public" group is not supported by default). You can change this by editing the site.properties file present in the '/ApromoreCE/ApromoreCore/Apromore-Assembly/virgo-tomcat-server-3.6.4.RELEASE/repository/usr/' directory. Specifically, to enable the option to share files and folders with the “public” group, you should set “security.publish.enable = true” in the site.properties file.

### Predictive monitoring setup (optional)

* Predictive monitoring requires the use of MySQL; see the Apromore Core README for MySQL setup instructions.
  Populate the database with additional tables as follows:
```bash
mysql -u root -p apromore < Supplements/database/Nirdizati.MySQL-1.0.sql
```
* Check out predictive monitoring repository from GitHub:
```bash
git clone https://github.com/nirdizati/nirdizati-training-backend.git
```
* Set up additional servers (alongside the Apromore server), as directed in `nirdizati-training-backend/apromore/README.md`
* In site.properties, set the following properties:
  - `training.python` must be set to the location of a Python 3 executable
   - `training.backend` must be directory containing `nirdizati-training-backend`
   - `training.tmpDir` must be a writable directory for temporary files
   - `training.logFile` must be a writable file path for logging
The following properties may usually by left at their default values:
   - `kafka.host` can be left at the default `localhost:909`2, presuming Zookeeper and Kafka are running locally
   - the various `kafka.*.topic` properties should already match those used in the `nirdizati-training-backend` scripts
* Stop and restart the server so that it picks up the changes to `site.properties`.
* Ensure that the following bundles are present in the Virgo `pickup` directory (`ant start-virgo` copies them there on startup):
  - Predictive-Monitor-Logic/target/predictive-monitor-logic-1.0.jar
  - Predictive-Monitor-Portal-Plugin/target/predictive-monitor-portal-plugin-1.0.war
  - Predictor-Training-Portal-Plugin/target/predictor-training-portal-plugin-1.0.war


