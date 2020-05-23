package com.faiop.core.util;

import com.faiop.core.pojo.Menu;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @Description: 处理menu集合
 * @Author RM
 */
public class MenuModel {
    private List<Menu> menus = new ArrayList<>();

    public MenuModel(List<Menu> menus){
        this.menus= menus;
    }

    public Map<Long, ParentItem> getMenuList(){
        Map<Long,ParentItem> menuMap = new HashMap<>();
        for(Menu menu : menus){
            //如果此menu没有父id
            if(menu.getParentId() == null){
                //如果menuMap里没有这个menu
                if(null == menuMap.get(menu.getId())){
                    //创建一个父menu
                    ParentItem parentItem = new ParentItem();
                    parentItem.setName(menu.getName());
                    parentItem.setIsSubShow(false);
                    menuMap.put(menu.getId(), parentItem);
                }
            }
            else{
                SubItem subItem = new SubItem();
                subItem.setName(menu.getName());
                subItem.setUrl(menu.getUrl());
                menuMap.get(menu.getParentId()).getSubMap().put(menu.getId(),subItem);
            }
        }
        return menuMap;
    }
}
