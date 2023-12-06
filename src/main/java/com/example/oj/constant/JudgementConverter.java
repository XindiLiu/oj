package com.example.oj.constant;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class JudgementConverter implements AttributeConverter<Judgement, String> {
    @Override
    public String convertToDatabaseColumn(Judgement judgement) {
        return judgement.name();
    }

    @Override
    public Judgement convertToEntityAttribute(String dbData) {
        return Judgement.PD;
//        return Judgement.valueOf(dbData);
    }

}
