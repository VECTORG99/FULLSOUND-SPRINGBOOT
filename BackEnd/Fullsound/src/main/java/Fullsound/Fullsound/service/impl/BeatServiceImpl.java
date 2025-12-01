package Fullsound.Fullsound.service.impl;

import Fullsound.Fullsound.dto.request.BeatRequest;
import Fullsound.Fullsound.dto.response.BeatResponse;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.mapper.BeatMapper;
import Fullsound.Fullsound.model.Beat;
import Fullsound.Fullsound.repository.BeatRepository;
import Fullsound.Fullsound.service.BeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de beats.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Service
@RequiredArgsConstructor
public class BeatServiceImpl implements BeatService {
    
    private final BeatRepository beatRepository;
    private final BeatMapper beatMapper;
    
    @Override
    @Transactional
    public BeatResponse create(BeatRequest request) {
        Beat beat = beatMapper.toEntity(request);
        
        // Generar slug a partir del título
        beat.setSlug(generateSlug(request.getTitulo()));
        
        Beat savedBeat = beatRepository.save(beat);
        return beatMapper.toResponse(savedBeat);
    }
    
    @Override
    @Transactional
    public BeatResponse update(Integer id, BeatRequest request) {
        Beat beat = beatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beat", "id", id));
        
        beatMapper.updateEntity(request, beat);
        
        // Actualizar slug si cambió el título
        if (request.getTitulo() != null && !request.getTitulo().equals(beat.getTitulo())) {
            beat.setSlug(generateSlug(request.getTitulo()));
        }
        
        Beat updatedBeat = beatRepository.save(beat);
        return beatMapper.toResponse(updatedBeat);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BeatResponse getById(Integer id) {
        Beat beat = beatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beat", "id", id));
        return beatMapper.toResponse(beat);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BeatResponse getBySlug(String slug) {
        Beat beat = beatRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Beat", "slug", slug));
        return beatMapper.toResponse(beat);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BeatResponse> getAllActive() {
        return beatRepository.findAll().stream()
                .map(beatMapper::toResponse)
                .collect(Collectors.toList());
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
    public List<BeatResponse> filterByPrice(Integer min, Integer max) {
        return beatRepository.findAll().stream()
                .filter(beat -> beat.getPrecio() >= min && beat.getPrecio() <= max)
                .map(beatMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BeatResponse> filterByBpm(Integer min, Integer max) {
        return beatRepository.findAll().stream()
                .filter(beat -> beat.getBpm() != null && beat.getBpm() >= min && beat.getBpm() <= max)
                .map(beatMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void delete(Integer id) {
        Beat beat = beatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beat", "id", id));
        beatRepository.delete(beat);
    }
    
    @Override
    @Transactional
    public void incrementPlays(Integer id) {
        Beat beat = beatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beat", "id", id));
        beat.setReproducciones(beat.getReproducciones() + 1);
        beatRepository.save(beat);
    }
    
    @Override
    @Transactional
    public void incrementLikes(Integer id) {
        // Funcionalidad de likes removida - columna eliminada del schema PostgreSQL
        throw new UnsupportedOperationException("La funcionalidad de likes ha sido removida del schema de base de datos");
    }
    
    /**
     * Genera un slug SEO-friendly a partir del título.
     */
    private String generateSlug(String titulo) {
        String slug = Normalizer.normalize(titulo, Normalizer.Form.NFD);
        slug = slug.replaceAll("[^\\p{ASCII}]", "");
        slug = slug.toLowerCase().replaceAll("[^a-z0-9]+", "-");
        slug = slug.replaceAll("^-|-$", "");
        
        // Asegurar unicidad
        String finalSlug = slug;
        int counter = 1;
        while (beatRepository.findBySlug(finalSlug).isPresent()) {
            finalSlug = slug + "-" + counter++;
        }
        
        return finalSlug;
    }
}
