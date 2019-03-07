package old.handle.jd.bean;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : zhangyuchen
 * @date : 2019/1/24 11:38
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JD extends BaseRowModel {
    @ExcelProperty(value = "商品名",index = 0)
    private String name;
    @ExcelProperty(value = "商品价格",index = 1)
    private String price;
    @ExcelProperty(value = "一级分类",index = 2)
    private String  firstType;
    @ExcelProperty(value = "二级分类",index = 3)
    private String  secondType;
    @ExcelProperty(value = "店铺",index = 4)
    private String addr;
    @ExcelProperty(value = "data_sku",index = 5)
    private String dataSku;
    @ExcelProperty(value = "data_spu",index = 6)
    private String dataSpu;
    @ExcelProperty(value = "data_pid",index = 7)
    private String dataPid;
    @ExcelProperty(value = "商品详情地址",index = 8)
    private String url;

}
