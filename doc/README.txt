CSCI201 Final Project 

Project Name: Puzzungeon
Project Number: 7
Project Category: Game

Team Members:  
Daniel Santoyo: dsantoyo@usc.edu  USC ID: 6926712177  
Hayley Pike: hpike@usc.edu  USC ID: 8568149839
Yi(Ian) Sui: ysui@usc.edu  USC ID: 2961712187
Ekta Gogri: egogri@usc.edu  USC ID: 9607321862


Everything should work.

Project requirements, specifications, detailed design document,
testing, deployment documents are in doc/Puzzungeon.pdf


HOW RUN THE CODE IN ECLIPSE

Step 1: Download Java8 or newer JRE/JDK. 

Step 2: Download Eclipse Photon (4.8) or newer
 Useful Link for Environment Setup: 
https://libgdx.badlogicgames.com/documentation/gettingstarted/Setting%20Up.html

Step 3: Now we need to install Gradle. Gradle is used to build Libgdx projects. Now in this step you can download the Gradle plugins to your eclipse from Help-> Eclipse Marketplace-> search and install Gradle Integration for Eclipse. 

Step 4: Importing the project into Eclipse
On Eclipse, go to File->Import->Gradle->Existing Gradle Project->search for the unzipped folder.

Step 5:On Eclipse, go to Puzzungeon-core->src->project.server->Server.java.
Right click and run it as a java application.

Step 6: Find the database.sql script in the core/src/project/server folder
Run this sql file on mySQL to create the database.
default username and password combination is root/root;

Step 7: In Eclipse, right click on Puzzengeon-core->Properties->Java Build Path->Libraries-> Add External Jars-> 
browse and add the mysql-connector driver jar file.
(in /core/src/project/server)

Step 8. In Eclipse, right click Puzzengeon-core->src->project.puzzungeon.desktop->Desktoplauncher. 
Right click and run it as a java application. IT WILL FAIL FOR THE FIRST TIME and we need this step to do the next step.

Step 9:  In Eclipse, go to Run->Run Configurations->Arguments and make sure that the Working Directory is set to /core/assets folder for DesktopLaucher.

Step 10: In Eclipse, run Desktoplauncher.java as a java application again. This time it should be able to find the correct Working Directory.

If you want both players to play on the same computer, run the desktop application twice. If you want both players to play on different computers, repeat 
all the steps on the other computer. Set up the server ip in  Puzzengeon-core/src/project/puzzungeon/Puzzungeon.java 
line 36: public String serverAddress = "localhost";

Enjoy the game! 


