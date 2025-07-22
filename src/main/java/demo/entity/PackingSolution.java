package demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// 装箱方案
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackingSolution {
    private double utilization;
    private List<Placement> placements;
    private String message;
}