package kopo.poly.controller;

import kopo.poly.dto.DiaryDTO;
import kopo.poly.dto.FollowDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IDiaryService;
import kopo.poly.service.IHashTagService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
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
@Controller   // 모든 값을 다 JSON으로 돌려줄 게 아니라면 그냥 컨트롤러를 써야 한다!
@RequestMapping(value = "/diary")
@RequiredArgsConstructor
@ToString
public class DiaryController {

    // 서비스 호출하기 위해서 컨트롤러가 실행되는 동안 메모리에 서비스도 같이 올려두기
    private final IDiaryService diaryService;

    // NFT 다이어리 리스트 가져오기
    @GetMapping(value = "/getNFTDiaryList")
    @ResponseBody
    public List<DiaryDTO> getNFTDiaryList(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + " NFT 다이어리 리스트 페이지 가져오기 컨트롤러(getNFTDiaryList) 실행!");

        String user_id = (String) session.getAttribute("SS_USER_ID");

        DiaryDTO pDTO = new DiaryDTO();
        pDTO.setReg_id(user_id);

        List<DiaryDTO> rList = diaryService.getNFTDiaryList(pDTO);

        log.info(this.getClass().getName() + " NFT 다이어리 리스트 페이지 가져오기(getNFTDiaryList) 종료!");

