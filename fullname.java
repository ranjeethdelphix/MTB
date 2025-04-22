package com.delphix.masking.customer.mtb;

import com.delphix.masking.api.plugin.MaskingAlgorithm;
import com.delphix.masking.api.plugin.MaskingComponent;
import com.delphix.masking.api.plugin.MaskingAlgorithm.MaskingType;
import com.delphix.masking.api.plugin.exception.MaskingException;
import com.delphix.masking.api.plugin.referenceType.AlgorithmInstanceReference;
import com.delphix.masking.api.provider.ComponentService;
import java.lang.String;
import java.util.Collection;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import javax.annotation.Nullable;
import java.util.Properties;
import java.io.StringReader;
import java.io.IOException;


public class fullname implements MaskingAlgorithm<String> {

    /**
     * Masks String object
     * @param input The String object to be masked. This method should handle null inputs.
     * @return Returns the masked value.
     */
    @JsonProperty("Individual_name_algorithm")
    @JsonPropertyDescription("AlgorithmInstanceReference of type string")
    public AlgorithmInstanceReference Individual_name_algorithm;
    private MaskingAlgorithm<String> Ind_algo_instance;

    @JsonProperty("Company_name_algorithm")
    @JsonPropertyDescription("AlgorithmInstanceReference of type string")
    public AlgorithmInstanceReference Company_name_algorithm;
    private MaskingAlgorithm<String> Cmp_algo_instance;

    @JsonProperty("Company_Indicator")
    @JsonPropertyDescription("The prefix indicator. Regular expression format")
    public String Company_Indicator;
    private String unescaped_indicator;

    @JsonProperty("Short_Name_Length")
    @JsonPropertyDescription("Length of Short Name field")
    public int Short_Length;

    public boolean getAllowFurtherInstances() {
        return true;
    }

    public Collection<MaskingComponent> getDefaultInstances() {
        return null;
    }

    public void setup(ComponentService serviceProvider) {
        try {
            this.Ind_algo_instance = serviceProvider.getAlgorithmByName(this.Individual_name_algorithm, MaskingType.STRING);
            this.Cmp_algo_instance = serviceProvider.getAlgorithmByName(this.Company_name_algorithm, MaskingType.STRING);
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }

        try {
            validateCompanyIndicator(this.Company_Indicator);

        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return;
        }

        try {
            Properties properties = new Properties();
            properties.load(new StringReader("key=" + this.Company_Indicator));
            this.unescaped_indicator = properties.getProperty("key");
        } catch (IOException e2) {
            e2.printStackTrace();
            this.unescaped_indicator = this.Company_Indicator;
            return;
        }
    }

    public static void validateCompanyIndicator(String c_ind) {
        if (c_ind.isEmpty() || c_ind == null) {
            throw new IllegalArgumentException("Company indicator cannot be blank or null!");
        }
    }

    private static boolean empty(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    public String mask(@Nullable String input) throws MaskingException {
        String output,input_formatted, input_trim;
        if (empty(input)) {
            return input;
        } else {
                input_trim = input.trim();
                if (input_trim.startsWith(this.unescaped_indicator)) {
                    input_formatted = input.split(this.Company_Indicator, 2)[1].substring(0,Math.min(input_trim.length()-1, this.Short_Length));
                    output = this.unescaped_indicator + this.Cmp_algo_instance.mask(input_formatted.trim());
                } else {
                    input_formatted = input.substring(0,Math.min(input_trim.length()-1, this.Short_Length));
                    output = this.Ind_algo_instance.mask(input_formatted.trim());
                }
                return output;
        }
    }

    /**
     * Get the recommended name of this Algorithm.
     * @return The name of this algorithm
     */
    @Override
    public String getName() {
        // TODO: Change this if you'd like to name your algorithm differently from the Java class.
        return "fullname";
    }
}
