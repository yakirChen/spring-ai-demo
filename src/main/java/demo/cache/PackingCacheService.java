package demo.cache;

import demo.entity.Container;
import demo.entity.Item;
import demo.entity.PackingSolution;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class PackingCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public PackingCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheSolution(String key, PackingSolution solution) {
        redisTemplate.opsForValue().set(key, solution, 1, TimeUnit.HOURS);
    }

    public PackingSolution getCachedSolution(String key) {
        return (PackingSolution) redisTemplate.opsForValue().get(key);
    }

    public void cacheSimilarSolution(Container container, List<Item> items, PackingSolution solution) {
        // 生成物品特征向量
        double[] itemVector = generateItemVector(items);

        // 存储到Redis有序集合
        String vectorKey = "item_vector:" + container.hashCode();
        redisTemplate.opsForZSet().add(vectorKey, solution, calculateVectorDistance(itemVector));
    }

    public PackingSolution findSimilarSolution(Container container, List<Item> items) {
        double[] currentVector = generateItemVector(items);
        String vectorKey = "item_vector:" + container.hashCode();

        // 使用Redis的ZRANGEBYSCORE查找最相似的方案
        Set<Object> solutions = redisTemplate.opsForZSet().rangeByScore(
                vectorKey,
                calculateMinScore(currentVector),
                calculateMaxScore(currentVector)
        );

        if (solutions != null && !solutions.isEmpty()) {
            return (PackingSolution) solutions.iterator().next();
        }
        return null;
    }

    private double[] generateItemVector(List<Item> items) {
        // 简化的特征向量：平均体积、总重量、易碎品比例
        double totalVolume = 0;
        double totalWeight = 0;
        int fragileCount = 0;

        for (Item item : items) {
            double volume = item.getLength() * item.getWidth() * item.getHeight();
            totalVolume += volume;
            totalWeight += item.getWeight();
        }

        double avgVolume = items.isEmpty() ? 0 : totalVolume / items.size();
        double fragileRatio = items.isEmpty() ? 0 : (double) fragileCount / items.size();

        return new double[]{avgVolume, totalWeight, fragileRatio};
    }

    // --------- 相似度评分辅助 ---------
    private double calculateVectorDistance(double[] vec) {
        // 简化：使用总重量作为排序分值
        return vec[1];
    }

    private double calculateMinScore(double[] vec) {
        return calculateVectorDistance(vec) * 0.9;
    }

    private double calculateMaxScore(double[] vec) {
        return calculateVectorDistance(vec) * 1.1;
    }
}