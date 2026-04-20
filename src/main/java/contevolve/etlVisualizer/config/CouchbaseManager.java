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

@Component
public class CouchbaseManager {

    private Cluster cluster;

    
    @Value("${couchbase.username}")
    private String username;
    
    @Value("${couchbase.password}")
    private String password;

    @PostConstruct
    public void connect() {
        cluster = Cluster.connect(
            "couchbase://localhost",
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
    }

    public Cluster getCluster() { return cluster; }
    public Collection getCollection(String bucketName, String scopeName, String collectionName) {
    	Bucket bucket = cluster.bucket(bucketName);
        bucket.waitUntilReady(Duration.ofSeconds(10));
        
        return bucket.scope(scopeName).collection(collectionName);
    }

    @PreDestroy
    public void shutdown() {
    	if (cluster != null) {
            cluster.disconnect();
        }
    }
}