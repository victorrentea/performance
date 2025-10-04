package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class Leak11_UnknownSource {
  @GetMapping("leak11")
  public String endpoint() {
    return "✔️ Money transferred at " + LocalDateTime.now();
  }
}


// === === === === === === === Support code  === === === === === === ===

@RestController
class Leak11_Caller {
  @GetMapping("leak11/caller")
  public String endpoint() {
    return """
          <button onclick='call(self.crypto.randomUUID())'>Call it</button>
          <div id='response'></div>
         <p>
         But a <a href='/leak11'>direct call</a> gets rejected 
         if it doesn't contain a new 'Idempotency-Key' header.<p>
           The same header value <button onclick='call("same-ik")'>Should Fail</button>
        <p> 
         Give it some 🔥 with Leak11Load
          <script type='text/javascript'>
              function call(idempotencyKey) {
                  fetch('/leak11', {
                      headers: { 'Idempotency-Key': idempotencyKey}
                  })
                  .then(resp => resp.text())
                  .then(text => document.getElementById('response').innerText = text)
              } 
          </script>
        """;
  }
}
