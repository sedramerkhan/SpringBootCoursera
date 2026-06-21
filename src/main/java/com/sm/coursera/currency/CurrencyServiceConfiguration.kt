package com.sm.coursera.currency

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component


@Component
@ConfigurationProperties(prefix = "currency-service")
class CurrencyServiceConfiguration {
    var url: String? = null
    var username: String? = null
    var key: String? = null
}