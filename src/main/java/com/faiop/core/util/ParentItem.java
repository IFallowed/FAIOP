package com.faiop.core.util;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author RM
 */
@Data
public class ParentItem {
    private String name;
    private Boolean isSubShow;
    private Map<Long, SubItem> subMap = new HashMap<>();
}
