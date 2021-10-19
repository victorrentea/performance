package victor.training.performance.cache;

import javax.persistence.*;
import java.util.List;

import static java.util.Collections.emptyList;

@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String name;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    public User() {
    }
    public User(String username) {
        this(username, username, UserRole.USER, emptyList());
    }
    public User(String fullName, String username, UserRole role, List<Long> managedTeacherIds) {
        this.username = username;
        this.name=fullName;
        this.role = role;
    }

    public String getName() {
        return name;
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

    public User setName(String name) {
        this.name = name;
        return this;
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
