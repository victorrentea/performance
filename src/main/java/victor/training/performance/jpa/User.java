package victor.training.performance.jpa;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;

@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    @Enumerated(STRING)
    private UserRole role;

    public enum UserRole {
        USER,
        ADMIN
    }

    public User() {
    }
    public User(String username) {
        this(username, UserRole.USER);
    }
    public User(String username, UserRole role) {
        this.username = username;
        this.role = role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public UserRole getRole() {
        return role;
    }

    public Long getId() {
        return id;
    }


    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", role=" + role +
               '}';
    }
}
