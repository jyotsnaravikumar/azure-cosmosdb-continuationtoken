# azure-cosmosdb-continuationtoken
This Java sample code shows usage of continuationtoken for pagination \
For this Sample code I have populated CosmosDB collection with 1576 documents \
Set CosmosDB HostName and MasterKey in AccountSettings.java \
Run Main.java \
Code comments are explanatory \
sets the number of items to return in a single page result to 500 \
continuationToken1: null \
continuationToken2: {"token":"-RID:fQwhANHkuIv1AQAAAAAAAA==#RT:1#TRC:500#ISV:2#IEO:65536","range":{"min":"","max":"FF"}} \
continuationToken2: {"token":"-RID:fQwhANHkuIvpAwAAAAAAAA==#RT:2#TRC:1000#ISV:2#IEO:65536","range":{"min":"","max":"FF" \
continuationToken2: {"token":"-RID:fQwhANHkuIvdBQAAAAAAAA==#RT:3#TRC:1500#ISV:2#IEO:65536","range":{"min":"","max":"FF"}} \
continuationToken2: null \
