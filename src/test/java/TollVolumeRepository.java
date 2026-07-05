import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TollVolumeRepository extends JpaRepository<TollVolume, Long> {
    List<TollVolume> findTopN(int n);
    List<TollVolume> getTrendData();
    List<TollVolume> findByDate(Date date);
}