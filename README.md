# Srchr: text file search engine [![Build Status](https://travis-ci.org/thedotedge/srchr.svg?branch=master)](https://travis-ci.org/thedotedge/srchr)

## Features

* Search is case-insensitive
* Suggestions are based on word frequency
* Word frequency is a ranking factor
* Stoplist is supported

## Commands
```
:ls
:search war peace
:suggest 5 gun baron prince
:add /absolute/path/to/file
:rm /absolute/path/to/file
:exit
```

## Running

```
# make sure you have Java 8 installed
java -jar target/srchr-all.jar ./sample ./stopwords.txt
```

## Building
Requires Maven 3 and JDK 8.
```
mvn clean package
```