package kopo.poly.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/search")
@RequiredArgsConstructor
@Controller
public class SearchController {

    @GetMapping("searchFeed")
    public String searchFeed() {
        log.info(this.getClass().getName()+".search/searchFeed Start!!");
        return "html/searchFeed";
    }

}
