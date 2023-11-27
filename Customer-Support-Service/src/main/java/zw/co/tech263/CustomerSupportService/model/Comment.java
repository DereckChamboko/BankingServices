package zw.co.tech263.CustomerSupportService.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private String userId;
    private String commentText;
    private long commentDate;
}
