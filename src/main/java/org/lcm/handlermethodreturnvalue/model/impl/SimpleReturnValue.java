package org.lcm.handlermethodreturnvalue.model.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lcm.handlermethodreturnvalue.model.ReturnValue;

import java.io.Serial;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleReturnValue implements ReturnValue {
    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean success;
    private Object data;
    private Integer code;
    private String status;
}
