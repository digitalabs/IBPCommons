IBPCommons
============

Overview
----------
The IBPCommons library contains the features common to projects developed by Digitalabs for GCP. 
It is used by BreedingManager, DatasetImporter, Fieldbook, GermplasmStudyBrowser, IBPWebService and IBPWorkbench.

Prerequisites
---------------
IBPCommons is dependent on Middleware. To build the Middleware project, run the following command in the IBPMiddleware directory:  
<pre>
    mvn clean install
</pre>
    
* To build using a specific configuration, run the following:  
<pre>
    mvn clean install -DenvConfig=dev-config-dir  
</pre>  
 
To Build
----------
* To build IBPCommons, run the following command in the IBPCommons directory:  
<pre>
    mvn clean install
</pre>
    
* To build using a specific configuration, run the following:  
<pre>
    mvn clean install -DenvConfig=dev-config-dir  
</pre>  

* To build using Eclipse, right-click on the IBPCommons project, select Run As --> Maven build..., then input the target discussed above.

  
To Run Tests
--------------
* To run JUnit tests using the command line, issue the following commands in the IBPCommons directory:
  1.  To run all tests: <pre>mvn clean test</pre>
  2.  To run a specific test class: <pre>mvn clean test -Dtest=TestClassName</pre>
  3.  To run a specific test function: <pre>mvn clean test -Dtest=TestClassName#testFunctionName</pre>

* You need to specify the IBDB database to connect to in the testDatabaseConfig.properties file. 

* All JUnit test suites require the rice database, except for GenotypicDataManager that uses the groundnut crop in testing.

* Similar to building IBPCommons, add the -DenvConfig parameter to use a specific configuration.

* To run JUnit tests using Eclipse, right-click on the specific JUnit test suite in the IBPCommons project, select Run As --> JUnit test.
 
To Use
-----------
* To add IBPCommons as a dependency to your project using Apache Maven, add the following to your list of dependencies:  
<pre>
	<groupId>org.generationcp</groupId>
    <artifactId>ibpcommons</artifactId>
    <version>1.3.0</version>
      
</pre>

* Take note of the version.  Use of the latest version is recommended.  


Checking out the IBPCommons Project
-------------------
* The project is stored in the GIT repository hosted at github.com.  The URL for the repository is: 
<pre>
    https://github.com/digitalabs/IBPCommons   
</pre>
* An anonymous account may be used to checkout the project.  
* No username and password is required.  You can also browse the content of the repository using the same URL.  

