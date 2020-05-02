package com.dansiwiec.eda

import groovy.transform.ToString

@ToString(includeNames = true)
class Order {

    String id
    String customerId
    List<String> items
}
