package demo.service;

import demo.entity.Container;
import demo.entity.Item;
import demo.entity.PackingSolution;
import demo.entity.Placement;
import demo.pojo.Constraints;
import demo.pojo.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhysicsValidator {

    public ValidationResult validate(Container container, List<Item> items, Constraints constraints, PackingSolution solution) {

        ValidationResult result = new ValidationResult();
        result.setUtilization(solution.getUtilization());

        // 1. 检查边界冲突
        for (Placement placement : solution.getPlacements()) {
            Item item = findItemById(items, placement.getItemId());
            if (item == null) {
                result.addError("物品不存在: " + placement.getItemId());
                continue;
            }

            double[] dim = getRotatedDimensions(item, placement.getRotation());
            double[] pos = placement.getPosition();

            if (!checkBoundaries(container, pos, dim)) {
                result.addError(String.format("物品 %s 超出集装箱边界: 位置(%.2f,%.2f,%.2f), 尺寸(%.2f,%.2f,%.2f)",
                        item.getName(), pos[0], pos[1], pos[2], dim[0], dim[1], dim[2]));
            }
        }

        // 2. 检查物品重叠
        if (hasOverlappingItems(solution)) {
            result.addError("存在物品重叠问题");
        }

        // 3. 检查堆叠稳定性
        if (!checkStackingStability(container, items, solution)) {
            result.addError("存在堆叠不稳定问题");
        }

        // 4. 检查重心偏移
        double centerOffset = calculateCenterOffset(container, items, solution);
        if (centerOffset > constraints.getMaxCenterOffset()) {
            result.addError(String.format("重心偏移过大: %.2f%% > 允许 %.2f%%",
                    centerOffset * 100, constraints.getMaxCenterOffset() * 100));
        }

        return result;
    }

    private double[] getRotatedDimensions(Item item, double[] rotation) {
        double[] dim = {item.getLength(), item.getWidth(), item.getHeight()};
        // 简化处理：仅考虑绕Y轴旋转
        if (Math.abs(rotation[1] % 180) > 45) {
            dim[0] = item.getHeight();
            dim[2] = item.getLength();
        }
        return dim;
    }

    private boolean checkBoundaries(Container container, double[] pos, double[] dim) {
        return pos[0] >= 0 && pos[0] + dim[0] <= container.getLength() &&
                pos[1] >= 0 && pos[1] + dim[1] <= container.getWidth() &&
                pos[2] >= 0 && pos[2] + dim[2] <= container.getHeight();
    }

    // 其他验证方法...
}

