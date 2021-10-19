package victor.training.performance.cache;

import java.io.Serializable;

public class UserDto implements Serializable {
   public Long id;
   public String name;
   public UserRole profile;

   public UserDto() {
   }

   public UserDto(User user) {
      id = user.getId();
      name = user.getName();
      profile = user.getRole();
   }
}
