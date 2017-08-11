This plugin combines IBMs mainframe compilers with SonarQube. The principle is fairly easy: Compile the source file with one of IBMs compilers and use its output to populate Sonarqubes dashboard. Provide some syntax hightlighting so the code looks nicer.

Supported languages
===================
* Enterprise PL/I (best). Option used: XINFO(XML)
* Enterprise COBOL (fair). Option used: EXIT(ADDEXIT(ELAXMGUX))
* HL Assembler (so, so). Options used: EX(ADX(ELAXHASM))

Prerequisites
=============
* [SonarQube](http://www.sonarqube.org/downloads/) 4.5+
* [SonarQube Scanner](http://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) 2.8+
* Maven 3.0+

Usage
=====
* Compile the project:

        mvn clean package

* Install:

        cp target/sonar-xinfo-plugin-<version>.jar <sonarqube install dir>/extensions/plugins

* Setup the compiler to generate its output in XML format (e.g. PL/I: XINFO(XML))
* Choose the the way how to provide the compiler messages. This plugin comes with 3 preinstalled providers

1.  FILE: The source and the XINFO files are located somewhere in the file system. Use the property sonar.xinfo.root.xinfo to tell the plugin, where to look for them.  Make sure that pgm.pli|asm|cbl corresponds with pgm.xml
2.  FTP: The source files are located in the file system, the XINFO files in a remote partitioned dataset. Use the FTP* properties to tell the plugin how to access them. 
3.  REMOTE COMPILE: The source files are located in the file system, the XINFO files are generated on-the-fly. Use the REMOTE_COMPILE properties to tell the plugin how to generate them. You will have to adjust the *.jcl files in src/main/resources to match your compiler installation.
 
* Analyze your sources with SonarQube using Sonar-scanner:

        sonar-scanner

Known limitations
=================
* Code is tested on Windows using encoding Cp1252
* Code is known ** not to work ** with encoding UTF-8 (seems to be the case for all double-byte-character-sets)
