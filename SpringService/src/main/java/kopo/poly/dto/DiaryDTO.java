package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DiaryDTO {
    private String diary_seq;   // 게시물 번호
    private String like_cnt;    // 좋아요수
    private String read_cnt;    // 조회수
    private String nft_yn;      // NFT 등록 여부
    private String contents;    // 내용
    private String img_path;    // 다이어리 이미지 경로(로 띄워줌)
    private String reg_id;      // 등록자 아이디
    private String reg_dt;      // 등록일
    private String chg_id;      // 수정자 아이디
    private String chg_dt;      // 수정일
    private String bookmark_yn; // 북마크 여부


    private int diary_cnt;
}
