package example.vehicles.clients;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactHttpsProviderRuleMk2;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.RequestResponsePact;
import example.vehicles.Vehicles;
import example.vehicles.VehiclesRegistry;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CologneClientTest {

    @Rule
    public PactHttpsProviderRuleMk2 cologneService =
            new PactHttpsProviderRuleMk2("cologne-service", "localhost", 3000, this);


    @Pact(consumer = "info-app")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        return builder
                .given("data for Cologne from 2017 exists")
                .uponReceiving("a request for all vehicle registrations in Cologne")
                .path("/vehicles/registrations/all")
                .method("GET")
                .willRespondWith()
                .status(200)
                .body("{\"city\": \"Cologne\"," +
                        "\"gasoline\": " +
                        "{\"2017\": 110}," +
                        "\"diesel\": " +
                        "{\"2017\": 20}" +
                        "}")
                .toPact();
    }

    @Test
    @PactVerification("cologne-service")
    public void shouldRetriveTheVehiclesInformationFromCologneService() throws Exception {
        Vehicles vehicles = new CologneClient().getInformation();

        assertThat(vehicles.getCity(), is("Cologne"));
        assertThat(vehicles.getDiesel().get(2017), is(20L));
        assertThat(vehicles.getGasoline().get(2017), is(110L));
    }

}
