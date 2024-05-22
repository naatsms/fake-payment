package naatsms.person.controller;

import naatsms.person.dto.ProfileHistoryDto;
import naatsms.person.entity.ProfileHistory;
import naatsms.person.service.ProfileHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile-history")
public class ProfileHistoryController {

    private final ProfileHistoryService profileHistoryService;

    public ProfileHistoryController(ProfileHistoryService profileHistoryService) {
        this.profileHistoryService = profileHistoryService;
    }

    @GetMapping("/{id}")
    public Flux<ProfileHistoryDto> getProfileHistoryById(@PathVariable UUID id) {
        return profileHistoryService.getProfileHistoryForProfileId(id)
                .map(this::toDto);
    }

    private ProfileHistoryDto toDto(ProfileHistory profileHistory) {
        return new ProfileHistoryDto(profileHistory.getId(),
                profileHistory.getProfileId(),
                profileHistory.getProfileType(),
                profileHistory.getReason(),
                profileHistory.getComment(),
                profileHistory.getChangedValues().asString(),
                profileHistory.getCreatedAt());
    }

}