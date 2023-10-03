package kopo.poly.service.impl;

import kopo.poly.dto.FollowDTO;
import kopo.poly.persistance.mapper.IFollowMapper;
import kopo.poly.service.IFollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FollowService implements IFollowService {
    private final IFollowMapper followMapper; // Mapper 가져오기

    @Override
    public List<FollowDTO> getfollowingId(FollowDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getfollowingId 시작!");

        List<FollowDTO> rList = followMapper.getfollowingId(pDTO);

        log.info("팔로잉 목록: {}", rList);

        log.info(this.getClass().getName() + ".getfollowingId 끝!");

        return rList;
    }

    @Override
    public List<FollowDTO> getfollowId(FollowDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getfollowId 시작!");

        List<FollowDTO> rList = followMapper.getfollowId(pDTO);

        log.info("팔로우 목록: {}", rList);

        log.info(this.getClass().getName() + ".getfollowId 끝!");

        return rList;
    }

    public int countfollowingId(FollowDTO pDTO) throws Exception {
        return followMapper.countfollowingId(pDTO);
    }

    public int countfollowId(FollowDTO pDTO) throws Exception {
        return followMapper.countfollowerId(pDTO);
    }

    @Transactional
    @Override
    public FollowDTO countfollow(FollowDTO pDTO, boolean type) throws Exception {
        log.info(this.getClass().getName() + ".countfollow start!");

        pDTO.setFollower_count(countfollowId(pDTO) + "");
        pDTO.setFollowing_count(countfollowingId(pDTO) + "");
        log.info(pDTO.toString());
        return pDTO;
    }

    @Override
    public int unFollow(FollowDTO pDTO) throws Exception {
        return followMapper.unFollow(pDTO);
    }

}
