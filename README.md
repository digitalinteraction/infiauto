infiauto
========

Algorithms for approximate string matching

Forked from http://www.infiauto.com/projects/datastr/

Wrapped as a Maven project for convenience


Building
========
Run

    mvn install
  
to install the library to your local maven repository. Add the following dependency to your project's pom.xml:

    <dependency>
      <groupId>com.infiauto</groupId>
      <artifactId>infiauto-datastr</artifactId>
      <version>0.3.3</version>
    </dependency>
    
or if you are using SBT:

    libraryDependencies += "com.infiauto" %% "infiauto-datastr" % "0.3.3"

Notes
=====

Current Maven build definition (pom.xml) targets Java 8 (for Intake24 compatibility) -- change that in pom.xml if you need to build for older versions.

License
=======

Licensed under the terms of [GNU Lesser General Public License](http://www.gnu.org/licenses/lgpl-3.0.en.html).
