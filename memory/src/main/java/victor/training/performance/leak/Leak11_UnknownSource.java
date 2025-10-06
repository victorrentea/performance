package victor.training.performance.leak;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static victor.training.performance.util.PerformanceUtil.done;

@RestController
public class Leak11_UnknownSource {
  @GetMapping("leak11")
  public String endpoint() {
    return "Money transferred" + done();
  }
}




// === === === === === === === Support code  === === === === === === ===

@RestController
class Leak11_Caller {
  @GetMapping("leak11/caller")
  public String endpoint() {
    return """
          <button onclick='call(self.crypto.randomUUID())'>Call it ✅</button>
          <div id='response' style='background: lightyellow'></div>
         <p>
         But a <a href='/leak11'>direct call ❌</a> gets rejected 
         if it doesn't contain a new 'Idempotency-Key' header.<p>
           Sending the same header value 'same-ik' 
           <button onclick='call("same-ik")'>Should Fail ❌ after first call</button>
        <p> 
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
