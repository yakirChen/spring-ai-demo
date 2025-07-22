package demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.entity.Container;
import demo.entity.Item;
import demo.entity.PackingSolution;
import demo.pojo.Constraints;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiPackingService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiPackingService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public PackingSolution solvePacking(Container container, List<Item> items, Constraints constraints) {
        // 构建提示词
        String prompt = buildPrompt(container, items, constraints);

        // 调用DeepSeek模型
        String jsonResponse = chatClient.prompt(prompt).call().content();

        try {
            // 解析模型返回的JSON
            return objectMapper.readValue(jsonResponse, PackingSolution.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse model response", e);
        }
    }

    private String buildPrompt(Container container, List<Item> items, Constraints constraints) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个物流装箱优化专家。请根据以下集装箱规格和物品列表，生成最优的装箱方案。\n\n");

        // 集装箱信息
        prompt.append("### 集装箱规格\n");
        prompt.append(String.format("- 类型: %s\n", container.getType()));
        prompt.append(String.format("- 尺寸: %.2f x %.2f x %.2f 米\n",
                container.getLength(), container.getWidth(), container.getHeight()));
        prompt.append(String.format("- 最大承重: %.2f kg\n\n", container.getMaxWeight()));

        // 物品列表
        prompt.append("### 物品列表\n");
        int index = 1;
        for (Item item : items) {
            prompt.append(String.format("%d. %s (%s): ", index++, item.getName(), item.getType()));
            prompt.append(String.format("尺寸=%.2f x %.2f x %.2f m, ",
                    item.getLength(), item.getWidth(), item.getHeight()));
            prompt.append(String.format("重量=%.2f kg, ", item.getWeight()));
            prompt.append(String.format("易碎=%s, ", item.isFragile() ? "是" : "否"));
            prompt.append(String.format("可旋转=%s\n", item.isRotatable() ? "是" : "否"));
        }
        prompt.append("\n");

        // 约束条件
        prompt.append("### 约束条件\n");
        prompt.append(String.format("- 最大堆叠层数: %d\n", constraints.getMaxStack()));
        prompt.append(String.format("- 最大重心偏移: %.1f%%\n\n", constraints.getMaxCenterOffset() * 100));

        // 输出格式要求
        prompt.append("### 输出要求\n");
        prompt.append("请输出JSON格式的装箱方案，包含以下字段：\n");
        prompt.append("- utilization: 空间利用率(百分比值，如82.5)\n");
        prompt.append("- placements: 物品放置列表，每个元素包含:\n");
        prompt.append("  - itemId: 物品ID\n");
        prompt.append("  - position: [x, y, z] 位置坐标（米）\n");
        prompt.append("  - rotation: [rx, ry, rz] 旋转角度（度）\n");
        prompt.append("- message: 方案说明（可选）\n\n");
        prompt.append("只输出JSON对象，不要包含其他任何内容。");

        return prompt.toString();
    }
}