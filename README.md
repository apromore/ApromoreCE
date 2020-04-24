![apromore](http://apromore.org/wp-content/uploads/2019/11/Apromore-banner_narrow.png "apromore")

# Apromore Community Edition

This repository contains source code of [Apromore Community Edition](https://apromore.org/platform/editions/). This edition includes all the experimental plugins developed by the open-source community on top of Apromore Core. You can choose to use the H2 or MySQL database, the plugins you want to install, configure LDAP access and Apromore Portal’s URL, etc. Below you can find instructions to build this edition locally. Alternatively, you can run it from our public node in [Estonia](http://apromore-ce.cloud.ut.ee) or download a contenarized image in [Docker] (https://github.com/apromore/ApromoreDocker/releases).


## System Requirements
* Windows 7 or newer or Mac OSX 10.8 or newer (other users - check out our [Docker-based version](https://github.com/apromore/ApromoreDocker))
* Java SE 8 ["Server JRE"](https://www.oracle.com/technetwork/java/javase/downloads/server-jre8-downloads-2133154.html) or ["JDK"](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) Edition 1.8. Note that newer versions, including Java SE 11, are currently not supported
* [Apache Maven](https://maven.apache.org/download.cgi) 3.5.2 or newer
* [Apache Ant](https://ant.apache.org/bindownload.cgi) 1.10.1 or newer
* [Lessc](http://lesscss.org/usage/) 3.9.0 or newer
* (optional) [MySQL server](https://dev.mysql.com/downloads/mysql/5.7.html) 5.6 or 5.7. Note that version 8.0 is currently not supported.


## Installation instructions
* Download and unzip the latest [Apromore release](https://github.com/apromore/ApromoreCE/releases/latest) or check out the source code using git: `git clone https://github.com/apromore/ApromoreCore.git`
* Open command prompt/terminal and change to the root of the project `cd ApromoreCE`
* Execute the following commands: `git submodule init` and `git submodule update`.  This populates the ApromoreCore subdirectory.
* Run the maven command `mvn clean install`.  This will build the Apromore manager, portal and editor and all the extra plugins.
* Create an empty H2 database `ant create-h2`.  Only do this once, unless you just want to reset to a blank database later on.
* Run the ant command `ant start-virgo-community`.  This will install, configure and start Eclipse Virgo, and deploy Apromore.
* Open a web browser to [http://localhost:9000](http://localhost:9000). Use "admin”/“password” to access as administrator, or create a new account.
* Keep the prompt/terminal window open.  Ctrl-C on the window will shut the server down.


## Configuration
Many of the configuration options are common to all editions of Apromore.
These are described in the README document of the [Apromore Core repository](https://github.com/apromore/ApromoreCore).

Configuration options specific to the community edition follow.


### Applet Code-Signing
Some of Apromore's features are implemented as Java applets running client-side in the browser.  If you possess an code-signing
certificate (not an SSL certificate), you can edit the top-level `codesigning.properties` file to use your certificate rather
than the self-signed certificate included in the source tree.  This will avoid browser warnings.


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


### PQL setup (optional)
* [LoLA 2.0](http://service-technology.org/lola/) is required for PQL support
* PQL queries over the process store are only supported on MySQL.  Create and populate the database with additional tables for PQL:
```bash
mysql -u root -p apromore < Supplements/database/PQL.MySQL-1.2.sql
```
* In `site.properties`, perform the following changes:
  - Change `pql.numberOfIndexerThreads` to at least 1
  - Change `pql.numberOfQueryThreads` to at least 1
  - Change `pql.lola.dir` to the location of your LoLA 2.0 executable
  - Change the various `pql.mysql.*` properties to match your MySQL database
* In `build.xml`, uncomment the inclusion of the following PQL components in the `pickupRepo` fileset:
  - APQL-Portal-Plugin/target/apql-portal-plugin-1.1.war
  - Apromore-Assembly/PQL-Indexer-Assembly/src/main/resources/103-pql-indexer.plan
    PQL-Logic/target/pql-logic-1.1.jar
  - PQL-Logic-WS/target/pql-logic-ws-1.1.war
  - PQL-Portal-Plugin/target/pql-portal-plugin-1.1.jar
* Also, uncomment the following component in the `copy-virgo` target: `PQL-Indexer-Portal-Plugin/target/pql-indexer-portal-plugin-1.1.jar`


## Common problems

> I grabbed the PQL.MySQL-1.2.sql file directly from the PQL sources and it doesn't work!
* Edit the file and change the uuid attribute of the `jbpt_petri_nodes` table from `VARCHAR(50)` to `VARCHAR(100)` in two places

> Models always show up in the log as unable to be indexed.
* Check that LoLA executable is correctly configured.

