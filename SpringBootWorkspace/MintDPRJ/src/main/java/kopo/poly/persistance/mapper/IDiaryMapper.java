package kopo.poly.persistance.mapper;

import kopo.poly.dto.DiaryDTO;
import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IDiaryMapper {
    // 게시물 목록 가져오기(요청만 하면 됨 뭘 보내줄 필요 없음, 난 받아오기만 하면 됨)
    List<DiaryDTO> getDiaryList() throws Exception;

    // 나의 게시물 목록 가져오기(내 정보를 보내야 내가 등록한 게시물들을 가져올 수가 있음, 그리고 받아도 와야 함)
    List<DiaryDTO> getMyDiaryList(DiaryDTO pDTO) throws Exception;

    // 게시물 추가하기(추가하려면 로그인이 되어 있어야 함, 내 정보 보내야한다는 뜻, 뭐 안 받아와도 됨 넣기만 하면 됨)
    void insertDiaryInfo(DiaryDTO pDTO) throws Exception;

    // 게시물 상세보기(상세보기로 가려면 게시물 식별키(diary_seq), 그리고 화면에 띄울 모든 데이터를 받아와야 함)
    DiaryDTO getDiaryInfo(DiaryDTO pDTO) throws Exception;

    // 게시물 수정하기(게시물을 수정하려면 로그인이 되어 있어야 함, 내 정보 보내야한다는 뜻)
    void updateDiaryInfo(DiaryDTO pDTO) throws Exception;

    // 게시물 삭제하기(삭제하려면 로그인이 되어 있어야 함, 내 정보 보내야한다는 뜻)
    void deleteDiaryInfo(DiaryDTO pDTO) throws Exception;

    // 좋아요수 증가
    void updateDiaryLikeCnt(DiaryDTO pDTO) throws Exception;

    // 조회수 증가(상세보기로 이동 할 때마다 증가하도록 해두었음)
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
    DiaryDTO getMyDiaryCnt(DiaryDTO pDTO) throws Exception;


    //프로필사진
    UserInfoDTO getProfilePath(UserInfoDTO pDTO) throws Exception;
}
