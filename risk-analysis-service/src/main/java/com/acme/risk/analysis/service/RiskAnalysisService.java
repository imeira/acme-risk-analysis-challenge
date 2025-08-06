package com.acme.risk.analysis.service;

import com.acme.risk.analysis.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiskAnalysisService {

    private final RestTemplate restTemplate;

    @Value("${services.lists.url}")
    private String listsServiceUrl;

    @Value("${services.decision-engine.url}")
    private String decisionEngineServiceUrl;

    @Value("${risk.score.low.max:399}")
    private int lowRiskMaxScore;

    @Value("${risk.score.medium.max:699}")
    private int mediumRiskMaxScore;

    public RiskAnalysisResponse analyzeRisk(RiskAnalysisRequest request) {
        log.info("Iniciando análise de risco para CPF: {}, Tipo: {}, Valor: {}", 
                request.getCpf(), request.getTxType(), request.getTxValue());

        // 1. Consultar o serviço de listas
        ListsCheckResponse listsResponse = checkLists(request);

        // 2. Consultar o motor de decisão
        int score = calculateScore(request, listsResponse);

        // 3. Determinar a decisão com base no score
        String decision = determineDecision(score);

        log.info("Análise de risco concluída. Score: {}, Decisão: {}", score, decision);

        return new RiskAnalysisResponse(decision);
    }

    private ListsCheckResponse checkLists(RiskAnalysisRequest request) {
        try {
            ListsCheckRequest listsRequest = new ListsCheckRequest(
                    request.getCpf(), 
                    request.getIp(), 
                    request.getDeviceId()
            );

            return restTemplate.postForObject(
                    listsServiceUrl + "/lists/check",
                    listsRequest,
                    ListsCheckResponse.class
            );
        } catch (Exception e) {
            log.error("Erro ao consultar serviço de listas", e);
            // Retorna resposta padrão em caso de erro
            ListsCheckResponse response = new ListsCheckResponse();
            response.setCpf(new ListsCheckResponse.CpfListStatus());
            response.setIp(new ListsCheckResponse.IpListStatus());
            response.setDeviceId(new ListsCheckResponse.DeviceListStatus());
            return response;
        }
    }

    private int calculateScore(RiskAnalysisRequest request, ListsCheckResponse listsResponse) {
        try {
            ScoreCalculationRequest scoreRequest = new ScoreCalculationRequest(
                    request.getCpf(),
                    request.getIp(),
                    request.getDeviceId(),
                    request.getTxType(),
                    request.getTxValue(),
                    listsResponse.getCpf().isInPermissiveList(),
                    listsResponse.getCpf().isInRestrictiveList(),
                    listsResponse.getIp().isInRestrictiveList(),
                    listsResponse.getDeviceId().isInRestrictiveList()
            );

            ScoreCalculationResponse scoreResponse = restTemplate.postForObject(
                    decisionEngineServiceUrl + "/decision-engine/calculate-score",
                    scoreRequest,
                    ScoreCalculationResponse.class
            );

            return scoreResponse != null ? scoreResponse.getScore() : 500; // Score padrão em caso de erro
        } catch (Exception e) {
            log.error("Erro ao calcular score", e);
            return 500; // Score padrão em caso de erro
        }
    }

    private String determineDecision(int score) {
        if (score <= mediumRiskMaxScore) {
            return "Aprovada";
        } else {
            return "Negada";
        }
    }
}

