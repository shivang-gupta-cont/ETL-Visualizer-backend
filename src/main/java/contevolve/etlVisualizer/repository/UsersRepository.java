package contevolve.etlVisualizer.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import contevolve.etlVisualizer.model.Users;

public interface UsersRepository extends MongoRepository<Users, String>{

    Optional<Users> findByUsername(String username);

}
