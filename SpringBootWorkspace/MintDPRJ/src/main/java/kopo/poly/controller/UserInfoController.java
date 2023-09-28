package kopo.poly.controller;

import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IUserInfoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
@Controller
public class UserInfoController {
    private final IUserInfoService userInfoService; // 서비스는 모든 컨트롤러 함수에서 실행시킬 수 있어야 하므로 전역변수로 선언

    /**
     * 회원가입 화면으로 이동
     */
    @GetMapping(value = "signUp")
    public String signUp() {
        log.info(this.getClass().getName() + ".signUp");

        return "user/signUp";
    }

    /**
     * 회원 가입 전 아이디 중복체크하기(Ajax를 통해 입력한 아이디 정보 받음)
     */
    @ResponseBody
    @PostMapping(value = "getUserIdExists")
    public UserInfoDTO getUserIdExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getUserIdExists Start!");

        String user_id = CmmUtil.nvl(request.getParameter("user_id")); // 회원아이디

        log.info("user_id : " + user_id);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUser_id(user_id);

        // 회원아이디를 통해 중복된 아이디인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserIdExists(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".getUserIdExists End!");

        return rDTO;
    }

    /**
     * 회원 가입 전 이메일 중복체크하기(Ajax를 통해 입력한 아이디 정보 받음)
     * 유효한 이메일인 확인하기 위해 입력된 이메일에 인증번호 포함하여 메일 발송
     */
    @ResponseBody
    @PostMapping(value = "getUserEmailExists")
    public UserInfoDTO getUserEmailExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getUserEmailExists Start!");

        String user_email = CmmUtil.nvl(request.getParameter("user_email")); // 회원아이디

        log.info("user_email : " + user_email);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUser_email(EncryptUtil.encAES128CBC(user_email));

        // 입력된 이메일이 중복된 이메일인지 조회
