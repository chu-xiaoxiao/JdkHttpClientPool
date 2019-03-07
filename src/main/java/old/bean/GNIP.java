package old.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : zhangyuchen
 * @date : 2018/12/17 16:33
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GNIP {
    private String ip;
    private String port;
}
