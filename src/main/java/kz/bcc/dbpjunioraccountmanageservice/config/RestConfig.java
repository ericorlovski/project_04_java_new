package kz.bcc.dbpjunioraccountmanageservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;

@Configuration
public class RestConfig {

    @Value("${proxy.url}")
    private String proxyUrl;

    @Bean(name = "default")
    @Primary
    public RestTemplate defaultRestTemplate(RestTemplateBuilder builder) {
        return
                builder
                        .setConnectTimeout(Duration.ofSeconds(30))
                        .setReadTimeout(Duration.ofSeconds(30))
                        .build();
    }

    @Bean(name = "extended")
    public RestTemplate extendedTORestTemplate(RestTemplateBuilder builder) {
        return
                builder
                        .setConnectTimeout(Duration.ofSeconds(90))
                        .setReadTimeout(Duration.ofSeconds(90))
                        .build();
    }

    @Bean(name = "proxy")
    public RestTemplate proxyRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl, 8080));
        requestFactory.setProxy(proxy);

        return new RestTemplate(requestFactory);
    }
}
