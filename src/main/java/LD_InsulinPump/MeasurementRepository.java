package LD_InsulinPump;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface MeasurementRepository extends CrudRepository<Measurement, Long> {

    Measurement findById(long id);
}