//        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserEmailExists(pDTO)).orElseGet(UserInfoDTO::new);

        UserInfoDTO rDTO = userInfoService.getUserEmailExists(pDTO);

        if (rDTO == null) {
            log.info("rDTO가 널이라서 강제로 메모리에 올림");
            rDTO = new UserInfoDTO();
        }

        log.info(this.getClass().getName() + ".getUserEmailExists End!");

        return rDTO;
    }

    /**
     * 회원가입 로직 처리
     */
    @PostMapping(value = "insertUserInfo")
    public String insertUserInfo(HttpServletRequest request, ModelMap model) throws Exception {
        // request는 url 요청으로부터 넘어온 값을 키를 통해 꺼내고, ModelMap은 결과 페이지에 데이터를 넘겨줌
        log.info(this.getClass().getName() + ".insertUserInfo start!");
        int res;
        String msg = ""; //회원가입 결과에 대한 메시지를 전달할 변수
        String url = ""; //회원가입 결과에 대한  URL을 전달할 변수

        //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수
        UserInfoDTO pDTO = null;

        try {

            /*
             * #######################################################
             *        웹(회원정보 입력화면)에서 받는 정보를 String 변수에 저장 시작!!
             *
             *    무조건 웹으로 받은 정보는 DTO에 저장하기 위해 임시로 String 변수에 저장함
             * #######################################################
             */
            String user_id = CmmUtil.nvl(request.getParameter("user_id")); //아이디
            // 스프링에서 지원하는 request 객체의 getParameter 함수가 html form 태그의 input 태그의 name값을 키 값으로 가져옴
            // cmmutil에 정의된 nvl 함수로 null 처리함(null 처리는 null을 다른 값으로 바꿔주는 것)
            String user_name = CmmUtil.nvl(request.getParameter("user_name")); //이름
            String user_pwd = CmmUtil.nvl(request.getParameter("user_pwd")); //비밀번호
            String user_email = CmmUtil.nvl(request.getParameter("user_email")); //이메일
            String user_nick = CmmUtil.nvl(request.getParameter("user_nick")); //닉네임

            /*
             * #######################################################
             *        웹(회원정보 입력화면)에서 받는 정보를 String 변수에 저장 끝!!
             *
             *    무조건 웹으로 받은 정보는 DTO에 저장하기 위해 임시로 String 변수에 저장함
             * #######################################################
             */

            /*
             * #######################################################
             * 	 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함
             * 						반드시 작성할 것
             * #######################################################
             * */
            log.info("user_id : " + user_id);
            log.info("user_name : " + user_name);
            log.info("user_pwd : " + user_pwd);
            log.info("user_email : " + user_email);
            log.info("user_nick : " + user_nick);
//            log.info("addr2 : " + addr2);

            /*
             * #######################################################
             *        웹(회원정보 입력화면)에서 받는 정보를 DTO에 저장하기 시작!!
             *
             *        무조건 웹으로 받은 정보는 DTO에 저장해야 한다고 이해하길 권함
             * #######################################################
             */

            //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO(); // userinfodto 객체를 생성해서 pdtd라는 변수에 담음

            pDTO.setUser_id(user_id); // url값으로부터 읽어온 (user_id)를 setUser_id 함수로 pDTO 객체에 집어넣음
            pDTO.setUser_name(user_name);
            pDTO.setUser_nick(user_nick);

            //비밀번호는 절대로 복호화되지 않도록 해시 알고리즘으로 암호화함
            pDTO.setUser_pwd(EncryptUtil.encHashSHA256(user_pwd));

            //민감 정보인 이메일은 AES128-CBC로 암호화함
            pDTO.setUser_email(EncryptUtil.encAES128CBC(user_email));
//            pDTO.setAddr1(addr1);
//            pDTO.setAddr2(addr2);

            /*
             * #######################################################
             *        웹(회원정보 입력화면)에서 받는 정보를 DTO에 저장하기 끝!!
             *
             *        무조건 웹으로 받은 정보는 DTO에 저장해야 한다고 이해하길 권함
             * #######################################################
             */

            /*
             * 회원가입
             * */
            res = userInfoService.insertUserInfo(pDTO);
            // userInfoService라는 서비스 객체의 insertUserInfo 함수에 pDTO값을 넣어서 Service에서 실행


            log.info("회원가입 결과(res) : " + res);

            if (res == 1) {
                msg = "회원가입되었습니다.";
                url = "/user/login";
            } else {
                msg = "오류로 인해 회원가입이 실패하였습니다.";
                url = "/user/signUp";
            }
        } catch (DuplicateKeyException e) { //PK인 USER_ID가 중복되어 에러가 발생했다면
            msg = "이미 가입된 아이디입니다. 다른 아이디로 변경 후 다시 시도해주세요.";
            url = "/user/signUp";
            log.info(e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            //저장이 실패되면 사용자에게 보여줄 메시지
            msg = "시스템 오류로 실패하였습니다. 다시 시도해주세요.";
            url = "/user/signUp";
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            log.info("출력할 메세지 : " + msg);
            log.info("이동할 경로 : " + url);
            model.addAttribute("msg", msg);
            // 실제 전송할 메세지를 키값인 "msg"을 통해 밸류값 msg를 다음 페이지로 전달함
            model.addAttribute("url", url);

            log.info(this.getClass().getName() + ".insertUserInfo End!");
        }

        return "redirect";
        // return "다음으로 보여줄 페이지경로"
        // 페이지 이동과 팝업창을 띄우기 위해 msg와 url을 redirect로 보내줌.
    }

    /**
     * 로그인을 위한 입력 화면으로 이동
     */
    @GetMapping(value = "login")
    public String login() {
        log.info(this.getClass().getName() + ".user/login Start!");
        log.info(this.getClass().getName() + ".user/login End!");
        return "user/signUp";
    }
    @GetMapping(value = "login2")
    public String login2() {
        log.info(this.getClass().getName() + ".user/login2 Start!");
        log.info(this.getClass().getName() + ".user/login2 End!");
        return "signUp";
    }


    /**
     * 로그인 처리 및 결과 알려주는 화면으로 이동
     */
    @PostMapping(value = "loginProc")
    public String loginProc(HttpServletRequest request, ModelMap model, HttpSession session) {

        log.info(this.getClass().getName() + ".loginProc Start!");

        String msg = ""; //로그인 결과에 대한 메시지를 전달할 변수
        String url = "";
        //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수
        UserInfoDTO pDTO = null;

        try {

            String user_id = CmmUtil.nvl(request.getParameter("user_id")); //아이디
            String user_pwd = CmmUtil.nvl(request.getParameter("user_pwd")); //비밀번호

            log.info("user_id : " + user_id);
            log.info("user_pwd : " + user_pwd);

            //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO();

            pDTO.setUser_id(user_id);

            //비밀번호는 절대로 복호화되지 않도록 해시 알고리즘으로 암호화함
            pDTO.setUser_pwd(EncryptUtil.encHashSHA256(user_pwd));
            // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기 위한 userInfoService 호출하기
            UserInfoDTO rDTO = userInfoService.getLogin(pDTO);
            /*
             * 로그인을 성공했다면, 회원아이디 정보를 session에 저장함
             *
             * 세션은 톰켓(was)의 메모리에 존재하며, 웹사이트에 접속한 사람(연결된 객체)마다 메모리에 값을 올린다.
             * 			 *
             * 예) 톰켓에 100명의 사용자가 로그인했다면, 사용자 각각 회원아이디를 메모리에 저장하며.
             *    메모리에 저장된 객체의 수는 100개이다.
             *    따라서 과도한 세션은 톰켓의 메모리 부하를 발생시켜 서버가 다운되는 현상이 있을 수 있기때문에,
             *    최소한으로 사용하는 것을 권장한다.
             *
             * 스프링에서 세션을 사용하기 위해서는 함수명의 파라미터에 HttpSession session 존재해야 한다.
             * 세션은 톰켓의 메모리에 저장되기 때문에 url마다 전달하는게 필요하지 않고,
             * 그냥 메모리에서 부르면 되기 때문에 화면, controller에서 쉽게 불러서 쓸수 있다.
             * */
            if (CmmUtil.nvl(rDTO.getUser_id()).length() > 0) { //로그인 성공
                /*
                 * 세션에 회원아이디 저장하기, 추후 로그인여부를 체크하기 위해 세션에 값이 존재하는지 체크한다.
                 * 일반적으로 세션에 저장되는 키는 대문자로 입력하며, 앞에 SS를 붙인다.
                 *
                 * Session 단어에서 SS를 가져온 것이다.
                 */
                session.setAttribute("SS_USER_ID", user_id);
                session.setAttribute("SS_USER_NAME", CmmUtil.nvl(rDTO.getUser_name()));
                session.setAttribute("SS_PROFILE_PATH", CmmUtil.nvl(rDTO.getProfile_path()));
                session.setAttribute("SS_USER_INTRO", CmmUtil.nvl(rDTO.getUser_intro()));
                log.info("로그인 결과 : "+(rDTO.toString()));
                //로그인 성공 메세지와 이동할 경로의 url
                msg = "로그인이 성공했습니다. \n" + rDTO.getUser_nick() + "님 환영합니다.";
                url = "/diary/getMyDiaryList";

            } else {
                msg = "아이디와 비밀번호가 올바르지 않습니다.";
                url = "/user/login";
            }

        } catch (Exception e) {
            //저장이 실패되면 사용자에게 보여줄 메시지
            msg = "시스템 문제로 로그인이 실패했습니다.";
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            model.addAttribute("msg", msg);
            model.addAttribute("url", url);

            log.info(this.getClass().getName() + ".loginProc End!");
        }

        return "redirect";
    }


    /**
     * 아이디 찾기 화면
     */
    @GetMapping(value = "findId")
    public String findId() {
        log.info(this.getClass().getName() + ".findId Start!");

        log.info(this.getClass().getName() + ".findId End!");

        return "user/findId";

    }

    /**
     * 아이디 찾기 로직 수행
     */
    @PostMapping(value = "searchUserIdProc")
    public String searchUserIdProc(HttpServletRequest request, ModelMap model) throws Exception {
        log.info(this.getClass().getName() + "아이디찾기 Start!");
        /*
         * ########################################################################
         *        웹(회원정보 입력화면)에서 받는 정보를 String 변수에 저장!!
         *
         *    무조건 웹으로 받은 정보는 DTO에 저장하기 위해 임시로 String 변수에 저장함
         * ########################################################################
         */

        String user_email = CmmUtil.nvl(request.getParameter("user_email")); // 이메일
        /*
         * ########################################################################
         * 	 반드시, 값을 받았으면, 꼭 로그를 찍어서 값이 제대로 들어오는지 파악해야함
         * 						반드시 작성할 것
         * ########################################################################
         * */

        log.info("user_email : " + user_email);
        /*
         * ########################################################################
         *        웹(회원정보 입력화면)에서 받는 정보를 DTO에 저장하기!!
         *
         *        무조건 웹으로 받은 정보는 DTO에 저장해야 한다고 이해하길 권함
         * ########################################################################
         */


        UserInfoDTO pDTO = new UserInfoDTO();
//        pDTO.setUser_name(user_name);
        pDTO.setUser_email(EncryptUtil.encAES128CBC(user_email));

        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserId(pDTO))
                .orElseGet(UserInfoDTO::new);

        String user_id = CmmUtil.nvl(rDTO.getUser_id());

        String msg; //아이디 찾기 결과에 대한 메시지를 전달할 변수, 변수 선언을 안하면 데이터를 쓸 수가 없음
        String url;


        log.info("user_id : " + rDTO.getUser_id());


        if (user_id.length() != 0) {
            msg = "회원님의 아이디는 " + rDTO.getUser_id() + " 입니다.";
            url = "/user/login";
        } else {
            msg = "아이디를 찾을 수 없습니다.";
            url = "/user/findId";
        }

        model.addAttribute("msg", msg);
        model.addAttribute("url", url);

        log.info(this.getClass().getName() + "아이디찾기 End!");

        return "redirect";
    }


    /**
     * 비밀번호 찾기 화면
     */
    @GetMapping(value = "findPwd")
    public String findPwd(HttpSession session) {
        log.info(this.getClass().getName() + ".findPwd Start!");

        // 강제 URL 입력 등 오는 경우가 있어 세션 삭제
        // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_USER_PWD 세션 삭제
        session.setAttribute("NEW_USER_PWD", "");
        session.removeAttribute("NEW_USER_PWD");

        log.info(this.getClass().getName() + ".user/searchUser_pwd End!");

        return "user/findPwd";

    }

    /**
     * 비밀번호 찾기 로직 수행
     * <p>
     * 아이디, 이름, 이메일 일치하면, 비밀번호 재발급 화면 이동
     */
    @PostMapping(value = "searchUserPwdProc")
    public String searchUserPwdProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".user/searchUserPwdProc Start!");


        String user_id = CmmUtil.nvl(request.getParameter("user_id")); // 아이디
        String user_email = CmmUtil.nvl(request.getParameter("user_email")); // 이메일

        log.info("user_id : " + user_id);
        log.info("user_email : " + user_email);


        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUser_id(user_id); // 아이디가 pk값이므로 아이디만 일치하면 비밀번호 찾기 가능하므로 id값만 set한다.
        pDTO.setUser_email(EncryptUtil.encAES128CBC(user_email));

        // 아이디 있는지 조회하고 있으면 임시 비번 발송
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.checkUserId(pDTO)).orElseGet(UserInfoDTO::new);

        String user_pwd = CmmUtil.nvl(String.valueOf(rDTO.getAuthNumber())); //int인 authnumber를 string으로 바꾼 후 비밀번호에 넣음

        log.info("user_pwd : " + user_pwd);

        String msg; //비밀번호 찾기 결과에 대한 메시지를 전달할 변수, 변수 선언을 안하면 데이터를 쓸 수가 없음
        String url;

        if (user_pwd.length() == 8) { // 인증번호가 정상적으로 발송됐다면 8자리이므로
            msg = "회원님의 이메일로 임시 비밀번호를 전송했습니다.";
            url = "/user/changePwd";
        } else {
            msg = "일치하는 정보가 없습니다.";
            url = "/user/findPwd";
        }

        model.addAttribute("msg", msg);
        model.addAttribute("url", url);


        log.info("임시비번을 새 비밀번호로 변경 시작");

        UserInfoDTO userInfoDTO = new UserInfoDTO();

        userInfoDTO.setUser_id(user_id);
        userInfoDTO.setUser_pwd(EncryptUtil.encHashSHA256(user_pwd)); //db에 암호화된 비번이 저장되어있으므로 암호화 시켜줌

        userInfoService.newUserPwdProc(userInfoDTO);

        log.info("임시비밀번호를 새로운 비밀번호로 변경 완료");

        // 비밀번호 재생성하는 화면은 보안을 위해 반드시 NEW_USER_PWD 세션이 존재해야 접속 가능하도록 구현
        // user_id 값을 넣은 이유는 비밀번호 재설정하는 newUserPwdProc 함수에서 사용하기 위함
        // 왜? findPwd의 과정을 거치지않은채 비밀번호를 변경할수도 있으므로 인증을 받은 사용자만 비밀번호를 바꿀 수 있도록 하기위해

        session.setAttribute("NEW_USER_ID", user_id);
        // 비밀번호를 변경하고자하는 회원의 아이디를 세션에 등록
        // 왜? 비밀번호 최종 수정할 때 사용자가 입력한 비밀번호를  대조해서, 실제 메일로 발송된 인증번호와 일치하는지 확인하고,
        // 최종 비밀번호 업데이트 쿼리에도 사용됨 (비밀번호를 변경할 때, 대상 아이디를 지정해서 변경해야 하기 때문)

        log.info(this.getClass().getName() + ".user/searchUserPwdProc End!");

        return "redirect";

    }

    /**
     * 비밀번호 바꾸기 화면
     */
    @GetMapping(value = "changePwd")
    public String changePwd() {
        log.info(this.getClass().getName() + ".changePwd Start!");
        log.info(this.getClass().getName() + ".changePwd End!");
        return "user/changePwd";
    }

    /**
     * 비밀번호 바꾸기 수행
     */
    @PostMapping(value = "newUserPwdProc")
    public String newUserPwdProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + "비밀번호 바꾸기 Start!");

        String msg; // 웹에 보여줄 메시지
        String url; // 이동할 url

        // 정상적인 접근인지 체크, 비밀번호 업데이트하려는 사용자의 아이디를 세션에서 불러옴
        String new_user_id = CmmUtil.nvl((String) session.getAttribute("NEW_USER_ID"));
        log.info("user_id : " + new_user_id);

        if (new_user_id.length() > 0) { //정상 접근

            String user_pwd = CmmUtil.nvl(request.getParameter("user_pwd")); // 기존 비밀번호
            log.info("입력받은 임시 비밀번호 : " + user_pwd);

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUser_id(new_user_id);
            pDTO.setUser_pwd(EncryptUtil.encHashSHA256(user_pwd));


            // 임시 비번과 세션에 저장된 아이디로 로그인 로직 수행
            UserInfoDTO userInfoDTO = userInfoService.getLogin(pDTO);

            if (userInfoDTO.getUser_id().length() != 0) {
                log.info("비밀번호가 일치합니다.");

                String new_user_pwd = CmmUtil.nvl(request.getParameter("new_user_pwd")); // 신규 비밀번호
                log.info("입력받은 신규 비밀번호 : " + new_user_pwd);

                pDTO.setUser_pwd(EncryptUtil.encHashSHA256(new_user_pwd)); // 신규 비밀번호 암호화 set

                userInfoService.newUserPwdProc(pDTO);

                // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_USER_ID 세션 삭제
                session.setAttribute("NEW_USER_ID", "");
                session.removeAttribute("NEW_USER_ID");

                msg = "비밀번호 변경이 성공하였습니다.\n 로그인 페이지로 이동합니다.";
                url = "/user/login";

            } else { // 비밀번호가 일치하지 않을 때
                msg = "기존 비밀번호가 일치하지 않습니다.";
                url = "/user/changePwd";
            }


        } else { // 비정상 접근
            msg = "비정상 접근입니다.";
            url = "/user/changePwd";
        }

        model.addAttribute("msg", msg);
        model.addAttribute("url", url);

        log.info(this.getClass().getName() + "비밀번호 바꾸기 End!");

        return "redirect";

    }

    @GetMapping(value = "logout")
    public String logout(HttpSession session, ModelMap modelMap) {
        log.info(this.getClass().getName() + ".user/logout Start!");
        String msg = "로그아웃되었습니다.";
        String url = "/user/login";
        session.invalidate();
        modelMap.addAttribute("msg", msg);
        modelMap.addAttribute("url", url);
        log.info(this.getClass().getName() + ".user/logout End!");
        return "redirect";
    }

}
