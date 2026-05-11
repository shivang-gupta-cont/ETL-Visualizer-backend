//package contevolve.etlVisualizer.controller;
//
//import java.util.List;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import contevolve.etlVisualizer.dto.RegisterRequestInfoDTO;
//import contevolve.etlVisualizer.dto.UserInfoDTO;
//import contevolve.etlVisualizer.service.AdminService;
//import lombok.RequiredArgsConstructor;
//
//
//@RestController
//@RequestMapping("/api/v1/admin")
//@RequiredArgsConstructor
//public class AdminController {
//
//    private final AdminService adminService;
//
//    @GetMapping("/users")
//    public ResponseEntity<List<UserInfoDTO>> getAllUsers() {
//        return ResponseEntity.ok(adminService.getAllUsers());
//    }
//    
//    @DeleteMapping("/user/{username}/remove")
//    public ResponseEntity<String>removeUser(@PathVariable String username){
//    	adminService.removeUser(username);
//    	return ResponseEntity.noContent().build();
//    }
//
//
//    @GetMapping("/registrations")
//    public ResponseEntity<List<RegisterRequestInfoDTO>> getAllRequests() {
//        return ResponseEntity.ok(adminService.getAllRequests());
//    }
//
//    @PatchMapping("/registrations/{username}/approve")
//    public ResponseEntity<String> approveRequest(@PathVariable String username) {
//        adminService.approveRequest(username);
//        return ResponseEntity.noContent().build();
//    }
//
//    @DeleteMapping("/registrations/{username}/reject")
//    public ResponseEntity<String> rejectRequest(@PathVariable String username) {
//        adminService.rejectRequest(username);
//        return ResponseEntity.noContent().build();
//    }
//    
//    @DeleteMapping("/registerations/reject-all")
//    public ResponseEntity<String> rejectAllRequest(){
//    	adminService.rejectAllRequest();
//    	return ResponseEntity.noContent().build();
//    }
//}


package contevolve.etlVisualizer.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import contevolve.etlVisualizer.dto.RegisterRequestInfoDTO;
import contevolve.etlVisualizer.dto.UserInfoDTO;
import contevolve.etlVisualizer.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserInfoDTO>> getAllUsers() {
        log.debug("GET /api/v1/admin/users");
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @DeleteMapping("/user/{username}/remove")
    public ResponseEntity<String> removeUser(@PathVariable String username) {
        log.debug("DELETE /api/v1/admin/user/{}/remove", username);
        adminService.removeUser(username);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/registrations")
    public ResponseEntity<List<RegisterRequestInfoDTO>> getAllRequests() {
        log.debug("GET /api/v1/admin/registrations");
        return ResponseEntity.ok(adminService.getAllRequests());
    }

    @PatchMapping("/registrations/{username}/approve")
    public ResponseEntity<String> approveRequest(@PathVariable String username) {
        log.debug("PATCH /api/v1/admin/registrations/{}/approve", username);
        adminService.approveRequest(username);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/registrations/{username}/reject")
    public ResponseEntity<String> rejectRequest(@PathVariable String username) {
        log.debug("DELETE /api/v1/admin/registrations/{}/reject", username);
        adminService.rejectRequest(username);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/registerations/reject-all")
    public ResponseEntity<String> rejectAllRequest() {
        log.debug("DELETE /api/v1/admin/registerations/reject-all");
        adminService.rejectAllRequest();
        return ResponseEntity.noContent().build();
    }
}