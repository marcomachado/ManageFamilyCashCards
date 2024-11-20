package com.familycashcard.user;

import com.familycashcard.cashcard.CashCard;
import com.familycashcard.cashcard.CashCardDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        List<User> allUsers = userRepository.findAll();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findOneUser(@PathVariable Long id) {
        Optional<User> userOptional = getUserById(id);
        return userOptional.map(ResponseEntity::ok).orElseGet((() -> ResponseEntity.notFound().build()));
    }

    private Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @GetMapping("/getusers/{name}")
    public ResponseEntity<List<User>> findUsersByName(@PathVariable String name) {
        List<User> userList = userRepository.findAllByNameContainingIgnoreCase(name);
        return ResponseEntity.ok(userList);
    }

    @PostMapping()
    public ResponseEntity<Void> createOneUser(@RequestBody UserDTO userDTO, UriComponentsBuilder ucb) {
        User user = userDTO.convertToUser();
        User savedUser = userRepository.save(user);

        URI locationSavedCashCard = ucb
                .path("users/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(locationSavedCashCard).build(); //return 201 and location
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> updateUser(@PathVariable Long requestedId,
                                             @RequestBody UserDTO userDTO) {
        Optional<User> userOptional = getUserById(requestedId);
        if (userOptional.isPresent()) {
            User updatedUser = userDTO.convertToUser();
            updatedUser.setId(requestedId);
            updatedUser.setActive(userDTO.isActive());

            userRepository.save(updatedUser);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}























