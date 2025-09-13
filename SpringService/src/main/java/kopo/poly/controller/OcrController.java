package kopo.poly.controller;

import kopo.poly.service.IOcrService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RequiredArgsConstructor
@Controller
public class OcrController {

    private final IOcrService ocrService;

    @GetMapping("/ocrProc")
    public String ocrProc(@RequestParam MultipartFile multipartFile,
                          HttpServletRequest request, ModelMap modelMap, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".getOcrText Start!!");

        String url = "";
        String msg = "";

        try {
            log.info("request = {}", request);
            log.info("multipartFile = {}", multipartFile);

            String userSeq = CmmUtil.nvl((String) session.getAttribute("sessionNo"));
            log.info("userSeq : " + userSeq);
            String nowDate = DateUtil.getDateTime("yyyyMMdd-hhmmss");

            //저장한 파일 경로와 user정보를 전송하여 문제 생성
            String response = ocrService.detectText(multipartFile);

        } catch (Exception e) {

            msg = "오류입니다. 다시 시도해주세요";
            url = "/user/insertDocPage";
            log.info(e.toString());

        } finally {

            modelMap.addAttribute("url", url);
            modelMap.addAttribute("msg", msg);
            log.info(this.getClass().getName() + ".saveFileText End!");

        }

        return "/redirect";
    }

}
