package contevolve.etlVisualizer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import contevolve.etlVisualizer.service.CouchbaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("api/v1/cb")
@RequiredArgsConstructor
public class CouchbaseController {

	private final CouchbaseService couchbaseService;

	@GetMapping("validKeys")
	public ResponseEntity<List<String>> getValidKeys() {
		return ResponseEntity.ok(couchbaseService.getValidKeys());
	}

	@GetMapping("{bucket}/{scope}/{collection}/document/{Id}")
	public ResponseEntity<Map<String, Object>> getDocumentByID(@PathVariable String bucket, @PathVariable String scope,
			@PathVariable String collection, @PathVariable String Id) {
		log.info("GET /api/v1/cb/validKey");
		return couchbaseService.getDocumentByID(bucket, scope, collection, Id);

	}

	@GetMapping("{bucket}/{scope}/{collection}/documents-count")
	public ResponseEntity<Map<String, Object>> getDocumentCount(@PathVariable String bucket, @PathVariable String scope,
			@PathVariable String collection) {
		Map<String, Object> response = new HashMap<>();
		response.put("count", couchbaseService.getDocumentCount(bucket, scope, collection));
		return ResponseEntity.ok(response);
	}

	@GetMapping("{bucket}/{scope}/{collection}/distribution-data/{key}")
	public ResponseEntity<List<Map<String, Long>>> getDistributionByKey(@PathVariable String bucket,
			@PathVariable String scope, @PathVariable String collection, @PathVariable String key) {
		// this key should be in list of validKeys otherwise return []
		return ResponseEntity.ok(couchbaseService.getDistributionByKey(bucket, scope, collection, key));
	}

	@GetMapping("{bucket}/{scope}/{collection}/idList")
	public ResponseEntity<List<String>> getIdList(@PathVariable String bucket, @PathVariable String scope,
			@PathVariable String collection, @RequestParam String key, @RequestParam String value) {
		return ResponseEntity.ok(couchbaseService.getIdList(bucket, scope, collection, key, value));
	}

}
