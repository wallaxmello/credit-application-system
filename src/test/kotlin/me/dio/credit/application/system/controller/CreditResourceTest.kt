package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.dto.request.CreditDto
import me.dio.credit.application.system.dto.request.CustomerDto
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.repository.CustomerRepository
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {
  @Autowired
  private lateinit var creditRepository: CreditRepository

  @Autowired
  private lateinit var customerRepository: CustomerRepository

  @Autowired
  private lateinit var mockMvc: MockMvc

  @Autowired
  private lateinit var objectMapper: ObjectMapper

  companion object {
    const val URL_CREDITS: String = "/api/credits"
    const val URL_CUSTOMERS: String = "/api/customers"
  }

  //@BeforeEach
  //fun setup() = creditRepository.deleteAll()

  //@AfterEach
  //fun tearDown() = creditRepository.deleteAll()

  @Test
  @Order(value = 1)
  fun `should create a credit and return 201 status`() {
    //given
    customerRepository.save(builderCustomerDto().toEntity())
    val creditDto: CreditDto = builderCreditDto()
    val valueAsString: String = objectMapper.writeValueAsString(creditDto)
    //when
    mockMvc.perform(
            MockMvcRequestBuilders.post(URL_CREDITS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(valueAsString)
    )
    //then
      .andExpect(MockMvcResultMatchers.status().isCreated)
      .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value("5000.0"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value("wallax@email.com"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value("1100.0"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
      .andDo(MockMvcResultHandlers.print())
  }

  @Test
  @Order(value = 2)
  fun `should find credit by credit Code and Customer Id return 200 status`() {
    //given
    val credit = creditRepository.findById(1)
    //when
    //then
    mockMvc.perform(
      MockMvcRequestBuilders.get("$URL_CREDITS/${credit.get().creditCode}?customerId=1")
        .accept(MediaType.APPLICATION_JSON)
    )
      .andExpect(MockMvcResultMatchers.status().isOk)
      .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value("5000.0"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value("wallax@email.com"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value("1100.0"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
      .andDo(MockMvcResultHandlers.print())
  }

  @Test
  @Order(value = 3)
  fun `should find credit by customer id and return 200 status`() {
    //given
    //when
    //then
    mockMvc.perform(
            MockMvcRequestBuilders.get("${URL_CREDITS}?customerId=1")
                    .accept(MediaType.APPLICATION_JSON)
    )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())
  }

  @Test
  @Order(value = 4)
  fun `should not save a credit with empty numberOfInstallments and return 400 status`() {
    //given
    val creditDto: CreditDto = builderCreditDto(numberOfInstallments = 0)
    val valueAsString: String = objectMapper.writeValueAsString(creditDto)
    //when
    //then
    mockMvc.perform(
            MockMvcRequestBuilders.post(URL_CREDITS)
                    .content(valueAsString)
                    .contentType(MediaType.APPLICATION_JSON)
    )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                    MockMvcResultMatchers.jsonPath("$.exception")
                            .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
  }

  @Test
  @Order(value = 5)
  fun `should not find credit with invalid id and return 400 status`() {
    //given
    val invalidId: Long = 2L
    //when
    //then
    mockMvc.perform(
      MockMvcRequestBuilders.get("$URL_CREDITS/${invalidId}?customerId=1")
        .accept(MediaType.APPLICATION_JSON)
    )
      .andExpect(MockMvcResultMatchers.status().isBadRequest)
      .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
      .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
      .andExpect(
        MockMvcResultMatchers.jsonPath("$.exception")
          .value("class java.lang.IllegalArgumentException")
      )
      .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
      .andDo(MockMvcResultHandlers.print())
  }

  @Test
  @Order(value = 5)
  fun `should not find credit with invalid customer id and return 400 status`() {
    //given
    val invalidId: Long = 2L
    //when
    //then
    mockMvc.perform(
            MockMvcRequestBuilders.get("$URL_CREDITS/1?customerId=${invalidId}")
                    .accept(MediaType.APPLICATION_JSON)
    )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                    MockMvcResultMatchers.jsonPath("$.exception")
                            .value("class java.lang.IllegalArgumentException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
  }

  private fun builderCreditDto(
          creditValue: BigDecimal = BigDecimal.valueOf(5000.0),
          dayFirstOfInstallment: LocalDate = LocalDate.of(2023,5, 5),
          numberOfInstallments: Int = 1,
          customerId: Long = 1
  ) = CreditDto(
          creditValue = creditValue,
          dayFirstOfInstallment = dayFirstOfInstallment,
          numberOfInstallments = numberOfInstallments,
          customerId = customerId
  )

  private fun builderCustomerDto(
          firstName: String = "Wallax",
          lastName: String = "Mello",
          cpf: String = "28475934625",
          email: String = "wallax@email.com",
          income: BigDecimal = BigDecimal.valueOf(1100.0),
          password: String = "1234",
          zipCode: String = "000000",
          street: String = "Rua anonimo, 123",
  ) = CustomerDto(
          firstName = firstName,
          lastName = lastName,
          cpf = cpf,
          email = email,
          income = income,
          password = password,
          zipCode = zipCode,
          street = street
  )

}