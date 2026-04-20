package contevolve.etlVisualizer.dto;

import contevolve.etlVisualizer.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
	String username;
	Role role;
}
