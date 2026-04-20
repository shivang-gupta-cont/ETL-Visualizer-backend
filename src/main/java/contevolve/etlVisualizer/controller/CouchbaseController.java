package contevolve.etlVisualizer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.couchbase.client.core.error.DocumentNotFoundException;

import contevolve.etlVisualizer.service.CouchbaseService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/cb")
@RequiredArgsConstructor
public class CouchbaseController {
	
	private final CouchbaseService couchbaseService;
	
	@GetMapping("{bucket}/{scope}/{collection}/document/{key}")
	public ResponseEntity<Map<String, Object>> getDocumentByKey(
	        @PathVariable String bucket,
	        @PathVariable String scope,
	        @PathVariable String collection,
	        @PathVariable String key
	) {
	    try {
	        return couchbaseService.getDocumentByKey(bucket, scope, collection, key);

	    } catch (DocumentNotFoundException e) {
	        return ResponseEntity.status(404).body(null);

	    } catch (Exception e) {
	        return ResponseEntity.status(500).body(null);
	    }
	}
	
	@GetMapping("{bucket}/{scope}/{collection}/documents/total")
	public ResponseEntity<Map<String, Object>> getDocumentCount(
		@PathVariable String bucket,
        @PathVariable String scope,
        @PathVariable String collection
	){
		Map<String, Object> response = new HashMap<>();
		response.put("count", couchbaseService.getDocumentCount(bucket, scope, collection));
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("{bucket}/{scope}/{collection}/documents/dsaTypeData")
	public ResponseEntity<List<Map<String, Long>>> getDSAtypeData(@PathVariable String bucket,@PathVariable String scope,@PathVariable String collection) {
	    return ResponseEntity.ok(couchbaseService.getDSAtypeData(bucket, scope, collection));
	}
	
}
