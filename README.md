# Social Survey Architecture (Proposed)

Following is the proposed architecture for Social Survey. The architecture is been proposed for supporting the following
  * Scalability
  * Availability
  * Performance

Note: The current architecture varies from the proposed solution.

# Architecture Diagram

![Architecture diagram](https://github.com/Nishit-Kannan/SocialSurvey/blob/streamapi/images/socialsurvey.png?raw=true "Architecture Diagram")

# Components

Following are the components shown in the diagram above.

  * **Web App Cluster** :

    Web application accepts the request from the front end like browser. The web app component understands the request coming from the front end and redirects the request to appropriate components. Web app cluster is an AWS auto scaling group component that can be scaled as required.

  * **API Cluster** :

    API server hosts the APIs that the social survey application uses and exposes them to third party clients. API cluster is an AWS auto scaling group that can be scaled as per required.

  * **OAuth Server Cluster** :

    All the clients of APIs hosted on the API servers need to be authenticated and authorized. Social Survey use OAuth2 to authorize and authenticate the APIs. OAuth Server is responsible to authenticate users of APIs. OAuth server cluster is an AWS auto scaling group that can be scaled as per required.

  * **Redis Caching Cluster** :

    Web servers need to cache the session data of the users logged in. Session data need to be externalized so that the end users of the web application do not get affected in event of a web server within a cluster going down. API server need to cache most used API calls for better performance. Redis servers cache the data needed. Redis servers will be hosted on AWS which takes care of scaling.

  * **Stream API Cluster** :

    For high performance, asynchronous operations are streamed for processing. API server accepts these operations and queues for processing. Stream API cluster is an AWS auto scaling group that can be scaled as per required.

  * **Batch Server** :

    Some processes need to be run at specified interval. Those operations will be initiated from Batch Server. Batch server operation are streamed via the Streaming API for faster processing. Batch servers are not scalable.

  * **Kafka Cluster** :

    Stream API servers will stream data to Kafka clusters. Kafka clusters are scalable components for high parallel processing.

  * **Datastore** :

    Data in Social Survey is persisted in following three datastore solutions.
      * _Mongo DB_: Mongo DB holds the unstructured data in the application. Mongo DB provides clustering support for high availability and redundancy.
      * _MySQL Server_: MySQL Server holds the structured data in the application. MySQL servers are hosted on Amazon RDS.
      * _SOLR_: Searchable data is indexed in SOLR servers. SOLR servers support clustering for high availability and redundancy.
