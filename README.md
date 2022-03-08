# Getting Started

1. Install Java 11.

2. Install Apache Maven.

3. Run the following command to compile the java files and package them into a JAR:

```shell
mvn clean package
```

4. Run the following command to execute the application:

```shell
java -jar target/java-columnar-database-1.0-SNAPSHOT.jar -m <enter matriculation number>
```

5. The default application will be using the in memory database. To instead use the disk database,
   run the following command:

```shell
java -jar target/java-columnar-database-1.0-SNAPSHOT.jar -m <enter matriculation number> -d
```

6. The application will create a new output CSV file `ScanResult.csv` after running step 4 or
   running step 5.

7. Javadoc for the application can be viewed by running the following command:

```shell
open target/apidocs/index.html
```