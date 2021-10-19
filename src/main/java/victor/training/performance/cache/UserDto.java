package victor.training.performance.cache;

public class UserDto {
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
