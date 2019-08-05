# Stream Combiner

Homework realization of following assigment:
https://gist.github.com/m0mus/ba6c5419278239a19175445787420736  

## Mock XML stream server
`java -cp ./target/oracle.jakarta.hw-1.0-SNAPSHOT-jar-with-dependencies.jar cz.kec.oracle.jakarta.hw.XmlStreamServer 9696`
Xml stream server producing continuous never ending stream of xml fragments in random intervals 100 - 500 ms.
```xml
<data><timeStamp>1564922037806</timeStamp><amount>-40.24</amount></data>
``` 
For monitoring produced fragments use `-Dorg.slf4j.simpleLogger.defaultLogLevel=debug`.
Be aware Mock Xml server is able to serve only one client at a time.

## Combiner
`java -cp ./target/oracle.jakarta.hw-1.0-SNAPSHOT-jar-with-dependencies.jar cz.kec.oracle.jakarta.hw.Combiner jsc://localhost:9696`
Stream combiner app configurable to use stream from mock server(use custom protocol jsc) of file stream(file:/file.txt)

## Stream Combiner demo
`java -cp ./target/oracle.jakarta.hw-1.0-SNAPSHOT-jar-with-dependencies.jar cz.kec.oracle.jakarta.hw.Demo`

Run simple standalone demo which is starting 6 servers on 6 different ports and 1 Combiner listening to them all.
Demo stops after 10 seconds all servers, Combiner shuts down by itself when there is nothing to process.

### Functional requirements fulfilment
 * [x] Application should be configurable to read data from N hosts:ports
 * [x] Input streams provide data in XML format.
 * [x] Data in input stream could be veeery LARGE.
 * [x] Data in input stream is sorted by timestamp.
 * [x] Application output is JSON stream.
 * [x] Output data should be sorted by timestamp.
 * [x] If several inputs provide data with the same timestamp - amounts should be merged.
 * [x] Amounts could be positive/negative
 
#### Bonus points - TODO
 * [ ] add solution in case if some input streams hang.
 * [x] imagine that timestamps comparing operation is VERY expensive - try to minimize it's usage. -> Min heap instead of direct k-way merge see [812dbaa](https://github.com/danielkec/JakartaHW/commit/812dbaaf66ffdb14e63faafeb9f906a6dfba681f)
 
### Non-functional requirements fulfilment
 * [x] You must use Maven 3.x to build the project.
 * [x] You must use JUnit to write tests for the project.


   
