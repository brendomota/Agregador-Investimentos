package br.unesp.rc.agregadorinvestimentos.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.unesp.rc.agregadorinvestimentos.controller.dto.AccountDTO;
import br.unesp.rc.agregadorinvestimentos.controller.dto.AccountResponseDTO;
import br.unesp.rc.agregadorinvestimentos.controller.dto.UpdateUserDTO;
import br.unesp.rc.agregadorinvestimentos.controller.dto.UserDTO;
import br.unesp.rc.agregadorinvestimentos.entity.Account;
import br.unesp.rc.agregadorinvestimentos.entity.BillingAddress;
import br.unesp.rc.agregadorinvestimentos.entity.User;
import br.unesp.rc.agregadorinvestimentos.repository.AccountRepository;
import br.unesp.rc.agregadorinvestimentos.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    private UserRepository userRepository;

    private AccountRepository accountRepository;

    public UserService(UserRepository userRepository,
            AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
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

            if (updateUserDTO.name() != null) {
                user.setName(updateUserDTO.name());
            }

            if (updateUserDTO.password() != null) {
                user.setPassword(updateUserDTO.password());
            }
            userRepository.save(user);
        }
    }

    @Transactional
    public void createAccount(String id, AccountDTO accountDTO) {
        var user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var account = new Account(accountDTO.description(), user);
        account = accountRepository.save(account);

        var billingAddress = new BillingAddress(account, accountDTO.street(), accountDTO.number());

        account.setBillingAddress(billingAddress);

        accountRepository.save(account);
    }

    public List<AccountResponseDTO> getAccountsById(String id) {
        var user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return user.getAccounts()
            .stream()
            .map(ac -> new AccountResponseDTO(ac.getAccountId().toString(), ac.getDescription()))
            .toList();
    }

}
