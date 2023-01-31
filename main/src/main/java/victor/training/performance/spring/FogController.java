package victor.training.performance.spring;

import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static javax.persistence.CascadeType.ALL;

@RequiredArgsConstructor
@RestController
@RequestMapping("fog")
public class FogController {
   private final FogRepo fogRepo;

   @PostMapping
   public Long createFog(@RequestBody FogDto dto) {
      return fogRepo.save(dto.toEntity()).getId();
   }

   @GetMapping("{id}")
   public FogDto getFog(@PathVariable Long id) {
      return new FogDto(fogRepo.findById(id).get());
   }

   @PutMapping("{id}")
   public void updateFog(@RequestBody FogDto dto) {
      fogRepo.save(dto.toEntity());
   }

   @DeleteMapping("{id}")
   public void deleteFog(@PathVariable Long id) {
      fogRepo.deleteById(id);
   }
}

interface FogRepo extends JpaRepository<Fog, Long> {
}

@Data
@NoArgsConstructor
class FogDto {
   private Long id;
   private String name;
   private Double density;
   private BigDecimal lat;
   private BigDecimal lon;
   private Long height;
   private BigDecimal surface;
//   private List<FogCityDto> cities = new ArrayList<>();
   private List<FogAttributeDto> attributes = new ArrayList<>();

   public FogDto(Fog fog) {
      id = fog.getId();
      name = fog.getName();
      density = fog.getDensity();
      lat = fog.getLat();
      lon = fog.getLon();
      height = fog.getHeight();
      surface = fog.getSurface();
//      cities = fog.getCities().stream().map(FogCityDto::new).collect(Collectors.toList());
      attributes = fog.getAttributes().stream().map(FogAttributeDto::new).collect(toList());
   }
   public Fog toEntity() {
      Fog fog = new Fog();
      fog.setId(id);
      fog.setName(name);
      fog.setDensity(density);
      fog.setLat(lat);
      fog.setLon(lon);
      fog.setHeight(height);
      fog.setSurface(surface);
      fog.setAttributes(attributes.stream().map(FogAttributeDto::toEntity).collect(toSet()));
      return fog;
   }

   //   @NoArgsConstructor
//   @Data
//   static class FogCityDto {
//      private Long id;
//      private String cityName;
//      private String cityCode;
//      private String description;
//
//      public FogCityDto(FogCity fogCity) {
//         id = fogCity.getId();
//         cityName = fogCity.getCityName();
//         cityCode = fogCity.getCityCode();
//         description = fogCity.getDescription();
//      }
//      public FogCity toEntity() {
//         return
//      }
//   }
   @NoArgsConstructor
   @Data
   static class FogAttributeDto {
      private String name;
      private String code;
      private Double intensity;
      private Double risk;

      public FogAttributeDto(FogAttribute fogAttribute) {
         name = fogAttribute.getName();
         code = fogAttribute.getCode();
         intensity = fogAttribute.getIntensity();
         risk = fogAttribute.getRisk();
      }
      public FogAttribute toEntity() {
         FogAttribute entity = new FogAttribute();
         entity.setName(name);
         entity.setCode(code);
         entity.setIntensity(intensity);
         entity.setRisk(risk);
         return entity;
      }
   }
}

@Entity
@Data
class Fog {
   @Id
   @GeneratedValue
   private Long id;
   private String name;
   private Double density;
   private BigDecimal lat;
   private BigDecimal lon;
   private Long height;
   private BigDecimal surface;
   @OneToMany(cascade = ALL, orphanRemoval = true)
   @JoinColumn
   private Set<FogCity> cities = new HashSet<>();
   @ElementCollection
   private Set<FogAttribute> attributes = new HashSet<>();
}

@Entity
@Data
class FogCity {
   @Id
   @GeneratedValue
   @EqualsAndHashCode.Exclude
   private Long id;
   private String cityName;
   private String cityCode;
   @Lob
   private String description;
}

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
class FogAttribute {
   private String name;
   private String code;
   private Double intensity;
   private Double risk;
}