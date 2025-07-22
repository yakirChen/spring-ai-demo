package demo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 约束条件
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Constraints {
    private int maxStack;
    private double maxCenterOffset;
}