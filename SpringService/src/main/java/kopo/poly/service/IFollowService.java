package kopo.poly.service;

import kopo.poly.dto.FollowDTO;
import java.util.List;

public interface IFollowService {

    List<FollowDTO> getfollowingId(FollowDTO pDTO) throws Exception;

    List<FollowDTO> getfollowId(FollowDTO pDTO) throws Exception;

    FollowDTO countfollow(FollowDTO pDTO, boolean type) throws Exception;

    int unFollow(FollowDTO pDTO) throws Exception;
}
