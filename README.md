# FeedMe Tech Test

My solution to the stars technical test

## Instructions
At the root level of this project, I have provided a small bash script `build_and_run.sh`.

Executing this script will:
* take down the existing (if any) docker network
* perform a maven clean install on the project which:
    * clears the existing target directory
    * compiles all the project code
    * runs all the unit tests
    * packages each project as a JAR file
* creates docker containers out of the projects
* spins up the docker network containing:
    * FeedMe TCP Provider
    * FeedMe MongoDB
    * FeedMe Json Transformer (my creation)
    * FeedMe Data Store (my creation)
    
Executing this script is the only thing you need to do to have the whole solution up and running.

You can see in each controller class the exposed endpoints of each docker container but the two main ones you will need to use are:
* `http://localhost:8080/feedme/messages?n={numberOfMessages}` - this retrieves {numberOfMessages} messages from provider and returns them in JSON format
* `http://localhost:8080/feedme/store?n={numberOfMessages}` - this retrieves {numberOfMessages} messages from the provider and sends them to the data store container which in turn stores them into MongoDB in a hierarchical structure
* `http://localhost:8081/data/events` - this retrieves all event documents that are currently stored in the MongoDB database.

All the above endpoints are GET request so you can use a browser to trigger them but it is preferable to use a REST client such as Postman or Insomnia.

## Technology Choices
###Overall
The whole project has been developed using Java 11 using Maven as the build tool. The reason for this is that Java is my language of choice for developing web/network oriented microservice projects and is also the language I have the most professional experience in.

I chose maven to build the project with as it provides a nice way of managing all the tasks required to fully build the project. Using maven, using one command I can compile the code, run tests, package as jar, pull dependencies, manage plugins etc.

Using maven, I have used central dependency management to manage all dependencies that are used in all submodules to have centralised versioning so that we do not have to hypothetically update each dependency in all of these modules whenever there's an updated dependency etc

In each project, I have developed a solution using functional, reactive, event-driven code using the reactor framework which allows
asynchronous execution of tasks which perfectly suits an application interacting with a seemingly unlimited stream of data

Each project also uses Lombok which generates boilerplate code, which means whenever we would use a basic constructor, getter/setter, equals and hashcode etc, we can simply use lombok annotations
to automatically generate said boilerplate code, which means the code is a lot more readable as only the logic of components is present and only the fields
within a dto class.

### Data Transformer
This is the solution to the beginner task in which this app connects directly to TCP port 8282 of the provider container. This is a spring boot application which I have used as this is a tried and tested way to create a modular web application.

Each component of this application is a spring bean in which each component is fully managed and wired together by spring.

The flow of this application is as follows: we use a simple java socket to connect to the TCP port from the provider, we collect 
each message from the TCP port to a flux of strings from each line emitted from the port, then each message is transformed into a java class using
simple boilerplate code generated by lombok and then using spring, which uses jackson, transforms this class to a JSON object which is subsequently either returned to the user via an HTTP endpoint present in the app exposed using a spring rest controller
or it is sent to the data store application (see later). To get the messages from the provider as json or to send them to the mongodb database, it's as simple as a parameter-less GET request to the controller which triggers the logic.


The socket used to connect to the TCP server is only initialised when the user triggers the interaction from the controller. The user must define how many messages he/she wants to be processed as whilst it's theoretically possible to do all of them, trying to do
unlimited streams would result in the user waiting seemingly forever for the data to be processed. Once the user defined amount of messages is processed, the socket is closed as to not accept any more data.

### Data Store
This is the solution for the intermediate task, to take the json data generated in the previous task and storing it in a NoSQL database. 

This is a separate application from the beginner task solution, reason being is that at the start, I intended to complete the advanced task, however, I did not manage to do the advanced task, reason you can see in the next section. That being said,
I still like to decouple the two tasks as I prefer separating different responsibilities into different services, adhering to a microservices style architecture. 

This application is also a spring boot project and as this doesn't interact with any pure TCP service of any kind, it is purely an HTTP application. Which means I thought to make it a REST application using HTTP.

