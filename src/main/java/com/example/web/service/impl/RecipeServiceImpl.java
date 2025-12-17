package com.example.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.web.SysConst;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.mapper.*;
import com.example.web.enums.*;
import com.example.web.service.*;
import com.example.web.tools.dto.*;
import com.example.web.tools.exception.CustomException;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import lombok.SneakyThrows;
import java.io.IOException;
import com.example.web.tools.*;
import com.example.web.tools.TextSimilarityUtil;
import java.text.DecimalFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;

/**
 * 食谱功能实现类
 */
@Service
public class RecipeServiceImpl extends ServiceImpl<RecipeMapper, Recipe> implements RecipeService {

    /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的Recipe表mapper对象
     */
    @Autowired
    private RecipeMapper RecipeMapper;

    @Autowired
    private CollectRecordMapper CollectRecordMapper;

    @Autowired
    private LikeRecordMapper LikeRecordMapper;

    @Autowired
    private TextSimilarityUtil textSimilarityUtil;

    /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<Recipe> BuilderQuery(RecipePagedInput input) {
        // 声明一个支持食谱查询的(拉姆达)表达式
        LambdaQueryWrapper<Recipe> queryWrapper = Wrappers.<Recipe>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, Recipe::getId, input.getId());
        // 如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getTitle())) {
            queryWrapper = queryWrapper.like(Recipe::getTitle, input.getTitle());
        }

        if (input.getAuditUserId() != null) {
            queryWrapper = queryWrapper.eq(Recipe::getAuditUserId, input.getAuditUserId());
        }

        if (input.getPublishUserId() != null) {
            queryWrapper = queryWrapper.eq(Recipe::getPublishUserId, input.getPublishUserId());
        }

        if (input.getAuditStatus() != null) {
            queryWrapper = queryWrapper.eq(Recipe::getAuditStatus, input.getAuditStatus());
        }
        if (input.getAuditTimeRange() != null && !input.getAuditTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(Recipe::getAuditTime, input.getAuditTimeRange().get(1));
            queryWrapper = queryWrapper.ge(Recipe::getAuditTime, input.getAuditTimeRange().get(0));
        }
        if (Extension.isNotNullOrEmpty(input.getContent())) {
            queryWrapper = queryWrapper.like(Recipe::getContent, input.getContent());
        }

        if (Extension.isNotNullOrEmpty(input.getKeyWord())) {
            queryWrapper = queryWrapper.and(i -> i
                    .like(Recipe::getTitle, input.getKeyWord()).or()
                    .like(Recipe::getContent, input.getKeyWord()).or());

        }

        return queryWrapper;
    }

    /**
     * 处理食谱对于的外键数据
     */
    private List<RecipeDto> DispatchItem(List<RecipeDto> items)
            throws InvocationTargetException, IllegalAccessException {

        for (RecipeDto item : items) {

            // 查询出关联的AppUser表信息
            AppUser PublishUserEntity = AppUserMapper.selectById(item.getPublishUserId());
            item.setPublishUserDto(PublishUserEntity != null ? PublishUserEntity.MapToDto() : new AppUserDto());

            // 查询出关联的AppUser表信息
            AppUser AuditUserEntity = AppUserMapper.selectById(item.getAuditUserId());
            item.setAuditUserDto(AuditUserEntity != null ? AuditUserEntity.MapToDto() : new AppUserDto());
        }

        return items;
    }

    /**
     * 食谱分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<RecipeDto> List(RecipePagedInput input) {
        // 构建where条件+排序
        LambdaQueryWrapper<Recipe> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(Recipe::getCreationTime);
        }

        // 构建一个分页查询的model
        Page<Recipe> page = new Page<>(input.getPage(), input.getLimit());
        // 从数据库进行分页查询获取食谱数据
        IPage<Recipe> pageRecords = RecipeMapper.selectPage(page, queryWrapper);
        // 获取所有满足条件的数据行数
        Long totalCount = RecipeMapper.selectCount(queryWrapper);
        // 把Recipe实体转换成Recipe传输模型
        List<RecipeDto> items = Extension.copyBeanList(pageRecords.getRecords(), RecipeDto.class);

        DispatchItem(items);
        // 返回一个分页结构给前端
        return PagedResult.GetInstance(items, totalCount);

    }

    /**
     * 单个食谱查询
     */
    @SneakyThrows
    @Override
    public RecipeDto Get(RecipePagedInput input) {
        if (input.getId() == null) {
            return new RecipeDto();
        }

        PagedResult<RecipeDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new RecipeDto());
    }

    /**
     * 食谱创建或者修改
     */
    @SneakyThrows
    @Override
    public RecipeDto CreateOrEdit(RecipeDto input) {
        // 声明一个食谱实体
        Recipe Recipe = input.MapToEntity();
        // 调用数据库的增加或者修改方法
        saveOrUpdate(Recipe);
        // 把传输模型返回给前端
        return Recipe.MapToDto();
    }

    /**
     * 食谱删除
     */
    @Override
    public void Delete(IdInput input) {
        Recipe entity = RecipeMapper.selectById(input.getId());
        RecipeMapper.deleteById(entity);
    }

    /**
     * 食谱批量删除
     */
    @Override
    public void BatchDelete(IdsInput input) {
        for (Integer id : input.getIds()) {
            IdInput idInput = new IdInput();
            idInput.setId(id);
            Delete(idInput);
        }
    }

    /**
     * 食谱审核
     */
    @Override
    public void Audit(RecipeDto input) {
        Recipe entity = RecipeMapper.selectById(input.getId());
        entity.setAuditStatus(input.getAuditStatus());
        entity.setAuditTime(LocalDateTime.now());
        entity.setAuditUserId(input.getAuditUserId());
        entity.setAuditReply(input.getAuditReply());
        RecipeMapper.updateById(entity);
    }

    /**
     * 食谱浏览次数增加
     */
    @Override
    public void AddViewCount(RecipeDto input) {
        Recipe entity = RecipeMapper.selectById(input.getId());
        entity.setViewCount(entity.getViewCount() + 1);
        RecipeMapper.updateById(entity);
    }

    /**
     * 推荐算法(基于行为+权重的协同过滤算法)
     */
    @SneakyThrows
    @Override
    public List<RecipeDto> RecommendList(RecipePagedInput input) {
        Integer userId = BaseContext.getCurrentUserDto().getUserId();
        List<RecipeDto> recommendedRecipes = new ArrayList<>();

        try {
            // 判断用户是否登录
            if (userId == null) {
                // 使用基于内容推荐的算法
                recommendedRecipes = contentBasedRecommendation(input);
            } else {
                // 基于用户的协同过滤算法
                recommendedRecipes = collaborativeFilteringRecommendation(userId, input);
            }
        } catch (Exception e) {
            // 出现异常时，返回热门食谱作为备选推荐
            recommendedRecipes = getPopularRecipes(input);
        }

        return recommendedRecipes;
    }

    /**
     * 基于内容的推荐算法（未登录用户）
     * 结合热门度和内容相似性进行推荐
     */
    private List<RecipeDto> contentBasedRecommendation(RecipePagedInput input)
            throws InvocationTargetException, IllegalAccessException {

        int limit = input.getLimit() != null ? input.getLimit().intValue() : 10;

        // 如果提供了食谱ID，基于该食谱内容进行相似推荐
        if (input.getId() != null && input.getId() != 0) {
            return contentBasedRecommendationById(input.getId(), limit);
        }

        // 否则推荐热门食谱
        return getPopularRecipesByLimit(limit);
    }

    /**
     * 基于指定食谱ID的内容推荐
     */
    private List<RecipeDto> contentBasedRecommendationById(Integer recipeId, int limit)
            throws InvocationTargetException, IllegalAccessException {

        // 查询目标食谱
        Recipe targetRecipe = RecipeMapper.selectById(recipeId);
        if (targetRecipe == null) {
            return getPopularRecipesByLimit(limit);
        }

        // 获取所有已审核通过的食谱（除了目标食谱）
        LambdaQueryWrapper<Recipe> queryWrapper = Wrappers.<Recipe>lambdaQuery()
                .eq(Recipe::getAuditStatus, AuditStatusEnum.审核通过.index())
                .ne(Recipe::getId, recipeId); // 排除目标食谱本身

        List<Recipe> candidateRecipes = RecipeMapper.selectList(queryWrapper);

        if (candidateRecipes.isEmpty()) {
            return new ArrayList<>();
        }

        // 准备目标食谱的内容
        String targetText = (targetRecipe.getTitle() != null ? targetRecipe.getTitle() : "") + " " +
                (targetRecipe.getContent() != null ? targetRecipe.getContent() : "");

        // 准备候选食谱的内容
        List<String> candidateTexts = candidateRecipes.stream()
                .map(recipe -> (recipe.getTitle() != null ? recipe.getTitle() : "") + " " +
                        (recipe.getContent() != null ? recipe.getContent() : ""))
                .collect(Collectors.toList());

        // 计算文本相似度
        List<TextSimilarityUtil.TextSimilarityResult> similarityResults = textSimilarityUtil
                .calculateBatchSimilarity(targetText, candidateTexts);

        // 获取最相似的食谱
        List<RecipeDto> recommendedRecipes = new ArrayList<>();
        int count = 0;

        for (TextSimilarityUtil.TextSimilarityResult result : similarityResults) {
            if (count >= limit) {
                break;
            }

            // 只推荐相似度大于阈值的食谱
            if (result.getSimilarity() > 0.1) {
                Recipe recipe = candidateRecipes.get(result.getIndex());
                RecipeDto recipeDto = Extension.copyBeanList(Arrays.asList(recipe), RecipeDto.class)
                        .get(0);
                recipeDto.setSimilarity(result.getSimilarity());
                recommendedRecipes.add(recipeDto);
                count++;
            }
        }

        // 如果基于相似度的推荐结果不足，用热门食谱补充
        if (recommendedRecipes.size() < limit) {
            List<RecipeDto> popularRecipes = getPopularRecipesByLimit(limit - recommendedRecipes.size());

            // 避免重复推荐
            Set<Integer> existingIds = recommendedRecipes.stream()
                    .map(RecipeDto::getId)
                    .collect(Collectors.toSet());
            existingIds.add(recipeId); // 也要排除目标食谱

            for (RecipeDto popular : popularRecipes) {
                if (!existingIds.contains(popular.getId()) && recommendedRecipes.size() < limit) {
                    recommendedRecipes.add(popular);
                }
            }
        }

        // 处理关联数据
        DispatchItem(recommendedRecipes);

        return recommendedRecipes;
    }

    /**
     * 获取指定数量的热门食谱
     */
    private List<RecipeDto> getPopularRecipesByLimit(int limit)
            throws InvocationTargetException, IllegalAccessException {

        LambdaQueryWrapper<Recipe> queryWrapper = Wrappers.<Recipe>lambdaQuery()
                .eq(Recipe::getAuditStatus, AuditStatusEnum.审核通过.index())
                .orderByDesc(Recipe::getViewCount)
                .orderByDesc(Recipe::getCreationTime)
                .last("LIMIT " + limit);

        List<Recipe> recipes = RecipeMapper.selectList(queryWrapper);
        return Extension.copyBeanList(recipes, RecipeDto.class);
    }

    /**
     * 基于用户协同过滤的推荐算法（已登录用户）
     * 根据用户的点赞和收藏行为，找到相似用户，推荐相似用户喜欢的内容
     */
    private List<RecipeDto> collaborativeFilteringRecommendation(Integer userId, RecipePagedInput input)
            throws InvocationTargetException, IllegalAccessException {

        // 获取用户行为数据（点赞和收藏）
        List<Integer> userLikedRecipes = getUserLikedRecipes(userId);
        List<Integer> userCollectedRecipes = getUserCollectedRecipes(userId);

        // 如果用户没有任何行为数据，使用基于内容的推荐
        if (userLikedRecipes.isEmpty() && userCollectedRecipes.isEmpty()) {
            return contentBasedRecommendation(input);
        }

        // 计算用户相似度并获取推荐食谱
        List<Integer> recommendedRecipeIds = calculateUserSimilarityAndRecommend(userId, userLikedRecipes,
                userCollectedRecipes);

        if (recommendedRecipeIds.isEmpty()) {
            // 如果没有推荐结果，返回热门食谱
            return getPopularRecipes(input);
        }

        // 限制推荐数量
        int limit = input.getLimit() != null ? input.getLimit().intValue() : 10;
        List<Integer> finalRecommendedRecipeIds = recommendedRecipeIds.subList(0,
                Math.min(recommendedRecipeIds.size(), limit));

        // 根据推荐的食谱ID获取食谱详情
        LambdaQueryWrapper<Recipe> queryWrapper = Wrappers.<Recipe>lambdaQuery()
                .in(Recipe::getId, finalRecommendedRecipeIds)
                .eq(Recipe::getAuditStatus, AuditStatusEnum.审核通过.index());

        List<Recipe> recipes = RecipeMapper.selectList(queryWrapper);
        List<RecipeDto> recipeDtos = Extension.copyBeanList(recipes, RecipeDto.class);

        // 处理关联数据
        DispatchItem(recipeDtos);

        // 按推荐分数排序（这里简化处理，按ID在推荐列表中的顺序）
        recipeDtos.sort((a, b) -> {
            int indexA = finalRecommendedRecipeIds.indexOf(a.getId());
            int indexB = finalRecommendedRecipeIds.indexOf(b.getId());
            return Integer.compare(indexA, indexB);
        });

        return recipeDtos;
    }

    /**
     * 获取用户点赞的食谱列表
     */
    private List<Integer> getUserLikedRecipes(Integer userId) {
        LambdaQueryWrapper<LikeRecord> queryWrapper = Wrappers.<LikeRecord>lambdaQuery()
                .eq(LikeRecord::getLikeUserId, userId)
                .eq(LikeRecord::getLikeType, "食谱");

        List<LikeRecord> likeRecords = LikeRecordMapper.selectList(queryWrapper);
        return likeRecords.stream()
                .map(record -> Integer.parseInt(record.getRelativeId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取用户收藏的食谱列表
     */
    private List<Integer> getUserCollectedRecipes(Integer userId) {
        LambdaQueryWrapper<CollectRecord> queryWrapper = Wrappers.<CollectRecord>lambdaQuery()
                .eq(CollectRecord::getCollectUserId, userId)
                .eq(CollectRecord::getCollectType, "食谱");

        List<CollectRecord> collectRecords = CollectRecordMapper.selectList(queryWrapper);
        return collectRecords.stream()
                .map(CollectRecord::getRelativeId)
                .collect(Collectors.toList());
    }

    /**
     * 计算用户相似度并生成推荐
     * 使用加权的余弦相似度算法，点赞权重为1.0，收藏权重为2.0
     */
    private List<Integer> calculateUserSimilarityAndRecommend(Integer targetUserId,
            List<Integer> targetUserLikes, List<Integer> targetUserCollects) {

        // 获取所有用户的行为数据
        Map<Integer, List<Integer>> allUserLikes = getAllUserLikes();
        Map<Integer, List<Integer>> allUserCollects = getAllUserCollects();

        // 计算目标用户与其他用户的相似度
        Map<Integer, Double> userSimilarities = new HashMap<>();

        for (Integer otherUserId : allUserLikes.keySet()) {
            if (otherUserId.equals(targetUserId)) {
                continue; // 跳过自己
            }

            List<Integer> otherUserLikes = allUserLikes.get(otherUserId);
            List<Integer> otherUserCollects = allUserCollects.getOrDefault(otherUserId, new ArrayList<>());

            // 计算加权余弦相似度
            double similarity = calculateWeightedCosineSimilarity(
                    targetUserLikes, targetUserCollects,
                    otherUserLikes, otherUserCollects);

            if (similarity > 0) {
                userSimilarities.put(otherUserId, similarity);
            }
        }

        // 根据相似度推荐食谱
        return generateRecommendations(targetUserId, targetUserLikes, targetUserCollects, userSimilarities,
                allUserLikes, allUserCollects);
    }

    /**
     * 计算加权余弦相似度
     * 点赞权重：1.0，收藏权重：2.0
     */
    private double calculateWeightedCosineSimilarity(List<Integer> user1Likes, List<Integer> user1Collects,
            List<Integer> user2Likes, List<Integer> user2Collects) {

        // 获取所有涉及的食谱ID
        Set<Integer> allRecipes = new HashSet<>();
        allRecipes.addAll(user1Likes);
        allRecipes.addAll(user1Collects);
        allRecipes.addAll(user2Likes);
        allRecipes.addAll(user2Collects);

        if (allRecipes.isEmpty()) {
            return 0.0;
        }

        // 计算向量
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (Integer recipeId : allRecipes) {
            // 用户1的权重分数
            double score1 = 0.0;
            if (user1Likes.contains(recipeId))
                score1 += 1.0; // 点赞权重
            if (user1Collects.contains(recipeId))
                score1 += 2.0; // 收藏权重

            // 用户2的权重分数
            double score2 = 0.0;
            if (user2Likes.contains(recipeId))
                score2 += 1.0; // 点赞权重
            if (user2Collects.contains(recipeId))
                score2 += 2.0; // 收藏权重

            dotProduct += score1 * score2;
            norm1 += score1 * score1;
            norm2 += score2 * score2;
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * 根据用户相似度生成推荐
     */
    private List<Integer> generateRecommendations(Integer targetUserId,
            List<Integer> targetUserLikes, List<Integer> targetUserCollects,
            Map<Integer, Double> userSimilarities,
            Map<Integer, List<Integer>> allUserLikes,
            Map<Integer, List<Integer>> allUserCollects) {

        // 目标用户已经交互过的食谱
        Set<Integer> targetUserInteracted = new HashSet<>();
        targetUserInteracted.addAll(targetUserLikes);
        targetUserInteracted.addAll(targetUserCollects);

        // 计算每个食谱的推荐分数
        Map<Integer, Double> recipeScores = new HashMap<>();

        for (Map.Entry<Integer, Double> entry : userSimilarities.entrySet()) {
            Integer similarUserId = entry.getKey();
            Double similarity = entry.getValue();

            List<Integer> similarUserLikes = allUserLikes.get(similarUserId);
            List<Integer> similarUserCollects = allUserCollects.getOrDefault(similarUserId, new ArrayList<>());

            // 为相似用户喜欢的食谱加分
            for (Integer recipeId : similarUserLikes) {
                if (!targetUserInteracted.contains(recipeId)) {
                    recipeScores.put(recipeId,
                            recipeScores.getOrDefault(recipeId, 0.0) + similarity * 1.0); // 点赞权重
                }
            }

            for (Integer recipeId : similarUserCollects) {
                if (!targetUserInteracted.contains(recipeId)) {
                    recipeScores.put(recipeId,
                            recipeScores.getOrDefault(recipeId, 0.0) + similarity * 2.0); // 收藏权重
                }
            }
        }

        // 按分数排序并返回推荐列表
        return recipeScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有用户的点赞数据
     */
    private Map<Integer, List<Integer>> getAllUserLikes() {
        LambdaQueryWrapper<LikeRecord> queryWrapper = Wrappers.<LikeRecord>lambdaQuery()
                .eq(LikeRecord::getLikeType, "食谱");

        List<LikeRecord> allLikes = LikeRecordMapper.selectList(queryWrapper);

        return allLikes.stream()
                .collect(Collectors.groupingBy(
                        LikeRecord::getLikeUserId,
                        Collectors.mapping(record -> Integer.parseInt(record.getRelativeId()),
                                Collectors.toList())));
    }

    /**
     * 获取所有用户的收藏数据
     */
    private Map<Integer, List<Integer>> getAllUserCollects() {
        LambdaQueryWrapper<CollectRecord> queryWrapper = Wrappers.<CollectRecord>lambdaQuery()
                .eq(CollectRecord::getCollectType, "食谱");

        List<CollectRecord> allCollects = CollectRecordMapper.selectList(queryWrapper);

        return allCollects.stream()
                .collect(Collectors.groupingBy(
                        CollectRecord::getCollectUserId,
                        Collectors.mapping(CollectRecord::getRelativeId, Collectors.toList())));
    }

    /**
     * 获取热门食谱作为备选推荐
     */
    private List<RecipeDto> getPopularRecipes(RecipePagedInput input)
            throws InvocationTargetException, IllegalAccessException {

        LambdaQueryWrapper<Recipe> queryWrapper = Wrappers.<Recipe>lambdaQuery()
                .eq(Recipe::getAuditStatus, AuditStatusEnum.审核通过.index())
                .orderByDesc(Recipe::getViewCount)
                .orderByDesc(Recipe::getCreationTime)
                .last("LIMIT " + (input.getLimit() != null ? input.getLimit() : 10));

        List<Recipe> recipes = RecipeMapper.selectList(queryWrapper);
        List<RecipeDto> recipeDtos = Extension.copyBeanList(recipes, RecipeDto.class);

        DispatchItem(recipeDtos);

        return recipeDtos;
    }
}
