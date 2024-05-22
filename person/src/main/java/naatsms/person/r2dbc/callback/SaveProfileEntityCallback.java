package naatsms.person.r2dbc.callback;

import naatsms.person.entity.Individual;
import naatsms.person.repository.ProfileRepository;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.BeforeSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;

@Component
public class SaveProfileEntityCallback implements BeforeSaveCallback<Individual> {

    private ProfileRepository profileRepository;

    @Override
    public Publisher<Individual> onBeforeSave(Individual entity, OutboundRow row, SqlIdentifier table) {
        return profileRepository.save(entity.getProfile()).thenReturn(entity);
    }

    @Autowired
    public void setProfileRepository(@Lazy ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }
}
