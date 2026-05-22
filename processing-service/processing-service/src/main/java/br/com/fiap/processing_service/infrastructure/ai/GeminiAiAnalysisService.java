package br.com.fiap.processing_service.infrastructure.ai;

import br.com.fiap.processing_service.domain.model.AnalysisReport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Primary
@Service
public class GeminiAiAnalysisService implements AiAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(GeminiAiAnalysisService.class);

    @Value("${ai.gemini.api-key}")
    private String apiKey;

    @Value("${ai.gemini.url}")
    private String apiUrl;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AnalysisReport analyze(String analysisId, String fileName, String fileType) {
        log.info("Analisando diagrama com Gemini - analysisId: {}", analysisId);

        try {
            String prompt = buildPrompt(fileName, fileType);
            String requestBody = buildRequestBody(prompt);

            Request request = new Request.Builder()
                    .url(apiUrl + "?key=" + apiKey)
                    .post(RequestBody.create(requestBody,
                            MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Erro na API Gemini: {}", response.code());
                    return fallbackReport(analysisId);
                }

                String responseBody = response.body().string();
                return parseGeminiResponse(analysisId, responseBody);
            }
        } catch (Exception e) {
            log.error("Erro ao chamar Gemini: {}", e.getMessage());
            return fallbackReport(analysisId);
        }
    }

    private String buildPrompt(String fileName, String fileType) {
        return String.format("""
                Você é um especialista em arquitetura de software.
                Analise o diagrama de arquitetura chamado '%s' (tipo: %s).
                
                Responda APENAS em JSON válido, sem markdown, sem explicações, exatamente neste formato:
                {
                  "components": ["componente1", "componente2", "componente3"],
                  "risks": ["risco1", "risco2", "risco3"],
                  "recommendations": ["recomendacao1", "recomendacao2", "recomendacao3"],
                  "summary": "resumo da arquitetura em uma frase"
                }
                
                Identifique componentes arquiteturais típicos, riscos e recomendações baseados no nome do arquivo.
                """, fileName, fileType);
    }

    private String buildRequestBody(String prompt) throws Exception {
        return objectMapper.writeValueAsString(
                objectMapper.createObjectNode()
                        .set("contents", objectMapper.createArrayNode()
                                .add(objectMapper.createObjectNode()
                                        .set("parts", objectMapper.createArrayNode()
                                                .add(objectMapper.createObjectNode()
                                                        .put("text", prompt))))));
    }

    private AnalysisReport parseGeminiResponse(String analysisId, String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String text = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            // Remove markdown se vier
            text = text.replaceAll("```json", "").replaceAll("```", "").trim();

            JsonNode json = objectMapper.readTree(text);

            List<String> components = parseList(json.get("components"));
            List<String> risks = parseList(json.get("risks"));
            List<String> recommendations = parseList(json.get("recommendations"));
            String summary = json.get("summary").asText();

            log.info("Análise Gemini concluída - analysisId: {}", analysisId);
            return new AnalysisReport(analysisId, components, risks, recommendations, summary);

        } catch (Exception e) {
            log.error("Erro ao parsear resposta do Gemini: {}", e.getMessage());
            return fallbackReport(analysisId);
        }
    }

    private List<String> parseList(JsonNode node) {
        List<String> list = new ArrayList<>();
        if (node != null && node.isArray()) {
            node.forEach(item -> list.add(item.asText()));
        }
        return list;
    }

    private AnalysisReport fallbackReport(String analysisId) {
        log.warn("Usando relatório fallback para analysisId: {}", analysisId);
        return new AnalysisReport(
                analysisId,
                List.of("Componente não identificado"),
                List.of("Não foi possível analisar o diagrama"),
                List.of("Reenvie o diagrama para nova análise"),
                "Erro na análise automática — tente novamente."
        );
    }
}