//package contevolve.etlVisualizer.config;
//
//import java.time.Duration;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import com.couchbase.client.java.Bucket;
//import com.couchbase.client.java.Cluster;
//import com.couchbase.client.java.ClusterOptions;
//import com.couchbase.client.java.Collection;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//
//@Component
//public class CouchbaseManager {
//
//    @Value("${couchbase.username}")
//    private String username;
//
//    @Value("${couchbase.password}")
//    private String password;
//
//    private Cluster cluster;
//
//    @PostConstruct
//    public void connect() {
//        cluster = Cluster.connect(
//            "192.168.1.111",
//            ClusterOptions.clusterOptions(username, password)
//                .environment(env -> {
//                    env.timeoutConfig(timeout ->
//                        timeout.connectTimeout(Duration.ofSeconds(10))
//                               .kvTimeout(Duration.ofSeconds(5))
//                    );
//                    env.ioConfig(io ->
//                        io.numKvConnections(4)
//                    );
//                })
//        );
//    }
//    
//    public Collection getCollection(
//            String bucketName,
//            String scopeName,
//            String collectionName
//    ) {
//        try {
//            Bucket bucket = cluster.bucket(bucketName);
//            bucket.waitUntilReady(Duration.ofSeconds(5));
//            return bucket.scope(scopeName).collection(collectionName);
//
//        } catch (Exception e) {
//            // Couchbase is down or unreachable
//            throw new IllegalStateException(
//                "Cannot connect to Couchbase: " + e.getMessage()
//            );
//        }
//    }
//
//    public Cluster getCluster() {return cluster;}
//    
//    @PreDestroy
//    public void shutdown() {
//        if (cluster != null) {
//            cluster.disconnect();
//            System.out.println("Couchbase disconnected.");
//        }
//    }
//}



package contevolve.etlVisualizer.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.Collection;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CouchbaseManager {

    @Value("${couchbase.username}")
    private String username;

    @Value("${couchbase.password}")
    private String password;

    private Cluster cluster;

    @PostConstruct
    public void connect() {
        log.info("Connecting to Couchbase cluster...");
        cluster = Cluster.connect(
            "localhost",
            ClusterOptions.clusterOptions(username, password)
                .environment(env -> {
                    env.timeoutConfig(timeout ->
                        timeout.connectTimeout(Duration.ofSeconds(10))
                               .kvTimeout(Duration.ofSeconds(5))
                    );
                    env.ioConfig(io ->
                        io.numKvConnections(4)
                    );
                })
        );
        log.info("Couchbase cluster connected successfully");
    }

    public Collection getCollection(String bucketName, String scopeName, String collectionName) {
        log.debug("Getting collection: {}.{}.{}", bucketName, scopeName, collectionName);
        try {
            Bucket bucket = cluster.bucket(bucketName);
            bucket.waitUntilReady(Duration.ofSeconds(5));
            return bucket.scope(scopeName).collection(collectionName);
        } catch (Exception e) {
            log.error("Failed to get collection {}.{}.{} - {}", bucketName, scopeName, collectionName, e.getMessage());
            throw new IllegalStateException("Cannot connect to Couchbase: " + e.getMessage());
        }
    }

    public Cluster getCluster() {
        return cluster;
    }

    @PreDestroy
    public void shutdown() {
        if (cluster != null) {
            cluster.disconnect();
            log.info("Couchbase cluster disconnected");
        }
    }
}