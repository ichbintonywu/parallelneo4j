import org.neo4j.driver.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.neo4j.driver.Values.parameters;

public class Neo4jClient implements AutoCloseable{
    private final Driver driver;

    public Neo4jClient(String uri, String user, String password, int connectionPoolSize) {
        Config config = Config.builder()
                .withMaxConnectionLifetime( 10, TimeUnit.MINUTES )
                .withMaxConnectionPoolSize( connectionPoolSize )
                .withConnectionAcquisitionTimeout( 1, TimeUnit.MINUTES )
                .build();
        if (user != null && password != null) {
            driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ), config );
        } else {
            driver = GraphDatabase.driver(uri, config);
        }
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    public void runCypherParallel(int threads, List<String> cypher_list, String database) throws Exception {
      int count=0;
        SessionConfig sessionConfig = SessionConfig.builder()
                .withDatabase(database)
                .withDefaultAccessMode(AccessMode.WRITE)
                .build();

        ExecutorService service = Executors.newFixedThreadPool(threads);
        List<Future<Runnable>> futures = new ArrayList<>();

        long startTime = System.nanoTime();
        for (int i=0;i<1000;i++) {
            for (int id = 0; id < cypher_list.size(); id++) {
                String cypher = cypher_list.get(id);
                Future f = service.submit(
                        new RunQueryInThread(
                                sessionConfig,
                                cypher,
                                parameters()
                        ));
                futures.add(f);
                count = count + 1;
            }
        }

        for (Future<Runnable> f2 : futures) {
            f2.get();
        }
        System.out.println(String.format("Time taken %s ms. Count: %s", (System.nanoTime() - startTime)/1000000.0, count));
        service.shutdownNow();


    }

    public class RunQueryInThread implements Runnable
    {
        private SessionConfig sessionConfig;
        private String cypher;
        private Value params;
        private TransactionConfig txConfig;

        RunQueryInThread(SessionConfig sessionConfig, String cypher, Value params) {
            this.sessionConfig=sessionConfig;
            this.cypher=cypher;
            this.params=params;
        }
        public void run()
        {
            try ( Session session = driver.session(sessionConfig) ) {
                session.run(cypher, params);
            }
        }

    }
}
