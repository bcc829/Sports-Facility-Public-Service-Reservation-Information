package org.jeong.reservationinformation.common.handler

import org.jeong.reservationinformation.common.util.Log
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.security.InvalidParameterException


@Component
@Order(-2)
class GlobalWebExceptionHandler : ErrorWebExceptionHandler {

    companion object LOG: Log()

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {

        val responseStatusException = when (ex) {
            is NoSuchElementException -> ResponseStatusException (HttpStatus.NOT_FOUND, ex.message)
            is IllegalAccessException -> ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, ex.message)
            is AssertionError -> ResponseStatusException(HttpStatus.BAD_REQUEST, ex.message)
            is NullPointerException -> ResponseStatusException(HttpStatus.BAD_REQUEST)
            is InvalidParameterException -> ResponseStatusException(HttpStatus.BAD_REQUEST, ex.message)
            is ResponseStatusException -> { logger.error(ex.message); ex }
            else -> { logger.error(ex.message); ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "server error") }
        }



        exchange.response.headers.contentType = MediaType.APPLICATION_PROBLEM_JSON
        exchange.response.statusCode = responseStatusException.status
        val buffer = exchange.response.bufferFactory().wrap("""{
                "message": "${responseStatusException.reason}",
                "status": "${responseStatusException.status}",
                "error": "${responseStatusException.status.reasonPhrase}"
            }""".toByteArray())
        return exchange.response.writeWith(Mono.just(buffer))
    }

}