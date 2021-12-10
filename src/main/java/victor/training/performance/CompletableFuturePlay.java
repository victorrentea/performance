package victor.training.performance;

import victor.training.performance.util.PerformanceUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class NewsFeed {
   private final List<String> news;
   private final String weather;

   NewsFeed(List<String> news, String weather) {
      this.news = news;
      this.weather = weather;
   }
}

public class CompletableFuturePlay {
   private static final ExecutorService languageRetrievalPool = Executors.newFixedThreadPool(20);
   private static final ExecutorService inifinitePool = Executors.newCachedThreadPool();

   public CompletableFuture<NewsFeed> provideSuggestion(int personId, Long gpsLocation) { // takes orders from arbitrary threads (any number) 2-100
      CompletableFuture<Void> authCF = CompletableFuture.runAsync(() -> {
         if (!authenticate(personId)) {
            throw new IllegalArgumentException();
         }
      },inifinitePool);

      // fork in 2 parallel flows
      CompletableFuture<List<String>> newsCF = authCF
          .thenApplyAsync(r -> retrieveLanguage(personId), languageRetrievalPool)
          .thenApplyAsync(language -> retrieveAreasOfInterest(personId, language), inifinitePool)
          .thenApplyAsync(person -> provideSuggestionViaPythonInvokedAIOnMyMachine(person.getLanguage(), person.getAreas()));

      CompletableFuture<String> weatherCF = authCF.thenApplyAsync(r -> fetchWeather(gpsLocation), inifinitePool);

      // join back.
      return newsCF.thenCombine(weatherCF, (news,weather) -> new NewsFeed(news, weather));

//      new AsyncRestTemplate().get
   }

   public boolean authenticate(int personId)  { // NETWORK superfast superscalable
      return Math.random() < 0.5;
   }

   public String fetchWeather(Long gpsLocation) { // highly scalable
      PerformanceUtil.sleepq(100);
      return "UK cloudy";
   }

   private static class PersonWithAreas {
      private final int personId;
      private final String language;
      private final List<String> areas;

      private PersonWithAreas(int personId, String language, List<String> areas) {
         this.personId = personId;
         this.language = language;
         this.areas = areas;
      }

      public List<String> getAreas() {
         return areas;
      }

      public String getLanguage() {
         return language;
      }
   }

   // try to reduce the bottleneck by continuously firing 20 || requests .
   private String retrieveLanguage(int personId) {  // max 20 req in parallel
      PerformanceUtil.sleepq(10);
      return "a";
   }

   public PersonWithAreas retrieveAreasOfInterest(int personId, String language) { // separate system super scalable. cloud, elastisc autoscaling AWS Lambda
      PerformanceUtil.sleepq(100);
      return new PersonWithAreas(personId, language, List.of("dogs", "whishey"));
   }

   public List<String> provideSuggestionViaPythonInvokedAIOnMyMachine(String language, List<String> areasOfInterest) {
      // 100 % CPU on my machine
      PerformanceUtil.cpu(10);
      return List.of("news1", "news22");
   }
}
