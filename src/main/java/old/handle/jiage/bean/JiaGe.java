package old.handle.jiage.bean;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Builder;
import lombok.Data;

/**
 * @author : zhangyuchen
 * @date : 2019/1/24 11:38
 */
@Data
@Builder
public class JiaGe extends BaseRowModel {
    @ExcelProperty(value = "价格日期",index = 0)
    private String date;
    @ExcelProperty(value = "商品名",index = 0)
    private String name;
    @ExcelProperty(value = "商品价格",index = 0)
    private String price;
    @ExcelProperty(value = "地址",index = 0)
    private String addr;
}
