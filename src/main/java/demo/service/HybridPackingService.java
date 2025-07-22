package demo.service;

import demo.entity.Container;
import demo.entity.Item;
import demo.entity.PackingSolution;
import demo.pojo.Constraints;
import demo.pojo.ValidationResult;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class HybridPackingService {

    private final AiPackingService aiPackingService;
    private final TraditionalPackingSolver traditionalSolver;
    private final PhysicsValidator physicsValidator;
    private final RedisTemplate<String, Object> redisTemplate;

    public PackingSolution solve(Container container, List<Item> items, Constraints constraints) {
        // 1. 检查缓存
        String cacheKey = generateCacheKey(container, items);
        PackingSolution cachedSolution = (PackingSolution) redisTemplate.opsForValue().get(cacheKey);
        if (cachedSolution != null) {
            return cachedSolution;
        }

        // 2. 使用AI模型生成方案
        PackingSolution aiSolution = aiPackingService.solvePacking(container, items, constraints);

        // 3. 物理验证
        ValidationResult validation = physicsValidator.validate(container, items, constraints, aiSolution);

        // 4. 如果验证通过，使用AI方案
        if (validation.isValid() && aiSolution.getUtilization() > 75.0) {
            redisTemplate.opsForValue().set(cacheKey, aiSolution, 1, TimeUnit.HOURS);
            return aiSolution;
        }

        // 5. 否则使用传统求解器
        PackingSolution tradSolution = traditionalSolver.solve(container, items, constraints);

        // 6. 再次验证
        ValidationResult tradValidation = physicsValidator.validate(container, items, constraints, tradSolution);

        if (tradValidation.isValid()) {
            redisTemplate.opsForValue().set(cacheKey, tradSolution, 1, TimeUnit.HOURS);
            return tradSolution;
        }

        // 7. 如果都不行，返回错误
        PackingSolution fallback = new PackingSolution();
        fallback.setMessage("无法生成有效的装箱方案");
        fallback.setUtilization(0);
        return fallback;
    }

    private String generateCacheKey(Container container, List<Item> items) {
        StringBuilder key = new StringBuilder("packing:");
        key.append(container.hashCode()).append(":");

        // 按ID排序物品以确保一致的缓存键
        items.sort(Comparator.comparing(Item::getId));
        for (Item item : items) {
            key.append(item.getId()).append("_");
        }

        return key.toString();
    }
}