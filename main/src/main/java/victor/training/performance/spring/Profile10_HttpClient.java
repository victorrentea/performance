package victor.training.performance.spring;

import brave.http.HttpServerResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.sql.Clob;

@RestController
@RequestMapping("profile/httpclient")
class Profile10_HttpClient {
  @Autowired
  private RestTemplate customRestTemplate;

  @GetMapping
  public String method(HttpServletResponse response)  {
//    Clob c;
//    IOUtils.copy(c.getCharacterStream(), response.getWriter());
    String data = customRestTemplate.getForObject("http://localhost:9999/fast20ms", String.class);
    return "result: " + data;
  }

  @Configuration
  public static class RestTemplateOverHttpClientConfig {
    @Bean
    public static RestTemplate customRestTemplate() {
      //  🛑 Using a large unknown framework for CRITICAL operations without benchmarking it
      HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
      clientHttpRequestFactory.setConnectTimeout(5000);
      // copy-pasted code from Stack Overflow (like a Pro)
      return new RestTemplate(clientHttpRequestFactory);
    }
  }


}

