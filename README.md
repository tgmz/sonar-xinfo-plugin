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

Supported languages
===================
* Enterprise PL/I (best). Option used: XINFO(XML)
* Enterprise COBOL (fair). Option used: EXIT(ADDEXIT(ELAXMGUX))
* HL Assembler (so, so). Options used: EX(ADX(ELAXHASM))
* C/C++. Not supported so far.

Prerequisites
=============
* [SonarQube](http://www.sonarqube.org/downloads/) 7.9.6
* [SonarQube Scanner](http://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) sonar-scanner-cli-4.7.0.2747
* Java 11
* Maven 3.0+

Usage
=====
* Clone from GitHub:

        git clone https://github.com/tgmz/sonar-xinfo-plugin.git
        
* Checkout desired Sonar API version:

        git checkout Sonar-API-x.y
        
* Compile the project:

        mvn clean package
        
* Retry if errors occur (happens sometimes, don't know why)

        mvn package
        
* Install:

        cp target/sonar-xinfo-plugin-<version>.jar <sonarqube install dir>/extensions/plugins

* Setup the compiler to generate its output in XML format (e.g. PL/I: XINFO(XML))
* Store sources and XINFO-output somewhere on the filesystem.
* Use the properties sonar.sources to tell the sonar-scanner where to look for the sources and sonar.xinfo.root.xinfo to tell the plugin where to look for the XINFO-files. Make sure that pgm.pli|asm|cbl corresponds with pgm.xml
 
* Analyze your sources with SonarQube using Sonar-scanner:

		cd examples
		sonar-scanner
        
