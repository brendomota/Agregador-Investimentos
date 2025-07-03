package br.unesp.rc.agregadorinvestimentos.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.unesp.rc.agregadorinvestimentos.controller.UserDTO;
import br.unesp.rc.agregadorinvestimentos.entity.User;
import br.unesp.rc.agregadorinvestimentos.repository.UserRepository;

@Service
public class UserService {
    

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UUID createUser(UserDTO userDTO) {

        User user = new User(
        userDTO.name(), 
        userDTO.email(), 
        userDTO.password(),
        Instant.now(),
        null);
        userRepository.save(user);
        return user.getId();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(UUID.fromString(id));
    }
}
