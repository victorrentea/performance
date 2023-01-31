package victor.training.performance.spring.caching;

import victor.training.performance.jpa.User;
import victor.training.performance.jpa.User.UserRole;

import java.io.Serializable;

public class UserDto implements Serializable {
   public Long id;
   public String username;
   public UserRole profile;

   public UserDto() {
   }

   public UserDto(User user) {
      id = user.getId();
      username = user.getUsername();
      profile = user.getRole();
   }
}
