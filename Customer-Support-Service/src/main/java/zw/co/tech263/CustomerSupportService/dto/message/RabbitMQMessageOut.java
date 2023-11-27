package zw.co.tech263.CustomerSupportService.dto.message;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RabbitMQMessageOut {
    private String accountNumber;
    private String messageTittle;
    private Object message;


}
