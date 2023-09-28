package kopo.poly.service;

import kopo.poly.dto.DiaryDTO;
import kopo.poly.dto.UserInfoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IDiaryService {

    // 게시물 목록 가져오기
    List<DiaryDTO> getDiaryList() throws Exception;

    // 나의 게시물 목록 가져오기
    List<DiaryDTO> getMyDiaryList(DiaryDTO pDTO) throws Exception;

    // 게시물 추가하기
    void insertDiaryInfo(DiaryDTO pDTO, MultipartFile file) throws Exception;

    // 게시물 상세보기
    DiaryDTO getDiaryInfo(DiaryDTO pDTO) throws Exception;

    // 게시물 수정하기 --> 이거 아직 하지말 것!
    void updateDiaryInfo(DiaryDTO pDTO) throws Exception;

    // 게시물 삭제하기
    void deleteDiaryInfo(DiaryDTO pDTO) throws Exception;

    // 게시물 좋아요수 증가
    void updateDiaryLikeCnt(DiaryDTO pDTO) throws Exception;

    // 게시물 조회수 증가
    void updateDiaryReadCnt(DiaryDTO pDTO) throws Exception;

    // 북마크 추가
    void insertBookMark(DiaryDTO pDTO) throws Exception;

    // 북마크 여부 조회
    String getBookMarkExists(DiaryDTO pDTO) throws Exception;

    // NFT 다이어리 리스트 가져오기
    List<DiaryDTO> getNFTDiaryList(DiaryDTO pDTO) throws Exception;

    // 보관함 리스트 가져오기 전 데이터 조회(북마크 테이블)
    List<DiaryDTO> getBookMarkFind(DiaryDTO pDTO) throws Exception;

    // 보관함 리스트 가져오기
    List<DiaryDTO> getBookMarkList(DiaryDTO pDTO) throws Exception;

    // 검색 페이지 가져오기
    List<DiaryDTO> getSearchFeed() throws Exception;

    // 검색 페이지에서 인기순으로 정렬
    List<DiaryDTO> getSortByPopularity() throws Exception;

    // 검색 페이지에서 최신순으로 정렬
    List<DiaryDTO> getSortByDate() throws Exception;

    // 검색 페이지에서 조회수순으로 정렬
    List<DiaryDTO> getSortByViewCount() throws Exception;

    //게시글 수 카운트
    int getMyDiaryCnt(DiaryDTO pDTO) throws Exception;

    //프로필사진
    UserInfoDTO getProfilePath(UserInfoDTO pDTO) throws Exception;
}
