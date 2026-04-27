package contevolve.etlVisualizer.model;

import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import contevolve.etlVisualizer.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Document(collection = "users")
@Builder
public class Users implements UserDetails{
	
	@Id
	private String id;  // ← MongoDB _id maps to this and auto generates this if null
	
	@Indexed(unique =  true)
	private String username;
	@Indexed(unique = true)
	private String email;
	
	private String password;
	private Role role;
	
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
	    
}
