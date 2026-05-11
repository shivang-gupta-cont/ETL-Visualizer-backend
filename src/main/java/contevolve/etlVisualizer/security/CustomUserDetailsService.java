//package contevolve.etlVisualizer.security;
//
//
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import contevolve.etlVisualizer.repository.UsersRepository;
//import lombok.RequiredArgsConstructor;
//
//
//@Service
//@RequiredArgsConstructor
//public class CustomUserDetailsService implements UserDetailsService {
//
//	private final UsersRepository usersRepository;
//	
//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        return usersRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException(
//                    "Email not found: " + email
//                ));
//    }
//	
//}


package contevolve.etlVisualizer.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import contevolve.etlVisualizer.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found for email: {}", email);
                    return new UsernameNotFoundException("Email not found: " + email);
                });
    }
}