package contevolve.etlVisualizer.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import contevolve.etlVisualizer.model.RegisterationReqs;
import contevolve.etlVisualizer.model.Users;

public interface RegisterationReqsRepository extends MongoRepository<RegisterationReqs, String> {

	Optional<RegisterationReqs> findByUsername(String username);

	Users save(Users users);
	
	Optional<RegisterationReqs> findByEmail(String email);

}
