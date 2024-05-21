package naatsms.person.strategy;

import com.google.gson.JsonObject;

public interface DeltaDetectionStrategy<T> {

    JsonObject calculateDelta(T oldItem, T newItem);

}
