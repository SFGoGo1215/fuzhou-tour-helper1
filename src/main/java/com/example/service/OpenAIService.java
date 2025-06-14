package com.example.service;

import com.example.model.RouteResponse;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class OpenAIService {

    // 日志配置 - 修复log错误
    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    // 使用您提供的API密钥和代理地址
    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.base-url}")
    private String baseUrl;

    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    private static final String SYSTEM_PROMPT = "你是一个福州旅游助手，专门回答福州的历史、景点、小吃相关问题。" +
            "使用中文回复，回答要详细专业，用emoji增加亲和力。" +
            "福州相关景点：三坊七巷、鼓山、西湖公园、涌泉寺等；" +
            "福州著名小吃：鱼丸、肉燕、鼎边糊、荔枝肉等。";

    private static final Map<String, List<String>> LOCAL_ROUTES = new HashMap<>();

    static {
        LOCAL_ROUTES.put("1", Arrays.asList(
                "上午: 参观三坊七巷，感受福州的历史文化街区",
                "中午: 在三坊七巷附近品尝福州特色小吃",
                "下午: 游览西湖公园，欣赏福州最古老的园林景观",
                "晚上: 自由活动，推荐品尝福州鱼丸和肉燕"
        ));

        LOCAL_ROUTES.put("2", Arrays.asList(
                "上午: 前往鼓山，登山观景并参观涌泉寺",
                "中午: 在鼓山脚下的餐馆用餐",
                "下午: 参观马尾船政文化园，了解中国近代海军历史",
                "晚上: 返回市区，推荐品尝鼎边糊"
        ));

        LOCAL_ROUTES.put("3", Arrays.asList(
                "上午: 参观福州国家森林公园，呼吸新鲜空气",
                "中午: 在森林公园附近用餐",
                "下午: 游览福州熊猫世界，观看大熊猫",
                "晚上: 逛福州夜市，体验当地夜生活"
        ));

        LOCAL_ROUTES.put("4", Arrays.asList(
                "上午: 参观福建省博物馆，了解福建历史文化",
                "中午: 在博物馆附近用餐",
                "下午: 游览金山寺，欣赏闽江风光",
                "晚上: 自由购物，推荐购买福州特产"
        ));

        LOCAL_ROUTES.put("5", Arrays.asList(
                "上午: 前往福州郊区的青云山风景区",
                "中午: 在景区内用餐",
                "下午: 继续游览青云山或返回市区自由活动",
                "晚上: 结束愉快的福州之旅"
        ));
    }

    public RouteResponse generateLocalRoute(int budget, int days) {
        RouteResponse route = new RouteResponse();
        route.setTitle("福州精选" + days + "日游");
        route.setDescription("为您精心设计的" + budget + "元" + days + "天福州深度游");
        route.setTransportation("公共交通与出租车结合");
        route.setTimeRequired(days + "天");
        route.setBudget(budget + "元");
        route.setBestTime("春秋季节 (3-5月, 9-11月)");

        // 随机选择景点组合
        List<String> allSpots = Arrays.asList(
                "三坊七巷", "鼓山", "西湖公园", "马尾船政文化园", "涌泉寺",
                "福州国家森林公园", "福州熊猫世界", "金山寺", "青云山风景区",
                "福建省博物馆", "福州动物园", "左海公园", "乌山风景区"
        );

        Collections.shuffle(allSpots);
        route.setSpots(allSpots.subList(0, Math.min(days * 2, allSpots.size())));

        // 生成每日行程
        Map<String, List<String>> dailyItinerary = new LinkedHashMap<>();
        int budgetPerDay = budget / days;

        for (int i = 1; i <= days; i++) {
            String dayKey = "第" + i + "天";
            List<String> activities = new ArrayList<>(LOCAL_ROUTES.getOrDefault(
                    String.valueOf(i),
                    Collections.emptyList()
            ));

            // 添加预算信息
            activities.add("当日预算: " + budgetPerDay + "元 (餐饮: " + (budgetPerDay/3) + "元, 交通: " + (budgetPerDay/4) + "元, 门票: " + (budgetPerDay/3) + "元)");

            dailyItinerary.put(dayKey, activities);
        }

        route.setDailyItinerary(dailyItinerary);
        return route;
    }

    public RouteResponse getFallbackRoute(int budget, int days) {
        RouteResponse route = new RouteResponse();
        route.setTitle("福州经典" + days + "日游");
        route.setDescription("保障行程 - 预算" + budget + "元");
        route.setSpots(Arrays.asList("三坊七巷", "鼓山", "西湖公园"));
        route.setTransportation("公共交通");
        route.setTimeRequired(days + "天");
        route.setBudget(budget + "元");
        route.setBestTime("全年适宜");

        Map<String, List<String>> dailyItinerary = new LinkedHashMap<>();
        dailyItinerary.put("第1天", Arrays.asList(
                "上午: 参观三坊七巷",
                "中午: 品尝福州小吃",
                "下午: 游览西湖公园",
                "晚上: 自由活动"
        ));

        if (days > 1) {
            dailyItinerary.put("第2天", Arrays.asList(
                    "上午: 前往鼓山",
                    "中午: 鼓山脚下用餐",
                    "下午: 参观涌泉寺",
                    "晚上: 返回市区"
            ));
        }

        if (days > 2) {
            dailyItinerary.put("第3天", Arrays.asList(
                    "上午: 前往福州国家森林公园",
                    "中午: 森林区附近用餐",
                    "下午: 自由购物和品尝美食",
                    "晚上: 结束愉快的福州之旅"
            ));
        }

        route.setDailyItinerary(dailyItinerary);
        return route;
    }

    public Map<String, String> askAI(String question, String history) {
        // 使用正确的API方法（修复generate方法调用问题）
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl) // 使用自定义代理地址
                .timeout(TIMEOUT)
                .modelName("gpt-3.5-turbo")
                .maxTokens(800)
                .temperature(0.7)
                .build();

        // 构建完整的提示词（简化方式）
        StringBuilder fullPrompt = new StringBuilder(SYSTEM_PROMPT);

        if (history != null && !history.isEmpty()) {
            fullPrompt.append("\n\n以下是之前的对话记录:\n").append(history);
        }

        fullPrompt.append("\n\n当前问题:\n用户: ").append(question).append("\nAI:");

        try {
            // 直接调用generate方法传递完整字符串（修复List<ChatMessage>问题）
            String answer = model.generate(fullPrompt.toString());

            logger.info("OpenAI调用成功 - 问题: {}", question);
            logger.debug("OpenAI响应内容: {}", answer);

            // 清理AI回答中的多余前缀
            if (answer.startsWith("AI:") || answer.startsWith("助手:")) {
                answer = answer.substring(answer.indexOf(":") + 1).trim();
            }

            // 构建新的对话历史
            String newHistory = (history == null ? "" : history) +
                    "用户: " + question + "\nAI: " + answer + "\n";

            // 限制历史记录长度（防止过大）
            if (newHistory.length() > 3000) {
                newHistory = newHistory.substring(newHistory.length() - 3000);
            }

            // 返回结果
            return Map.of(
                    "answer", answer,
                    "newHistory", newHistory
            );

        } catch (Exception e) {
            // 出错处理
            logger.error("OpenAI服务错误 - 问题: {} - 错误: {}", question, e.getMessage());

            // 尝试对福州相关问题提供本地回答
            String answer = getLocalResponseForQuestion(question);
            String newHistory = (history == null ? "" : history) +
                    "用户: " + question + "\nAI: " + answer + "\n";

            return Map.of(
                    "answer", answer,
                    "newHistory", newHistory
            );
        }
    }

    // 本地问题回答库（解决OpenAI无法调用时的备选方案）
    private String getLocalResponseForQuestion(String question) {
        // 特别处理"福州有什么好玩的景点"问题
        if (question.contains("福州") && (question.contains("好玩的景点") || question.contains("好玩的地方"))) {
            return "福州有许多著名的景点，我为您推荐：\n\n" +
                    "🏯 三坊七巷 - 福州的历史文化街区\n" +
                    "⛰️ 鼓山 - 登山观景的好去处\n" +
                    "🌳 西湖公园 - 福州最古老的公园\n" +
                    "🛳️ 马尾船政文化园 - 了解中国近代海军历史\n\n" +
                    "以上景点都是福州必去的打卡地！😊";
        }

        // 福州小吃推荐
        if (question.contains("福州") && (question.contains("特色小吃") || question.contains("什么小吃"))) {
            return "福州特色小吃有：\n\n" +
                    "🍡 鱼丸\n" +
                    "🥟 肉燕\n" +
                    "🥘 鼎边糊\n" +
                    "🍖 荔枝肉\n" +
                    "🍲 佛跳墙\n\n" +
                    "强烈推荐尝试这些地道小吃，味道独特！😋";
        }

        // 默认回答
        return "抱歉，处理您的问题时出了点小差错，请稍后再试。😢 您也可以问福州相关的景点和小吃问题。";
    }
}