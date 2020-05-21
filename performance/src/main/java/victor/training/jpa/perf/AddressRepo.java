package victor.training.jpa.perf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepo extends JpaRepository<Address, Long> {

//    @Query(nativeQuery = true)
    Page<Address> findAllByCityId(Long id, Pageable page);

    @Query(nativeQuery= true, value = "SELECT count(*) FROM PARENT")
    int countX();
}
