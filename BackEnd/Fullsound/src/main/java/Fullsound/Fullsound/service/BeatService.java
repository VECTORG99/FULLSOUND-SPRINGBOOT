package Fullsound.Fullsound.service;
import Fullsound.Fullsound.dto.request.BeatRequest;
import Fullsound.Fullsound.dto.response.BeatResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
public interface BeatService {
    BeatResponse create(BeatRequest request);
    BeatResponse update(Integer id, BeatRequest request);
    BeatResponse getById(Integer id);
    BeatResponse getBySlug(String slug);
    List<BeatResponse> getAllActive();
    Page<BeatResponse> getAllActive(Pageable pageable);
    List<BeatResponse> getFeatured();
    List<BeatResponse> search(String query);
    Page<BeatResponse> search(String query, Pageable pageable);
    List<BeatResponse> filterByPrice(Integer min, Integer max);
    Page<BeatResponse> filterByPrice(Integer min, Integer max, Pageable pageable);
    List<BeatResponse> filterByBpm(Integer min, Integer max);
    Page<BeatResponse> filterByBpm(Integer min, Integer max, Pageable pageable);
    void delete(Integer id);
    void incrementPlays(Integer id);
    void incrementLikes(Integer id);
}