The way this application works is that it's started up via spring boot and it listens to incoming requests to save either an event, a market or an outcome to the mongodb database. At the controller layer, it simply takes the request and passes it to the service
layer.
 
 This application uses Spring Data JPA to interact with the MongoDB database. I chose this, especially as this is my first time using MongoDB because all I have to do is declare what interactions I want with the database and spring does most of the heavy lifting in terms of 
 database transactions, provisioning and other stuff like that, I write declarative code on what I want the repository to do and whilst spring handles the low level database interactions, 
 I can focus on writing the application logic that uses the repository. 
 
 The database stores documents in a hierarchical structure using embedded documents. To achieve this, the structure of the java objects that interact with the database is different to that of the json objects initially created by the data transform service. 
 A converter similar to what I did in the beginner task was required to transform the incoming json into hierarchical data that is to be stored in mongoDB. 
 
 Documents are stored in a hierarchical structure such that an event has a collection of markets and a market has a collection of outcomes. To achieve this, due to the reactive nature of the application, the service layer is implemented
 as such to handle data being accepted in any order. e.g. if an outcome is received before it's market is, a temporary event is created to store a market with ID as defined in the outcome with aforementioned outcome and then this event is
 deleted once this outcome's market is saved as this market gets saved into an event with this market's eventId. So the service can handle messages being received in any order. 
 
 In the service layer, I extracted a lot of code into functions, sometimes these functions may only be used once, this is because before extracting into functions, the code was very long, complex and un-readable, I wanted the code to be
 perfectly readable by a human reading the code, there are a few comments in such, but I like to keep comments to a minimum as littering comments everywhere can easily make code more difficult to read. 
 I find it's much better to keep code well structured and use sensible variable/function names so that a reader can understand what is happening.
 
 At the controller layer of the application, there is an endpoint that simply retrieves every document in the database and presents it in hierarchical json.
 
 ### Advanced Task
 
 Unfortunately, as I have no professional experience in message queuing, I did not complete the advanced task. I do have knowledge of the theory of message queuing and I could have taught myself RabbitMQ or Kafka but to do so to a professional standard would have taken longer
 than what would have taken longer than what would be acceptable for this task so I omitted it and focused on the first two tasks.
 
 ### Testing
 
 Every component of every task has been unit tested, mostly using mockito as the spirit of unit testing is to test one particular unit's functionality and mockito allows developers to mock units that the unit in question is using so that the unit test can focus solely
 on the functionality of the unit being tested. I used the AssertJ library to assert conditions as this library provides clean, obvious statements that makes unit test code easy to write and easy to read. 
 
 For the reactive components, I used the reactor test library. Without reactor test, I'd have to have blocked each reactive call which whilst okay for tests, I prefer using reactor test as it's a fully supported, developed test library for the reactor framework
 and makes assertions in a reactive fashion and it is very easy to write and read.
  
 For the connector between the transformer app and the data store app, I have used the OKHTTP3 library MockWebServer. This library allowed me to create a mock web server and I could enqueue anything I want on to it. 
 This allowed me to provide mock web responses for the connector to react to, such making the test a 'unit test' in that it tests the functionality of the unit (class) that is being tested. It also allows me to easily induce
 errors so that I can test how my connector reacts to them
 
 #### Future Considerations
 In an ideal world, I would have written integration tests for each application. Unfortunately, I had an ideal deadline as to which I could complete the task in.
 What I would have love to have done would be to write some integration tests, either using cucumber acceptance testing, using wiremock as a mock server to mock out the interactions with downstream services, or, I would have loved to write some contract tests using spring tests,
 writing these integration tests in groovy that define the tests in a declarative way (I give a request, I expect this response). Using the maven surefire plugin, I could have integrated this into my maven build 
 by defining the required plugins and dependencies. Unfortunately I ran out of time to write integration tests so this project is tested but only on a unit level. Writing cucumber acceptance tests, I could have defined my tests completely
 in business language (Given, When, Then) and have the implementations of each under the hood. With spring cloud contract tests, I could have written them simply in terms of "This is the HTTP request I'm going to send in JSON,
 this is the HTTP response I expect back in JSON"
 
 #### What I didn't test
 I have not written unit tests for the repository functions, I figured that since the majority of what I wrote at the repository layer is declarative interfaces. Spring automatically implements the code that deals  with the low level database interactions so 
 there would not have been much point in doing so as spring is a thoroughly tried and tested framework that has been tested itself as part of it's implementations. 
 
 ### Additional Tasks
 #### Front End
 Unfortunately I did not write a front end to interact with this application, I have been a back end developer for most of my career and I have very little professional experience in front end development. I understand the theory of AJAX and I understand how a front end would communicate with the back end,
 but overall I don't have substantial experience in developing front end applications.
 
 #### Docker
 I have written a Dockerfile for both the json transformer app and the data store app. I have also added to the provided `docker-compose.yml` file the resultant images such that the containers created by these dockerfiles.
 I did this as to simulate a proper microservices architecture, decoupling the apps and making it very easy to spin up the entire solution with one command. Due to my docker images not being in a repository, these docker containers get built
 as part of the `build_and_run.sh` script so that every component of the docker network is spun up simply by executing the script.
 
 ### How I would improve this in the future.
 Despite my best efforts to make this application fully reactive, I have had to block the interaction in the transformer app with the data store app. With my solution, each request to the data store app happens on one thread and the connector sends messages to the data store
 one at a time. This is because the service layer of the data store application makes multiple queries to the database in which is not atomic and the service code is pretty complex. 
 
 To improve on this, I would store the event, market and outcome as separate documents instead of using embedded documents in a hierarchical fashion. Then when the user requests the data store applications for the documents,
 the service would collect each relevant document using their IDs (eventID and marketID for markets and marketID and outcomeID for outcomes) and then process the data in such a way that despite it not being originally hierarchical
 the response would be hierarchical. This would mean that the service code for storing data would not be so complex, the application could be more reactive and there'd be less calls to the database,
 improving the performance of the application.
 
 ## Other Notes
 I read that the deliverable was expected as a git repository/bundle after developing the solution so I do not have a git history.