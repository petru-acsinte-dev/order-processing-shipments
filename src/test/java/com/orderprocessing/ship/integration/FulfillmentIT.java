package com.orderprocessing.ship.integration;

import static com.orderprocessing.common.constants.Constants.ADMIN_ROLE;
import static com.orderprocessing.common.constants.Constants.ADMIN_UUID0;
import static com.orderprocessing.common.constants.Constants.PAGE_CONTENT_ATTR;
import static com.orderprocessing.common.constants.Constants.USER_ROLE;
import static com.orderprocessing.common.tests.TestConstants.ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.orderprocessing.common.tests.AbstractIntegrationTestBase;
import com.orderprocessing.order_processing_shipments.OrderProcessingShipmentsApplication;
import com.orderprocessing.ship.constants.Constants;
import com.orderprocessing.ship.constants.Status;
import com.orderprocessing.ship.dto.FulfillmentResponse;
import com.orderprocessing.ship.dto.ShipmentResponse;
import com.orderprocessing.ship.entities.Fulfillment;
import com.orderprocessing.ship.entities.ShipStatus;
import com.orderprocessing.ship.repositories.FulfillmentRepository;

@SpringBootTest(classes = OrderProcessingShipmentsApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles(value={"test"})
@Transactional
class FulfillmentIT extends AbstractIntegrationTestBase {

	private static Logger log = LoggerFactory.getLogger(FulfillmentIT.class);

	private static final String MEMBR_TMPLT = "$.%s"; //$NON-NLS-1$
	private static final String FIELD_STATUS = "status"; //$NON-NLS-1$

	private static final String ORDER_STATUS_SHIPPED = "SHIPPED"; //$NON-NLS-1$

	@Value("${jwt.secret}")
	private String secret;

	@Autowired
	private FulfillmentRepository fulfillmentRepository;

	// used by tests to remember order outside the transactional context (workaround for events)
	private UUID orderId = null;
	private Status expectedStatus = null;

	private final UUID userExternalId = UUID.randomUUID();

	@Override
	protected Logger getLog() {
		return log;
	}

	@BeforeEach
	void setup() {
		mockLogin(TEST_USER, userExternalId, USER_ROLE, secret);
	}

	@Test
	@DisplayName("Confirms an order and retrieves its fulfillment")
	@Rollback
	void testConfirmOrderCheckFulfillment() {

		orderId = UUID.randomUUID();

		final Fulfillment newFulfillment = generateFulfillment(orderId);

		expectedStatus = Status.READY_TO_SHIP;

		assertEquals(Status.READY_TO_SHIP.getId(), newFulfillment.getStatus().getId());
		assertEquals(Status.READY_TO_SHIP.name(), newFulfillment.getStatus().getStatus());
	}

	@Test
	@DisplayName("Confirms an order and ships its fulfillment")
	@Rollback
	void testConfirmOrderShipFulfillment() throws Exception {

		orderId = UUID.randomUUID();
		final Fulfillment newFulfillment = generateFulfillment(orderId);
		assertEquals(Status.READY_TO_SHIP.getId(), newFulfillment.getStatus().getId());
		assertEquals(Status.READY_TO_SHIP.name(), newFulfillment.getStatus().getStatus());

		mockLogin(ADMIN, ADMIN_UUID0, ADMIN_ROLE, secret);

		shipOrder(Constants.FULFILLMENTS_PATH + '/' + orderId, false);

		// shipping again should fail
		shipOrder(Constants.FULFILLMENTS_PATH + '/' + orderId, true);

		expectedStatus = Status.SHIPPED;

	}

	@Test
	@DisplayName("Retrieves all available fulfillments and shipments")
	@Rollback
	void testGetAllFulfillmentsAndShipments() throws Exception {
		final UUID order1Id = UUID.randomUUID();
		var newFulfillment = generateFulfillment(order1Id);
		assertEquals(Status.READY_TO_SHIP.getId(), newFulfillment.getStatus().getId());
		assertEquals(Status.READY_TO_SHIP.name(), newFulfillment.getStatus().getStatus());

		final UUID order2Id = UUID.randomUUID();
		newFulfillment = generateFulfillment(order2Id);
		assertEquals(Status.READY_TO_SHIP.getId(), newFulfillment.getStatus().getId());
		assertEquals(Status.READY_TO_SHIP.name(), newFulfillment.getStatus().getStatus());

		mockLogin(ADMIN, ADMIN_UUID0, ADMIN_ROLE, secret);

		checkFulfillments(Status.READY_TO_SHIP, List.of(order1Id, order2Id));

		shipOrder(Constants.FULFILLMENTS_PATH + '/' + order1Id, false);

		shipOrder(Constants.FULFILLMENTS_PATH + '/' + order2Id, false);

		checkShipments(Status.SHIPPED, List.of(order1Id, order2Id));

		assertEquals(HttpStatus.OK.value(), checkFulfillment(order1Id, ORDER_STATUS_SHIPPED));

		assertEquals(HttpStatus.OK.value(), checkFulfillment(order2Id, ORDER_STATUS_SHIPPED));

		assertEquals(HttpStatus.OK.value(), checkShipment(order1Id, ORDER_STATUS_SHIPPED));

		assertEquals(HttpStatus.OK.value(), checkShipment(order2Id, ORDER_STATUS_SHIPPED));
	}

	// this needs to happen after the test or else events don't take effect
	@AfterEach
	void postTestCheck() {
		if (null != orderId) {
			mockLogin(ADMIN, ADMIN_UUID0, ADMIN_ROLE, secret);
			// might need to wait for eventual consistency
			Awaitility.await(expectedStatus.name())
				.pollInSameThread()
				.pollDelay(1, TimeUnit.SECONDS)
				.atMost(3, TimeUnit.SECONDS)
				.untilAsserted(() ->
					assertEquals(HttpStatus.OK.value(), checkFulfillment(orderId, expectedStatus.name()))
			);
		}
	}

	private void checkFulfillments(Status expectedStatus, List<UUID> orderExternalIds) throws Exception {

		mockLogin(ADMIN, ADMIN_UUID0, ADMIN_ROLE, secret);

		final MvcResult actions = mockMvc.perform(get(Constants.FULFILLMENTS_PATH)
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getBearer()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$." + PAGE_CONTENT_ATTR).isArray()) //$NON-NLS-1$
			.andExpect(jsonPath("$." + PAGE_CONTENT_ATTR + ".length()").value(2)) //$NON-NLS-1$ //$NON-NLS-2$
			.andReturn();

		final JsonNode root = objectMapper.readTree(actions.getResponse().getContentAsString());
		final JsonNode contentNode = root.get(PAGE_CONTENT_ATTR);
		final List<FulfillmentResponse> contents =
				objectMapper.convertValue(contentNode, new TypeReference<List<FulfillmentResponse>>() {});

		for (final FulfillmentResponse fulfillment : contents) {
			assertEquals(expectedStatus.name(), fulfillment.getStatus());
			assertTrue(orderExternalIds.contains(fulfillment.getOrderExternalId()));
		}
	}

	private void checkShipments(Status expectedStatus, List<UUID> orderExternalIds) throws Exception {

		mockLogin(ADMIN, ADMIN_UUID0, ADMIN_ROLE, secret);

		final MvcResult actions = mockMvc.perform(get(Constants.SHIPMENTS_PATH)
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getBearer()))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$." + PAGE_CONTENT_ATTR).isArray()) //$NON-NLS-1$
			.andExpect(jsonPath("$." + PAGE_CONTENT_ATTR + ".length()").value(2)) //$NON-NLS-1$ //$NON-NLS-2$
			.andReturn();

		final JsonNode root = objectMapper.readTree(actions.getResponse().getContentAsString());
		final JsonNode contentNode = root.get(PAGE_CONTENT_ATTR);
		final List<ShipmentResponse> contents =
				objectMapper.convertValue(contentNode, new TypeReference<List<ShipmentResponse>>() {});

		for (final ShipmentResponse fulfillment : contents) {
			assertEquals(expectedStatus.name(), fulfillment.getStatus());
			assertTrue(orderExternalIds.contains(fulfillment.getOrderExternalId()));
		}
	}

	private int checkFulfillment(UUID orderExternalId, String expectedStatus) throws Exception {
		return mockMvc.perform(get(Constants.FULFILLMENTS_PATH + '/' + orderExternalId)
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getBearer()))
			.andExpect(status().isOk())
			.andExpect(jsonPath(MEMBR_TMPLT, FIELD_STATUS).value(expectedStatus))
			.andReturn()
			.getResponse()
			.getStatus();
	}

	private int checkShipment(UUID orderExternalId, String expectedStatus) throws Exception {
		return mockMvc.perform(get(Constants.SHIPMENTS_PATH + '/' + orderExternalId)
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getBearer()))
			.andExpect(status().isOk())
			.andExpect(jsonPath(MEMBR_TMPLT, FIELD_STATUS).value(expectedStatus))
			.andReturn()
			.getResponse()
			.getStatus();
	}

	private ResultActions updateStatus(String endPoint, String pathVariable) throws Exception {
		return mockMvc.perform(post(endPoint + pathVariable)
				.accept(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getBearer()));
	}

	private Fulfillment generateFulfillment(UUID externalOrderId) {
		final Fulfillment fulfillment = new Fulfillment();
		fulfillment.setCreated(LocalDateTime.now());
		fulfillment.setExternalId(UUID.randomUUID());
		fulfillment.setOrderExternalId(externalOrderId);
		fulfillment.setStatus(new ShipStatus(Status.READY_TO_SHIP.getId(), Status.READY_TO_SHIP.name()));
		return fulfillmentRepository.save(fulfillment);
	}

	private void shipOrder(String fulfillmentLocation, boolean expectBadRequest) throws Exception {
		final ResultActions actions = updateStatus(fulfillmentLocation, "/ship"); //$NON-NLS-1$
		if (expectBadRequest) {
			actions.andExpect(status().isBadRequest());
		} else {
			actions
				.andExpect(status().isOk())
				.andExpect(jsonPath(MEMBR_TMPLT, FIELD_STATUS).value(ORDER_STATUS_SHIPPED));
		}
	}

}
