package contevolve.etlVisualizer.error;

import java.util.stream.Collectors;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.core.error.DocumentNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandling {

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException e) {
		ApiError apiError = e.getMessage().contains("@")
				? new ApiError("Email not found: " + e.getMessage(), HttpStatus.NOT_FOUND)
				: new ApiError("Username not found: " + e.getMessage(), HttpStatus.NOT_FOUND);

		return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ApiError> handleDuplicateResource(DuplicateResourceException e) {
		ApiError apiError = new ApiError(e.getMessage(), HttpStatus.CONFLICT);
		return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException e) {
		ApiError apiError = new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException e) {
		ApiError apiError = new ApiError("Invalid email or password ", HttpStatus.UNAUTHORIZED);
		return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException e) {
		String errorMessage = e.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField().toUpperCase() + ": " + error.getDefaultMessage())
				.collect(Collectors.joining(", "));

		ApiError apiError = new ApiError(errorMessage, HttpStatus.BAD_REQUEST);

		return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
	}

	// couchbaseException Handlers

	@ExceptionHandler(DataRetrievalFailureException.class)
	public ResponseEntity<ApiError> handleDataRetrievalFailureException(DataRetrievalFailureException e) {
		ApiError apiError = new ApiError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(DocumentNotFoundException.class)
	public ResponseEntity<ApiError> handleDocumentNotFoundException(DocumentNotFoundException e) {
		ApiError apiError = new ApiError("Document with the given id not found.", HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(CouchbaseException.class)
	public ResponseEntity<ApiError> handleCouchbaseException(CouchbaseException e) {
		ApiError apiError = new ApiError("Some CouchBase Error", HttpStatus.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}