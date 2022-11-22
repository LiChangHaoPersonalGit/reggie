package com.lch.reggie.dto;

//import com.reggie.entity.Dish;
//import com.reggie.entity.DishFlavor;
import com.lch.reggie.entity.Dish;
import com.lch.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lch
 * @create 2022/11/4
 * Description:DTO全称为Data Transfer Object，即数据传输对象，一般用于展示层和服务层之间的数据传输
 * 由于我们的Dish类无法完全接收添加菜品类传来的数据，所以扩展一个新的DTO用于接收这些数据
 */

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
