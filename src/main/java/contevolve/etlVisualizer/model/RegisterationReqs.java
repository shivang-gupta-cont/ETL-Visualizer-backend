package contevolve.etlVisualizer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import contevolve.etlVisualizer.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Document(collection = "registrationReqs")
@Builder
public class RegisterationReqs {
	@Id
	private String id;  // ← MongoDB _id maps to this and auto generates this if null
	
	@Indexed(unique =  true)
	private String username;

	@Indexed(unique = true)
	private String email;
	
	private String password;
	private Role role;

}
