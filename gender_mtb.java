package com.delphix.masking.customer.mtb;

import com.delphix.masking.api.plugin.MaskingAlgorithm;
import java.lang.String;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.delphix.masking.api.provider.ComponentService;

public class gender_mtb implements MaskingAlgorithm<String> {

    /**
     * Masks String object
     * @param input The String object to be masked. This method should handle null inputs.
     * @return Returns the masked value.
     */
    @JsonProperty("listValues")
    @JsonPropertyDescription("List of values to randomize from")
    public List<String> listValues;

    @JsonProperty("retain_default")
    @JsonPropertyDescription("Ignore spaces and nulls? True / False")
    public boolean retain_default;

    public boolean getAllowFurtherInstances() {
        return true;
    }

    private static boolean empty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public void setup(ComponentService serviceProvider) {
        return;
    }

        @Override
    public String mask(@Nullable String input) {
        if (this.retain_default) {
           if (empty(input)) {
                return input;
            }
        }
        Random random = new Random();
        int randomIndex = random.nextInt(this.listValues.size());
        return this.listValues.get(randomIndex);

    }

    /**
     * Get the recommended name of this Algorithm.
     * @return The name of this algorithm
     */
    @Override
    public String getName() {
        // TODO: Change this if you'd like to name your algorithm differently from the Java class.
        return "gender_mtb";
    }
}
