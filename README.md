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
* MySQL for data source

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
