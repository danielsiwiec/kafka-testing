package com.dansiwiec.eda

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.connect.json.JsonDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@EmbeddedKafka(topics = ["orders"],
        bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        brokerProperties = ["log.dir=build/embedded-kafka"])
class EdaApplicationTests extends  Specification {

    @LocalServerPort int port

    @Autowired TestRestTemplate restTemplate

    @Autowired EmbeddedKafkaBroker kafkaBroker

    Consumer<String, Order> consumer

    def setup() {
        def consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", kafkaBroker).tap {
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.canonicalName)
        }
        def cf = new DefaultKafkaConsumerFactory<String, Order>(consumerProps)
        consumer = cf.createConsumer()
    }

    def "should create an order and post on topic"() {
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
        kafkaBroker.consumeFromAnEmbeddedTopic(consumer, "orders")
        def replies = KafkaTestUtils.getRecords(consumer)
        assert replies.count() == 1
    }
}