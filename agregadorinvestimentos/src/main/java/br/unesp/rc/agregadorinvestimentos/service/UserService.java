package br.unesp.rc.agregadorinvestimentos.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.unesp.rc.agregadorinvestimentos.controller.UpdateUserDTO;
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
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(UUID.fromString(id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteById(String id) {
        var userId = UUID.fromString(id);

        var userExists = userRepository.existsById(userId);
        if (userExists) {
            userRepository.deleteById(userId);
        }
    }

    public void updateUserById(String id, UpdateUserDTO updateUserDTO) {
        var userId = UUID.fromString(id);

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            var user = userOptional.get();

            if(updateUserDTO.name() != null) {
                user.setName(updateUserDTO.name());
            }

            if(updateUserDTO.password() != null) {
                user.setPassword(updateUserDTO.password());
            }
            userRepository.save(user);
        }
    }
}
