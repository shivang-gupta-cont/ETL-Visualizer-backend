package contevolve.etlVisualizer.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mongodb.DuplicateKeyException;

import contevolve.etlVisualizer.dto.LoginRequestDTO;
import contevolve.etlVisualizer.dto.LoginResponseDTO;
import contevolve.etlVisualizer.dto.RegisterRequestDTO;
import contevolve.etlVisualizer.enums.Role;
import contevolve.etlVisualizer.error.DuplicateResourceException;
import contevolve.etlVisualizer.model.RegisterationReqs;
import contevolve.etlVisualizer.model.Users;
import contevolve.etlVisualizer.repository.RegisterationReqsRepository;
import contevolve.etlVisualizer.repository.UsersRepository;
import contevolve.etlVisualizer.utils.JwtUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final UsersRepository usersRepository;
	private final RegisterationReqsRepository registerationReqsRepository;
	private final PasswordEncoder passwordEncoder;
	
		public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) throws UsernameNotFoundException{
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail().toLowerCase(), loginRequestDTO.getPassword())
			);
			
			Users user = (Users) authentication.getPrincipal(); 
			
			String tokenString = jwtUtil.generateAccessToken(user);
			
			return new LoginResponseDTO(tokenString, user.getUsername(), user.getEmail(), user.getRole());
		}

	public ResponseEntity<HttpStatus> register(RegisterRequestDTO registerRequestDTO) throws IllegalArgumentException{
		Users check1 = usersRepository.findByEmail(registerRequestDTO.getEmail()).orElse(null);
		if(check1 != null) throw new DuplicateResourceException("User Already Exists");
		
		RegisterationReqs check2 = registerationReqsRepository.findByEmail(registerRequestDTO.getEmail()).orElse(null);
		if(check2 != null) throw new DuplicateResourceException("Request Already Made.");
		
		if(usersRepository.findByUsername(registerRequestDTO.getUsername()).isPresent() ||
				registerationReqsRepository.findByUsername(registerRequestDTO.getUsername()).isPresent()) 
			throw new DuplicateResourceException("Username already taken.");
		
		registerationReqsRepository.save(RegisterationReqs.builder()
				.username(registerRequestDTO.getUsername())
				.email(registerRequestDTO.getEmail().toLowerCase())
				.password(passwordEncoder.encode(registerRequestDTO.getPassword()))
				.role(registerRequestDTO.getRole())
				.build()
		);
		
		return ResponseEntity.ok(null);
	}
}
