# sm360
This repository is related to the SM360 Backend Tech Assignment

This is what we will cover in this page:
* [Requirements](#requirements)
* [What have been implemented](#workdone)
* [How to execute](#execute)
* [Possible enhancements](#enhancements)


## <a name="requirements"></a>Requirements
The current implementation is using the following tools
* Java 17
* MySQL -- for data source
* Maven -- to build the project

The default configuration set for the data source are the faullowing 
* url: `jdbc:mysql://localhost:3306/db_sm360`
* username: `admin_db_sm360`
* password: `TEj6$wrCtgYxwYN5`
Other configuration like the tier limit can be find in `application.properties`

Here is the script to create the default data source user
`
CREATE DATABASE db_sm360;
CREATE USER 'admin_db_sm360'@'localhost' IDENTIFIED BY 'TEj6$wrCtgYxwYN5';
GRANT ALL PRIVILEGES ON db_sm360.* TO 'admin_db_sm360'@'localhost';
`

## <a name="workdone"></a>What have been implemented
The actual implementation is in line with what have been described [here](https://github.com/sm360/backend-tech-assignment)
It include basically endpoints for:
* Creating a listing. All the created listings have state `draft` by default;
* Updating a listing;
* Get all listings of a dealer with a given state;
* Publishing a listing;
* Unpublishing a listing.
Exception handling, tests, logs and openapi documentation are available.

## <a name="execute"></a>How to execute
Run the following command to execute the project
### Build
`mvn clean package`

### Run
`java -jar .\target\sm360-0.0.1-SNAPSHOT.jar`
This starts tomcat server on port 8080. You can change this behaviour by setting the argument --Dserver.port. 
e.g. `java --Dserver.port=8081 -jar .\target\sm360-0.0.1-SNAPSHOT.jar` to run tomcat on port 8081

When the application has started, the documentation is available via the following urls
* `http://server:port/api-docs` json version
* `http://server:port/swagger-ui.html`

## <a name="enhancements"></a>Possible enhancements
Here are some propositions to further enhance the actual implementation

### Security
Actually the service is not secure at all. 
We need to secure it using Spring security with JWT authentication for example.

#### Tier limit
Actually, the tier limit is configured in the configuration file `application.properties`. It is not flexible like that and can require to stop and restart the service if we need to change it.
We should provide an endpoint to set this configuration and remove it from `application.properties`.

### Listing removal
A dealer may need to delete a listing he created. We could provide an endpoint for that.

### Deployment
We can also enhance the deployment process by intoducing Docker and CI/CD tools such as Github Actions
