package victor.training.performance.jpa.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import victor.training.performance.jpa.entity.ScopeEnum;

@Converter
public class ScopeEnumConverter implements AttributeConverter<ScopeEnum, String> {
  @Override
  public String convertToDatabaseColumn(ScopeEnum attribute) {
    if (attribute == null) {
       return null;
    }
    return attribute.dbCode;
  }

  @Override
  public ScopeEnum convertToEntityAttribute(String dbCode) {
    if (dbCode == null) {
      return null;
    }
    return ScopeEnum.fromDbCode(dbCode);
  }
}
