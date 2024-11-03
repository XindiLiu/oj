package com.example.oj.constant;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class JudgementConverter implements AttributeConverter<SubmissionResultType, String> {
	@Override
	public String convertToDatabaseColumn(SubmissionResultType submissionResultType) {
		return submissionResultType.name();
	}

	@Override
	public SubmissionResultType convertToEntityAttribute(String dbData) {
		return SubmissionResultType.PD;
//        return Judgement.valueOf(dbData);
	}

}
