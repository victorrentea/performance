package victor.training.performance;

public interface ExternalDependency {
   boolean isAlive(int id);

   /** Imagine some some external call */
   String retrieveEmail(int id);

   /** Imagine some some external call */
   boolean checkEmail(String email);
}
