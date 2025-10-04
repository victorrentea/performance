package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class Leak28_UnknownSource {
  @GetMapping("leak28")
  public String endpoint() {
    return "✔️ Money transferred at " + LocalDateTime.now();
  }
}


// === === === === === === === Support code  === === === === === === ===

@RestController
class Leak28_Caller {
  @GetMapping("leak28/caller")
  public String endpoint() {
    return """
          <button onclick='call(self.crypto.randomUUID())'>Call it</button>
          <div id='response'></div>
         <p>
         But a <a href='/leak28'>direct call</a> gets rejected 
         if it doesn't contain a new 'Idempotency-Key' header.<p>
           The same header value <button onclick='call("same-ik")'>Should Fail</button>
        <p> 
         Give it some 🔥 with Leak28Load
          <script type='text/javascript'>
              function call(idempotencyKey) {
                  fetch('/leak28', {
                      headers: { 'Idempotency-Key': idempotencyKey}
                  })
                  .then(resp => resp.text())
                  .then(text => document.getElementById('response').innerText = text)
              } 
          </script>
        """;
  }
}
