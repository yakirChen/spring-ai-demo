package demo.action;

import demo.entity.PackingSolution;
import demo.pojo.PackingRequest;
import demo.pojo.ValidationRequest;
import demo.pojo.ValidationResult;
import demo.service.HybridPackingService;
import demo.service.PackingVisualizerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/packing")
@AllArgsConstructor
public class PackingController {

    private final HybridPackingService packingService;
    private final PackingVisualizerService packingVisualizer;

    @PostMapping("/solve")
    public ResponseEntity<PackingSolution> solvePackingProblem(
            @RequestBody PackingRequest request) {

        PackingSolution solution = packingService.solve(
                request.getContainer(),
                request.getItems(),
                request.getConstraints()
        );

        return ResponseEntity.ok(solution);
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidationResult> validateSolution(
            @RequestBody ValidationRequest request) {

        ValidationResult result = packingService.validate(
                request.getContainer(),
                request.getItems(),
                request.getConstraints(),
                request.getSolution()
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/visualize/{solutionId}")
    public ResponseEntity<String> visualizeSolution(@PathVariable String solutionId) {
        // 生成Three.js可视化代码
        String visualization = packingVisualizer.generate3DVisualization(solutionId);
        return ResponseEntity.ok(visualization);
    }
}