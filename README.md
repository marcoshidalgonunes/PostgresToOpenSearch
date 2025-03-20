#PostgreSQL to ElasticSearch

This project shows how to implement [Command Query Responsability Segregation (CQRS)](https://www.kurrent.io/cqrs-pattern) using PostgreSQL database to write commands and ElasticSearch for read queries.

## Data

The data used here was originally taken from the
[Graduate Admissions](https://www.kaggle.com/mohansacharya/graduate-admissions)
open dataset available on Kaggle.
The admit CSV files are records of students and test scores with their chances
of college admission.  The research CSV files contain a flag per student
for whether or not they have research experience.
These CSV files are in `data` folder.

## Components

The following technologies are used through Docker containers:
* Kafka, the streaming platform
* Kafka Connect, pulled from [Debezium](https://debezium.io/), which will
source data through Kafka
* [PostgreSQL](https://www.postgresql.org/), pulled from [Debezium](https://debezium.io/), tailored for use with Connect, as Database for commands.
* [ElasticSearch](https://www.elastic.co/elasticsearch), as database for queries.
* [Java 21+](https://openjdk.java.net), to create the projects for applications used in this demo
* [Spring](https://spring.io/), Java framework used in an agregator application to save Kafka Streams data into ElasticSearch and in an API application to query ElasticSearch data
* [Apache Maven](https://maven.apache.org), to manage the Java projects for the applications used in this demo

The containers are pulled directly from official Docker Hub images.
The Debezium Connect image used here needs some additional packages, some of them from `libs` folder, so it must be built from the 
included Dockerfile. The same applies for Debezium Postgres.

### Build the debezium images for Kafka Connect and Postgres

```
docker build -t debezium-connect -f debezium.Dockerfile .

docker build -t debezium-postgres -f postgres.Dockerfile .
```

### Create volumes for Postgres and ElasticSearch

```
docker volume create postgresdata

docker volume create elasticdata
```

### Bring up the entire environment

```
docker compose up -d --build
```

## Loading data into Postgres

We will copy CSV to postgres container following by execute psql command line, mount our local data
files inside, create a database called `students`, and load the data on
students' chance of admission into the `admission` table.

```
docker cp data/admit_1.csv postgres:/tmp
docker cp data/research_1.csv postgres:/tmp

docker exec -it postgres psql -U postgres
```

At the command line:

```
CREATE DATABASE students;
\connect students;
```

Load our admission data table:

```
CREATE TABLE admission
(student_id INTEGER, gre INTEGER, toefl INTEGER, cpga DOUBLE PRECISION, admit_chance DOUBLE PRECISION,
CONSTRAINT student_id_pk PRIMARY KEY (student_id));

\copy admission FROM '/tmp/admit_1.csv' DELIMITER ',' CSV HEADER
```

Load the research data table with:

```
CREATE TABLE research
(student_id INTEGER, rating INTEGER, research INTEGER,
PRIMARY KEY (student_id));

\copy research FROM '/tmp/research_1.csv' DELIMITER ',' CSV HEADER
```

We can disconnect from Postgres container with the command `exit`.

## Use Postgres database as a source to Kafka

The `postgres-source.json` file contains the configuration settings needed to
sink all of the students database to Kafka.

```
curl -X POST -H "Accept:application/json" -H "Content-Type: application/json" \
      --data @postgres-source.json http://localhost:8083/connectors
```

The connector `postgres-source` should show up when curling for the list
of existing connectors:

```
curl -H "Accept:application/json" localhost:8083/connectors/
```

The two tables in the `students` database will now show up as topics in Kafka.
You can check this by entering the Kafka container:

```
docker exec -it kafka /bin/bash
```

Lists the available topics:

```
kafka-topics --list --bootstrap-server localhost:9092
```

View the topics

```
kafka-console-consumer --bootstrap-server localhost:9092 --topic dbserver1.public.admission --from-beginning

kafka-console-consumer --bootstrap-server localhost:9092 --topic dbserver1.public.research --from-beginning
```

We can disconnect from Kafka container with the command `exit`.

## Agregate topics into ElasticSearch

Now we will persist the join of Students and Research tables in an ElasticSearch index. To do it , call the create endpoint of agregator application.

```
curl -X POST http://localhost:8081/agregator

```

The endpoint return counters of topics readed and written in ElasticSearch. Since the write occurs only if the topic is not recorded, in subsequent runs the counters can differ.

If needed, you can delete the ElasticSearch index calling the following endpoint.

```
curl -X DELETE http://localhost:8081/agregator
```


## Query data from ElasticSearch

The api application has endpoints to query ElasticSearch data populated with agregator application.

### Query students chances to be enrolled in a research

```
curl http://localhost:8082/api/boosts/:research
```

### Query a specific student chance to enroll in a research

```
curl http://localhost:8082/api/boosts/chance/:studentId
```


### Query average chance of enrollment in a research

```
curl http://localhost:8082/api/boosts/research/chance/:research
```



