# Data [![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/Lni-Energy-dev/data.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Lni-Energy-dev/data/context:java)
Data is used to store information and convert it to JSON
or convert JSON to Data objects


## Installation ![Maven Central](https://img.shields.io/maven-central/v/io.github.lni-dev/data?label=current%20newest%20version%3A%20)
Add it as implementation to your build.gradle. 
```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'io.github.lni-dev:data:[version]'
}
```
Replace `[version]` with the version you want to use.

## Getting Started
First create a data using the static methods of `SOData`. `SOData.newOrderedDataWithKnownSize(10)` will create a new
data, which is backed by an `ArrayList` with the initial capacity 10. It is possible to add more than 10 entries.
<br><br>
The simplest way of adding entries to a data is by using the add function: `add(String key, Object value)`.
value can be any primitive data type, String, Array, Collection or Datable.
<br><br>
To parse the data to a json-string simply call the `toJsonString()` method:
```java
//create a new data
SOData data = SOData.newOrderedDataWithKnownSize(10);

//add entries
data.add("key", "value");
data.add("a int", 10);
data.add("something else", new String[]{"1", "a", "2"});

//parse it to a json string
System.out.println(data.toJsonString());
```

The output will look like that:
```json
{
	"key": "value",
	"a int": 10,
	"something else": [
		"1",
		"a",
		"2"
	]
}
```