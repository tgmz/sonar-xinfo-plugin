<!---
/*******************************************************************************
  * Copyright (c) 20.10.2021 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v2.0
  * which accompanies this distribution, and is available at
  * http://www.eclipse.org/legal/epl-v20.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
-->
This plugin combines IBMs mainframe compilers with SonarQube. The principle is fairly easy: Compile the source file with one of IBMs compilers and use its output to populate Sonarqubes dashboard. Provide some syntax hightlighting so the code looks nicer.

# Supported languages
* Enterprise PL/I (best). Option used: XINFO(XML)
* Enterprise COBOL (fair). Option used: EXIT(ADDEXIT(ELAXMGUX))
* HL Assembler (fair). Options used: EX(ADX(ELAXHASM))
* C/C++. (fair). Options used: /CXX EVENTS

# Prerequisites
* [SonarQube](http://www.sonarqube.org/downloads/) [6.4, 10.4.1]
* [SonarQube Scanner](http://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) sonar-scanner-cli-4.7.0.2747+
* Java 17
* Maven 3.8.1+

# Usage
* Clone from GitHub:

        git clone https://github.com/tgmz/sonar-xinfo-plugin.git
        
* Checkout desired Sonar API version:

        git checkout Sonar-API-x.y
        
* Compile the project (From version 2.0.0,  "install" is mandatory. Skip tests because the contain test cases which need access to a z/OS system)

        mvn install -DskipTests

* Install to Sonarqube

        cp sonar-xinfo-plugin/target/sonar-xinfo-plugin-<version>.jar <sonarqube install dir>/extensions/plugins
        
* Restart SonarQube

## Static analysis
* Setup the compiler to generate its output in XML or EVENTS format (e.g. PL/I: XINFO(XML), or C++: /CXX EVENTS) and compile the sources you want to analyze
* Store sources and XINFO-output somewhere on your file system. (Windows or *NIX. z/OS is not supported)
* Use the properties sonar.sources to tell the sonar-scanner where to look for the sources and sonar.xinfo.root.xinfo to tell the plugin where to look for the XINFO-files. Make sure that pgm.pli|asm|cbl|c|cpp corresponds with pgm.xml|event
 
* Analyze your sources with SonarQube using Sonar-scanner or Maven:

		cd examples
		sonar-scanner-cli-4.7.0.2747-<target platform>/sonar-scanner
		mvn sonar:sonar
        
## Dynamic analysis
* Tailor the example JCL procedures in "examples/jcl" for your system and store them in your system's PROCLIB
* Provide credentials etc. Use template "examples/sonar-project-on-the-fly.properties"
* Analyze

## More
See this projects [Wiki](https://github.com/tgmz/sonar-xinfo-plugin/wiki) 


