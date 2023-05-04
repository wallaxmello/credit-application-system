package me.dio.credit.application.system.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.enummeration.Status
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.service.impl.CreditService
import me.dio.credit.application.system.service.impl.CustomerService
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
class CreditServiceTest {
  @MockK lateinit var creditRepository: CreditRepository
  @MockK lateinit var customerService: CustomerService
  @InjectMockKs lateinit var creditService: CreditService

  @Test
  fun `should create credit`(){
    //given
    val fakeCredit: Credit = buildCredit()
    val fakeCustomer: Customer = buildCustomer()
    every { creditRepository.save(any()) } returns fakeCredit
    every { customerService.findById(any()) } returns fakeCustomer
    //when
    val actual: Credit = creditService.save(fakeCredit)
    //then
    Assertions.assertThat(actual).isNotNull
    Assertions.assertThat(actual).isSameAs(fakeCredit)
    verify(exactly = 1) { creditRepository.save(fakeCredit) }
  }

  private fun buildCredit(
          creditCode: UUID = UUID.randomUUID(),
          creditValue: BigDecimal = BigDecimal.valueOf(5000.0),
          dayFirstInstallment: LocalDate = LocalDate.of(2023,5,6),
          numberOfInstallments: Int = 0,
          status: Status = Status.IN_PROGRESS,
          firstName: String = "Wallax",
          lastName: String = "Mello",
          cpf: String = "28475934625",
          email: String = "wallax@gmail.com",
          password: String = "12345",
          zipCode: String = "12345",
          street: String = "Rua anonimo",
          income: BigDecimal = BigDecimal.valueOf(1000.0),
          idCustomer: Long = 1L,
          idCredit: Long? = 1L
  ) = Credit(
      creditCode = creditCode,
      creditValue = creditValue,
      dayFirstInstallment = dayFirstInstallment,
      numberOfInstallments = numberOfInstallments,
      status = status,
      customer = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
          address = Address(
          zipCode = zipCode,
          street = street,
          ),
        income = income,
        id = idCustomer
        ),
      id = idCredit
  )

  private fun buildCustomer(
          firstName: String = "Cami",
          lastName: String = "Cavalcante",
          cpf: String = "28475934625",
          email: String = "camila@gmail.com",
          password: String = "12345",
          zipCode: String = "12345",
          street: String = "Rua da Cami",
          income: BigDecimal = BigDecimal.valueOf(1000.0),
          id: Long = 1L
  ) = Customer(
          firstName = firstName,
          lastName = lastName,
          cpf = cpf,
          email = email,
          password = password,
          address = Address(
                  zipCode = zipCode,
                  street = street,
          ),
          income = income,
          id = id
  )

}