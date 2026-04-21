package com.example.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:authtest",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "app.jwt.secret=dGhpcyBpcyBhIHZlcnkgbG9uZyBzZWNyZXQga2V5IGZvciBkZW1vIHB1cnBvc2VzIG9ubHkgLSBjaGFuZ2UgaXQgaW4gcHJvZHVjdGlvbiAtIG1pbiA1MTIgYml0cw=="
})
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
