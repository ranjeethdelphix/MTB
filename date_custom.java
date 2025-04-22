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

	@JsonProperty("cd_jan")
	@JsonPropertyDescription("No of days to change for Jan")
	public int cd_jan;

	@JsonProperty("cd_feb")
	@JsonPropertyDescription("No of days to change for Feb")
	public int cd_feb;

	@JsonProperty("cd_mar")
	@JsonPropertyDescription("No of days to change for Mar")
	public int cd_mar;

	@JsonProperty("cd_apr")
	@JsonPropertyDescription("No of days to change for Apr")
	public int cd_apr;

	@JsonProperty("cd_may")
	@JsonPropertyDescription("No of days to change for May")
	public int cd_may;

	@JsonProperty("cd_jun")
	@JsonPropertyDescription("No of days to change for June")
	public int cd_jun;

	@JsonProperty("cd_jul")
	@JsonPropertyDescription("No of days to change for July")
	public int cd_jul;

	@JsonProperty("cd_aug")
	@JsonPropertyDescription("No of days to change for Aug")
	public int cd_aug;

	@JsonProperty("cd_sep")
	@JsonPropertyDescription("No of days to change for Sep")
	public int cd_sep;

	@JsonProperty("cd_oct")
	@JsonPropertyDescription("No of days to change for Oct")
	public int cd_oct;

	@JsonProperty("cd_nov")
	@JsonPropertyDescription("No of days to change for Nov")
	public int cd_nov;

	@JsonProperty("cd_dec")
	@JsonPropertyDescription("No of days to change for Dec")
	public int cd_dec;

    private DateTimeFormatter internal_dateformat;
	private boolean century;
	private String date_format_decimal;
	private int[] cd_list = new int[12];
	
    public boolean getAllowFurtherInstances() {
        return true;
    }

    public Collection<MaskingComponent> getDefaultInstances() {
        return null;
    }

    public void setup(ComponentService serviceProvider) {
		
		String dformat_tmp = this.date_format.toUpperCase();

		this.cd_list[0] = cd_jan;
		this.cd_list[1] = cd_feb;
		this.cd_list[2] = cd_mar;
		this.cd_list[3] = cd_apr;
		this.cd_list[4] = cd_may;
		this.cd_list[5] = cd_jun;
		this.cd_list[6] = cd_jul;
		this.cd_list[7] = cd_aug;
		this.cd_list[8] = cd_sep;
		this.cd_list[9] = cd_oct;
		this.cd_list[10] = cd_nov;
		this.cd_list[11] = cd_dec;

		if (dformat_tmp.equals("CYYJJJ")) {
			this.internal_dateformat = DateTimeFormatter.ofPattern("yyDDD");
			this.century = true;
			this.date_format_decimal = "%06d";}
		else if (dformat_tmp.equals("CYYMMDD")) {
			this.internal_dateformat = DateTimeFormatter.ofPattern("yyMMdd");
			this.century = true;
			this.date_format_decimal = "%07d";}
		else if (dformat_tmp.equals("YYMMDD")) {
			this.internal_dateformat = DateTimeFormatter.ofPattern("yyMMdd");
			this.century = false;
			this.date_format_decimal = "%06d";}
		else if (dformat_tmp.equals("MMDDYY")) {
			this.internal_dateformat = DateTimeFormatter.ofPattern("MMddyy");
			this.century = false;
			this.date_format_decimal = "%06d";}
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
        String output,interim_input;

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

				ld2 = ld.minusDays(cd_list[ld.getMonthValue()-1]);
				
				if (ld2.getYear() != ld.getYear()) {
					ld2 = ld.plusDays(cd_list[ld.getMonthValue()-1]);
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
