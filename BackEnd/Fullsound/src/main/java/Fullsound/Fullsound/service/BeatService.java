package Fullsound.Fullsound.service;
import Fullsound.Fullsound.dto.request.BeatRequest;
import Fullsound.Fullsound.dto.response.BeatResponse;
import java.math.BigDecimal;
import java.util.List;
public interface BeatService {
    BeatResponse create(BeatRequest request);
    BeatResponse update(Integer id, BeatRequest request);
    BeatResponse getById(Integer id);
    BeatResponse getBySlug(String slug);
    List<BeatResponse> getAllActive();
    List<BeatResponse> getFeatured();
    List<BeatResponse> search(String query);
    List<BeatResponse> filterByPrice(Integer min, Integer max);
    List<BeatResponse> filterByBpm(Integer min, Integer max);
    void delete(Integer id);
    void incrementPlays(Integer id);
    void incrementLikes(Integer id);
}
