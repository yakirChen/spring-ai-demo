package demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 集装箱实体
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Container {
    private String type;
    private double length;
    private double width;
    private double height;
    private double maxWeight;
}