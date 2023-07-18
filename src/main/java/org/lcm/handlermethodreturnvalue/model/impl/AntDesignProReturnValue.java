package org.lcm.handlermethodreturnvalue.model.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lcm.handlermethodreturnvalue.model.ReturnValue;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AntDesignProReturnValue  implements ReturnValue {
    private static final long serialVersionUID = 1L;

    private Boolean success;
    private Object data;
    private Integer code;
    private String status;

    public void setPage(Page<?> page){
        this.data = new AntDesignProPage(page);
    }
}
