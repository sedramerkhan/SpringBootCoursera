package com.sm.coursera.auth

import org.springframework.stereotype.Service

/**
 * Business logic for authentication, kept out of the controller.
 *
 * @Service marks this as a Spring bean so it can be injected into the
 * controller (constructor injection). The controller stays thin: it handles
 * HTTP, this class decides whether the credentials are valid.
 *
 * Credentials are hard-coded for testing. In a real app they would come from
 * a user store / database and the password would be hashed, never compared
 * in plain text.
 */
@Service
class AuthService {

    // trim() guards against stray leading/trailing whitespace from the input
    // (autofill, copy-paste). "Sedra " would otherwise fail to match "Sedra".
    fun isValid(name: String, password: String): Boolean =
        name.trim().equals(VALID_NAME, ignoreCase = true) && password.trim() == VALID_PASSWORD

    companion object {
        private const val VALID_NAME = "Sedra"
        private const val VALID_PASSWORD = "123456789"
    }
}