package kopo.poly.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/nft")
@RequiredArgsConstructor
@Controller
public class NFTController {

    @GetMapping(value = "minting")
    public String minting() {
        log.info(this.getClass().getName()+".minting start");
        return "nft/minting";
    }
}
