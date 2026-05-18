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
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
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
//        log.info("Connecting to Couchbase cluster...");
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
//        log.info("Couchbase cluster connected successfully");
//    }
//
//    public Collection getCollection(String bucketName, String scopeName, String collectionName) {
//        log.debug("Getting collection: {}.{}.{}", bucketName, scopeName, collectionName);
//        try {
//            Bucket bucket = cluster.bucket(bucketName);
//            bucket.waitUntilReady(Duration.ofSeconds(5));
//            return bucket.scope(scopeName).collection(collectionName);
//        } catch (Exception e) {
//            log.error("Failed to get collection {}.{}.{} - {}", bucketName, scopeName, collectionName, e.getMessage());
//            throw new IllegalStateException("Cannot connect to Couchbase: " + e.getMessage());
//        }
//    }
//
//    public Cluster getCluster() {
//        return cluster;
//    }
//
//    @PreDestroy
//    public void shutdown() {
//        if (cluster != null) {
//            cluster.disconnect();
//            log.info("Couchbase cluster disconnected");
//        }
//    }
//}

package contevolve.etlVisualizer.config;

import java.time.Duration;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.core.error.UnambiguousTimeoutException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;

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

	private String currHost;

	public void connect(String host) {
		if (Objects.equals(host, currHost)) {
			log.info("Already connected to host: {}. Skipping reconnection.", host);
			return;
		}
		shutdown();
		log.info("Connecting to Couchbase at: {}", host);
		try {
			Cluster newCluster = Cluster.connect(host,
					ClusterOptions.clusterOptions(username, password).environment(env -> {
						env.timeoutConfig(timeout -> timeout.connectTimeout(Duration.ofSeconds(10))
								.kvTimeout(Duration.ofSeconds(5)).queryTimeout(Duration.ofSeconds(5)));
						env.ioConfig(io -> io.numKvConnections(4));
					}));
			newCluster.waitUntilReady(Duration.ofSeconds(10));
			cluster = newCluster;
			currHost = host;
			log.info("Couchbase connected successfully to: {}", host);
		} catch (Exception e) {
			cluster = null;
			currHost = null;
			log.error("Couchbase connection failed at {}: {}", host, e.getMessage());
			throw new IllegalStateException("Cannot connect to Couchbase at [" + host + "]: " + e.getMessage());
		}
	}

	public Cluster getCluster() {
		return cluster;
	}

	public String getCurrentHost() {
		return currHost;
	}

	public Collection getCollection(String bucketName, String scopeName, String collectionName) {
		log.debug("Getting collection: {}.{}.{}", bucketName, scopeName, collectionName);
		try {
			Bucket bucket = getCluster().bucket(bucketName);
			bucket.waitUntilReady(Duration.ofSeconds(5));
			return bucket.scope(scopeName).collection(collectionName);
		} catch (NullPointerException e) {
			log.error(
					"Cluster is null. No active connection to Couchbase. Call /connect endpoint first before accessing data.");
			throw new IllegalStateException("Couchbase is not connected. Please connect to a host first.");
		} catch (Exception e) {
			log.error("Failed to get collection {}.{}.{} - {}", bucketName, scopeName, collectionName, e.getMessage());
			throw new IllegalStateException("Cannot connect to Couchbase: " + e.getMessage());
		}
	}

	public QueryResult runQuery(String query) {
		log.debug("Running query: {}", query);
		try {
			QueryResult result = getCluster().query(query, QueryOptions.queryOptions().timeout(Duration.ofSeconds(5)));
			log.debug("Query executed successfully");
			return result;
		} catch (UnambiguousTimeoutException e) {
			log.error("Query timed out after 5 seconds: {}", query);
			throw new IllegalStateException("Query timed out after 5 seconds. Couchbase may be slow or down.");
		} catch (CouchbaseException e) {
			log.error("Query failed: {} - {}", query, e.getMessage());
			throw new IllegalStateException("Query failed: " + e.getMessage());
		} catch (NullPointerException e) {
			log.error(
					"Cluster is null. No active connection to Couchbase. Call /connect endpoint first before accessing data.");
			throw new IllegalStateException("Couchbase is not connected. Please connect to a host first.");
		}
	}

	@PreDestroy
	public void shutdown() {
		if (cluster != null) {
			String host = currHost;
			cluster.disconnect();
			cluster = null;
			currHost = null;
			log.info("Couchbase disconnected from: {}", host);
		}
	}
}
