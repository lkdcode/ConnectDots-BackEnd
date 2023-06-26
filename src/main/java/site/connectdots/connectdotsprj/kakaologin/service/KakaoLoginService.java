package site.connectdots.connectdotsprj.kakaologin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import site.connectdots.connectdotsprj.kakaologin.dto.KaKaoLoginResponseDTO;
import site.connectdots.connectdotsprj.kakaologin.dto.KakaoLoginRequestDTO;
import site.connectdots.connectdotsprj.kakaologin.entity.KakaoMember;
import site.connectdots.connectdotsprj.kakaologin.repository.KakaoLoginRepository;
import site.connectdots.connectdotsprj.member.dto.request.MemberSignUpRequestDTO;
import site.connectdots.connectdotsprj.member.service.MemberSignUpService;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoLoginService {

    private final KakaoLoginRepository kakaoLoginRepository;

    public void kakaoService(Map<String, String> requestMap) throws RuntimeException {
        String accessToken = getKakaoAccessToken(requestMap);
        log.info("발급받은 토큰 : {}", accessToken);

        KaKaoLoginResponseDTO kakaoUserInfo = getKakaoUserInfo(accessToken);
        KaKaoLoginResponseDTO.KakaoAccount kakaoAccount = kakaoUserInfo.getKakaoAccount();


//        // 이메일 중복 검사
//        // 만약, 나중에 회원테이블에 카카오가입자도 들어가게 되면 &&로 조건 걸어줘야함!!!!
        if (kakaoLoginRepository.existsByKakaoEmail(kakaoAccount.getEmail())) {
            log.warn("존재하는 회원입니다 {}", kakaoAccount.getEmail());
            throw new RuntimeException("이메일이 중복되었습니다.");

        } else {
            // 데이터베이스 저장
            KakaoMember kakaoMember = KakaoMember.builder()
                    .kakaoEmail(kakaoAccount.getEmail())
                    .kakaoNickname(kakaoAccount.getProfile().getNickname())
                    .kakaoProfileImage(kakaoAccount.getProfile().getProfileImageUrl())
                    .build();

            kakaoLoginRepository.save(kakaoMember);
            log.info("가입 성공 {}", kakaoAccount.getEmail());
        }
    }

    private KaKaoLoginResponseDTO getKakaoUserInfo(String accessToken) {

        String requestUri = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        RestTemplate template = new RestTemplate();
        ResponseEntity<KaKaoLoginResponseDTO> responseEntity = template.exchange(requestUri, HttpMethod.GET, new HttpEntity<>(headers), KaKaoLoginResponseDTO.class);

        KaKaoLoginResponseDTO responseData = responseEntity.getBody();
        log.info("사용자 프로필 정보: {}", responseData);

        return responseData;

    }


    private String getKakaoAccessToken(Map<String, String> requestMap) {

        String requestUri = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", requestMap.get("appkey"));
        params.add("redirect_uri", requestMap.get("redirect"));
        params.add("code", requestMap.get("code"));


        RestTemplate template = new RestTemplate();

        HttpEntity<Object> requestEntity = new HttpEntity<>(params, headers);

        ResponseEntity<Map> responseEntity = template.exchange(requestUri, HttpMethod.POST, requestEntity, Map.class);

        Map<String, Object> responseData = (Map<String, Object>) responseEntity.getBody();
        log.info("토큰요청 응답데이터 : {}", responseData);

        return (String) responseData.get("access_token");

    }

}
