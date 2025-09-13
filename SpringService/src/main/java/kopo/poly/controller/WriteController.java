package kopo.poly.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/write")
@Controller
@RequiredArgsConstructor
public class WriteController {

    private String status;
    private String title;
    private String msg;
    private String url;
    private String redirect = "redirect";

    @GetMapping(value = "choiceWriteMethod")
    public String choiceWriteMethod() {
        log.info(this.getClass().getName() + ".choiceWriteMethod");
        return "write/choiceWriteMethod";
    }

    @GetMapping(value = "choiceKeyWord")
    public String choiceKeyWord() {
        log.info(this.getClass().getName() + ".choiceKeyWord");
        return "write/choiceKeyWord";
    }

    @GetMapping(value = "choiceSize")
    public String choiceSize() {
        log.info(this.getClass().getName() + ".choiceSize");
        return "write/choiceSize";
    }

    @GetMapping(value = "confirmDiaryText")
    public String confirmDiaryText() {
        log.info(this.getClass().getName() + ".confirmDiaryText");
        return "write/confirmDiaryText";
    }
    @GetMapping(value = "imgWrite")
    public String imgWrite() {
        log.info(this.getClass().getName() + ".confirmDiaryText");
        return "write/imgWrite";
    }

    @GetMapping(value = "postWriting")
    public String postWriting(ModelMap modelMap) {
        log.info(this.getClass().getName() + ".postWriting");
        return "write/postWriting";
    }

    @GetMapping(value = "speech")
    public String speech() {
        log.info(this.getClass().getName() + ".speech");
        return "write/speech";
    }

    @GetMapping(value = "textWrite")
    public String textWrite() {
        log.info(this.getClass().getName() + ".textWrite");
        return "write/textWrite";
    }

    @GetMapping(value = "voiceWrite")
    public String voiceWrite() {
        log.info(this.getClass().getName() + ".voiceWrite");
        return "write/voiceWrite";
    }

    @GetMapping(value = "makingKeyword")
    public String makingKeyword(ModelMap modelMap) {
        log.info(this.getClass().getName() + ".makingKeyword");
        String url = "/write/choiceKeyWord";
        String msg = "성공적으로 키워드를 추출하였습니다.";
        modelMap.addAttribute("msg", msg);
        modelMap.addAttribute("url", url);
        return "redirect";
    }

    @GetMapping(value = "upload")
    public String upload(ModelMap modelMap) {
        log.info(this.getClass().getName() + ".upload");
        String url = "/write/confirmDiaryText";
        String msg = "문자 인식에 성공하였습니다.";
        modelMap.addAttribute("msg", msg);
        modelMap.addAttribute("url", url);
        return "redirect";
    }
}
