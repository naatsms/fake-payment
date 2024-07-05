package naatsms.orchestra.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naatsms.dto.IndividualDto;
import com.naatsms.dto.ProfileDto;
import com.naatsms.enums.ItemStatus;
import naatsms.orchestra.exception.SagaRollbackErrorException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultPersonServiceTest {

    private DefaultPersonService personService;

    WebClient client;
    MockWebServer server = new MockWebServer();

    @BeforeEach
    void setUp() {
        client = WebClient.builder().baseUrl(server.url("/").toString()).build();
        personService = new DefaultPersonService(client);
    }

    @Test
    void testCreateUserSuccess() throws JsonProcessingException {
        var uuid = UUID.randomUUID();
        var response = getMockResponseBody(uuid);
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody(response).setResponseCode(201));
        var requestDto = new IndividualDto("username", "password");
        StepVerifier.create(personService.createUser(requestDto))
                .expectNextMatches(this::assertDto).verifyComplete();
    }

    @Test
    void testCreateUserBadRequest()  {
        server.enqueue(new MockResponse().setResponseCode(400));
        var requestDto = new IndividualDto("username", "password");
        StepVerifier.create(personService.createUser(requestDto))
                .expectError(WebClientResponseException.class).verify();
    }

    @Test
    void testDeleteUserSuccess() throws InterruptedException {
        var uuid = UUID.randomUUID();
        server.enqueue(new MockResponse().setResponseCode(200));
        var requestDto = getMockDto(uuid);
        StepVerifier.create(personService.deleteUser(requestDto)).verifyComplete();
        var request = server.takeRequest();
        assertEquals("PUT", request.getMethod());
        assertEquals("/api/individuals/" + uuid, request.getPath());
    }

    @Test
    void testDeleteUserFailed() throws InterruptedException {
        var uuid = UUID.randomUUID();
        server.enqueue(new MockResponse().setResponseCode(400));
        var requestDto = getMockDto(uuid);
        StepVerifier.create(personService.deleteUser(requestDto)).verifyError(SagaRollbackErrorException.class);
        var request = server.takeRequest();
        assertEquals("PUT", request.getMethod());
        assertEquals("/api/individuals/" + uuid, request.getPath());
    }

    @Test
    void testGetUserSuccess() throws InterruptedException, JsonProcessingException {
        var uuid = UUID.randomUUID();
        getMockDto(uuid);
        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setResponseCode(200)
                .setBody(getMockResponseBody(uuid)));
        StepVerifier.create(personService.getUser(uuid.toString())).expectNext(getMockDto(uuid)).verifyComplete();
        var request = server.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/api/individuals/" + uuid, request.getPath());
    }

    private static @NotNull String getMockResponseBody(UUID uuid) throws JsonProcessingException {
        var dto = getMockDto(uuid);
        return new ObjectMapper().writeValueAsString(dto);
    }

    private static @NotNull IndividualDto getMockDto(UUID uuid) {
        return new IndividualDto(uuid, "", "", "testEmail", new ProfileDto(uuid, "testSecret", "", "", ItemStatus.ACTIVE, false, null, null, null, null, null));
    }

    private boolean assertDto(IndividualDto ind) {
        return ind.profileId() != null
                && ind.profile().status().equals(ItemStatus.ACTIVE);
    }


}