This plugin combines IBMs mainframe compilers with SonarQube. The principle is fairly easy: Compile the source file with one of IBMs compilers and use its output to populate Sonarqubes dashboard. Provide some syntax hightlighting so the code looks nicer.

Supported languages
===================
* Enterprise PL/I (best). Option used: XINFO(XML)
* Enterprise COBOL (fair). Option used: EXIT(ADDEXIT(ELAXMGUX))
* HL Assembler (so, so). Options used: EX(ADX(ELAXHASM))
* C/C++. Not supported so far.

Prerequisites
=============
* [SonarQube](http://www.sonarqube.org/downloads/) 6.3+
* [SonarQube Scanner](http://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) 2.8+
* Maven 3.0+

Usage
=====
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
        
