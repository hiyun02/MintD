package kopo.poly.controller;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@RequestMapping("/main")
@RequiredArgsConstructor
@Controller
public class MainController {
    @GetMapping("/redirect")
    public String redirectPage(HttpServletRequest request, ModelMap modelMap) throws Exception {
        log.info(this.getClass().getName() + ".redirect 페이지 보여주는 함수 실행");
        String msg = CmmUtil.nvl(request.getParameter("msg"), "로그인해주세요.");
        modelMap.addAttribute("msg", msg);
        modelMap.addAttribute("url", "/user/login");
        return "/redirect";
    }

    @GetMapping("/speech")
    public String index() throws Exception {
        log.info(this.getClass().getName() + ".index 페이지 보여주는 함수 실행");
        return "speech";
    }

    @GetMapping("/imgUpload")
    public String imgUpload() throws Exception {
        log.info(this.getClass().getName() + ".imgUpload 페이지 보여주는 함수 실행");
        return "/imgUpload";
    }

}
