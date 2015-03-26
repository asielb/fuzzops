# Introduction #

This document is meant to describe how to set up a development environment for fuzzops so you can write and build the code.


# Setup Steps #

  1. Download and install the [LDSTech IDE](http://tech.lds.org/devcentral/)
  1. Checkout the code from svn (see source tab)
  1. Open IDE
    1. Choose File -> Import
    1. Select Maven -> Existing Maven Projects
    1. Choose the directory you checked out the code to and import

After the import process is complete you have a couple more manual steps.

  1. Right click on the project and select properties
    1. choose Java Build Path in the left pane
      1. Select the Libraries tab
      1. Click Add Library...
      1. Choose JRE System Library
      1. Select the version of the JRE you have installed (should be jdk1.6.0\_xx) then finish
      1. Remove the 1.4 system library that shows in the list
    1. Manually add dependencies that aren't available through maven
      1. In the libraries tab still click Add JARs...
      1. Browse to the dependencies directory and add all the jars in the folder
    1. In the left pane select Java Compiler
      1. Set all compiler levels to 1.6 in the drop down lists.
      1. Click OK - You will be prompted to rebuild the workspace. Allow it.

Now your project should be ready for development. If you see any Errors something is still missing. Review the steps above one more time.

# Running Fuzzops #
While we are still in an alpha state the currently recommended way to run fuzzops is from within the IDE. To run fuzzops you simply run the server and client pieces both as Java Applications.

  * The server class is: `/webfuzzer/src/main/java/com/google/code/fuzzops/webfuzzer/controller/FuzzController.java`

  * The client class is: `/webfuzzer/src/main/java/com/google/code/fuzzops/webfuzzer/applet/WebFuzzerNonApplet.java`