package com.familycashcard.user;

public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String username;
    private String password;
    private boolean active;

    @Deprecated
    public UserDTO() {
    }

    public UserDTO(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public UserDTO(String name, String email, String username, String password, boolean active) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isActive() {
        return active;
    }

    public User convertToUser() {
        return new User(this.name, this.email, this.username, this.password, this.active);
    }
}
