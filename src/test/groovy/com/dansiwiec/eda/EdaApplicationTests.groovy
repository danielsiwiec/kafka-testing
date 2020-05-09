package com.dansiwiec.eda


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.kafka.test.context.EmbeddedKafka
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@EmbeddedKafka(topics = ["orders"],
        bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        brokerProperties = ["log.dir=build/embedded-kafka"])
class EdaApplicationTests extends  Specification {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    def "should create an order"() {
        given:
        def url = "http://localhost:$port/orders"
        def request = new OrderRequest(itemId: "321", customerId: "123")
        when:
        def order = restTemplate.postForObject(url, request, Order)
        then:
        order.with {
            assert id == "0"
            assert items == ["321"]
        }
    }
}