        return rList;
    }

    // 보관함 리스트 가져오기
    @GetMapping(value = "/getBookMarkList")
    @ResponseBody
    public List<DiaryDTO> getBookMarkList(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + " 북마크 리스트 페이지 가져오기 실행");

        //추후 다른 사용자의 북마크 리스트도 볼 수 있도록 세션이 아닌, 피드로부터 user_id를 받아오도록 변경해야 함
        String user_id = (String) session.getAttribute("SS_USER_ID");

        DiaryDTO pDTO = new DiaryDTO();
        pDTO.setReg_id(user_id);

        List<DiaryDTO> bList = new ArrayList<>();

        // 북마크 리스트에서 유저 아이디가 체크한 다이어리의 게시물이 있으면 diary_seq 가져와라
        // 여기 수민님이 보더니 가독성 떨어진다고 정리해주심
        // 수민님 왈 : 저는 길어지면 보기 불편해서 이렇게 줄 바꿔버려요
        List<DiaryDTO> rList = Optional.ofNullable(
                diaryService.getBookMarkFind(pDTO)
        ).orElseGet(ArrayList::new);

        log.info("북마크한 게시글 개수 : " + rList.size());
        // 여기서 그냥 바로 호출하면 안 되지 받아온 것 중 값을 꺼내서 보내야지 그래야 seq값에 해당하는 데이터를 가져올 수 있으니까
        // 결과로 받아온 리스트 개수가 1개 이상이면
        if (rList.size() >= 1) {
            // 다이어리dto를 담은 새로운 ArrayList 생성하고
            List<DiaryDTO> dtos = new ArrayList<DiaryDTO>();
            // 받아온 리스트의 개수만큼 반복문을 돌려라
            for (int i = 0; i < rList.size(); i++) {
                // 리스트 하나싹 읽으면서 dto에 담는데(List에서는 한 번에 변수 값을 뽑아낼 수 없음 일단 dto로 분해를 하고 그 dto에서 찾아야 함)
                DiaryDTO dto = rList.get(i);
                // dto내에서 diayr_seq라는 이름의 변수가 나올 때마다 그 변수가 갖고 있는 꺼내와서 담아라
                String diary_seq = dto.getDiary_seq();
                log.info("사용자가 북마크한 게시글의 번호 : " + diary_seq);

                // DTO를 새로 만든 뒤에
                DiaryDTO sDTO = new DiaryDTO();
                // 그 DTO의 diary_seq값을 위에서 꺼낸 값으로 바꿔서 저장한 후
                sDTO.setDiary_seq(diary_seq);

                // 매퍼를 호출하여 결과를 받아와서 리스트에 하나씩 추가(리스트에 추가하는 문법을 써야 함)
                // List<DiaryDTO> bList = diaryService.getBookMarkList(sDTO) 이렇게 쓰면 안 됨
                // 이 부분만 수민님이 알려주심(리스트에는 이렇게 추가해야 한다고) 내가 저렇게(윗줄) 써놨어서 하 여기때문에 머리 터지는 줄....
                bList.add(diaryService.getDiaryInfo(sDTO));
            }
        }

        // 몇 개 가져왔는지 로그 찍어서 확인해보고50
        log.info("bList size : " + bList.size());

        log.info(this.getClass().getName() + " 북마크 리스트 페이지 가져오기 종료");

        return bList;
    }

    private final IHashTagService hashTagService;

    // 나의 게시물 리스트 보여주기(기본 페이지)
    @GetMapping(value = "/getMyDiaryList")
    public String getMyDiaryList(ModelMap modelMap, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + " 나의 게시물 목록 가져오기(getMyDiaryList)컨트롤러 실행!");

        try {
            // 나의 게시물 목록을 가져오려면 세션으로부터 아이디를 받아와야 함, 매개변수로 넣어놓은 게 key! 이 이름으로 저장했으니 불러올 때도 이름으로 불러오면 됨
            String user_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

            // DTO의 객체 새로 생성한 뒤, reg_id라는 변수에 세션으로부터 받아온 값을 저장
            DiaryDTO pDTO = new DiaryDTO();
            pDTO.setReg_id(user_id);

            // 서비스의 함수를 호출한 뒤, 그 결과를 받아와서 rList에 저장
            List<DiaryDTO> rList = diaryService.getMyDiaryList(pDTO);

            // 만약 서비스의 함수 실행 뒤, 받아온 값이 아무것도 없다면 새로운 리스트를 생성해서 rList에 담기
            if (rList == null) {
                rList = new ArrayList<>();
            }

            // 화면으로 보낼 모델맵의 "rList" 속성에 값으로 rList를 추가해서 보내기
            modelMap.addAttribute("rList", rList);

            //게시글 수 구하기
            int diary_cnt = diaryService.getMyDiaryCnt(pDTO);
            log.info("diary_cnt : " + diary_cnt);
            modelMap.addAttribute("diary_cnt", diary_cnt);

            //팔로우 수 구하기
            log.info("user_id : " + user_id);

            FollowDTO followDTO = new FollowDTO();
            followDTO.setFollow_id(user_id);
            followDTO.setFollowing_id(user_id);

            followDTO = Optional.ofNullable(hashTagService.countfollow(followDTO, true))
                    .orElseGet(FollowDTO::new);
            log.info(followDTO.toString());
            modelMap.addAttribute("followDTO", followDTO);

            // 에러가 나면 잡기
        } catch (Exception e) {
            log.info(e.toString());
            e.printStackTrace();

            // 에러가 있든 없든 로그 찍기
        } finally {
            log.info(this.getClass().getName() + " 나의 게시물 목록 가져오기(getMyDiaryList)컨트롤러 종료!");
        }

        // 나의 게시물 리스트 가져올 주소
        return "main/mainFeed";
    }

    @GetMapping(value = "/getUserDiaryList")
    public String getUserDiaryList(ModelMap modelMap, HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + " 나의 게시물 목록 가져오기(getMyDiaryList)컨트롤러 실행!");

        try {
            // 나의 게시물 목록을 가져오려면 세션으로부터 아이디를 받아와야 함, 매개변수로 넣어놓은 게 key! 이 이름으로 저장했으니 불러올 때도 이름으로 불러오면 됨
            String reg_id = CmmUtil.nvl((String) request.getParameter("user_id"), "hayun");
            log.info("reg_id : " + reg_id);


            // DTO의 객체 새로 생성한 뒤, reg_id라는 변수에 세션으로부터 받아온 값을 저장
            DiaryDTO pDTO = new DiaryDTO();
            pDTO.setReg_id(reg_id);

            // 서비스의 함수를 호출한 뒤, 그 결과를 받아와서 rList에 저장
            List<DiaryDTO> rList = diaryService.getMyDiaryList(pDTO);

            // 만약 서비스의 함수 실행 뒤, 받아온 값이 아무것도 없다면 새로운 리스트를 생성해서 rList에 담기
            if (rList == null) {
                rList = new ArrayList<>();
            }

            // 화면으로 보낼 모델맵의 "rList" 속성에 값으로 rList를 추가해서 보내기
            modelMap.addAttribute("rList", rList);

            // 에러가 나면 잡기
        } catch (Exception e) {
            log.info(e.toString());
            e.printStackTrace();

            // 에러가 있든 없든 로그 찍기
        } finally {
            log.info(this.getClass().getName() + " 나의 게시물 목록 가져오기(getMyDiaryList)컨트롤러 종료!");
        }

        // 나의 게시물 리스트 가져올 주소
        return "user/userFeed";
    }


    // 게시물 등록 페이지로 이동
    @GetMapping(value = "/writeDiary")
    public String writeDiary() throws Exception {
        log.info(" 작성페이지로 이동하는 컨트롤러(writeDiary)실행!");

        log.info(" 작성페이지로 이동하는 컨트롤러(writeDiary)종료!");

        return "html/writeDiary";
    }

    // 게시물 추가하기
    @PostMapping(value = "insertDiaryInfo")
    // 매개변수에 file을 넣지 않으면 파일 자체를 받아올 수가 없음(MultipartFile file)
    public String insertDiaryInfo(HttpServletRequest request, MultipartFile file, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + " 게시물 추가 컨트롤러(insertDiaryInfo) 실행!");

        try {

            // TODO 나중에 로그인 구현되면 아이디 수정 지금 강제로 넣어놓은 것
//            String user_id = "soyoung";
            String user_id = (String) session.getAttribute("SS_USER_ID");
            String contents = "안녕? 내 멋진 페이지를 눌러보다니 이 페이지를 누른 너 오늘 상당히 멋진 하루를 보내겠는데? ";

            // 요청받은 것 중 "" 안에 들어있는 키의 값을 읽어와서 자바 변수에 저장 --> 이미지 경로가 들어가는지 확인하기 위해 일단 이것만!
            // String reg_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            // String nft_yn = CmmUtil.nvl((String) request.getAttribute("nft_yn"));
            // String contents = CmmUtil.nvl((String) request.getAttribute("contents"));
            // 로그에 안 찍히는 것 보니 못 읽어오는듯함
            String img_path = CmmUtil.nvl((String) request.getAttribute("file"));
            // 이미지 경로를 여기서 만들어서 담아줘야 함 그냥 읽는 게 아니라(1->2로 이동)

            // 어떤 값이 들어왔나 로그 찍어서 확인하기
            log.info("reg_id : " + user_id);
//            log.info("nft_yn : " + nft_yn);
            log.info("contents : " + contents);
            // 그래서 그냥 로그를 이렇게 찍어버림(2, 아래)
            log.info("img_path : " + file);

            // 새로운 DTO 객체 생성하기
            DiaryDTO pDTO = new DiaryDTO();

            // 생성한 DTO의 변수 값들을 내가 받아온 요청 값들로 설정
            pDTO.setReg_id(user_id);
//            pDTO.setNft_yn(nft_yn);
            pDTO.setContents(contents);
            pDTO.setImg_path(img_path);

            // 서비스의 게시물 추가 함수 DTO 보내면서 호출하기, 서비스에서 파일을 저장할 거니까 받아온 파일도 같이 보내줘야 함
            diaryService.insertDiaryInfo(pDTO, file);

            // 에러가 있다면 잡고
        } catch (Exception e) {
            log.info(e.toString());
            e.printStackTrace();
            // 실행이 끝나면 무조건 로그 찍기
        } finally {
            log.info(this.getClass().getName() + " 게시물 추가 컨트롤러(insertDiaryInfo) 종료!");
        }

        // 게시물 추가가 끝나고 이동할 페이지의 주소
        // return "html/getMyDiaryList";  지금은 이 파일이 없으니까 주석처리, 이동하는지 확인하기 위해 확실한 페이지로 설정
        return "redirect";
    }


    // 게시물 상세보기  --> url이 계속 바뀌어야 함 기본 url + diary_seq 값으로! 그래야 게시물마다 상세보기를 할 수가 있으니까
    // 똑같은 틀에 다른 맛 : 슈크림 붕어빵, 단팥 붕어빵 어쩌구 저쩌구 넣는 재료 따라 맛이 바뀌죠?
    // GetMapping에서 PostMapping으로 변경 조회수 올려서 저장해야 하니까 (헛소리 하지마 저장을 왜 하냐 실행될 때마다 카운트만 하면 되는데 되게 졸리신가 봐요;;;)
    @GetMapping(value = "diaryInfo")
    public String getDiaryInfo(HttpServletRequest request, ModelMap modelMap, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + " 게시물 상세보기 컨트롤러(getDiaryInfo) 실행!");

        try {
            // 요청 받아온 것 중에서 "diary_seq"라는 값을 읽어서 CmmUtil파일의 nvl이라는 함수를 통해서 널 값 처리한 다음 변수에 담기
            // 민재 유진 보시오 이렇게 긴 코드는 오른쪽부터 왼쪽으로 읽으라고 누가 알려줬는데 누구였는지가 기억이 안 남 왼쪽 말고 오른쪽부터 읽기!
            // 특정 게시물을 하나 클릭해서 그 정보를 띄우려면 다른 게시물과 내가 클릭한 게시물을 식별할 무언가가 있어야 함 그게 "diary_seq"의 값
            String diarySeq = CmmUtil.nvl(request.getParameter("diary_seq"));


            // 게시물을 클릭했으니 조회수 증가시켜야 됨  --> 아니야!!!!! 이건 함수 실행시켜서 하는 거야!!! 내가 넣지마!!!
//            String read_cnt = CmmUtil.nvl((String)request.getAttribute("read_cnt"));

            // 로그 찍어서 값이 제대로 들어왔나 확인
            log.info("diary_seq : " + diarySeq);

            // DiaryDTO 새로운 객체 생성해서 pDTO에 담기(타입이 DiaryDTO)
            DiaryDTO pDTO = new DiaryDTO();
            // DTO 내 변수에 요청으로 들어온 값 중 diary_seq라는 이름을 가진 변수의 값을 읽어와서 저장
            pDTO.setDiary_seq(diarySeq);

            // Java8부터 지원하는 NPE(Null Point Exception)를 처리 해주는 클래스 -->  서비스에서 넘겨 받은 값(항상 해줘야 함 언제 날아갈 지 몰라서)
            // null값에 예민해지라고 배웠음.
            DiaryDTO rDTO = Optional.ofNullable(diaryService.getDiaryInfo(pDTO)).orElseGet(DiaryDTO::new);

            //북마크 여부 조회를 위해 가져와 요청 dto에 넣음. diary_seq는 이미 집어넣었으므로 user_id만 추가
            String ss_user_id = (String) session.getAttribute("SS_USER_ID");
            log.info("user_id : " + ss_user_id);
            pDTO.setReg_id(ss_user_id); // 다이어리 북마크 여부는 등록자를 기준으로 판별하니 reg_id 변수에 대입

            // 북마크 여부 조회
            String bookmark_yn = diaryService.getBookMarkExists(pDTO);
            log.info(bookmark_yn);

            // 서비스의 조회수 증가 함수 호출(여기서는 서비스를 총 3번 호출함)
            diaryService.updateDiaryReadCnt(pDTO);

            // 위에서 조회한 값을 DTO에 저장(pDTO 아님 rDTO! 페이지로 보낼 값이니까!)
            rDTO.setBookmark_yn(bookmark_yn);

            // 가져온 값 로그 싹 다 찍어보기  --> 이거 아니야 학교가서 다시 확인해
            log.info(rDTO.toString());

//            // 모델 맵에 저 이름을 가진 속성 찾아서 서비스 호출한 결과를 저장 --> 오 이렇게 한 줄로 끝낼 수도 있구나
//            modelMap.addAttribute("rDTO", diaryService.getDiaryInfo(pDTO));
            modelMap.addAttribute("rDTO", rDTO);

            String imgPath;
            if (!ss_user_id.equals(rDTO.getReg_id())) {
                UserInfoDTO userInfoDTO = new UserInfoDTO();
                userInfoDTO.setUser_id(rDTO.getReg_id());
                imgPath = diaryService.getProfilePath(userInfoDTO).getProfile_path();
                log.info("다른 사용자의 프로필 사진을 가져옴 imgPath : " + imgPath);
            } else {
                imgPath = (String) session.getAttribute("SS_PROFILE_PATH");
            }
            modelMap.addAttribute("imgPath", imgPath);

        } catch (Exception e) {
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + " 게시물 상세보기 컨트롤러(getDiaryInfo) 종료!");
        }

        // 게시물 하나 클릭(상세보기) 시 이동할 페이지의 주소
        return "diary/diaryInfo";
    }


    // 게시물 수정페이지 불러오기
    @GetMapping(value = "update_test")
    public String updateDiary() throws Exception {
        log.info(this.getClass().getName() + " 게시물 수정 페이지로 이동 컨트롤러 실행");

        log.info(this.getClass().getName() + " 게시물 수정 페이지로 이동 컨트롤러 종료");

        return "html/update_test";
    }

    // 게시물 수정 작업 요청
    @PostMapping(value = "updateDiary")
    public String updateDiary(HttpServletRequest request, HttpSession session, MultipartFile file) throws Exception {
        log.info(this.getClass().getName() + " 게시물 수정 컨트롤러(updateDairy) 실행");

//        String diary_seq = "8"; // 번호 내가 임의로 설정
//        session.setAttribute("diary_seq", diary_seq);  // 세션에 저장
//        String diarySeq = (String)session.getAttribute("diary_seq");  // 해놓고 가져옴

        String diary_seq = CmmUtil.nvl(request.getParameter("diary_seq"));
        String contents = CmmUtil.nvl(request.getParameter("contents"));
        log.info("contents: " + contents);
        //값이 수정은 되는데 안 들어감 진짜 들어온 게 맞나 확인해봐야겠음  --> 뭐야 안 들어오잖아?  => 해결!

        DiaryDTO pDTO = new DiaryDTO();

        pDTO.setDiary_seq(diary_seq);
        pDTO.setContents(contents);

        diaryService.updateDiaryInfo(pDTO);

        log.info(this.getClass().getName() + " 게시물 수정 컨트롤러(updateDiary)종료!");

        // 게시물 수정 후 이동할 페이지의 주소 --> 임시로 지정해둠
        return "/html/main_test";
    }

    // 게시물 삭제 페이지로 이동4
