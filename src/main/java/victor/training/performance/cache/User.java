package victor.training.performance.cache;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableSet;

@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String name;
    @ElementCollection
    private Set<Long> managedTeacherIds = new HashSet<>();
    public User() {
    }
    public User(String username) {
        this(username, username, emptyList());
    }
    public User(String fullName, String username, List<Long> managedTeacherIds) {
        this.username = username;
        this.name=fullName;
        this.managedTeacherIds = new HashSet<>(managedTeacherIds);
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public Set<Long> getManagedTeacherIds() {
        return unmodifiableSet(managedTeacherIds);
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
               ", managedTeacherIds=" + managedTeacherIds +
               '}';
    }
}
