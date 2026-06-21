package com.sm.coursera.currency

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/currency-configuration")
class CurrencyServiceConfigurationController(
    private val configuration: CurrencyServiceConfiguration
) {

    @GetMapping
    fun getConfiguration(): CurrencyServiceConfiguration = configuration
}