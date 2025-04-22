package com.delphix.masking.customer.mtb;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

public class date_custom implements MaskingAlgorithm<String> {

    /**
     * Masks String object
     * @param input The String object to be masked. This method should handle null inputs.
     * @return Returns the masked value.
     */
    @JsonProperty("date_format")
    @JsonPropertyDescription("date format")
    public String date_format;
    
    @JsonProperty("change_days")
    @JsonPropertyDescription("No of days to change along with direction")
    public String change_days;
    private int minDays;
    private DateTimeFormatter internal_dateformat;
	private boolean century;
	
    public boolean getAllowFurtherInstances() {
        return true;
    }

    public Collection<MaskingComponent> getDefaultInstances() {
        return null;
    }

    public void setup(ComponentService serviceProvider) {
		
		if (this.change_days == null || this.change_days.trim().isEmpty()) {
			this.minDays = 31;
		} else {
			this.minDays = Integer.parseInt(change_days);
		}
		String dformat_tmp = this.date_format.toUpperCase();
		
		if (dformat_tmp.equals("CYYJJJ")) {
			this.internal_dateformat = DateTimeFormatter.ofPattern("yyD");
			this.century = true;} 
		else if (dformat_tmp.equals("CYYMMDD")) {
			this.internal_dateformat = DateTimeFormatter.ofPattern("yyMMdd");
			this.century = true;} 
		else if (dformat_tmp.equals("YYMMDD")) {
			this.internal_dateformat = DateTimeFormatter.ofPattern("yyMMdd");
			this.century = false;} 
		else if (dformat_tmp.equals("MMDDYY")) {
			this.internal_dateformat = DateTimeFormatter.ofPattern("MMddyy");
			this.century = false;} 
		else if (dformat_tmp.equals("YY-MM-DD")) {
			this.internal_dateformat = DateTimeFormatter.ofPattern("yy-MM-dd");
			this.century = false;} 
		else if (dformat_tmp.equals("YYYY-MM-DD")) {
			this.internal_dateformat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			this.century = false;} 
		else if (dformat_tmp.equals("MM-DD-YY")) {
			this.internal_dateformat = DateTimeFormatter.ofPattern("MM-dd-yy");
			this.century = false;}
			
			
	}

    private static boolean empty(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    public String mask(@Nullable String input) throws MaskingException {
        String output;
        int monthValue;
		
		if (empty(input)) {
            return input;
        } else {
			try {
				LocalDate ld = null, ld2 = null;
				interim_input = String.format(date_format_decimal, Integer.parseInt(input.trim()));
				if (this.century) {
					ld = LocalDate.parse(interim_input.substring(1), this.internal_dateformat);
				}
				else {
					ld = LocalDate.parse(interim_input, this.internal_dateformat);
				}

				ld2 = ld.minusDays(minDays);
				
				if (ld2.getYear() != ld.getYear()) {
					ld2 = ld.plusDays(minDays);
				}
				
				if (this.century) {
					output = interim_input.charAt(0) + ld2.format(this.internal_dateformat);
				} else {
					output = ld2.format(this.internal_dateformat);
				}
				
			} catch (DateTimeParseException e1) {
				return input;
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
        return "date_custom";
    }
}
