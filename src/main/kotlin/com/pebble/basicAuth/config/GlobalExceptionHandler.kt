package com.pebble.basicAuth.config

import com.pebble.basicAuth.domain.UserException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserException::class)
    fun handleUserException(e: UserException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.badRequest()
            .body(mapOf("message" to (e.message ?: "알 수 없는 오류가 발생했습니다.")))
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(e: BadCredentialsException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(mapOf("message" to "아이디 또는 비밀번호가 올바르지 않습니다."))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<Map<String, String>> {
        val message = e.bindingResult.fieldErrors
            .firstOrNull()?.defaultMessage ?: "입력값이 올바르지 않습니다."
        return ResponseEntity.badRequest()
            .body(mapOf("message" to message))
    }

    @ExceptionHandler(Exception::class)
    fun handleAllException(e: Exception): ResponseEntity<Map<String, String>> {
        return ResponseEntity.internalServerError()
            .body(mapOf("message" to "서버 내부 오류가 발생했습니다."))
    }
}
