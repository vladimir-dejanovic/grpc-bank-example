# README #

This is example code used in my talk REST API vs gRPC.

This is just show case example to demonstrate gRPC and some of it's functionalities. This code shouldn't be used in real production systems.


### How do I get set up? ###

* Java 1.8 minimum
* Maven or some IDE from which you can build code

After you download the code just build it and make sure it will compile with your setup

```
mvn clean install
```

### How to start it?

This example consists of example gRPC Bank Server which expose multiple services and gRPC clients for calling those services. 


* To Start Bank server run this command

```
  mvn exec:java -Dexec.mainClass=xyz.itshark.play.rpc.grpc.bank.BankServer
```

* To request transfer run this command

```
mvn exec:java -Dexec.mainClass=xyz.itshark.play.rpc.grpc.bank.client.RequestClient
```


* Transfer money from account 1 to account 2

```
mvn exec:java -Dexec.mainClass=xyz.itshark.play.rpc.grpc.bank.client.TransferClient
```
