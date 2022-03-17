package victor.training.performance.concurrency;

public interface ExternalDependency {
   /** Imagine some some external call */
//   @Timed("retrieve.email") // cauzeaza un Proxy sa intercepteze apelul si sa
   // raporteze timpul mediu/maxim... cat a durat >>> Prometheus > Grafana >
   // Telefonul tau alarma pe Slack cand in prod timpul max a sarit de 10s
   String retrieveEmail(int id);

   /** Imagine some some external call */
   boolean isEmailValid(String email);
}
