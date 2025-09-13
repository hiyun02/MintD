package kopo.poly.controller;

import kopo.poly.dto.FollowDTO;
import kopo.poly.service.IFollowService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

    private final IFollowService followService;

    @GetMapping(value = "getFollowingList")
    public String getFollowingList(HttpSession session, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".getFollowingList 시작!");

        String user_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
        log.info("user_id : " + user_id);

        FollowDTO pDTO = new FollowDTO();
        pDTO.setFollow_id(user_id);

        List<FollowDTO> rList = Optional.ofNullable(followService.getfollowingId(pDTO))
                .orElseGet(ArrayList::new);


        log.info("Controller Layer rList content: {}", rList);
        log.info("rList size: " + rList.size());
        model.addAttribute("rList", rList);

        log.info(this.getClass().getName() + ".getFollowingList End!");

        return "follow/followingList";
    }

    @GetMapping(value = "getFollowerList")
    public String getFollowerList(HttpSession session, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".getFollowerList 시작!");

        String user_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
        log.info("user_id : " + user_id);

        FollowDTO pDTO = new FollowDTO();
        pDTO.setFollowing_id(user_id);

        List<FollowDTO> rList = Optional.ofNullable(followService.getfollowId(pDTO))
                .orElseGet(ArrayList::new);

        model.addAttribute("rList", rList);

        log.info("Controller Layer rList content: {}", rList);
        log.info("rList size: " + rList.size());

        log.info(this.getClass().getName() + ".getFollowerList End!");

        return "follow/followerList";
    }

    @GetMapping("unFollow")
    public String unFollow(HttpServletRequest request, HttpSession session, ModelMap modelMap) throws Exception {
        log.info(this.getClass().getName() + ".unfollow 시작");

        String status;
        String title;
        String msg;
        String url;

        String follow_id = (String) session.getAttribute("SS_USER_ID");
        String unfollowing_id = request.getParameter("unfollowing_id");
        log.info("follow_id : " + follow_id);
        log.info("unfollowing_id : " + unfollowing_id);

        FollowDTO followDTO = new FollowDTO();
        followDTO.setFollow_id(follow_id);
        followDTO.setFollowing_id(unfollowing_id);

        int res = followService.unFollow(followDTO);

        if (res == 1) {
            status = "success";
            title = "성공";
            msg = "팔로우가 취소되었습니다.";
            url = "/diary/getMyDiaryList";
        } else {
            status = "error";
            title = "실패";
            msg = "다시 시도해주세요.";
            url = "/diary/getFeedInfo";
        }

        modelMap.addAttribute("status", status);
        modelMap.addAttribute("title", title);
        modelMap.addAttribute("msg", msg);
        modelMap.addAttribute("url", url);

        return "redirect";
    }

}
