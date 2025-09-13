package kopo.poly.persistance.mapper;

import kopo.poly.dto.FollowDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IFollowMapper {

    List<FollowDTO> getfollowingId(FollowDTO pDTO) throws Exception;

    List<FollowDTO> getfollowId(FollowDTO pDTO) throws Exception;

    int countfollowingId(FollowDTO pDTO) throws Exception;

    int countfollowerId(FollowDTO pDTO) throws Exception;

    FollowDTO countfollow(FollowDTO pDTO) throws Exception;

    int unFollow(FollowDTO pDTO) throws Exception;
}
