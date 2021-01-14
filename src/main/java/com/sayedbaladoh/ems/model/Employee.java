package com.sayedbaladoh.ems.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NaturalId;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.sayedbaladoh.ems.validator.Country;
import com.sayedbaladoh.ems.validator.EmailTaken;
import com.sayedbaladoh.ems.validator.Gender;
import com.sayedbaladoh.ems.validator.Number;
import com.sayedbaladoh.ems.validator.PhoneTaken;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModelProperty.AccessMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Employee entity. All details about the Employee.
 * 
 * Extends <code>DateAudit</code> to automatically populate createdAt and
 * updatedAt values when we persist an <code>Employee</code> entity.
 * 
 * @author Sayed Baladoh
 *
 */
@ApiModel(description = "The Employee entity has all details about the Employee.")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees", uniqueConstraints = { @UniqueConstraint(columnNames = { "phone_number" }),
		@UniqueConstraint(columnNames = { "email" }) })
@DynamicUpdate(true)
public class Employee extends DateAudit {

	/**
	 * The employee Id.
	 */
	@ApiModelProperty(notes = "The generated employee Id.", hidden = true, accessMode = AccessMode.READ_ONLY)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * The employee first name.
	 */
	@ApiModelProperty(notes = "The employee first name.")
	@NotBlank(message = "{required}")
	@Size(min = 3, max = 55)
	@JsonProperty("first_name")
	@Column(name = "first_name", length = 55, nullable = false)
	private String firstName;

	/**
	 * The employee last name.
	 */
	@ApiModelProperty(notes = "The employee last name.")
	@NotBlank(message = "{required}")
	@Size(min = 3, max = 255)
	@JsonProperty("last_name")
	@Column(name = "last_name", length = 255, nullable = false)
	private String lastName;

	/*
	 * Validate International Phone Numbers.
	 * 
	 * To validate international phone numbers. The numbers should start with a plus
	 * sign, followed by the country code and national number.
	 * 
	 * ^ # Assert position at the beginning of the string.
	 * 
	 * \+ # Match a literal "+" character.
	 * 
	 * (?: # Group but don't capture:
	 * 
	 * [0-9] # Match a digit.
	 * 
	 * \\s # Match a space character
	 * 
	 * ? # between zero and one time.
	 * 
	 * ) # End the noncapturing group.
	 * 
	 * {6,14} # Repeat the group between 6 and 14 times.
	 * 
	 * [0-9] # Match a digit.
	 * 
	 * $ # Assert position at the end of the string.
	 * 
	 */
	/**
	 * The employee phone number.
	 */
	@ApiModelProperty(notes = "The employee phone number.")
	@NaturalId
	@NotBlank(message = "{required}")
	@Number
	@Pattern(regexp = "^\\+?(?:[0-9]\\s?){6,14}[0-9]$", message = "{phone.invalid}")
	@Size(min = 10, message = "{min}")
	@Size(max = 15, message = "{max}")
	@PhoneTaken
	@Column(name = "phone_number", length = 17, nullable = false)
	@JsonProperty("phone_number")
	private String phoneNumber;

	/**
	 * The employee email.
	 */
	@ApiModelProperty(notes = "The employee email")
	@Email(message = "{email.invalid}")
	@EmailTaken
	@Size(min = 5, max = 55)
	@Column(length = 55)
	private String email;

	/**
	 * The employee password.
	 */
	@ApiModelProperty(notes = "The employee password.")
	@JsonProperty(access = Access.WRITE_ONLY)
	@NotBlank(message = "{required}")
	@Size(min = 8, max = 100)
	private String password;
	

	/**
	 * The employee birth date.
	 */
	@ApiModelProperty(notes = "The employee birth date.")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "{required}")
	@Past(message = "{past}")
	@JsonProperty("birth_date")
	@Column(name = "birth_date", length = 15)
	private Date birthDate;
	
	/**
	 * The employee gender.
	 */
	@ApiModelProperty(notes = "The employee gender male or female.")
	@Gender
	@Column(length = 9)
	private String gender;
	
	/**
	 * The employee country code.
	 */
	@ApiModelProperty(notes = "The employee country code.")
	@Country
	@JsonProperty("country_code")
	@Column(name = "country_code", length = 5, nullable = false)
	private String countryCode;

	/**
	 * The employee position.
	 */
	@ApiModelProperty(notes = "The employee position.")
	@Size(max = 255)
	@Column(length = 255)
	private String position;
	
	/**
	 * The employee contract information.
	 */
	@ApiModelProperty(notes = "The employee contract information.")
	@JsonProperty("contract_information")
	@Column(name = "contract_information")
	private String contractInformation;
		
	/**
	 * The employee current state.
	 */
	@ApiModelProperty(notes = "The employee current state.", hidden = true, accessMode=AccessMode.READ_ONLY)
	@Enumerated(EnumType.STRING)
	private EmployeeState state;

	@Override
	public String toString() {
		return "Employee [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", phoneNumber="
				+ phoneNumber + ", email=" + email + ", birthDate=" + birthDate + ", gender=" + gender
				+ ", countryCode=" + countryCode + ", position=" + position + ", contractInformation="
				+ contractInformation + ", state=" + state + "]";
	}
}
