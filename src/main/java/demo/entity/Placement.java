package demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 放置位置
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Placement {
    private String itemId;
    private double[] position = new double[3]; // [x, y, z]
    private double[] rotation = new double[3]; // [rx, ry, rz]
}