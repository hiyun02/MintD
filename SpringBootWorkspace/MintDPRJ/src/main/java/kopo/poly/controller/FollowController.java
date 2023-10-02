package kopo.poly.controller;

import kopo.poly.dto.FollowDTO;
import kopo.poly.service.IHashTagService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final IHashTagService hashTagService;

    @GetMapping(value = "getFollowingList")
    public String getfollowingId(HttpSession session, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".getFollowingList 시작!");

        String user_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
        log.info("user_id : " + user_id);

        FollowDTO pDTO = new FollowDTO();
        pDTO.setFollow_id(user_id);

        List<FollowDTO> rList = Optional.ofNullable(hashTagService.getfollowingId(pDTO))
                .orElseGet(ArrayList::new);


        log.info("Controller Layer rList content: {}", rList);
        log.info("rList size: " + rList.size());
        model.addAttribute("rList", rList);

        log.info(this.getClass().getName() + ".getFollowingList End!");

        return "follow/followingList";
    }

    @GetMapping(value = "getFollowerList")
    public String getfollowId(HttpSession session, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".getFollowerList 시작!");

        String user_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
        log.info("user_id : " + user_id);

        FollowDTO pDTO = new FollowDTO();
        pDTO.setFollowing_id(user_id);

        List<FollowDTO> rList = Optional.ofNullable(hashTagService.getfollowId(pDTO))
                .orElseGet(ArrayList::new);

        model.addAttribute("rList", rList);

        log.info("Controller Layer rList content: {}", rList);
        log.info("rList size: " + rList.size());

        log.info(this.getClass().getName() + ".getFollowerList End!");

        return "follow/followerList";
    }

    @GetMapping("unFollow")
    public String unFollow(ModelMap modelMap) throws Exception {
        log.info(this.getClass().getName()+".unfollow 시작");

        String url = "";
        String msg = "";

        FollowDTO followDTO = new FollowDTO();
        followDTO.setFollow_id("soyoung");
        followDTO.setFollowing_id("hayun");

        int res = hashTagService.unFollow(followDTO);

        if (res == 1) {
            msg = "팔로우가 취소되었습니다.";
            url = "/diary/getMyDiaryList";
        } else {
            msg = "다시 시도해주세요.";
            url = "/diary/getUserDiaryList";
        }
        modelMap.addAttribute("url",url);
        modelMap.addAttribute("msg",msg);

        return "redirect";
    }

}
