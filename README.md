# Summary

The application can manage the data in the main memory or on disk in a column-oriented manner. The
input data for the application is taken from the Singapore ASOS
system ([link](https://mesonet.agron.iastate.edu/request/download.phtml?network=SG__ASOS)) located
at Changi Climate Station and Paya Lebar Meteorological Station.

The application is able to query the input data for the respective monthly minimum and maximum
values of temperature and humidity columns given years and location condition. The years and
location condition is determined by the matriculation number argument provided to the application.

The last digit of the required years equals to the last digit of matriculation number, while the
location depends on the second last digit of matriculation number, with even number for Changi and
odd number for Paya Lebar.

E.g. Providing the application a matriculation number A1234567B will return the four extreme values
of each month for the years 2007 and 2017 at Changi.

## Input Data

Each row represets a weather data entry, separated by a comma “,”. Empty data are marked as “M”. The
columns for each row are described below:

- `id`: the increasing index of weather records.
- `Station`: `Changi` or `Paya Lebar`, represents the site of the observation.
- `Timestamp`: the timestamp of the observation, in format `YYYY-MM-DD hh:mm` of UTC+8 time zone.
- `Temperature`: air temperature in degrees celcius (°C).
- `Humidity`: relative humidity in %.

E.g.

| id  | Timestamp        | Station | Temperature | Humidity |
|-----|------------------|---------|-------------|----------|
| 0   | 2002-01-01 00:00 | Changi  | 25.00       | 88.67    |
| 1   | 2002-01-01 00:30 | Changi  | 25.00       | 88.67    |
| 2   | 2002-01-01 01:00 | Changi  | 25.00       | 88.67    |
| 3   | 2002-01-01 01:30 | Changi  | 25.00       | 88.67    |
| ... | ...              | ...     | ...         | ...      |

## Output Data

Each row contains one maximum or minimum value of temperature or humidity and the corresponding
date, separated by a comma “,”. The columns for each row are described below:

- `Date`: the corresponding date of the Value, in format `YYYY-MM-DD` of UTC+8 time zone.
- `Station`: `Changi` or `Paya Lebar`, represents the station of the Value
- `Category`: `Min Temperature`, `Max Temperature`, `Min Humidity`, or `Max Humidity`, represents
  the meaning of the `Value`.
- `Value`: the value of temperature or humidity.

E.g.

| Date       | Station | Category        | Value |
|------------|---------|-----------------|-------|
| 2007-01-25 | Changi  | Min Temperature | 23.0  |
| 2007-01-06 | Changi  | Max Temperature | 32.0  |
| 2007-01-07 | Changi  | Max Temperature | 32.0  |
| 2007-01-08 | Changi  | Max Temperature | 32.0  |
| ...        | ...     | ...             | ...   |

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