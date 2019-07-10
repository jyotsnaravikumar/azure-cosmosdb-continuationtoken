package com.microsoft.azure.cosmos.document;


import com.microsoft.azure.cosmosdb.*;
import com.microsoft.azure.cosmosdb.rx.AsyncDocumentClient;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;


import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {
    private final ExecutorService executorService;
    private final Scheduler scheduler;

    private AsyncDocumentClient client;

    private final String databaseName = "test-kcdb";
    private final String collectionName = "volcanoCollection";

    public Main() {
        executorService = Executors.newFixedThreadPool(100);
        scheduler = Schedulers.from(executorService);
    }

    public void close() {
        executorService.shutdown();
        client.close();
    }


    public static void main(String[] args) {
        Main p = new Main();

        try {
            p.getStartedDemo();
            System.out.println(String.format("Demo complete, please hold while resources are released"));
        } catch (Exception e) {
            System.err.println(String.format("DocumentDB GetStarted failed with %s", e));
        } finally {
            System.out.println("close the client");
            p.close();
        }
        System.exit(0);
    }

    private void getStartedDemo() throws Exception {
        System.out.println("Using Azure Cosmos DB endpoint: " + AccountSettings.HOST);

        client = new AsyncDocumentClient.Builder()
                .withServiceEndpoint(AccountSettings.HOST)
                .withMasterKeyOrResourceToken(AccountSettings.MASTER_KEY)
                .withConnectionPolicy(ConnectionPolicy.GetDefault())
                .withConsistencyLevel(ConsistencyLevel.Eventual)
                .build();

        CountDownLatch queryCompletionLatch = new CountDownLatch(1);

        System.out.println("Querying documents async and registering listener for the result.");
        executeSimpleQueryWithContinuationToken(queryCompletionLatch);

        queryCompletionLatch.await();
    }



    private void executeSimpleQueryWithContinuationToken(CountDownLatch completionLatch) throws JSONException {

        FeedOptions queryOptions = new FeedOptions();
        // note that setMaxItemCount sets the number of items to return in a single page result
        queryOptions.setMaxItemCount(500);
        queryOptions.setEnableCrossPartitionQuery(true);
        String continuationToken = null;
        queryOptions.setRequestContinuation(continuationToken);


        String collectionLink = String.format("/dbs/%s/colls/%s", databaseName, collectionName);
        Observable<FeedResponse<Document>> queryObservable =
                client.queryDocuments(collectionLink,
                        "SELECT * FROM volcanoCollection", queryOptions);

        Iterator<FeedResponse<Document>> it = queryObservable.toBlocking().getIterator();

        System.out.println("continuationToken1: " + continuationToken);
        do {
            FeedResponse<Document> page = it.next();
            List<Document> results = page.getResults();
            System.out.println("count " + results.size());
            for (Document doc : results) {
                JSONObject obj = new JSONObject(doc.toJson());
                //System.out.println("json: " + obj.toString());
                System.out.println("id: " + obj.getString("id"));

            }
            continuationToken = page.getResponseContinuation();
            System.out.println("continuationToken2: " + continuationToken);

        }
        while (continuationToken != null);
        System.out.println("continuationToken3: " + continuationToken);
        System.out.println("finished");

    }



}
