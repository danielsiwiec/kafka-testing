package com.dansiwiec.eda

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = "/orders")
class OrdersController {

    @Autowired
    KafkaTemplate<String, Order> template

    int currentOrderId = 0

    @PostMapping
    Order create(@RequestBody OrderRequest request) {
        def order = new Order(id: currentOrderId++, customerId: request.customerId, items: [request.itemId])
        template.send(Topics.ORDERS, order.id, order)
        return order
    }
}
