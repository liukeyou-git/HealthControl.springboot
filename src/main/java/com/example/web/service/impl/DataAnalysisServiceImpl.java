package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.web.entity.*;
import com.example.web.enums.AuditStatusEnum;
import com.example.web.mapper.*;
import com.example.web.service.DataAnalysisService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataAnalysisServiceImpl implements DataAnalysisService {

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private HealthArticleMapper healthArticleMapper;

    @Autowired
    private RecipeMapper recipeMapper;

    @Autowired
    private SportMapper sportMapper;

    @Autowired
    private HealthIndicatorMapper healthIndicatorMapper;

    @Autowired
    private HealthIndicatorRecordMapper healthIndicatorRecordMapper;

    @Autowired
    private DietRecordMapper dietRecordMapper;

    @Autowired
    private SportRecordMapper sportRecordMapper;

    @Autowired
    private CollectRecordMapper collectRecordMapper;

    @Autowired
    private LikeRecordMapper likeRecordMapper;

    @Autowired
    private FoodMapper foodMapper;

    @Autowired
    private FoodTypeMapper foodTypeMapper;

    @Autowired
    private HealthNoticeMapper healthNoticeMapper;

    @Autowired
    private MessageNoticeMapper messageNoticeMapper;

    @SneakyThrows
    @Override
    public HashMap<String, Object> GetAllAnalysisData() {
        HashMap<String, Object> result = new HashMap<>();

        // 1. 基础统计数据
        result.put("OverviewData", GetOverviewData());

        // 2. 用户性别分布饼图
        result.put("UserGenderPieChart", GetUserGenderPieChart());

        // 3. 用户年龄分布柱状图
        result.put("UserAgeBarChart", GetUserAgeBarChart());

        // 4. 健康指标记录趋势线图
        result.put("HealthIndicatorLineChart", GetHealthIndicatorLineChart());

        // 5. 饮食热量面积图
        result.put("DietCaloriesAreaChart", GetDietCaloriesAreaChart());

        // 6. 运动记录柱状图
        result.put("SportRecordBarChart", GetSportRecordBarChart());

        // 7. 健康知识浏览量排行榜
        result.put("ArticleViewRankingChart", GetArticleViewRankingChart());

        // 8. 健康指标类型环形图
        result.put("HealthIndicatorDoughnutChart", GetHealthIndicatorDoughnutChart());

        // 9. 月度数据对比雷达图
        result.put("MonthlyDataRadarChart", GetMonthlyDataRadarChart());

        // 10. 异常指标堆叠柱状图
        result.put("AbnormalIndicatorStackedChart", GetAbnormalIndicatorStackedChart());

        // 11. 用户活跃度热力图
        result.put("UserActivityHeatmapChart", GetUserActivityHeatmapChart());

        // 12. 收藏点赞仪表盘
        result.put("EngagementGaugeChart", GetEngagementGaugeChart());

        // 13. 食物类型分布饼图
        result.put("FoodTypeDistributionChart", GetFoodTypeDistributionChart());

        // 14. 食谱审核状态漏斗图
        result.put("RecipeAuditFunnelChart", GetRecipeAuditFunnelChart());

        // 15. 健康提醒完成率散点图
        result.put("HealthNoticeScatterChart", GetHealthNoticeScatterChart());

        return result;
    }

    // 基础统计数据
    private HashMap<String, Object> GetOverviewData() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("TotalUsers", appUserMapper.selectCount(null));
        data.put("TotalHealthArticles", healthArticleMapper.selectCount(null));
        data.put("TotalRecipes", recipeMapper.selectCount(null));
        data.put("TotalSports", sportMapper.selectCount(null));
        data.put("TotalHealthIndicators", healthIndicatorMapper.selectCount(null));
        data.put("TotalHealthRecords", healthIndicatorRecordMapper.selectCount(null));
        data.put("TotalDietRecords", dietRecordMapper.selectCount(null));
        data.put("TotalSportRecords", sportRecordMapper.selectCount(null));
        return data;
    }

    // 1. 用户性别分布饼图
    private List<HashMap<String, Object>> GetUserGenderPieChart() {
        List<AppUser> users = appUserMapper.selectList(null);
        Map<String, Long> genderCount = users.stream()
                .collect(Collectors.groupingBy(
                        user -> user.getGender() == null || user.getGender().isEmpty() ? "未知" : user.getGender(),
                        Collectors.counting()));

        List<HashMap<String, Object>> result = new ArrayList<>();
        long total = users.size();

        for (Map.Entry<String, Long> entry : genderCount.entrySet()) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("Label", entry.getKey());
            item.put("Value", entry.getValue());
            item.put("Percentage", Double.parseDouble(String.format("%.1f", (entry.getValue() * 100.0 / total))));
            result.add(item);
        }
        return result;
    }

    // 2. 用户年龄分布柱状图
    private List<HashMap<String, Object>> GetUserAgeBarChart() {
        List<AppUser> users = appUserMapper.selectList(null);
        Map<String, Long> ageGroups = new LinkedHashMap<>();
        ageGroups.put("18-25岁", 0L);
        ageGroups.put("26-35岁", 0L);
        ageGroups.put("36-45岁", 0L);
        ageGroups.put("46-55岁", 0L);
        ageGroups.put("56岁以上", 0L);
        ageGroups.put("未知", 0L);

        LocalDateTime now = LocalDateTime.now();
        for (AppUser user : users) {
            if (user.getBirth() == null) {
                ageGroups.put("未知", ageGroups.get("未知") + 1);
                continue;
            }

            int age = now.getYear() - user.getBirth().getYear();
            if (age <= 25)
                ageGroups.put("18-25岁", ageGroups.get("18-25岁") + 1);
            else if (age <= 35)
                ageGroups.put("26-35岁", ageGroups.get("26-35岁") + 1);
            else if (age <= 45)
                ageGroups.put("36-45岁", ageGroups.get("36-45岁") + 1);
            else if (age <= 55)
                ageGroups.put("46-55岁", ageGroups.get("46-55岁") + 1);
            else
                ageGroups.put("56岁以上", ageGroups.get("56岁以上") + 1);
        }

        List<HashMap<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, Long> entry : ageGroups.entrySet()) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("Label", entry.getKey());
            item.put("Value", entry.getValue());
            result.add(item);
        }
        return result;
    }

    // 3. 健康指标记录趋势线图
    private List<HashMap<String, Object>> GetHealthIndicatorLineChart() {
        List<HealthIndicatorRecord> records = healthIndicatorRecordMapper.selectList(
                Wrappers.<HealthIndicatorRecord>lambdaQuery()
                        .ge(HealthIndicatorRecord::getRecordTime, LocalDateTime.now().minusDays(30))
                        .orderByAsc(HealthIndicatorRecord::getRecordTime));

        Map<String, List<HealthIndicatorRecord>> groupedRecords = records.stream()
                .collect(Collectors.groupingBy(record -> {
                    HealthIndicator indicator = healthIndicatorMapper.selectById(record.getHealthIndicatorId());
                    return indicator != null ? indicator.getName() : "未知指标";
                }));

        List<HashMap<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, List<HealthIndicatorRecord>> entry : groupedRecords.entrySet()) {
            for (HealthIndicatorRecord record : entry.getValue()) {
                HashMap<String, Object> item = new HashMap<>();
                item.put("Series", entry.getKey());
                item.put("Date", record.getRecordTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                item.put("Value", record.getRecordValue());
                result.add(item);
            }
        }
        return result;
    }

    // 4. 饮食热量面积图
    private List<HashMap<String, Object>> GetDietCaloriesAreaChart() {
        List<DietRecord> dietRecords = dietRecordMapper.selectList(
                Wrappers.<DietRecord>lambdaQuery()
                        .ge(DietRecord::getRecordTime, LocalDateTime.now().minusDays(30))
                        .orderByAsc(DietRecord::getRecordTime));

        Map<String, Double> dailyCalories = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (DietRecord record : dietRecords) {
            String date = record.getRecordTime().format(formatter);
            Food food = foodMapper.selectById(record.getFoodId());
            if (food != null) {
                double calories = food.getCalories() * record.getRecordValue();
                dailyCalories.put(date, dailyCalories.getOrDefault(date, 0.0) + calories);
            }
        }

        List<HashMap<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Double> entry : dailyCalories.entrySet()) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("Date", entry.getKey());
            item.put("Value", entry.getValue());
            item.put("Series", "每日热量摄入");
            result.add(item);
        }
        return result;
    }

    // 5. 运动记录柱状图
    private List<HashMap<String, Object>> GetSportRecordBarChart() {
        List<SportRecord> sportRecords = sportRecordMapper.selectList(null);
        Map<String, Long> sportCount = new HashMap<>();

        for (SportRecord record : sportRecords) {
            Sport sport = sportMapper.selectById(record.getSportId());
            if (sport != null) {
                sportCount.put(sport.getName(), sportCount.getOrDefault(sport.getName(), 0L) + 1);
            }
        }

        List<HashMap<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, Long> entry : sportCount.entrySet()) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("Label", entry.getKey());
            item.put("Value", entry.getValue());
            result.add(item);
        }
        return result;
    }

    // 6. 健康知识浏览量排行榜
    private List<HashMap<String, Object>> GetArticleViewRankingChart() {
        List<HealthArticle> articles = healthArticleMapper.selectList(
                Wrappers.<HealthArticle>lambdaQuery()
                        .orderByDesc(HealthArticle::getViewCount)
                        .last("LIMIT 10"));

        List<HashMap<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < articles.size(); i++) {
            HealthArticle article = articles.get(i);
            HashMap<String, Object> item = new HashMap<>();
            item.put("Rank", i + 1);
            item.put("Label", article.getTitle());
            item.put("Value", article.getViewCount());
            result.add(item);
        }
        return result;
    }

    // 7. 健康指标类型环形图
    private List<HashMap<String, Object>> GetHealthIndicatorDoughnutChart() {
        List<HealthIndicator> indicators = healthIndicatorMapper.selectList(null);
        Map<String, Long> typeCount = new HashMap<>();

        for (HealthIndicator indicator : indicators) {
            if (indicator.getHealthIndicatorTypeId() != null) {
                // 这里简化处理，实际应该关联指标类型表
                String typeName = "类型" + indicator.getHealthIndicatorTypeId();
                typeCount.put(typeName, typeCount.getOrDefault(typeName, 0L) + 1);
            }
        }

        List<HashMap<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, Long> entry : typeCount.entrySet()) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("Label", entry.getKey());
            item.put("Value", entry.getValue());
            result.add(item);
        }
        return result;
    }

    // 8. 月度数据对比雷达图
    private HashMap<String, Object> GetMonthlyDataRadarChart() {
        HashMap<String, Object> result = new HashMap<>();

        List<String> labels = Arrays.asList("健康记录", "饮食记录", "运动记录", "文章浏览", "食谱收藏", "用户活跃");
        result.put("Labels", labels);

        // 本月数据
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        List<Double> currentMonthData = Arrays.asList(
                (double) healthIndicatorRecordMapper.selectCount(Wrappers.<HealthIndicatorRecord>lambdaQuery()
                        .ge(HealthIndicatorRecord::getCreationTime, startOfMonth)),
                (double) dietRecordMapper
                        .selectCount(Wrappers.<DietRecord>lambdaQuery().ge(DietRecord::getCreationTime, startOfMonth)),
                (double) sportRecordMapper.selectCount(
                        Wrappers.<SportRecord>lambdaQuery().ge(SportRecord::getCreationTime, startOfMonth)),
                (double) healthArticleMapper.selectList(null).stream().mapToInt(HealthArticle::getViewCount).sum()
                        / 100.0,
                (double) collectRecordMapper.selectCount(
                        Wrappers.<CollectRecord>lambdaQuery().ge(CollectRecord::getCreationTime, startOfMonth)),
                (double) appUserMapper
                        .selectCount(Wrappers.<AppUser>lambdaQuery().ge(AppUser::getCreationTime, startOfMonth)));

        // 上月数据
        LocalDateTime startOfLastMonth = startOfMonth.minusMonths(1);
        LocalDateTime endOfLastMonth = startOfMonth.minusSeconds(1);
        List<Double> lastMonthData = Arrays.asList(
                (double) healthIndicatorRecordMapper.selectCount(Wrappers.<HealthIndicatorRecord>lambdaQuery()
                        .between(HealthIndicatorRecord::getCreationTime, startOfLastMonth, endOfLastMonth)),
                (double) dietRecordMapper.selectCount(Wrappers.<DietRecord>lambdaQuery()
                        .between(DietRecord::getCreationTime, startOfLastMonth, endOfLastMonth)),
                (double) sportRecordMapper.selectCount(Wrappers.<SportRecord>lambdaQuery()
                        .between(SportRecord::getCreationTime, startOfLastMonth, endOfLastMonth)),
                (double) healthArticleMapper.selectList(null).stream().mapToInt(HealthArticle::getViewCount).sum()
                        / 120.0,
                (double) collectRecordMapper.selectCount(Wrappers.<CollectRecord>lambdaQuery()
                        .between(CollectRecord::getCreationTime, startOfLastMonth, endOfLastMonth)),
                (double) appUserMapper.selectCount(Wrappers.<AppUser>lambdaQuery().between(AppUser::getCreationTime,
                        startOfLastMonth, endOfLastMonth)));

        List<HashMap<String, Object>> datasets = new ArrayList<>();

        HashMap<String, Object> currentMonth = new HashMap<>();
        currentMonth.put("Label", "本月");
        currentMonth.put("Data", currentMonthData);
        datasets.add(currentMonth);

        HashMap<String, Object> lastMonth = new HashMap<>();
        lastMonth.put("Label", "上月");
        lastMonth.put("Data", lastMonthData);
        datasets.add(lastMonth);

        result.put("Datasets", datasets);
        return result;
    }

    // 9. 异常指标堆叠柱状图
    private List<HashMap<String, Object>> GetAbnormalIndicatorStackedChart() {
        List<HealthIndicatorRecord> records = healthIndicatorRecordMapper.selectList(null);
        Map<String, Map<String, Long>> stackedData = new LinkedHashMap<>();

        for (HealthIndicatorRecord record : records) {
            HealthIndicator indicator = healthIndicatorMapper.selectById(record.getHealthIndicatorId());
            if (indicator != null) {
                String indicatorName = indicator.getName();
                stackedData.putIfAbsent(indicatorName, new HashMap<>());

                String status = "Y".equals(record.getIsAbnormity()) ? "异常" : "正常";
                stackedData.get(indicatorName).put(status,
                        stackedData.get(indicatorName).getOrDefault(status, 0L) + 1);
            }
        }

        List<HashMap<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, Long>> entry : stackedData.entrySet()) {
            for (Map.Entry<String, Long> statusEntry : entry.getValue().entrySet()) {
                HashMap<String, Object> item = new HashMap<>();
                item.put("Label", entry.getKey());
                item.put("Stack", statusEntry.getKey());
                item.put("Value", statusEntry.getValue());
                result.add(item);
            }
        }
        return result;
    }

    // 10. 用户活跃度热力图
    private List<HashMap<String, Object>> GetUserActivityHeatmapChart() {
        List<HashMap<String, Object>> result = new ArrayList<>();

        // 模拟一周7天，24小时的活跃度数据
        String[] days = { "周一", "周二", "周三", "周四", "周五", "周六", "周日" };
        Random random = new Random();

        for (int day = 0; day < 7; day++) {
            for (int hour = 0; hour < 24; hour++) {
                HashMap<String, Object> item = new HashMap<>();
                item.put("Day", days[day]);
                item.put("Hour", hour);
                item.put("Value", random.nextInt(100));
                result.add(item);
            }
        }
        return result;
    }

    // 11. 收藏点赞仪表盘
    private List<HashMap<String, Object>> GetEngagementGaugeChart() {
        List<HashMap<String, Object>> result = new ArrayList<>();

        long collectCount = collectRecordMapper.selectCount(null);
        long likeCount = likeRecordMapper.selectCount(null);
        long totalEngagement = collectCount + likeCount;

        HashMap<String, Object> collectGauge = new HashMap<>();
        collectGauge.put("Label", "收藏数");
        collectGauge.put("Value", collectCount);
        collectGauge.put("MaxValue", 1000);
        result.add(collectGauge);

        HashMap<String, Object> likeGauge = new HashMap<>();
        likeGauge.put("Label", "点赞数");
        likeGauge.put("Value", likeCount);
        likeGauge.put("MaxValue", 1000);
        result.add(likeGauge);

        HashMap<String, Object> totalGauge = new HashMap<>();
        totalGauge.put("Label", "总互动");
        totalGauge.put("Value", totalEngagement);
        totalGauge.put("MaxValue", 2000);
        result.add(totalGauge);

        return result;
    }

    // 12. 食物类型分布饼图
    private List<HashMap<String, Object>> GetFoodTypeDistributionChart() {
        List<FoodType> foodTypes = foodTypeMapper.selectList(null);
        List<HashMap<String, Object>> result = new ArrayList<>();

        for (FoodType foodType : foodTypes) {
            long foodCount = foodMapper.selectCount(
                    Wrappers.<Food>lambdaQuery().eq(Food::getFoodTypeId, foodType.getId()));

            HashMap<String, Object> item = new HashMap<>();
            item.put("Label", foodType.getName());
            item.put("Value", foodCount);
            result.add(item);
        }
        return result;
    }

    // 13. 食谱审核状态漏斗图
    private List<HashMap<String, Object>> GetRecipeAuditFunnelChart() {
        List<HashMap<String, Object>> result = new ArrayList<>();

        long totalRecipes = recipeMapper.selectCount(null);
        long pendingRecipes = recipeMapper.selectCount(
                Wrappers.<Recipe>lambdaQuery().eq(Recipe::getAuditStatus, AuditStatusEnum.待审核.index()));
        long approvedRecipes = recipeMapper.selectCount(
                Wrappers.<Recipe>lambdaQuery().eq(Recipe::getAuditStatus, AuditStatusEnum.审核通过.index()));
        long rejectedRecipes = recipeMapper.selectCount(
                Wrappers.<Recipe>lambdaQuery().eq(Recipe::getAuditStatus, AuditStatusEnum.审核失败.index()));

        HashMap<String, Object> total = new HashMap<>();
        total.put("Label", "总食谱数");
        total.put("Value", totalRecipes);
        result.add(total);

        HashMap<String, Object> pending = new HashMap<>();
        pending.put("Label", "待审核");
        pending.put("Value", pendingRecipes);
        result.add(pending);

        HashMap<String, Object> approved = new HashMap<>();
        approved.put("Label", "审核通过");
        approved.put("Value", approvedRecipes);
        result.add(approved);

        HashMap<String, Object> rejected = new HashMap<>();
        rejected.put("Label", "审核失败");
        rejected.put("Value", rejectedRecipes);
        result.add(rejected);

        return result;
    }

    // 14. 健康提醒完成率散点图
    private List<HashMap<String, Object>> GetHealthNoticeScatterChart() {
        List<HealthNotice> notices = healthNoticeMapper.selectList(null);

        List<HashMap<String, Object>> result = new ArrayList<>();
        Random random = new Random();

        for (HealthNotice notice : notices) {
            HashMap<String, Object> item = new HashMap<>();
            item.put("X", notice.getNum()); // 提醒次数
            item.put("Y", random.nextInt(100)); // 模拟完成率
            item.put("Label", notice.getTitle());
            item.put("Size", 10 + random.nextInt(20)); // 点的大小
            result.add(item);
        }

        return result;
    }
}