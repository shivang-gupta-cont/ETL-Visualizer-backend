package contevolve.etlVisualizer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.couchbase.client.java.codec.TypeRef;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryResult;

import contevolve.etlVisualizer.config.CouchbaseManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouchbaseService {
	private final CouchbaseManager couchbaseManager;
	
	public QueryResult runQuery(String query) {
		return couchbaseManager.getCluster().query(query);
	}
	
	public ResponseEntity<Map<String, Object>> getDocumentByKey(
	       String bucket,
	       String scope,
	       String collection,
	       String key
	) {
        GetResult result = couchbaseManager
            .getCollection(bucket, scope, collection)
            .get(key);
        
        Map<String, Object> fullDocument = result.contentAs(new TypeRef<Map<String, Object>>() {});

        return ResponseEntity.ok(fullDocument);
	}
		
	public long getDocumentCount(String bucket, String scope, String collection) {
		String query = String.format(
				"SELECT COUNT(*) AS docCount FROM `%s`.`%s`.`%s`",
				bucket, scope, collection
		);
		
		QueryResult result = runQuery(query);
		
		Map<String, Object> row = result.rowsAs(new TypeRef<Map<String, Object>>() {}).get(0);
	    return ((Number) row.get("docCount")).longValue();
	}

	public List<Map<String, Long>> getDSAtypeData(String bucket, String scope, String collection) {
	    String query = String.format(
	        "SELECT `dsaType`, COUNT(*) AS count FROM `%s`.`%s`.`%s` WHERE `dsaType` IS NOT MISSING GROUP BY `dsaType`",
	        bucket, scope, collection
	    );

	    QueryResult result = runQuery(query);
	    
	    return result
	        .rowsAsObject()
	        .stream()
	        .map(row -> {
	            Map<String, Long> entry = new HashMap<>();
	            entry.put(row.getString("dsaType"), row.getLong("count"));
	            return entry;
	        })
	        .toList();
	}
}
