package pl.pjatk.jazs29866nbp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NbpRepository extends JpaRepository<ExchangeRateQuery, Long> {
}