# Data 
Data is used to store information and convert it to JSON
or convert JSON to Data objects


## Installation ![Maven Central](https://img.shields.io/maven-central/v/de.linusdev/data?label=current%20newest%20version%3A%20)
Add it as implementation to your build.gradle. 
```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'de.linusdev:data:[version]'
}
```
Replace `[version]` with the version you want to use.

## Getting Started
A data can be converted to a json string and vice verca.
### SOData to Json
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

### Json to SOData
To parse a json string to a data you need to get a instance of a `JsonParser`:
```java
JsonParser parser = new JsonParser();
```
With this parser you can parse a `Reader` or an `Inputstream`:
```java
SOData dataFromReader = parser.parseReader(reader);
SOData dataFromStream = parser.parseStream(inputStream);
```
Here is an example of parsing a `String` to a `SOData`:
```java
String json = "{...}";
Reader reader = new StringReader(json);
        
JsonParser parser = new JsonParser();
SOData data = parser.parseReader(reader);
```
If you want to parse a json array to a data you can use the function `setArrayWrapperKey(String key)`. After parsing the
array will then be accessible with the given key as a `List<Object>`:
```java
String json = "[1,2,3]";
Reader reader = new StringReader(json);

JsonParser parser = new JsonParser();
parser.setArrayWrapperKey("array");

SOData data = parser.parseReader(reader);
List<Object> list = data.getList("array");

System.out.println(list);
```
The above code will give the following output:
```js
[1,2,3]
```