//    @GetMapping(value="delete_test")
//    public String deleteDiary() throws Exception {
//        log.info(this.getClass().getName() + " 게시물 삭제 페이지로 이동 컨트롤러 실행!");
//
//        log.info(this.getClass().getName() + " 게시물 삭제 페이지로 이동 컨트롤러 종료!");
//        // 삭제할 수 있는 정보를 갖고 았는 페이지
//        return "/html/delete_test";
//    }

    // 게시물 삭제 작업 요청
    @GetMapping(value = "deleteDiaryInfo")
    public String deleteDiaryInfo(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + " 게시물 삭제하기 컨트롤러(deleteDiaryInfo) 실행!");

        try {

//            // 어떤 게시물을 삭제하고 싶다면 그 게시물의 "diary_seq"값을 알아야 함
//            // String diary_seq = CmmUtil.nvl(request.getParameter("diary_seq"));
//            String diary_seq = "10"; 값 고정하기
            // url을 통해 읽어오기
            String diary_seq = CmmUtil.nvl((String) request.getParameter("diary_seq"));
            // DTO의 객체 생성
            DiaryDTO pDTO = new DiaryDTO();
//
//            // DTO가 갖고 있는 변수의 값을 내가 받아온 값으로 설정
            pDTO.setDiary_seq(diary_seq);

            // 서비스의 함수 호출
            diaryService.deleteDiaryInfo(pDTO);

            // 오류가 발생하면 잡기
        } catch (Exception e) {
            log.info(e.toString());
            e.printStackTrace();

            // 오류 발생 유무와 상관 없이 실행이 끝났다면 로그 찍기
        } finally {
            log.info(this.getClass().getName() + " 게시물 삭제하기 컨트롤러(deleteDiaryInfo) 종료!");
        }

        // 게시물 삭제 후 이동할 페이지의 주소 --> 임시
        return "/html/main_test";
    }

    // 좋아요수 증가시키기
    @PostMapping(value = "updateLikeCnt")
    @ResponseBody // ajax로 값을 주고받을 때는 반드시 써야 하는 어노테이션
    public void updateDiaryLikeCnt(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + " 좋아요 수 컨트롤러 (updateLikeCnt) 실행!");

        try {
            // 어떤 게시물의 좋아요수를 증가시키고 싶다면 그 게시물의 "diary_seq"를 알아야 한다.
            // 민재 유진 보고 있다면 여기 꼭 보기 세션에서 가져올 때는 애트리뷰트, 요청값에서 꺼낼때는 파라미터! 애트리뷰트로 써도 에러가 안 나서 놓칠 수 있음!
            String diary_seq = CmmUtil.nvl(request.getParameter("diary_seq"));

            log.info("diary_seq : " + diary_seq);

            // DTO의 객체 생성
            DiaryDTO pDTO = new DiaryDTO();

            // DTO로 부터 받은 변수들 중 diary_seq라는 변수의 값을 내가 받아온 값으로 설정
            pDTO.setDiary_seq(diary_seq);

            // 서비스의 함수 호출
            diaryService.updateDiaryLikeCnt(pDTO);
        } catch (Exception e) {
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + " 좋아요 수 컨트롤러 (updateLikeCnt) 종료!");
        }

        // 게시물의 좋아요수 증가가 끝난 후 이동할 페이지의 주소 -> 없음 페이지 이동 안 함(비동기 전송)
    }

    // 북마크 추가하기
    @PostMapping(value = "insertBookMark")
    @ResponseBody
    public void insertBookMark(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + " 북마크 추가 컨트롤러(insertBookMark)실행!");

        // user_id는 일단 임시로 설정(계속해서 등장하는 소영씨)
