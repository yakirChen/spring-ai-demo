package demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
public class SolutionResult {
    private boolean success;
    private String message;
    private double spaceUtilization;
    private int containersUsed;
    private List<Placement> placements;
    private Map<String, Double> containerUtilization;
}