package kr.co.danal.naverworks.api.gateway.service;

import kr.co.danal.naverworks.api.gateway.config.PositionsConfig;
import kr.co.danal.naverworks.api.gateway.util.ClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionsService {

    private final ClientUtils clientUtils;
    private final PositionsConfig positionsConfig;

    public boolean isManagerPosition(String userId) {
        boolean result = false;
        ResponseEntity<Object> responseEntity = clientUtils.get("/users/" + userId).block();
        if (responseEntity != null && responseEntity.getBody() != null) {
            String content = responseEntity.getBody().toString();
            for (String keyword : positionsConfig.getManagers()) {
                if (content.contains(keyword)) {
                    return true;
                }
            }
        }
        return result;
    }
}