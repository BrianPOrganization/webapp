# webapp

### Prerequisites for building the application
1. Need to have Java installed (JDK 17 or higher) that's it :D

### Instructions to build and deploy the web application
1. Clone the repository on the system
   - git clone reponame
2. run ./mvnw clean install 
3. You can run the jar file using
   - ./mvnw springboot:run
OR
1. Same as previous step one
2. run ./mvnw package 
3. Step 2 is done to obtain the jar file. It will be within the target directory
4. java -jar target/jarfilename
