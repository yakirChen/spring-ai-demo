package demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 物品实体
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private String id;
    private String name;
    private String type;
    private double length;
    private double width;
    private double height;
    private double weight;
    private boolean fragile;
    private boolean rotatable;
}