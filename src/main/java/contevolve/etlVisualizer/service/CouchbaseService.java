package contevolve.etlVisualizer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.codec.TypeRef;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryResult;

import contevolve.etlVisualizer.config.CouchbaseManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouchbaseService {
	private final CouchbaseManager couchbaseManager;

	// in future: keyList can be specified according to buckets
	List<String> KEYS = List.of("dsaType", "srcType");

	// Main Services
	public ResponseEntity<Map<String, Object>> getDocumentByID(String bucket, String scope, String collection, String Id) {
		GetResult result = couchbaseManager.getCollection(bucket, scope, collection).get(Id);

		Map<String, Object> fullDocument = result.contentAs(new TypeRef<Map<String, Object>>() {
		});

		return ResponseEntity.ok(fullDocument);
	}

	public long getDocumentCount(String bucket, String scope, String collection) {
		String query = String.format("SELECT COUNT(*) AS docCount FROM `%s`.`%s`.`%s`", bucket, scope, collection);

		QueryResult result = runQuery(query);

		Map<String, Object> row = result.rowsAs(new TypeRef<Map<String, Object>>() {
		}).get(0);
		return ((Number) row.get("docCount")).longValue();
	}

	public List<Map<String, Long>> getDistributionByKey(String bucket, String scope, String collection, String key) {
		if (!isValidKey(key))
			throw new IllegalArgumentException("Distribution cannot be provided by this key:" + key);

		String query = String.format(
				"SELECT `%s`, COUNT(*) AS count FROM `%s`.`%s`.`%s` " + "WHERE `%s` IS NOT MISSING GROUP BY `%s`", key,
				bucket, scope, collection, key, key);

		QueryResult result = runQuery(query);

		return result.rowsAsObject().stream().map(row -> {
			Map<String, Long> entry = new HashMap<>();
			entry.put(row.getString(key), row.getLong("count"));
			return entry;
		}).toList();
	}

	public List<String> getValidKeys() {
		return KEYS;
	}

	public List<String> getIdList(String bucket, String scope, String collection, String key, String value) {
		// Get all document IDs
		String query = String.format("SELECT RAW META().id FROM `%s`.`%s`.`%s`", bucket, scope, collection);

		QueryResult idResult = runQuery(query);
		List<String> allIds = idResult.rowsAs(String.class);

		Collection col = couchbaseManager.getCollection(bucket, scope, collection);
		return allIds.stream().map(id -> {
			try {
				// Fetch full document by ID
				GetResult doc = col.get(id); // whole document in couchbase format

				// Convert to Java Map // whole document in java map format
				Map<String, Object> docMap = doc.contentAs(new TypeRef<Map<String, Object>>() {
				});

				Object extractedValue = docMap.get(key);
				if (extractedValue == null)
					return null;

				return String.valueOf(extractedValue).equals(value) ? id : null;

			} catch (Exception e) {
				System.err.println("Failed to fetch doc: " + id + " → " + e.getMessage());
				return null;
			}
		}).filter(Objects::nonNull) // remove all nulls from the list
				.collect(Collectors.toList());
	}

	// Helper Services
	public QueryResult runQuery(String query) {
		try {
			return couchbaseManager.getCluster().query(query);
		} catch (CouchbaseException e) {
			throw new DataRetrievalFailureException("Failed in retrieving data.");
		}
	}

	public boolean isValidKey(String key) {
		// in future: it will fetch all the valid keys from the document (maybe also
		// depending upon the bucket)
		return KEYS.contains(key);
	}

}
