package victor.training.performance.primitives.interning;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.Map.Entry;

public class ObviousJsonReader {

   public static void main(String[] args) throws Exception {
      List<Map<String, Object>> compacted = readJson();



      System.out.println("Entries in file: " + compacted.size());
      System.out.println("Memory loaded. ENTER to continue... ");
      new Scanner(System.in).nextLine();
   }

   private static List<Map<String, Object>> readJson() throws IOException {
      System.out.println("Loading JSON...");
      ObjectMapper mapper = new ObjectMapper();
      try (Reader reader = new FileReader("big.json")) {
         List<Map<String, Object>> list = mapper.readValue(reader, new TypeReference<List<Map<String, Object>>>() {
         });

         return compactMap(list);
      }
   }

   private static List<Map<String, Object>> compactMap(List<Map<String, Object>> list) {
      System.out.println("Compacting...");
      Map<String, String> identityMap = new HashMap<>();
      List<Map<String, Object>> compacted = new ArrayList<>();
      for (Map<String, Object> map : list) {
         Map<String, Object> compactMap = new HashMap<>(map.size());
         for (Entry<String, Object> entry : map.entrySet()) {
            String newValue = (String) entry.getValue(); // TODO hack here

            if (identityMap.containsValue(newValue)) {
               newValue = identityMap.get(newValue);
            } else {
               identityMap.put(newValue, newValue);
            }
            compactMap.put(entry.getKey(), newValue);
         }
         compacted.add(compactMap);
      }
      return compacted;
   }

}
