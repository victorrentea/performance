package victor.training.performance.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ScopeEnumConverter implements AttributeConverter<ScopeEnum, String> {
   @Override
   public String convertToDatabaseColumn(ScopeEnum attribute) {
      if (attribute == null) return null;
      return attribute.dbCode;
   }

   @Override
   public ScopeEnum convertToEntityAttribute(String dbCode)
   {
      if (dbCode == null) {
         return null;
      }
      return ScopeEnum.fromDbCode(dbCode);
   }
}
