package com.example.web.tools;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文本相似度计算工具类
 * 基于HanLP分词和TF-IDF算法计算文本相似度
 */
@Component
public class TextSimilarityUtil {

    /**
     * 停用词集合（简化版，实际项目中可以从文件加载更完整的停用词表）
     */
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "的", "是", "在", "了", "和", "有", "这", "个", "也", "就", "不", "我", "你", "他", "她", "它",
            "我们", "你们", "他们", "她们", "它们", "这个", "那个", "这些", "那些", "这样", "那样",
            "一个", "一些", "很", "非常", "比较", "最", "更", "还", "都", "会", "要", "可以", "能够",
            "应该", "需要", "必须", "可能", "或者", "但是", "然后", "因为", "所以", "如果", "虽然",
            "虽说", "尽管", "无论", "不管", "只要", "只有", "除了", "除非", "关于", "对于", "按照",
            "根据", "通过", "由于", "为了", "以及", "以便", "以免", "以防", "以免", "而且", "并且"));

    /**
     * 对文本进行分词并过滤停用词
     * 
     * @param text 输入文本
     * @return 分词结果列表
     */
    public List<String> segmentText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 使用HanLP进行分词
        List<Term> terms = HanLP.segment(text);

        return terms.stream()
                .map(term -> term.word)
                .filter(word -> word.length() > 1) // 过滤单字符
                .filter(word -> !STOP_WORDS.contains(word)) // 过滤停用词
                .filter(word -> !word.matches("\\d+")) // 过滤纯数字
                .filter(word -> !word.matches("[\\p{Punct}]+")) // 过滤标点符号
                .collect(Collectors.toList());
    }

    /**
     * 计算词频(TF)
     * 
     * @param words 分词结果
     * @return 词频映射
     */
    public Map<String, Double> calculateTF(List<String> words) {
        Map<String, Double> tf = new HashMap<>();
        int totalWords = words.size();

        if (totalWords == 0) {
            return tf;
        }

        // 计算每个词的频次
        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        // 计算TF值 (词频/总词数)
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            tf.put(entry.getKey(), (double) entry.getValue() / totalWords);
        }

        return tf;
    }

    /**
     * 计算逆文档频率(IDF)
     * 
     * @param documents 所有文档的分词结果列表
     * @return IDF映射
     */
    public Map<String, Double> calculateIDF(List<List<String>> documents) {
        Map<String, Double> idf = new HashMap<>();
        int totalDocs = documents.size();

        if (totalDocs == 0) {
            return idf;
        }

        // 统计每个词出现在多少个文档中
        Map<String, Integer> docCount = new HashMap<>();
        for (List<String> doc : documents) {
            Set<String> uniqueWords = new HashSet<>(doc);
            for (String word : uniqueWords) {
                docCount.put(word, docCount.getOrDefault(word, 0) + 1);
            }
        }

        // 计算IDF值 log(总文档数/包含该词的文档数)
        for (Map.Entry<String, Integer> entry : docCount.entrySet()) {
            double idfValue = Math.log((double) totalDocs / entry.getValue());
            idf.put(entry.getKey(), idfValue);
        }

        return idf;
    }

    /**
     * 计算TF-IDF向量
     * 
     * @param tf  词频映射
     * @param idf IDF映射
     * @return TF-IDF向量
     */
    public Map<String, Double> calculateTFIDF(Map<String, Double> tf, Map<String, Double> idf) {
        Map<String, Double> tfidf = new HashMap<>();

        for (Map.Entry<String, Double> entry : tf.entrySet()) {
            String word = entry.getKey();
            double tfValue = entry.getValue();
            double idfValue = idf.getOrDefault(word, 0.0);
            tfidf.put(word, tfValue * idfValue);
        }

        return tfidf;
    }

    /**
     * 计算余弦相似度
     * 
     * @param vector1 第一个TF-IDF向量
     * @param vector2 第二个TF-IDF向量
     * @return 余弦相似度值 (0-1之间，1表示完全相似)
     */
    public double calculateCosineSimilarity(Map<String, Double> vector1, Map<String, Double> vector2) {
        if (vector1.isEmpty() || vector2.isEmpty()) {
            return 0.0;
        }

        // 获取两个向量的所有词汇
        Set<String> allWords = new HashSet<>();
        allWords.addAll(vector1.keySet());
        allWords.addAll(vector2.keySet());

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String word : allWords) {
            double value1 = vector1.getOrDefault(word, 0.0);
            double value2 = vector2.getOrDefault(word, 0.0);

            dotProduct += value1 * value2;
            norm1 += value1 * value1;
            norm2 += value2 * value2;
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * 计算两个文本的相似度
     * 
     * @param text1    文本1
     * @param text2    文本2
     * @param allTexts 所有文本列表（用于计算IDF）
     * @return 相似度值 (0-1之间)
     */
    public double calculateTextSimilarity(String text1, String text2, List<String> allTexts) {
        // 分词
        List<String> words1 = segmentText(text1);
        List<String> words2 = segmentText(text2);

        if (words1.isEmpty() || words2.isEmpty()) {
            return 0.0;
        }

        // 对所有文本进行分词
        List<List<String>> allDocuments = allTexts.stream()
                .map(this::segmentText)
                .collect(Collectors.toList());

        // 计算IDF
        Map<String, Double> idf = calculateIDF(allDocuments);

        // 计算TF-IDF向量
        Map<String, Double> tf1 = calculateTF(words1);
        Map<String, Double> tf2 = calculateTF(words2);
        Map<String, Double> tfidf1 = calculateTFIDF(tf1, idf);
        Map<String, Double> tfidf2 = calculateTFIDF(tf2, idf);

        // 计算余弦相似度
        return calculateCosineSimilarity(tfidf1, tfidf2);
    }

    /**
     * 批量计算文本相似度
     * 
     * @param targetText     目标文本
     * @param candidateTexts 候选文本列表
     * @return 相似度结果列表，按相似度降序排列
     */
    public List<TextSimilarityResult> calculateBatchSimilarity(String targetText, List<String> candidateTexts) {
        List<TextSimilarityResult> results = new ArrayList<>();

        // 准备所有文本用于IDF计算
        List<String> allTexts = new ArrayList<>(candidateTexts);
        allTexts.add(targetText);

        for (int i = 0; i < candidateTexts.size(); i++) {
            double similarity = calculateTextSimilarity(targetText, candidateTexts.get(i), allTexts);
            results.add(new TextSimilarityResult(i, candidateTexts.get(i), similarity));
        }

        // 按相似度降序排列
        results.sort((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()));

        return results;
    }

    /**
     * 文本相似度结果类
     */
    public static class TextSimilarityResult {
        private int index;
        private String text;
        private double similarity;

        public TextSimilarityResult(int index, String text, double similarity) {
            this.index = index;
            this.text = text;
            this.similarity = similarity;
        }

        // Getters
        public int getIndex() {
            return index;
        }

        public String getText() {
            return text;
        }

        public double getSimilarity() {
            return similarity;
        }
    }
}