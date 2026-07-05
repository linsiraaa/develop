import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TollVolumeService {
    @Autowired
    private TollVolumeRepository tollVolumeRepository;

    public List<TollVolume> getTopVolumes(int limit) {
        return tollVolumeRepository.findTopN(limit);
    }

    public List<TollVolume> getTrendData() {
        return tollVolumeRepository.getTrendData();
    }

    public List<TollVolume> getTollVolumeByDate(Date date) {
        return tollVolumeRepository.findByDate(date);
    }

    public List<TollVolume> getTollVolumeRanking(int limit) {
        List<TollVolume> volumes = tollVolumeRepository.findAll();
        volumes.sort(Comparator.comparingDouble(TollVolume::getAmount).reversed());
        if (volumes.size() > limit) {
            return volumes.subList(0, limit);
        }
        return volumes;
    }
}