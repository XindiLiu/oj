package com.example.oj.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
/*
 * Used for creating a submission.
 */
public class SubmissionDTO {
	String code;
	String language;
	String fileName;

}
