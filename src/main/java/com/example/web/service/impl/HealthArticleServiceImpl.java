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
 * 健康知识功能实现类
 */
@Service
public class HealthArticleServiceImpl extends ServiceImpl<HealthArticleMapper, HealthArticle>
        implements HealthArticleService {

    /**
     * 操作数据库AppUser表mapper对象
     */
    @Autowired
    private AppUserMapper AppUserMapper;
    /**
     * 操作数据库的HealthArticle表mapper对象
     */
    @Autowired
    private HealthArticleMapper HealthArticleMapper;
    @Autowired
    private HealthArticleTypeMapper HealthArticleTypeMapper;

    @Autowired
    private CollectRecordMapper CollectRecordMapper;

    @Autowired
    private LikeRecordMapper LikeRecordMapper;

    @Autowired
    private TextSimilarityUtil textSimilarityUtil;

    /**
     * 构建表查询sql
     */
    private LambdaQueryWrapper<HealthArticle> BuilderQuery(HealthArticlePagedInput input) {
        // 声明一个支持健康知识查询的(拉姆达)表达式
        LambdaQueryWrapper<HealthArticle> queryWrapper = Wrappers.<HealthArticle>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, HealthArticle::getId, input.getId());
        // 如果前端搜索传入查询条件则拼接查询条件
        if (Extension.isNotNullOrEmpty(input.getTitle())) {
            queryWrapper = queryWrapper.like(HealthArticle::getTitle, input.getTitle());
        }

        if (input.getHealthArticleTypeId() != null) {
            queryWrapper = queryWrapper.eq(HealthArticle::getHealthArticleTypeId, input.getHealthArticleTypeId());
        }

        if (input.getPublishUserId() != null) {
            queryWrapper = queryWrapper.eq(HealthArticle::getPublishUserId, input.getPublishUserId());
        }

        if (input.getAuditStatus() != null) {
            queryWrapper = queryWrapper.eq(HealthArticle::getAuditStatus, input.getAuditStatus());
        }

        if (input.getAuditUserId() != null) {
            queryWrapper = queryWrapper.eq(HealthArticle::getAuditUserId, input.getAuditUserId());
        }
        if (input.getAuditTimeRange() != null && !input.getAuditTimeRange().isEmpty()) {
            queryWrapper = queryWrapper.le(HealthArticle::getAuditTime, input.getAuditTimeRange().get(1));
            queryWrapper = queryWrapper.ge(HealthArticle::getAuditTime, input.getAuditTimeRange().get(0));
        }
        if (Extension.isNotNullOrEmpty(input.getContent())) {
            queryWrapper = queryWrapper.like(HealthArticle::getContent, input.getContent());
        }

        if (Extension.isNotNullOrEmpty(input.getKeyWord())) {
            queryWrapper = queryWrapper.and(i -> i
                    .like(HealthArticle::getTitle, input.getKeyWord()).or()
                    .like(HealthArticle::getContent, input.getKeyWord()).or());

        }

        return queryWrapper;
    }

    /**
     * 处理健康知识对于的外键数据
     */
    private List<HealthArticleDto> DispatchItem(List<HealthArticleDto> items)
            throws InvocationTargetException, IllegalAccessException {

        for (HealthArticleDto item : items) {

            // 查询出关联的AppUser表信息
            AppUser PublishUserEntity = AppUserMapper.selectById(item.getPublishUserId());
            item.setPublishUserDto(PublishUserEntity != null ? PublishUserEntity.MapToDto() : new AppUserDto());

            // 查询出关联的AppUser表信息
            AppUser AuditUserEntity = AppUserMapper.selectById(item.getAuditUserId());
            item.setAuditUserDto(AuditUserEntity != null ? AuditUserEntity.MapToDto() : new AppUserDto());

            // 查询出关联的HealthArticleType表信息
            HealthArticleType HealthArticleTypeEntity = HealthArticleTypeMapper
                    .selectById(item.getHealthArticleTypeId());
            item.setHealthArticleTypeDto(
                    HealthArticleTypeEntity != null ? HealthArticleTypeEntity.MapToDto() : new HealthArticleTypeDto());
        }

        return items;
    }

    /**
     * 健康知识分页查询
     */
    @SneakyThrows
    @Override
    public PagedResult<HealthArticleDto> List(HealthArticlePagedInput input) {
        // 构建where条件+排序
        LambdaQueryWrapper<HealthArticle> queryWrapper = BuilderQuery(input);
        // 动态排序处理
        if (input.getSortItem() != null) {
            // 根据字段名动态排序
            queryWrapper.last("ORDER BY " + input.getSortItem().getFieldName()
                    + (input.getSortItem().getIsAsc() ? " ASC" : " DESC"));
        } else {
            // 默认按创建时间从大到小排序
            queryWrapper = queryWrapper.orderByDesc(HealthArticle::getCreationTime);
        }

        // 构建一个分页查询的model
        Page<HealthArticle> page = new Page<>(input.getPage(), input.getLimit());
        // 从数据库进行分页查询获取健康知识数据
        IPage<HealthArticle> pageRecords = HealthArticleMapper.selectPage(page, queryWrapper);
        // 获取所有满足条件的数据行数
        Long totalCount = HealthArticleMapper.selectCount(queryWrapper);
        // 把HealthArticle实体转换成HealthArticle传输模型
        List<HealthArticleDto> items = Extension.copyBeanList(pageRecords.getRecords(), HealthArticleDto.class);

        DispatchItem(items);
        // 返回一个分页结构给前端
        return PagedResult.GetInstance(items, totalCount);

    }

    /**
     * 单个健康知识查询
     */
    @SneakyThrows
    @Override
    public HealthArticleDto Get(HealthArticlePagedInput input) {
        if (input.getId() == null) {
            return new HealthArticleDto();
        }

        PagedResult<HealthArticleDto> pagedResult = List(input);
        return pagedResult.getItems().stream().findFirst().orElse(new HealthArticleDto());
    }

    /**
     * 健康知识创建或者修改
     */
    @SneakyThrows
    @Override
    public HealthArticleDto CreateOrEdit(HealthArticleDto input) {
        // 声明一个健康知识实体
        HealthArticle HealthArticle = input.MapToEntity();
        // 调用数据库的增加或者修改方法
        saveOrUpdate(HealthArticle);
        // 把传输模型返回给前端
        return HealthArticle.MapToDto();
    }

    /**
     * 健康知识删除
     */
    @Override
    public void Delete(IdInput input) {
        HealthArticle entity = HealthArticleMapper.selectById(input.getId());
        HealthArticleMapper.deleteById(entity);
    }

    /**
     * 健康知识批量删除
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
     * 健康知识审核
     */
    @Override
    public void Audit(HealthArticleDto input) {
        HealthArticle entity = HealthArticleMapper.selectById(input.getId());
        entity.setAuditStatus(input.getAuditStatus());
        entity.setAuditReply(input.getAuditReply());
        entity.setAuditTime(LocalDateTime.now());
        entity.setAuditUserId(BaseContext.getCurrentUserDto().getUserId());
        HealthArticleMapper.updateById(entity);
    }

    /**
     * 浏览次数增加
     */
    @Override
    public void AddViewCount(HealthArticleDto input) {
        HealthArticle entity = HealthArticleMapper.selectById(input.getId());
        if (entity.getViewCount() == null) {
            entity.setViewCount(0);
        }
        entity.setViewCount(entity.getViewCount() + 1);
        HealthArticleMapper.updateById(entity);
    }

    /**
     * 推荐算法(基于行为+权重的协同过滤算法)
     */
    @SneakyThrows
    @Override
    public List<HealthArticleDto> RecommendList(HealthArticlePagedInput input) {
        Integer userId = BaseContext.getCurrentUserDto().getUserId();
        List<HealthArticleDto> recommendedArticles = new ArrayList<>();

        try {
            // 判断用户是否登录
            if (userId == null) {
                // 使用基于内容推荐的算法
                recommendedArticles = contentBasedRecommendation(input);
            } else {
                // 基于用户的协同过滤算法
                recommendedArticles = collaborativeFilteringRecommendation(userId, input);
            }
        } catch (Exception e) {
            // 出现异常时，返回热门文章作为备选推荐
            recommendedArticles = getPopularArticles(input);
        }

        return recommendedArticles;
    }

    /**
     * 基于内容的推荐算法（未登录用户）
     * 结合热门度和内容相似性进行推荐
     */
    private List<HealthArticleDto> contentBasedRecommendation(HealthArticlePagedInput input)
            throws InvocationTargetException, IllegalAccessException {

        int limit = input.getLimit() != null ? input.getLimit().intValue() : 10;

        // 如果提供了文章ID，基于该文章内容进行相似推荐
        if (input.getId() != null && input.getId() != 0) {
            return contentBasedRecommendationById(input.getId(), limit);
        }

        // 否则推荐热门文章
        return getPopularArticlesByLimit(limit);
    }

    /**
     * 基于指定文章ID的内容推荐
     */
    private List<HealthArticleDto> contentBasedRecommendationById(Integer articleId, int limit)
            throws InvocationTargetException, IllegalAccessException {

        // 查询目标文章
        HealthArticle targetArticle = HealthArticleMapper.selectById(articleId);
        if (targetArticle == null) {
            return getPopularArticlesByLimit(limit);
        }

        // 获取所有已审核通过的文章（除了目标文章）
        LambdaQueryWrapper<HealthArticle> queryWrapper = Wrappers.<HealthArticle>lambdaQuery()
                .eq(HealthArticle::getAuditStatus, AuditStatusEnum.审核通过.index())
                .ne(HealthArticle::getId, articleId); // 排除目标文章本身

        List<HealthArticle> candidateArticles = HealthArticleMapper.selectList(queryWrapper);

        if (candidateArticles.isEmpty()) {
            return new ArrayList<>();
        }

        // 准备目标文章的内容
        String targetText = (targetArticle.getTitle() != null ? targetArticle.getTitle() : "") + " " +
                (targetArticle.getContent() != null ? targetArticle.getContent() : "");

        // 准备候选文章的内容
        List<String> candidateTexts = candidateArticles.stream()
                .map(article -> (article.getTitle() != null ? article.getTitle() : "") + " " +
                        (article.getContent() != null ? article.getContent() : ""))
                .collect(Collectors.toList());

        // 计算文本相似度
        List<TextSimilarityUtil.TextSimilarityResult> similarityResults = textSimilarityUtil
                .calculateBatchSimilarity(targetText, candidateTexts);

        // 获取最相似的文章
        List<HealthArticleDto> recommendedArticles = new ArrayList<>();
        int count = 0;

        for (TextSimilarityUtil.TextSimilarityResult result : similarityResults) {
            if (count >= limit) {
                break;
            }

            // 只推荐相似度大于阈值的文章
            if (result.getSimilarity() > 0.1) {
                HealthArticle article = candidateArticles.get(result.getIndex());
                HealthArticleDto articleDto = Extension.copyBeanList(Arrays.asList(article), HealthArticleDto.class)
                        .get(0);
                articleDto.setSimilarity(result.getSimilarity());
                recommendedArticles.add(articleDto);
                count++;
            }
        }

        // 如果基于相似度的推荐结果不足，用热门文章补充
        if (recommendedArticles.size() < limit) {
            List<HealthArticleDto> popularArticles = getPopularArticlesByLimit(limit - recommendedArticles.size());

            // 避免重复推荐
            Set<Integer> existingIds = recommendedArticles.stream()
                    .map(HealthArticleDto::getId)
                    .collect(Collectors.toSet());
            existingIds.add(articleId); // 也要排除目标文章

            for (HealthArticleDto popular : popularArticles) {
                if (!existingIds.contains(popular.getId()) && recommendedArticles.size() < limit) {
                    recommendedArticles.add(popular);
                }
            }
        }

        // 处理关联数据
        DispatchItem(recommendedArticles);

        return recommendedArticles;
    }

    /**
     * 获取指定数量的热门文章
     */
    private List<HealthArticleDto> getPopularArticlesByLimit(int limit)
            throws InvocationTargetException, IllegalAccessException {

        LambdaQueryWrapper<HealthArticle> queryWrapper = Wrappers.<HealthArticle>lambdaQuery()
                .eq(HealthArticle::getAuditStatus, AuditStatusEnum.审核通过.index())
                .orderByDesc(HealthArticle::getViewCount)
                .orderByDesc(HealthArticle::getCreationTime)
                .last("LIMIT " + limit);

        List<HealthArticle> articles = HealthArticleMapper.selectList(queryWrapper);
        return Extension.copyBeanList(articles, HealthArticleDto.class);
    }

    /**
     * 基于用户协同过滤的推荐算法（已登录用户）
     * 根据用户的点赞和收藏行为，找到相似用户，推荐相似用户喜欢的内容
     */
    private List<HealthArticleDto> collaborativeFilteringRecommendation(Integer userId, HealthArticlePagedInput input)
            throws InvocationTargetException, IllegalAccessException {

        // 获取用户行为数据（点赞和收藏）
        List<Integer> userLikedArticles = getUserLikedArticles(userId);
        List<Integer> userCollectedArticles = getUserCollectedArticles(userId);

        // 如果用户没有任何行为数据，使用基于内容的推荐
        if (userLikedArticles.isEmpty() && userCollectedArticles.isEmpty()) {
            return contentBasedRecommendation(input);
        }

        // 计算用户相似度并获取推荐文章
        List<Integer> recommendedArticleIds = calculateUserSimilarityAndRecommend(userId, userLikedArticles,
                userCollectedArticles);

        if (recommendedArticleIds.isEmpty()) {
            // 如果没有推荐结果，返回热门文章
            return getPopularArticles(input);
        }

        // 限制推荐数量
        int limit = input.getLimit() != null ? input.getLimit().intValue() : 10;
        List<Integer> finalRecommendedArticleIds = recommendedArticleIds.subList(0,
                Math.min(recommendedArticleIds.size(), limit));

        // 根据推荐的文章ID获取文章详情
        LambdaQueryWrapper<HealthArticle> queryWrapper = Wrappers.<HealthArticle>lambdaQuery()
                .in(HealthArticle::getId, finalRecommendedArticleIds)
                .eq(HealthArticle::getAuditStatus, AuditStatusEnum.审核通过.index());

        List<HealthArticle> articles = HealthArticleMapper.selectList(queryWrapper);
        List<HealthArticleDto> articleDtos = Extension.copyBeanList(articles, HealthArticleDto.class);

        // 处理关联数据
        DispatchItem(articleDtos);

        // 按推荐分数排序（这里简化处理，按ID在推荐列表中的顺序）
        articleDtos.sort((a, b) -> {
            int indexA = finalRecommendedArticleIds.indexOf(a.getId());
            int indexB = finalRecommendedArticleIds.indexOf(b.getId());
            return Integer.compare(indexA, indexB);
        });

        return articleDtos;
    }

    /**
     * 获取用户点赞的文章列表
     */
    private List<Integer> getUserLikedArticles(Integer userId) {
        LambdaQueryWrapper<LikeRecord> queryWrapper = Wrappers.<LikeRecord>lambdaQuery()
                .eq(LikeRecord::getLikeUserId, userId)
                .eq(LikeRecord::getLikeType, "健康知识");

        List<LikeRecord> likeRecords = LikeRecordMapper.selectList(queryWrapper);
        return likeRecords.stream()
                .map(record -> Integer.parseInt(record.getRelativeId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取用户收藏的文章列表
     */
    private List<Integer> getUserCollectedArticles(Integer userId) {
        LambdaQueryWrapper<CollectRecord> queryWrapper = Wrappers.<CollectRecord>lambdaQuery()
                .eq(CollectRecord::getCollectUserId, userId)
                .eq(CollectRecord::getCollectType, "健康知识");

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

        // 根据相似度推荐文章
        return generateRecommendations(targetUserId, targetUserLikes, targetUserCollects, userSimilarities,
                allUserLikes, allUserCollects);
    }

    /**
     * 计算加权余弦相似度
     * 点赞权重：1.0，收藏权重：2.0
     */
    private double calculateWeightedCosineSimilarity(List<Integer> user1Likes, List<Integer> user1Collects,
            List<Integer> user2Likes, List<Integer> user2Collects) {

        // 获取所有涉及的文章ID
        Set<Integer> allArticles = new HashSet<>();
        allArticles.addAll(user1Likes);
        allArticles.addAll(user1Collects);
        allArticles.addAll(user2Likes);
        allArticles.addAll(user2Collects);

        if (allArticles.isEmpty()) {
            return 0.0;
        }

        // 计算向量
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (Integer articleId : allArticles) {
            // 用户1的权重分数
            double score1 = 0.0;
            if (user1Likes.contains(articleId))
                score1 += 1.0; // 点赞权重
            if (user1Collects.contains(articleId))
                score1 += 2.0; // 收藏权重

            // 用户2的权重分数
            double score2 = 0.0;
            if (user2Likes.contains(articleId))
                score2 += 1.0; // 点赞权重
            if (user2Collects.contains(articleId))
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

        // 目标用户已经交互过的文章
        Set<Integer> targetUserInteracted = new HashSet<>();
        targetUserInteracted.addAll(targetUserLikes);
        targetUserInteracted.addAll(targetUserCollects);

        // 计算每篇文章的推荐分数
        Map<Integer, Double> articleScores = new HashMap<>();

        for (Map.Entry<Integer, Double> entry : userSimilarities.entrySet()) {
            Integer similarUserId = entry.getKey();
            Double similarity = entry.getValue();

            List<Integer> similarUserLikes = allUserLikes.get(similarUserId);
            List<Integer> similarUserCollects = allUserCollects.getOrDefault(similarUserId, new ArrayList<>());

            // 为相似用户喜欢的文章加分
            for (Integer articleId : similarUserLikes) {
                if (!targetUserInteracted.contains(articleId)) {
                    articleScores.put(articleId,
                            articleScores.getOrDefault(articleId, 0.0) + similarity * 1.0); // 点赞权重
                }
            }

            for (Integer articleId : similarUserCollects) {
                if (!targetUserInteracted.contains(articleId)) {
                    articleScores.put(articleId,
                            articleScores.getOrDefault(articleId, 0.0) + similarity * 2.0); // 收藏权重
                }
            }
        }

        // 按分数排序并返回推荐列表
        return articleScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有用户的点赞数据
     */
    private Map<Integer, List<Integer>> getAllUserLikes() {
        LambdaQueryWrapper<LikeRecord> queryWrapper = Wrappers.<LikeRecord>lambdaQuery()
                .eq(LikeRecord::getLikeType, "健康知识");

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
                .eq(CollectRecord::getCollectType, "健康知识");

        List<CollectRecord> allCollects = CollectRecordMapper.selectList(queryWrapper);

        return allCollects.stream()
                .collect(Collectors.groupingBy(
                        CollectRecord::getCollectUserId,
                        Collectors.mapping(CollectRecord::getRelativeId, Collectors.toList())));
    }

    /**
     * 获取热门文章作为备选推荐
     */
    private List<HealthArticleDto> getPopularArticles(HealthArticlePagedInput input)
            throws InvocationTargetException, IllegalAccessException {

        LambdaQueryWrapper<HealthArticle> queryWrapper = Wrappers.<HealthArticle>lambdaQuery()
                .eq(HealthArticle::getAuditStatus, AuditStatusEnum.审核通过.index())
                .orderByDesc(HealthArticle::getViewCount)
                .orderByDesc(HealthArticle::getCreationTime)
                .last("LIMIT " + (input.getLimit() != null ? input.getLimit() : 10));

        List<HealthArticle> articles = HealthArticleMapper.selectList(queryWrapper);
        List<HealthArticleDto> articleDtos = Extension.copyBeanList(articles, HealthArticleDto.class);

        DispatchItem(articleDtos);

        return articleDtos;
    }
}
