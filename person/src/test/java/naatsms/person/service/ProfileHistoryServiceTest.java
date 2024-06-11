package naatsms.person.service;

import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import naatsms.person.entity.Address;
import naatsms.person.entity.Individual;
import naatsms.person.entity.Profile;
import naatsms.person.entity.ProfileHistory;
import naatsms.person.repository.ProfileHistoryRepository;
import naatsms.person.service.impl.DefaultProfileHistoryService;
import naatsms.person.strategy.DeltaDetectionStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProfileHistoryServiceTest {

    @Mock
    private ProfileHistoryRepository profileHistoryRepository;

    @Mock
    private DeltaDetectionStrategy<Profile> profileDeltaDetectionStrategy;

    @Mock
    private DeltaDetectionStrategy<Individual> individualDeltaDetectionStrategy;

    @Mock
    private DeltaDetectionStrategy<Address> addressDeltaDetectionStrategy;

    private DefaultProfileHistoryService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new DefaultProfileHistoryService(profileHistoryRepository, profileDeltaDetectionStrategy, individualDeltaDetectionStrategy, addressDeltaDetectionStrategy);
    }

    @Test
    void testCreateHistoryEntry() {
        Individual oldUser = Individual.builder().profileId(UUID.randomUUID()).profile(Profile.builder().address(new Address()).build()).build();
        Individual newUser = Individual.builder().profileId(UUID.randomUUID()).profile(Profile.builder().address(new Address()).build()).build();
        JsonObject json1 = new JsonObject();
        json1.addProperty("testChange", "testValue");
        JsonObject json2 = new JsonObject();
        json2.addProperty("testChange2", "testValue2");

        ProfileHistory history = ProfileHistory.builder().build();

        when(individualDeltaDetectionStrategy.calculateDelta(oldUser, newUser)).thenReturn(json1);
        when(profileDeltaDetectionStrategy.calculateDelta(oldUser.getProfile(), newUser.getProfile())).thenReturn(json2);
        when(addressDeltaDetectionStrategy.calculateDelta(oldUser.getProfile().getAddress(), newUser.getProfile().getAddress())).thenReturn(new JsonObject());
        when(profileHistoryRepository.save(any())).thenReturn(Mono.just(history));

        StepVerifier.create(service.createHistoryEntry(oldUser, newUser))
                .expectNextMatches(hist -> hist.equals(history))
                .verifyComplete();
        verify(profileHistoryRepository).save(argThat(this::assertJson));
        verify(profileDeltaDetectionStrategy).calculateDelta(oldUser.getProfile(), newUser.getProfile());
        verify(individualDeltaDetectionStrategy).calculateDelta(oldUser, newUser);
    }

    private boolean assertJson(ProfileHistory hist) {
        String json = hist.getChangedValues().asString();
        return JsonPath.parse(json).read("$.testChange").equals("testValue") &&
                JsonPath.parse(json).read("$.testChange2").equals("testValue2");
    }

}
