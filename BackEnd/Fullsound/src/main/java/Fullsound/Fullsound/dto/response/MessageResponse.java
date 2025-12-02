package Fullsound.Fullsound.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    private String message;
    private Boolean success;
    public MessageResponse(String message) {
        this.message = message;
        this.success = true;
    }
}
