//package contevolve.etlVisualizer.service;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import contevolve.etlVisualizer.dto.RegisterRequestInfoDTO;
//import contevolve.etlVisualizer.dto.UserInfoDTO;
//import contevolve.etlVisualizer.model.RegisterationReqs;
//import contevolve.etlVisualizer.model.Users;
//import contevolve.etlVisualizer.repository.RegisterationReqsRepository;
//import contevolve.etlVisualizer.repository.UsersRepository;
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class AdminService {
//
//    private final UsersRepository usersRepository;
//    private final RegisterationReqsRepository registerationReqsRepository;
//
//    public List<UserInfoDTO> getAllUsers() {
//        return usersRepository.findAll()
//        		.stream()
//        		.map(user -> new UserInfoDTO(user.getUsername(), user.getRole()))
//        		.toList();
//    }
//
//    public List<RegisterRequestInfoDTO> getAllRequests() {
//        return registerationReqsRepository.findAll()
//        		.stream()
//        		.map(registerationReqs -> new RegisterRequestInfoDTO(registerationReqs.getUsername(), registerationReqs.getRole()))
//        		.toList();
//    }
//
//    public void approveRequest(String username) {
//        RegisterationReqs req = registerationReqsRepository
//                .findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException(username));
//
//        Users newUser = Users.builder()
//                .username(req.getUsername())
//                .email(req.getEmail())
//                .password(req.getPassword()) // already hashed
//                .role(req.getRole())
//                .build();
//
//        usersRepository.save(newUser);
//        registerationReqsRepository.delete(req);
//    }
//
//    public void rejectRequest(String username) {
//        RegisterationReqs req = registerationReqsRepository
//                .findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException(username));
//
//        registerationReqsRepository.delete(req);
//    }
//
//	public void rejectAllRequest() {
//		registerationReqsRepository.deleteAll();
//	}
//
//	public void removeUser(String username) {
//		Users user = usersRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException(username));
//		usersRepository.deleteById(user.getId());
//	}
//    
//}


package contevolve.etlVisualizer.service;

import java.util.List;
import java.util.Optional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import contevolve.etlVisualizer.dto.RegisterRequestInfoDTO;
import contevolve.etlVisualizer.dto.UserInfoDTO;
import contevolve.etlVisualizer.model.RegisterationReqs;
import contevolve.etlVisualizer.model.Users;
import contevolve.etlVisualizer.repository.RegisterationReqsRepository;
import contevolve.etlVisualizer.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UsersRepository usersRepository;
    private final RegisterationReqsRepository registerationReqsRepository;

    public List<UserInfoDTO> getAllUsers() {
        log.debug("Fetching all users");
        List<UserInfoDTO> users = usersRepository.findAll()
                .stream()
                .map(user -> new UserInfoDTO(user.getUsername(), user.getRole()))
                .toList();
        log.debug("Total users found: {}", users.size());
        return users;
    }

    public List<RegisterRequestInfoDTO> getAllRequests() {
        log.debug("Fetching all pending registration requests");
        List<RegisterRequestInfoDTO> requests = registerationReqsRepository.findAll()
                .stream()
                .map(registerationReqs -> new RegisterRequestInfoDTO(registerationReqs.getUsername(), registerationReqs.getRole()))
                .toList();
        log.debug("Total pending requests found: {}", requests.size());
        return requests;
    }

    public void approveRequest(String username) {
        log.info("Approving registration request for username: {}", username);
        RegisterationReqs req = registerationReqsRepository
                .findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Approve failed - no registration request found for username: {}", username);
                    return new UsernameNotFoundException(username);
                });

        Users newUser = Users.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(req.getPassword())
                .role(req.getRole())
                .build();

        usersRepository.save(newUser);
        registerationReqsRepository.delete(req);
        log.info("User approved and created successfully: {}", username);
    }

    public void rejectRequest(String username) {
        log.info("Rejecting registration request for username: {}", username);
        RegisterationReqs req = registerationReqsRepository
                .findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Reject failed - no registration request found for username: {}", username);
                    return new UsernameNotFoundException(username);
                });

        registerationReqsRepository.delete(req);
        log.info("Registration request rejected for username: {}", username);
    }

    public void rejectAllRequest() {
        log.warn("Rejecting and deleting ALL pending registration requests");
        registerationReqsRepository.deleteAll();
        log.info("All pending registration requests deleted");
    }

    public void removeUser(String username) {
        log.info("Removing user: {}", username);
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Remove failed - user not found: {}", username);
                    return new UsernameNotFoundException(username);
                });

        usersRepository.deleteById(user.getId());
        log.info("User removed successfully: {}", username);
    }
}