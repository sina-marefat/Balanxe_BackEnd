package ir.balanxe.modules.log.service;
import ir.balanxe.modules.log.model.Log;
import ir.balanxe.modules.log.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private final LogRepository logRepository;

    @Autowired
    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Async
    public void addLog(Log log) {
        logRepository.save(log);
    }
}
