package eu.dreamix.rmi.migration.cucumber.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.dreamix.rmi.migration.adapter.out.rmi.OrderRmiService;
import eu.dreamix.rmi.migration.domain.Order;
import eu.dreamix.rmi.migration.repository.OrderRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestPropertySource(properties = {
    "feature.flag.useRest_order=true"
})
public class OrderParityStepDefinitions {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRmiService orderRmiService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Order testOrder;
    private Order rmiResult;
    private String restResult;

    @Given("an order with ID {int} exists in the system")
    public void anOrderWithIdExistsInTheSystem(Integer id) {
        // Create a test order and ensure it exists in the repository
        testOrder = new Order(id, "TEST_ORDER", new BigDecimal("42.99"));
        
        // Make test order available to both repository and RMI service
        // This uses reflection to access the static final orders map in OrderRepository
        try {
            java.lang.reflect.Field ordersField = OrderRepository.class.getDeclaredField("orders");
            ordersField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<Integer, Order> orders = (java.util.Map<Integer, Order>) ordersField.get(null); // null because it's static
            orders.put(id, testOrder);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test data", e);
        }
        
        // Verify the order was set up correctly
        Order savedOrder = orderRepository.findById(id);
        assertNotNull(savedOrder, "Test order should be available in repository");
    }

    @When("the order is retrieved via the RMI method findOrderById")
    public void theOrderIsRetrievedViaTheRMIMethod() throws RemoteException {
        rmiResult = orderRmiService.findOrderById(42);
        assertNotNull(rmiResult, "RMI service should return an order");
    }

    @When("the same order is retrieved via the REST endpoint GET \\/api\\/orders\\/{int}")
    public void theSameOrderIsRetrievedViaTheRESTEndpoint(Integer id) {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/orders/" + id, String.class);
        assertEquals(200, response.getStatusCodeValue(), "REST endpoint should return 200 OK");
        restResult = response.getBody();
        assertNotNull(restResult, "REST response body should not be null");
    }

    @Then("both responses should contain identical order data")
    public void bothResponsesShouldContainIdenticalOrderData() throws Exception {
        // Convert domain Order to JSON for comparison
        String rmiJson = objectMapper.writeValueAsString(rmiResult);
        
        // Compare JSON structures (order-independent comparison)
        // This ensures field values match regardless of order
        JSONAssert.assertEquals(
            "REST and RMI responses should be equivalent",
            rmiJson,
            restResult,
            JSONCompareMode.NON_EXTENSIBLE
        );
        
        // Additional structural comparison
        JsonNode rmiNode = objectMapper.readTree(rmiJson);
        JsonNode restNode = objectMapper.readTree(restResult);
        
        assertEquals(rmiNode.get("id").asInt(), restNode.get("id").asInt(),
                "Order IDs should match");
        assertEquals(rmiNode.get("status").asText(), restNode.get("status").asText(),
                "Order status should match");
        assertEquals(
                rmiNode.get("total").decimalValue(),
                restNode.get("total").decimalValue(),
                "Order total should match"
        );
    }
}
