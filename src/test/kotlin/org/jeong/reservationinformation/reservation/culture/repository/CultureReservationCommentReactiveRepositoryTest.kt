package org.jeong.reservationinformation.reservation.culture.repository

import org.jeong.reservationinformation.reservation.culture.domian.document.CultureReservationComment
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import reactor.test.StepVerifier

@SpringBootTest
class CultureReservationCommentReactiveRepositoryTest {

    @Autowired
    lateinit var cultureReservationCommentReactiveRepository: CultureReservationCommentReactiveRepository

    @BeforeEach
    fun beforeTest() {
        cultureReservationCommentReactiveRepository.deleteAll().block()
    }

    @Test
    fun cultureReservationCommentReactiveRepositorySaveTest() {
        val data = cultureReservationCommentReactiveRepository
                .save(CultureReservationComment(
                        cultureReservationSvcId = "testSvcId",
                        comment = "test comment",
                        password = "password",
                        rating = 3,
                        userName = "tester"
                ))

        StepVerifier
                .create(data)
                .assertNext {
                    assertEquals("password", it.password)
                    assertEquals("test comment", it.comment)
                }
                .verifyComplete()
    }

    @Test
    fun cultureReservationCommentReactiveRepositoryFindByCultureReservationSvcIdOrderByRegisterDateDesc() {
        cultureReservationCommentReactiveRepository
                .save(CultureReservationComment(
                        cultureReservationSvcId = "testSvcId",
                        comment = "test comment1",
                        password = "password",
                        rating = 3,
                        userName = "tester"
                )).block()

        cultureReservationCommentReactiveRepository
                .save(CultureReservationComment(
                        cultureReservationSvcId = "testSvcId",
                        comment = "test comment2",
                        password = "password",
                        rating = 3,
                        userName = "tester"
                )).block()

        val pageRequest = PageRequest.of(0, 2, Sort.by("registerDate").descending())

        val findData = cultureReservationCommentReactiveRepository
                .findAllByCultureReservationSvcId(pageRequest, "testSvcId")

        StepVerifier
                .create(findData)
                .assertNext {
                    assertEquals(it.cultureReservationSvcId, "testSvcId")
                    assertEquals("test comment2", it.comment)
                }
                .assertNext {
                    assertEquals(it.cultureReservationSvcId, "testSvcId")
                    assertEquals("test comment1", it.comment)
                }
                .verifyComplete()
    }

}