package com.smalaca.opentrainings.infrastructure.api.rest.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Lists;
import com.smalaca.opentrainings.domain.order.OrderRepository;
import com.smalaca.opentrainings.domain.order.OrderTestDto;
import com.smalaca.opentrainings.domain.order.OrderTestFactory;
import com.smalaca.opentrainings.infrastructure.repository.jpa.order.SpringCrudRepository;
import com.smalaca.test.type.SystemTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.smalaca.opentrainings.data.Random.randomAmount;
import static com.smalaca.opentrainings.data.Random.randomCurrency;
import static com.smalaca.opentrainings.data.Random.randomId;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SystemTest
class OrderRestControllerTest {
    @Autowired
    private OrderRepository repository;

    @Autowired
    private SpringCrudRepository springCrudRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final OrderTestFactory factory = OrderTestFactory.orderTestFactory();

    @BeforeEach
    void initObjectMapper() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @AfterEach
    void deleteOrders() {
        transactionTemplate.executeWithoutResult(transactionStatus -> springCrudRepository.deleteAll());
    }

    @Test
    void shouldNotFindNotExistingOrder() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/order/" + UUID.randomUUID());

        mockMvc.perform(request)
                .andExpect(status().is(NOT_FOUND.value()));
    }

    @Test
    void shouldFindExistingOrder() throws Exception {
        OrderTestDto dto = randomOrderDto();
        UUID orderId = givenOrder(dto);

        RequestBuilder request = MockMvcRequestBuilders.get("/order/" + orderId);

        MockHttpServletResponse response = mockMvc.perform(request)
                .andExpect(status().is(OK.value()))
                .andReturn()
                .getResponse();

        ResponseOrderTestDto order = asOrder(response);

        assertThatInitiatedOrderHasDataEqualTo(order, orderId, dto);
    }

    @Test
    void shouldRecognizeConfirmedOrderDoesNotExist() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.put("/order/confirm/" + UUID.randomUUID())
                .contentType(APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(NOT_FOUND.value()));
    }

    @Test
    void shouldProcessOrderConfirmationSuccessfully() throws Exception {
        UUID orderId = givenOrder(randomOrderDto());

        RequestBuilder request = MockMvcRequestBuilders.put("/order/confirm/" + orderId)
                .contentType(APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().is(OK.value()));
    }

    @Test
    void shouldFindAllOrders() throws Exception {
        OrderTestDto dtoOne = randomOrderDto();
        UUID orderIdOne = givenOrder(dtoOne);
        OrderTestDto dtoTwo = randomOrderDto();
        UUID orderIdTwo = givenOrder(dtoTwo);
        OrderTestDto dtoThree = randomOrderDto();
        UUID orderIdThree = givenOrder(dtoThree);
        OrderTestDto dtoFour = randomOrderDto();
        UUID orderIdFour = givenOrder(dtoFour);

        RequestBuilder request = MockMvcRequestBuilders.get("/order");

        MockHttpServletResponse response = mockMvc.perform(request)
                .andExpect(status().is(OK.value()))
                .andReturn()
                .getResponse();

        List<ResponseOrderTestDto> actual = asOrders(response);

        assertThat(actual)
                .hasSize(4)
                .anySatisfy(order -> assertThatInitiatedOrderHasDataEqualTo(order, orderIdOne, dtoOne))
                .anySatisfy(order -> assertThatInitiatedOrderHasDataEqualTo(order, orderIdTwo, dtoTwo))
                .anySatisfy(order -> assertThatInitiatedOrderHasDataEqualTo(order, orderIdThree, dtoThree))
                .anySatisfy(order -> assertThatInitiatedOrderHasDataEqualTo(order, orderIdFour, dtoFour));
    }

    private List<ResponseOrderTestDto> asOrders(MockHttpServletResponse response) {
        try {
            ResponseOrderTestDto[] orders = objectMapper.readValue(asString(response), ResponseOrderTestDto[].class);
            return Lists.newArrayList(orders);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    private  ResponseOrderTestDto asOrder(MockHttpServletResponse response) {
        try {
            return objectMapper.readValue(asString(response), ResponseOrderTestDto.class);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }

    private String asString(MockHttpServletResponse response) {
        try {
            return response.getContentAsString();
        } catch (UnsupportedEncodingException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void assertThatInitiatedOrderHasDataEqualTo(ResponseOrderTestDto actual, UUID expectedOrderId, OrderTestDto expected) {
        assertThat(actual.orderId()).isEqualTo(expectedOrderId);
        assertThat(actual.trainingId()).isEqualTo(expected.getTrainingId());
        assertThat(actual.participantId()).isEqualTo(expected.getParticipantId());
        assertThat(actual.creationDateTime()).isEqualToIgnoringNanos(expected.getCreationDateTime());
        assertThat(actual.status()).isEqualTo("INITIATED");
        assertThat(actual.priceAmount()).usingComparator(BigDecimal::compareTo).isEqualTo(expected.getAmount());
        assertThat(actual.priceCurrency()).isEqualTo(expected.getCurrency());
    }

    private UUID givenOrder(OrderTestDto dto) {
        return transactionTemplate.execute(transactionStatus -> repository.save(factory.orderCreatedAt(dto)));
    }

    private OrderTestDto randomOrderDto() {
        return OrderTestDto.builder()
                .trainingId(randomId())
                .participantId(randomId())
                .amount(randomAmount())
                .currency(randomCurrency())
                .creationDateTime(now())
                .build();
    }
}