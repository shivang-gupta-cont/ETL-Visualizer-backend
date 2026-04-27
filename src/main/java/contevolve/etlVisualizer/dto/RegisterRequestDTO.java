package contevolve.etlVisualizer.dto;

import contevolve.etlVisualizer.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {
	@NotBlank(message = "Username cannot be empty.")
	private String username;
	
	@NotBlank(message = "Email cannot be empty.")
	@Email(message = "Invalid email format")
	@Pattern(
		    regexp = "(?i)^[a-z0-9._%+-]+@contevolve\\.com$",
		    message = "Email must be from @contevolve.com domain"
	)
	private String email;
	
	@NotBlank(message = "Password cannot be empty.")
	@Size(min = 6, message = "Password must be at least 6 characters.")
	private String password;
	private Role role;
}
