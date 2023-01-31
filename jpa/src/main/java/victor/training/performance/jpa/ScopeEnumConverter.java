package victor.training.performance.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ScopeEnumConverter implements AttributeConverter<ScopeEnum, String> {
   @Override
   public String convertToDatabaseColumn(ScopeEnum attribute) {
      return attribute.dbCode;
   }

   @Override
   public ScopeEnum convertToEntityAttribute(String dbCode) {
      return ScopeEnum.fromDbCode(dbCode);
   }
}