//        String user_id = "soyoung";
        String user_id = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
        String diary_seq = CmmUtil.nvl(request.getParameter("diary_seq"));

        // 왜 자꾸 에러나지 그럴 때는 로그부터 찍자
        log.info("user_id : " + user_id);
        log.info("diary_seq : " + diary_seq);

        DiaryDTO pDTO = new DiaryDTO();
        pDTO.setReg_id(user_id);
        pDTO.setDiary_seq(diary_seq);

        // 북마크 추가 작업 요청
        diaryService.insertBookMark(pDTO);

        log.info(this.getClass().getName() + " 북마크 추가 컨트롤러(insertBookMark)종료!");
    }

    // 검색 페이지 불러오기
    @GetMapping(value = "searchFeed")
    public String getSearchFeed(ModelMap modelMap) throws Exception {
        log.info(this.getClass().getName() + " 검색 페이지 가져오기 컨트롤러(getSearchFeed) 실행!");

        try {

            List<DiaryDTO> rList = diaryService.getSearchFeed();

            if (rList == null) {
                rList = new ArrayList<>();
            }

            modelMap.addAttribute("rList", rList);

        } catch (Exception e) {
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            log.info(this.getClass().getName() + " 검색 페이지 가져오기 컨트롤러(getSearchFeed) 종료!");
        }

        return "html/searchFeed";
    }

    // 검색 페이지 결과 인기순으로 정렬
    @GetMapping(value = "sortByLikeCnt")
    @ResponseBody
    public List<DiaryDTO> sortByLikeCnt(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + " 인기순으로 리스트 정렬 요청 컨트롤러(sortByLikeCnt) 실행합니다!");

        List<DiaryDTO> rList = diaryService.sortByLikeCnt();

        log.info(this.getClass().getName() + " 인기순으로 리스트 정렬 요청 컨트롤러(sortByLikeCnt) 종료합니다!");

        return rList;
    }

    // 검색 페이지 결과 최신순으로 정렬
    @GetMapping(value = "sortByDatetime")
    @ResponseBody
    public List<DiaryDTO> sortByDatetime(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + " 최신순으로 리스트 정렬 요청 컨트롤러(sortByDatetime) 실행합니다!");

        List<DiaryDTO> rList = diaryService.sortByDatetime();

        log.info(this.getClass().getName() + " 최신순으로 리스트 정렬 요청 컨트롤러(sortByDatetime) 종료합니다!");

        return rList;
    }

    // 검색 페이지 결과 조회수순으로 정렬
    @GetMapping(value = "sortByReadCnt")
    @ResponseBody
    public List<DiaryDTO> sortByReadCnt(HttpServletRequest request) throws Exception {
        log.info(this.getClass().getName() + " 조회수순으로 리스트 정렬 요청 컨트롤러(sortByReadCnt) 실행합니다!");

        List<DiaryDTO> rList = diaryService.sortByReadCnt();

        log.info(this.getClass().getName() + " 조회수순으로 리스트 정렬 요청 컨트롤러(sortByReadCnt) 종료합니다!");

        return rList;
    }
}


