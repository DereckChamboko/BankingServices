package zw.co.tech263.CustomerSupportService.dto.request;


import io.swagger.v3.oas.annotations.callbacks.Callback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityDTO {
    private String user;
    private String description;
}
