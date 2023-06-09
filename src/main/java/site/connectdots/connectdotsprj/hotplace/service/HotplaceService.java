package site.connectdots.connectdotsprj.hotplace.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.connectdots.connectdotsprj.global.enums.Location;
import site.connectdots.connectdotsprj.hotplace.dto.requestDTO.HotplaceModifyRequestDTO;
import site.connectdots.connectdotsprj.hotplace.dto.requestDTO.HotplaceWriteRequestDTO;
import site.connectdots.connectdotsprj.hotplace.dto.responseDTO.HotplaceDetilResponseDTO;
import site.connectdots.connectdotsprj.hotplace.dto.responseDTO.HotplaceListResponseDTO;
import site.connectdots.connectdotsprj.hotplace.entity.Hotplace;
import site.connectdots.connectdotsprj.hotplace.repository.HotplaceRepository;
import site.connectdots.connectdotsprj.member.entity.Member;
import site.connectdots.connectdotsprj.member.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class HotplaceService {

    private final HotplaceRepository hotplaceRepository;
    private final MemberRepository memberRepository;

    // 글 전체조회
    public HotplaceListResponseDTO findAll() {

        List<Hotplace> hotplaceList = hotplaceRepository.findAll();

        List<HotplaceDetilResponseDTO> list = hotplaceList.stream()
                .map(HotplaceDetilResponseDTO::new)
                .collect(Collectors.toList());

        HotplaceListResponseDTO listResponseDTO = new HotplaceListResponseDTO();
        listResponseDTO.setHotplaceList(list);

        return listResponseDTO;
    }

    // 글 작성
    public HotplaceDetilResponseDTO write(final HotplaceWriteRequestDTO dto) throws RuntimeException {

//        Member member = memberRepository.findById(dto.getMemberIdx())
//                .orElseThrow();

        Hotplace hotplace = dto.toEntity();
//        hotplace.setMember(member);

//        log.info("hotplaceService.write.info {}", member);

        Hotplace save = hotplaceRepository.save(hotplace);

        return new HotplaceDetilResponseDTO(save);
    }

    // 글 삭제
    public void delete(Long hotplaceIdx) {
        Hotplace hotplace = hotplaceRepository.findById(hotplaceIdx)
                .orElseThrow(() -> new RuntimeException(hotplaceIdx + "의 글을 찾을 수 없습니다."));

        hotplaceRepository.delete(hotplace);
    }


    // 글 수정
    public HotplaceDetilResponseDTO modify(final HotplaceModifyRequestDTO dto) {
        // 조회
        final Hotplace hotplaceEntity = findOne(dto.getHotplaceIdx());

        //세터
        dto.updateHotplace(hotplaceEntity);

        // 저장
        Hotplace modified = hotplaceRepository.save(hotplaceEntity);

        return new HotplaceDetilResponseDTO(modified);
    }


    private Hotplace findOne(Long hotplaceIdx) {
        Hotplace hotplaceEntity = hotplaceRepository.findById(hotplaceIdx)
                .orElseThrow(() -> new RuntimeException(hotplaceIdx + "의 글을 찾을 수 없습니다."));
        return hotplaceEntity;
    }


    // 행정구역으로 핫플레이스 게시물 목록 조회하기
//    public HotplaceListResponseDTO findByHotplaceLocation(HotplaceLocation hotplaceLocation) {
//
//        List<Hotplace> byHotplaceLocation = hotplaceRepository.findByHotplaceLocation(hotplaceLocation);
//
//        List<HotplaceDetilResponseDTO> dtoList = byHotplaceLocation.stream()
//                .map(HotplaceDetilResponseDTO::new)
//                .collect(Collectors.toList());
//
//        HotplaceListResponseDTO listResponseDTO = new HotplaceListResponseDTO();
//        listResponseDTO.setHotplaceList(dtoList);
//
//        return listResponseDTO;
//    }

    public List<Hotplace> findByLocation(Location location) {
        return hotplaceRepository.findByLocation(location);
    }
}
