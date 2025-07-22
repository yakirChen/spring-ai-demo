package demo.service;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import demo.entity.Container;
import demo.entity.Item;
import demo.entity.PackingSolution;
import demo.entity.Placement;
import demo.pojo.Constraints;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TraditionalPackingSolver {

    public PackingSolution solve(Container container, List<Item> items, Constraints constraints) {
        // 创建求解器
        MPSolver solver = MPSolver.createSolver("SCIP");

        // 1. 创建变量
        Map<String, MPVariable> xVars = new HashMap<>();
        Map<String, MPVariable> yVars = new HashMap<>();
        Map<String, MPVariable> zVars = new HashMap<>();
        Map<String, MPVariable> rVars = new HashMap<>();

        for (Item item : items) {
            xVars.put(item.getId(), solver.makeNumVar(0, container.getLength(), "x_" + item.getId()));
            yVars.put(item.getId(), solver.makeNumVar(0, container.getWidth(), "y_" + item.getId()));
            zVars.put(item.getId(), solver.makeNumVar(0, container.getHeight(), "z_" + item.getId()));
            rVars.put(item.getId(), solver.makeIntVar(0, 3, "r_" + item.getId()));
        }

        // 2. 添加约束
        // a. 边界约束
        for (Item item : items) {
            MPVariable r = rVars.get(item.getId());
            // 根据旋转状态确定尺寸
            // 简化处理：仅考虑两种旋转状态
            double length = item.getLength();
            double width = item.getWidth();
            double height = item.getHeight();

            // 约束：x + length <= container.length
            MPConstraint xConstraint = solver.makeConstraint(
                    0, container.getLength() - length,
                    "x_bound_" + item.getId());
            xConstraint.setCoefficient(xVars.get(item.getId()), 1);

            // 类似添加其他约束...
        }

        // b. 非重叠约束
        for (int i = 0; i < items.size(); i++) {
            for (int j = i + 1; j < items.size(); j++) {
                Item item1 = items.get(i);
                Item item2 = items.get(j);

                // 添加至少一个方向上的非重叠约束
                // 使用大M方法实现OR条件
                // 具体实现略...
            }
        }

        // 3. 设置目标函数：最大化空间利用率
        MPObjective objective = solver.objective();
        for (Item item : items) {
            objective.setCoefficient(xVars.get(item.getId()), 0);
            // 实际应计算体积贡献
        }
        objective.setMaximization();

        // 4. 求解
        MPSolver.ResultStatus resultStatus = solver.solve();

        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            PackingSolution solution = new PackingSolution();
            List<Placement> placements = new ArrayList<>();

            for (Item item : items) {
                Placement placement = new Placement();
                placement.setItemId(item.getId());
                placement.setPosition(new double[]{
                        xVars.get(item.getId()).solutionValue(),
                        yVars.get(item.getId()).solutionValue(),
                        zVars.get(item.getId()).solutionValue()
                });

                int rotation = (int) rVars.get(item.getId()).solutionValue();
                placement.setRotation(new double[]{0, rotation * 90, 0});

                placements.add(placement);
            }

            solution.setPlacements(placements);
            return solution;
        }

        return null;
    }
}