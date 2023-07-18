package org.lcm.handlermethodreturnvalue.model.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AntDesignProPage {
    // AntDesignPro 规范
    private List<?> list;
    private int current;
    private int pageSize;

    // web项目中,Java后端传过来的Long/long类型，前端JS接收会丢失精度。
    private long total;

    // Page 规范
    private Pageable pageable;
    private int totalPages;
    private long totalElements;
    private int number;
    private int size;
    private int numberOfElements;
    private Sort sort;
    private boolean last;
    private boolean first;
    private boolean empty;

    public AntDesignProPage(Page<?> page){
        // Page 规范
        this.list = page.getContent();
        this.current = page.getNumber();
        this.pageSize = page.getSize();
        this.total = page.getTotalElements();

        // Page 规范
        this.pageable = page.getPageable();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.number = page.getNumber();
        this.size = page.getSize();
        this.numberOfElements = page.getNumberOfElements();
        this.sort = page.getSort();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
    }
}
