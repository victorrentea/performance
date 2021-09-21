package victor.training.performance.cache;

public class UserDto {
   public Long id;
   public String name;

   public UserDto() {
   }

   public UserDto(User user) {
      id = user.getId();
      name = user.getName();
   }
}
