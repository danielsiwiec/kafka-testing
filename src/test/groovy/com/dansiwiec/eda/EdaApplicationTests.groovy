package com.dansiwiec.eda


import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.kafka.test.context.EmbeddedKafka

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@EmbeddedKafka(topics = ["orders"],
        bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        brokerProperties = ["log.dir=/tmp/out/embedded-kafka"])
class EdaApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createsOrder() {
        def url = "http://localhost:$port/orders"

        def order1 = new OrderRequest(itemId: "321", customerId: "123")
        restTemplate.postForObject(url, order1, Order).with {
            assert id == "0"
            assert items == ["321"]
        }
    }

}