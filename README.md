# Tweets2Graph

A Java program to generate [Cytoscape.js](https://js.cytoscape.org/) JSON file or a [GraphML](http://graphml.graphdrawing.org/) file from a dataset of tweets. 

## Prerequisites

### Input file

The input file has to be under a `.jsonl` format. 

### Database

PostgeSQL has to be installed on your machine. You can create the schema and the tables using [this file](https://github.com/esuau/tweets2graph/blob/master/src/main/resources/schema.sql).

You will probably have to change the `application.properties` depending on your configuration.

## Compilation

Compile the project by running :

```bash
mvn install
```

## Execution

Run the program by executing the command :

```bash
java -jar target/twitter2graph-<version>.jar <path/to/input/file>.jsonl <path/to/output/file>.[json | graphml]
```

The output file can have either a `.json` or a `.graphml` extension.