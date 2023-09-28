package kopo.poly.controller;

import kopo.poly.dto.DiaryDTO;
import kopo.poly.dto.FollowDTO;
import kopo.poly.dto.HashtagDiaryDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IHashTagService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping("/hashtag")
@RequiredArgsConstructor
@Controller
public class HashtagController {
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

    @GetMapping(value = "countfollow")
    public String countfollow(HttpSession session, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + ".getfollowId 시작!");

        String user_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
        log.info("user_id : " + user_id);

        FollowDTO pDTO = new FollowDTO();
        pDTO.setFollow_id(user_id);
        pDTO.setFollowing_id(user_id);

        FollowDTO rDTO = Optional.ofNullable(hashTagService.countfollow(pDTO, true))
                .orElseGet(FollowDTO::new);

        model.addAttribute("rDTO", rDTO);

        log.info("Controller Layer rList content: {}", rDTO);

        log.info(this.getClass().getName() + ".getfollowId End!");

        return "/countfollow";
    }

    @PostMapping(value = "/uploadPhoto")
    public String uploadPhoto(@RequestPart MultipartFile file, ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".uploadPhoto Start!");

        hashTagService.uploadFile(file, session);

        String msg = "수정되었습니다.";
        model.addAttribute("msg", msg);

        return "/profile";
    }

    @GetMapping(value = "getHashtag")
    public String getHashtag(HttpServletRequest request, ModelMap modelMap) throws Exception {
        log.info(this.getClass().getName() + ".getHashtag 시작!");

        String hashtag_id = CmmUtil.nvl(request.getParameter("searchKeyword"));
        log.info("hashtag_id : " + hashtag_id);

        HashtagDiaryDTO pDTO = new HashtagDiaryDTO();
        pDTO.setHashtag_id(hashtag_id);

        List<DiaryDTO> rList = Optional.ofNullable(hashTagService.getHashtag(pDTO))
                .orElseGet(ArrayList::new);

        log.info("Controller Layer rList content: {}", rList);

        log.info("rList size: " + rList.size());
        log.info(this.getClass().getName() + ".getHashtag End!");
        modelMap.addAttribute("rList", rList);
        return "html/searchFeed";
    }

    @PostMapping(value = "/profileUpdate")
    public String profileUpdate(HttpSession session, ModelMap model, HttpServletRequest request){

        log.info(this.getClass().getName() + ".profileUpdate Start!");

        String msg = "";
        String url = "/searchFeed";

        try {
            String user_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String user_intro = CmmUtil.nvl( request.getParameter("user_intro"));
            String user_nick = CmmUtil.nvl( request.getParameter("user_nick"));

            log.info("user_id : " + user_id);
            log.info("user_intro : " + user_intro);
            log.info("user_nick : " + user_nick);

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUser_id(user_id);
            pDTO.setUser_intro(user_intro);
            pDTO.setUser_nick(user_nick);

            hashTagService.updateProfile(pDTO);

            msg = "수정되었습니다.";
        } catch (Exception e){
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            model.addAttribute("msg", msg);
            model.addAttribute("url", url);
            log.info(this.getClass().getName() + ".profileUpdate End!");
        }

        return "/redirect";
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
