package kopo.poly.service;

import kopo.poly.dto.DiaryDTO;
import kopo.poly.dto.FollowDTO;
import kopo.poly.dto.HashtagDiaryDTO;
import kopo.poly.dto.UserInfoDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface IHashTagService {
    List<DiaryDTO> getHashtag(HashtagDiaryDTO pDTO) throws Exception;

    void updateProfile(UserInfoDTO pDTO) throws Exception;

    void uploadFile(MultipartFile file, HttpSession session) throws Exception;

    List<FollowDTO> getfollowingId(FollowDTO pDTO) throws Exception;

    List<FollowDTO> getfollowId(FollowDTO pDTO) throws Exception;

    FollowDTO countfollow(FollowDTO pDTO, boolean type) throws Exception;

    int unFollow(FollowDTO pDTO) throws Exception;
}
