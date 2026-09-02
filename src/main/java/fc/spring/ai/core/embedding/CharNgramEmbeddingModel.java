package fc.spring.ai.core.embedding;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.AbstractEmbeddingModel;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地、确定性、零外部依赖的 EmbeddingModel，用于在不接任何向量化服务的情况下
 * 演示「文本 -> 向量 -> 向量检索」机制。
 *
 * 思路：把文本切分为字符(英文单词 / 中文单字与二字词)形式的词元，按词元哈希散列到
 * 固定维度向量桶中累加频次，再做 L2 归一化。属于词法近似检索，足够让相似片段在
 * SimpleVectorStore 的余弦相似度下靠前。若要真实语义，可后续直接替换成 OpenAI/Ollama
 * 等 EmbeddingModel Bean，其余代码无需改动。
 */
public class CharNgramEmbeddingModel extends AbstractEmbeddingModel {

    public static final int DIMENSIONS = 128;

    @Override
    public int dimensions() {
        return DIMENSIONS;
    }

    @Override
    public float[] embed(Document document) {
        String text = document.getText();
        if (text == null) {
            return new float[0];
        }
        return vectorize(text);
    }

    @Override
    public float[] embed(String text) {
        return vectorize(text);
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        List<Embedding> embeddings = new ArrayList<>();
        int index = 0;
        for (String instruction : request.getInstructions()) {
            embeddings.add(new Embedding(vectorize(instruction), index++));
        }
        return new EmbeddingResponse(embeddings);
    }

    private float[] vectorize(String text) {
        Map<Integer, Double> buckets = new HashMap<>();
        for (String token : tokenize(text)) {
            int hash = token.hashCode() & 0x7fffffff;
            int bucket = hash % DIMENSIONS;
            // 用第二段散列决定正负号，让不同词元尽量分散在不同方向上
            double sign = ((hash >>> 16) & 1) == 0 ? -1.0 : 1.0;
            buckets.merge(bucket, sign, Double::sum);
        }

        float[] vector = new float[DIMENSIONS];
        double normSq = 0;
        for (int i = 0; i < DIMENSIONS; i++) {
            double value = buckets.getOrDefault(i, 0.0);
            vector[i] = (float) value;
            normSq += value * value;
        }
        if (normSq > 0) {
            float norm = (float) Math.sqrt(normSq);
            for (int i = 0; i < DIMENSIONS; i++) {
                vector[i] /= norm;
            }
        }
        return vector;
    }

    /** 切词：连续 ASCII 字母/数字为一个词；中文按单字与相邻二字词输出。 */
    private List<String> tokenize(String text) {
        String lower = text.toLowerCase();
        List<String> tokens = new ArrayList<>();
        StringBuilder ascii = new StringBuilder();
        int length = lower.length();
        for (int i = 0; i < length; i++) {
            char c = lower.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= '0' && c <= '9') {
                ascii.append(c);
            } else {
                flushAscii(tokens, ascii);
                if (isCjk(c)) {
                    tokens.add(String.valueOf(c));
                    if (i + 1 < length && isCjk(lower.charAt(i + 1))) {
                        tokens.add(lower.substring(i, i + 2));
                    }
                }
            }
        }
        flushAscii(tokens, ascii);
        return tokens;
    }

    private void flushAscii(List<String> tokens, StringBuilder ascii) {
        if (!ascii.isEmpty()) {
            tokens.add(ascii.toString());
            ascii.setLength(0);
        }
    }

    private boolean isCjk(char c) {
        return c >= 0x3400 && c <= 0x9FFF;
    }
}
