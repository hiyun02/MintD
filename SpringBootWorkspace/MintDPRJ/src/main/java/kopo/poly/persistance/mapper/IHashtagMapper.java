package kopo.poly.persistance.mapper;

import kopo.poly.dto.DiaryDTO;
import kopo.poly.dto.FollowDTO;
import kopo.poly.dto.HashtagDiaryDTO;
import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IHashtagMapper {
    List<DiaryDTO> getHashtag(HashtagDiaryDTO pDTO) throws Exception;

    void updateProfile(UserInfoDTO pDTO) throws Exception;

    UserInfoDTO getUserId(UserInfoDTO pDTO) throws Exception;

    void updatePhoto(UserInfoDTO pDTO) throws Exception;

    List<FollowDTO> getfollowingId(FollowDTO pDTO) throws Exception;

    List<FollowDTO> getfollowId(FollowDTO pDTO) throws Exception;

    int countfollowingId(FollowDTO pDTO) throws Exception;

    int countfollowerId(FollowDTO pDTO) throws Exception;

    FollowDTO countfollow(FollowDTO pDTO) throws Exception;

    int unFollow(FollowDTO pDTO) throws Exception;
}
