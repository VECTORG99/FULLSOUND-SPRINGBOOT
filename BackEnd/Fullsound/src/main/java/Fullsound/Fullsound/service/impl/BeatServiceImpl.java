package Fullsound.Fullsound.service.impl;
import Fullsound.Fullsound.dto.request.BeatRequest;
import Fullsound.Fullsound.dto.response.BeatResponse;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.mapper.BeatMapper;
import Fullsound.Fullsound.model.Beat;
import Fullsound.Fullsound.repository.BeatRepository;
import Fullsound.Fullsound.service.BeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Slf4j
public class BeatServiceImpl implements BeatService {
    private final BeatRepository beatRepository;
    private final BeatMapper beatMapper;
    @Override
    @Transactional
    @CacheEvict(cacheNames = {"beats", "beatList"}, allEntries = true)
    public BeatResponse create(BeatRequest request) {
        Beat beat = beatMapper.toEntity(request);
        beat.setSlug(generateSlug(request.getTitulo()));
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Beat savedBeat = beatRepository.save(beat);
                return beatMapper.toResponse(savedBeat);
            } catch (DataIntegrityViolationException e) {
                if (attempt == maxRetries) {
                    log.error("No se pudo generar un slug único para el beat '{}' tras {} intentos", request.getTitulo(), maxRetries);
                    throw e;
                }
                log.warn("Conflicto de slug detectado (intento {}/{}), reintentando con contador incrementado", attempt, maxRetries);
                beat.setSlug(generateSlug(request.getTitulo(), attempt));
            }
        }
        throw new DataIntegrityViolationException("No se pudo generar un slug único para el beat");
    }
    @Override
    @Transactional
    @CacheEvict(cacheNames = {"beats", "beatList"}, allEntries = true)
    public BeatResponse update(Integer id, BeatRequest request) {
        Beat beat = beatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beat", "id", id));
        beatMapper.updateEntity(request, beat);
        if (request.getTitulo() != null && !request.getTitulo().equals(beat.getTitulo())) {
            beat.setSlug(generateSlug(request.getTitulo()));
        }
        Beat updatedBeat = beatRepository.save(beat);
        return beatMapper.toResponse(updatedBeat);
    }
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "beats", key = "#id")
    public BeatResponse getById(Integer id) {
        Beat beat = beatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beat", "id", id));
        return beatMapper.toResponse(beat);
    }
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "beats", key = "#slug")
    public BeatResponse getBySlug(String slug) {
        Beat beat = beatRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Beat", "slug", slug));
        return beatMapper.toResponse(beat);
    }
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "beatList", key = "'all'")
    public List<BeatResponse> getAllActive() {
        return beatRepository.findAll().stream()
                .map(beatMapper::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "beatList", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()")
    public Page<BeatResponse> getAllActive(Pageable pageable) {
        return beatRepository.findAll(pageable)
                .map(beatMapper::toResponse);
    }
    @Override
    @Transactional(readOnly = true)
    public List<BeatResponse> getFeatured() {
        return beatRepository.findAll().stream()
                .map(beatMapper::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<BeatResponse> search(String query) {
        return beatRepository.search(query).stream()
                .map(beatMapper::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public Page<BeatResponse> search(String query, Pageable pageable) {
        return beatRepository.search(query, pageable)
                .map(beatMapper::toResponse);
    }
    @Override
    @Transactional(readOnly = true)
    public List<BeatResponse> filterByPrice(Integer min, Integer max) {
        return beatRepository.findByPrecioBetween(min, max).stream()
                .map(beatMapper::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public Page<BeatResponse> filterByPrice(Integer min, Integer max, Pageable pageable) {
        return beatRepository.findByPrecioBetween(min, max, pageable)
                .map(beatMapper::toResponse);
    }
    @Override
    @Transactional(readOnly = true)
    public List<BeatResponse> filterByBpm(Integer min, Integer max) {
        return beatRepository.findByBpmBetween(min, max).stream()
                .map(beatMapper::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public Page<BeatResponse> filterByBpm(Integer min, Integer max, Pageable pageable) {
        return beatRepository.findByBpmBetween(min, max, pageable)
                .map(beatMapper::toResponse);
    }
    @Override
    @Transactional
    @CacheEvict(cacheNames = {"beats", "beatList"}, allEntries = true)
    public void delete(Integer id) {
        Beat beat = beatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beat", "id", id));
        beatRepository.delete(beat);
    }
    @Override
    @Transactional
    @CacheEvict(cacheNames = "beats", key = "#id")
    public void incrementPlays(Integer id) {
        Beat beat = beatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beat", "id", id));
        beat.setReproducciones(beat.getReproducciones() + 1);
        beatRepository.save(beat);
    }
    @Override
    @Transactional
    public void incrementLikes(Integer id) {
        throw new UnsupportedOperationException("La funcionalidad de likes ha sido removida del schema de base de datos");
    }
    private String generateSlug(String titulo) {
        return generateSlug(titulo, 1);
    }
    private String generateSlug(String titulo, int startCounter) {
        String slug = Normalizer.normalize(titulo, Normalizer.Form.NFD);
        slug = slug.replaceAll("[^\\p{ASCII}]", "");
        slug = slug.toLowerCase().replaceAll("[^a-z0-9]+", "-");
        slug = slug.replaceAll("^-|-$", "");
        String finalSlug = slug;
        int counter = startCounter;
        while (beatRepository.findBySlug(finalSlug).isPresent()) {
            finalSlug = slug + "-" + counter++;
        }
        return finalSlug;
    }
}
