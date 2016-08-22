# Srchr: text file search engine 
[![Build Status](https://travis-ci.org/thedotedge/srchr.svg?branch=master)](https://travis-ci.org/thedotedge/srchr) [![Code Climate](https://codeclimate.com/github/thedotedge/srchr/badges/gpa.svg)](https://codeclimate.com/github/thedotedge/srchr) 


## Features

* Search is case-insensitive
* Word frequency is used a ranking factor
* [Stoplist](stopwords.txt) support
* Suggestions are based on word frequency
* Supports unicode
* TODO suggestions based on search history

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
Make sure you have Java 8 installed.
```
#java -jar target/srchr-all.jar <path to dictionary file> <optional path to stopwords file>
java -jar target/srchr-all.jar ./sample ./stopwords.txt
```

## Building
Requires Maven 3 and JDK 8.
```
mvn clean package
```
