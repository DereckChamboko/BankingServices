package zw.co.tech263.CustomerSupportService.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {
    private String userId;
    private String description;
    private long activityDate;

}
