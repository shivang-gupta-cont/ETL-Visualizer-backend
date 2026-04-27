package contevolve.etlVisualizer.dto;

import contevolve.etlVisualizer.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

	String jwtString;
	String username;
	String email;
	Role role;
}
