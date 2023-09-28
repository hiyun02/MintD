package kopo.poly.service.impl;

import kopo.poly.dto.DiaryDTO;
import kopo.poly.dto.FollowDTO;
import kopo.poly.dto.HashtagDiaryDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.persistance.mapper.IHashtagMapper;
import kopo.poly.service.IHashTagService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service    // 서비스의 인터페이스 == 서비스에서 구현할 함수들을 명세해놓은 곳

public class HashTagService implements IHashTagService {
    private final IHashtagMapper hashtagMapper; // Mapper 가져오기

    @Override
    public List<FollowDTO> getfollowingId(FollowDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getfollowingId 시작!");

        List<FollowDTO> rList = hashtagMapper.getfollowingId(pDTO);

        log.info("팔로잉 목록: {}", rList);

        log.info(this.getClass().getName() + ".getfollowingId 끝!");

        return rList;
    }

    @Override
    public List<FollowDTO> getfollowId(FollowDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getfollowId 시작!");

        List<FollowDTO> rList = hashtagMapper.getfollowId(pDTO);

        log.info("팔로우 목록: {}", rList);

        log.info(this.getClass().getName() + ".getfollowId 끝!");

        return rList;
    }

    public int countfollowingId(FollowDTO pDTO) throws Exception {
        return hashtagMapper.countfollowingId(pDTO);
    }

    public int countfollowId(FollowDTO pDTO) throws Exception {
        return hashtagMapper.countfollowerId(pDTO);
    }

    @Transactional
    @Override
    public FollowDTO countfollow(FollowDTO pDTO, boolean type) throws Exception {
        log.info(this.getClass().getName() + ".countfollow start!");

        pDTO.setFollower_count(countfollowId(pDTO)+"");
        pDTO.setFollowing_count(countfollowingId(pDTO)+"");
        log.info(pDTO.toString());
        return pDTO;
    }

    @Override
    public int unFollow(FollowDTO pDTO) throws Exception {
        return hashtagMapper.unFollow(pDTO);
    }

    @Override
    public List<DiaryDTO> getHashtag(HashtagDiaryDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getHashtag 시작!");

        List<DiaryDTO> rList = hashtagMapper.getHashtag(pDTO);

        log.info("Service Layer rList content: {}", rList);

        log.info(this.getClass().getName() + ".getHashtag 끝!");

        return rList;
    }

    @Override
    public void updateProfile(UserInfoDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".updateProfile Start!");

        hashtagMapper.updateProfile(pDTO);
    }

    @Override
    public void uploadFile(MultipartFile file, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".uploadFile Start!");

        String user_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
        log.info("user_id : " + user_id);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUser_id(user_id);

        UserInfoDTO rDTO = hashtagMapper.getUserId(pDTO);
        log.info("getUserId : " + user_id);

        log.info(rDTO.getUser_id() + ".uploadFile Start!");

        // 파일 경로를 rDTO 객체에 할당
        String filePath = "C:\\SpringBootWorks\\Weather\\src\\main\\resources\\static\\user\\" + rDTO.getUser_id() + ".png";

        // 파일을 지정된 경로에 저장
        file.transferTo(new File(filePath));

        pDTO.setProfile_path(filePath);

        hashtagMapper.updatePhoto(pDTO);

        log.info(this.getClass().getName() + ".uploadFile End!");
    }

}
