package naatsms.person.service;

import naatsms.person.dto.AddressDto;
import naatsms.person.dto.IndividualDto;
import naatsms.person.dto.ProfileDto;
import naatsms.person.entity.Individual;
import naatsms.person.entity.Profile;
import naatsms.person.enums.ItemStatus;
import naatsms.person.repository.IndividualRepository;
import naatsms.person.service.impl.DefaultIndividualService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IndividualServiceTest {

    @InjectMocks
    private DefaultIndividualService individualService;

    @Mock
    private ProfileHistoryService profileHistoryService;

    @Mock
    private IndividualRepository individualRepository;

    @Mock
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetIndividualById() {
        UUID id = UUID.randomUUID();
        Individual mockIndividual = getIndividual(id);
        Profile mockProfile = getProfile(id);
        when(individualRepository.findByProfileId(id)).thenReturn(Mono.just(mockIndividual));
        when(profileService.getProfileById(mockIndividual.getProfileId())).thenReturn(Mono.just(mockProfile));

        StepVerifier.create(individualService.getIndividualById(id))
                .expectNextMatches(individual -> individual.getProfileId().equals(id) && individual.getProfile() != null
                        && "123456789".equals(individual.getPassportNumber())
                        && "Test".equals(individual.getProfile().getFirstName())
                        && "Test".equals(individual.getProfile().getLastName()))
                .verifyComplete();
    }

    @Test
    void testGetIndividualNotFound() {
        UUID id = UUID.randomUUID();
        when(individualRepository.findByProfileId(id)).thenReturn(Mono.empty());

        StepVerifier.create(individualService.getIndividualById(id))
                .expectError(IllegalArgumentException.class)
                .log()
                .verify();
    }

    @Test
    void testCreateIndividual() {
        IndividualDto individualDto = getIndividualDto();
        Profile mockProfile = new Profile();
        UUID id = UUID.randomUUID();
        mockProfile.setId(id);
        Individual mockIndividual = new Individual();

        when(profileService.createProfile(any())).thenReturn(Mono.just(mockProfile));
        when(profileHistoryService.createHistoryEntry(any(), any())).thenReturn(Mono.empty());
        when(individualRepository.save(any())).thenReturn(Mono.just(mockIndividual));

        StepVerifier.create(individualService.createIndividual(individualDto))
                .expectNextMatches(ind -> ind.equals(mockIndividual))
                .verifyComplete();

        verify(profileService).createProfile(any());
        verify(profileHistoryService).createHistoryEntry(any(), any());
        verify(individualRepository).save(ArgumentMatchers.argThat(individual -> assertIndividual(individual, id)));
    }

    private boolean assertIndividual(Individual arg, UUID id) {
        return arg.getProfileId().equals(id) &&
                arg.getEmail().equals("test@example.com") &&
                arg.getPassportNumber().equals("123456789");
    }

    @NotNull
    private IndividualDto getIndividualDto() {
        AddressDto addressDto = new AddressDto(null, 1, "testAddress", "123456", "testCity", "testState", null, null, null);
        ProfileDto profileDto = new ProfileDto(null, "secret", "Test", "Test", null, false, addressDto, null, null, null, null);
        return new IndividualDto(null, "123456789","123-456-789", "test@example.com", profileDto);
    }

    @NotNull
    private Profile getProfile(UUID id) {
        Profile mockProfile = new Profile();
        mockProfile.setId(id);
        mockProfile.setStatus(ItemStatus.ACTIVE);
        mockProfile.setFirstName("Test");
        mockProfile.setLastName("Test");
        return mockProfile;
    }

    @NotNull
    private Individual getIndividual(UUID id) {
        Individual mockIndividual = new Individual();
        mockIndividual.setProfileId(id);
        mockIndividual.setPassportNumber("123456789");
        mockIndividual.setPhoneNumber("123-456-7890");
        mockIndividual.setEmail("test@example.com");
        return mockIndividual;
    }
}
