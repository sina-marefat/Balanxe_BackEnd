package ir.balanxe.modules.log.repository;

import ir.balanxe.modules.log.model.Log;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LogRepository extends MongoRepository<Log, UUID> {
}
