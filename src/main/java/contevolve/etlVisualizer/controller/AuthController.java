//package contevolve.etlVisualizer.controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import contevolve.etlVisualizer.dto.LoginRequestDTO;
//import contevolve.etlVisualizer.dto.LoginResponseDTO;
//import contevolve.etlVisualizer.dto.RegisterRequestDTO;
//import contevolve.etlVisualizer.service.AuthService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//
//
//@RestController
//@RequestMapping("/api/v1/auth")
//@RequiredArgsConstructor
//public class AuthController {
//	
//	private final AuthService authService;
//	
//	@PostMapping("/login")
//	public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO){ 
//		return ResponseEntity.ok(authService.login(loginRequestDTO));
//	}
//	
//	@PostMapping("/register")
//	public ResponseEntity<HttpStatus> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO){
//		return authService.register(registerRequestDTO);
//	}
//	
//}


package contevolve.etlVisualizer.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import contevolve.etlVisualizer.dto.LoginRequestDTO;
import contevolve.etlVisualizer.dto.LoginResponseDTO;
import contevolve.etlVisualizer.dto.RegisterRequestDTO;
import contevolve.etlVisualizer.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        log.debug("POST /api/v1/auth/login - email: {}", loginRequestDTO.getEmail());
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        log.debug("POST /api/v1/auth/register - email: {}", registerRequestDTO.getEmail());
        return authService.register(registerRequestDTO);
    }
}
