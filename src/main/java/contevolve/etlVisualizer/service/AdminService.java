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

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UsersRepository usersRepository;
    private final RegisterationReqsRepository registerationReqsRepository;

    public List<UserInfoDTO> getAllUsers() {
        return usersRepository.findAll()
        		.stream()
        		.map(user -> new UserInfoDTO(user.getUsername(), user.getRole()))
        		.toList();
    }

    public List<RegisterRequestInfoDTO> getAllRequests() {
        return registerationReqsRepository.findAll()
        		.stream()
        		.map(registerationReqs -> new RegisterRequestInfoDTO(registerationReqs.getUsername(), registerationReqs.getRole()))
        		.toList();
    }

    public void approveRequest(String username) {
        RegisterationReqs req = registerationReqsRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Users newUser = Users.builder()
                .username(req.getUsername())
                .password(req.getPassword()) // already hashed
                .role(req.getRole())
                .build();

        usersRepository.save(newUser);
        registerationReqsRepository.delete(req);
    }

    public void rejectRequest(String username) {
        RegisterationReqs req = registerationReqsRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        registerationReqsRepository.delete(req);
    }

	public void rejectAllRequest() {
		registerationReqsRepository.deleteAll();
	}

	public void removeUser(String username) {
		Users user = usersRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException(username));
		usersRepository.deleteById(user.getId());
	}
    
    // TODO: clearAllRequests
}