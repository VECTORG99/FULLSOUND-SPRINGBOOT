package Fullsound.Fullsound.service;
import Fullsound.Fullsound.model.Beat;
import java.util.List;
import java.util.Map;
public interface EstadisticasService {
    Map<String, Object> getDashboardStats();
    Map<String, Object> getVentasStats();
    List<Beat> getBeatsPopulares(Integer limit);
}
