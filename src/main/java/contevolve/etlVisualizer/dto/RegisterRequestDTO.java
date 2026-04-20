package contevolve.etlVisualizer.dto;

import contevolve.etlVisualizer.enums.Role;
import lombok.Data;

@Data
public class RegisterRequestDTO {
	private String username;
	private String password;
	private Role role;
}
