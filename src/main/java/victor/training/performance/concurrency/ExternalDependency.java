package victor.training.performance.concurrency;

public interface ExternalDependency {
   /** Imagine some some external call */
   String retrieveEmail(int id);

   /** Imagine some some external call */
   boolean isEmailValid(String email);
}
