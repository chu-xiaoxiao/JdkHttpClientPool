package old.bean;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : zhangyuchen
 * @date : 2019/1/9 17:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeiTuanShangHh extends BaseRowModel {
    @ExcelProperty(value = "店铺名",index = 0)
    private String title;
    @ExcelProperty(value = "店铺电话",index = 1)
    private String phone;
    @ExcelProperty(value = "店铺类型",index = 2)
    private String type;
    @ExcelProperty(value = "店铺地址",index = 3)
    private String address;
    @ExcelProperty(value = "营业时间",index = 4)
    private String openTime;
    @ExcelProperty(value = "店铺经度",index = 5)
    private String longitude;
    @ExcelProperty(value = "店铺纬度",index = 6)
    private String latitude;

}
