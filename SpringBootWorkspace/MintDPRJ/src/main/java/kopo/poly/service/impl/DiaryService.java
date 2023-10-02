package kopo.poly.service.impl;

import kopo.poly.dto.DiaryDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.persistance.mapper.IDiaryMapper;
import kopo.poly.service.IDiaryService;
import kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class DiaryService implements IDiaryService {

    // 서비스 파일 내에서 매퍼를 호출하기 위해서 서비스가 실행되는 동안 메모리에 같이 올려두기
    private final IDiaryMapper diaryMapper;

    // 게시물 목록 가져오기
    @Override
    public List<DiaryDTO> getDiaryList() throws Exception {
        log.info(this.getClass().getName() + " 게시물 목록 가져오기 서비스(getDiaryList)실행");

        // 매퍼의 함수를 호출한 다음 받아온 값 변수에 저장(변수는 리스트 타입을 가짐)
        List<DiaryDTO> rList = diaryMapper.getDiaryList();

        // 리스트의 길이는 "사이즈라고 말함"(배열이 length)
        // 매퍼에게 작업을 요청한 후 받아온 결과 수 == 가져온 게시물의 개수(rList의 개수만큼 데이터를 가져온 것)
        log.info("rList의 개수는: " + rList.size());

        log.info(this.getClass().getName() + " 게시물 목록 가져오기 서비스(getDiaryList)종료!");

        // 매퍼 호출하고 받아온 결과값 돌려주기, 그래야 서비스가 받아서 컨트롤러에게 돌려주고 컨트롤러가 띄워줄 수 있음.
        return rList;
    }

    // 나의 게시물 목록 가져오기
    @Override
    public List<DiaryDTO> getMyDiaryList(DiaryDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + " 내 게시물 목록 가져오기 서비스(getMyDiaryList)실행!");
        // Who am I?(누구의 아이디가 들어왔는가)
        log.info("컨트롤러에서 넘어온 아이디 확인 : " + pDTO.getReg_id());

        // 매퍼의 함수 호출한 다음 받아온 값 변수에 저장하기(변수는 리스트 타입)
        List<DiaryDTO> rList = diaryMapper.getMyDiaryList(pDTO);

        // 리스트의 길이 == 내가 받아온 리스트의 개수
        log.info("내가 작성한 리스트의 개수 : " + rList.size());

        log.info(this.getClass().getName() + " 내 게시물 목록 가져오기 서비스(getMyDiaryList)종료!");

        // 매퍼한테 받아온 값 컨트롤러로 돌려주기, 그래야 컨트롤러에서 웹으로 전송할 수 있음
        return rList;
    }

    // @Value 어노테이션으로 properties에 미리 지정해둔 파일 저장 경로를 불러와서 변수에 저장하기(업로드한 이미지가 저장된 위치)
    @Value("${file.upload-dir}")
    private String uploadImagePath;

    // 게시물 추가하기(이미지 저장) --> 게시물 추가는 데이터베이스에 저장하는 작업 이전에, 업로드된 파일(이미지)을 지정한 경로에 저장하는 것이 먼저!
    // 기기에 저장이 안 됐으면 데이터베이스에 경로를 저장해도 이미지를 불러올 수가 없으니까(허위 경로!)
    // 무언가를 CRUD 할 때는 꼭 이 어노테이션을 사용해야 함(데이터베이스 관련 작업들을 한번에 묶어서 처리함. 전부 성공/전부 실패여야만 작동)
    @Transactional
    @Override
    // 컨트롤러에서 보낸 정보 받아서 작업 실행
    public void insertDiaryInfo(DiaryDTO pDTO, MultipartFile file) throws Exception {
        log.info(this.getClass().getName() + " 게시물 추가하기 서비스(insertDiaryInfo)실행!");

        try {

            // uploadImagePath(지정해둔 경로)에 날짜(DateUtil의 함수 호출해서 내 마음대로 형식 바꾼다음)와 시간, 원본파일명을 더해서 기기에 "저장"
            // 파일의 함수 호출해서 새 파일을 만드는데 그 새 파일은 ~~~를 가진 파일인 것!
            file.transferTo(new File(uploadImagePath + DateUtil.getDateTime("yyyyMMdd_HHmmss_") + file.getOriginalFilename()));

            // 업로드한 이미지의 위치(경로)와 업로드 날짜, 원본 파일명을 합쳐서 upLoadImgInfo에 저장(저장될 최종 파일명은 날짜 + 원본 파일명)
            // 위에서 파일 생성할 때 매개변수로 넣었던 것들을 그대로 갖는 변수를 하나 만드는 것임 위에서 만든 게 파일의 정보였다면 여기서 만드는 건 파일의 이름
            String upLoadImgInfo = uploadImagePath + DateUtil.getDateTime("yyyyMMdd_HHmmss_") + file.getOriginalFilename();
            log.info("추가된 파일의 정보는: {}", upLoadImgInfo);

            // 사용자가 지정한 경로에 해당하는 파일이나 디렉토리를 다루기 위해 File 객체를 생성(File이라는 클래스가 있는데 이 클래스의 객체를 생성할 것)
            // File은 변수명, 사용자가 지정한 경로(upLoadImgInfo)에 해당하는 파일 또는 디렉토리를 저장함(나타냄)
            // 생성자를 호출할 때 사용자가 지정한 경로(upLoadImgInfo)를 매개변수로 전달하여 생성과 동시에 파일 객체를 초기화시킴
            File File = new File(upLoadImgInfo);

            // .exists()는 파일 또는 디렉토리가 존재하는지 여부를 확인하는 함수이며 존재하는 경우 true를, 존재하지 않는 경우 false를 반환함
            //  false일 경우(존재하지 않는다면) 폴더를 새로 생성함(선생님 폴더가 없는데 저장을 어떻게 해요 그쵸?)
            if (!File.exists()) {
                File.mkdirs();
                log.info("폴더가 생성되었습니다.");
            } else {
                log.info("폴더가 이미 생성되어 있습니다.");
            }

            /*
             아래 싹 주석처리한 부분이 원래 파일명을 생성할 때 썼던 건데 다른 방법으로도 가능하길래 바꿈(위에 있음)
             근데 왜 안 지웠냐면 유진 민재 나중에 보라고(하 정말 너무 친절하죠? 네 알아요 감사합니다 여러분 행복하세요)

             StringBuilder는 가변적인 문자열을 다룰 때 사용되는 클래스, 한 번 생성된 StringBuilder 객체는 내부의 문자열을 수정할 수 있음
             즉, 문자열을 내 맘대로 넣었다 뺐다 요리조리 다룰 수 있다는 것!
             StringBuilder 객체를 생성하고, 변수명을 imgFileName으로 지정함
             하지만 이미 위에서 저장된 이름을 그대로 파일명으로 만들어서 변수에 담아놨기 때문에 StringBuilder 자체를 안 써도 된다!!
             StringBuilder imgFileName = new StringBuilder();
             이미지 경로 + '-' + 날짜 + .png --> 파일명
             imgFileName.append(pDTO.getImg_path());             // dto에서 경로를 갖고 있는 변수의 값을 가져오고
             imgFileName.append(upLoadImgInfo);                  // 이 변수가 가진 값을 그 뒤에 더하고
             imgFileName.append("_");                            // _ 이거 그 뒤에 추가하고
             imgFileName.append(DateUtil.getDateTime("HHmmss")); // 시간 추가한 뒤
             imgFileName.append(".png");                         // 확장자까지 붙임(정말 길죠? 네 그래서 안 썼어요)

            // 위에서 생성한 파일명을 문자열로 바꿔서 fileName이라는 변수에 저장 --> 이것도 할 필요 없음
            // String fileName = imgFileName.toString();
             */


            log.info("저장된 이미지의 파일명: {}", upLoadImgInfo);


//            unix 기반 디렉터리 구분자는 "/"
//            \\ -> window 기반 디렉터리 구분자(아래에 + 뒤에 원래 \\ 들어가있었음, 안 보인다고? 당연하지 내가 빼버렸으니까~!)
            // 사실 이거 안 써도 되는데 그냥...
            String fullFilePath = uploadImagePath + upLoadImgInfo;
            log.info("저장된 파일의 경로와 파일명 : {}", fullFilePath);

            // DTO에서 img_path를 담는 변수의 값을 내가 만든 파일명으로 변경
            pDTO.setImg_path(upLoadImgInfo);

            // 컨트롤러가 보내준 값 변경하고 매퍼한테 보내면서 함수 호출
            diaryMapper.insertDiaryInfo(pDTO);

        } catch (Exception e) {
            log.info(e.toString());
            e.printStackTrace();
        } finally {
            log.info(this.getClass().getName() + " 게시물 추가하기 서비스(insertDiaryInfo)종료!");
        }

        // 반환 값은 없음(추가만 하면 끝)

    }

    // 게시물 상세보기
    @Transactional
    @Override
    public DiaryDTO getDiaryInfo(DiaryDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + " 게시물 상세보기 서비스(getDiaryInfo)실행!");

        // 컨트롤러로부터 받은 값을 매퍼에게 주면서 매퍼가 갖고 있는 함수 호출한 뒤, 결과값을 받아와서 rDTO라는 변수에 저장
       DiaryDTO rDTO = diaryMapper.getDiaryInfo(pDTO);

        log.info(this.getClass().getName() + " 게시물 상세보기 서비스(getDiaryInfo)종료!");

        // 매퍼로부터 받아온 값을 컨트롤러로 돌려주기
        return rDTO;
    }

    // 게시물 조회수 증가시키기
    @Override
    public void updateDiaryReadCnt(DiaryDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + " 게시물 조회수 증가 서비스(updateDiaryReadCnt) 실행!");

        // 매퍼 함수 호출해서 조회수 증가시키기
        diaryMapper.updateDiaryReadCnt(pDTO);

        log.info(this.getClass().getName() + " 게시물 조회수 증가 서비스(updateDiaryReadCnt) 종료!");
    }

    // 게시물 수정하기 --> 이거는 아직 하지말 것!
    @Override
    public void updateDiaryInfo(DiaryDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + " 게시물 수정 서비스(updateDiaryInfo) 실행!");

        diaryMapper.updateDiaryInfo(pDTO);

        log.info(this.getClass().getName() + " 게시물 수정 서비스(updateDiaryInfo) 종료!");
    }

    // 게시물 삭제하기
    @Transactional
    @Override
    public void deleteDiaryInfo(DiaryDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + " 게시물 삭제 서비스(deleteDiaryInfo) 실행!");
        String diary_seq = "10";  //삭제할 게시물의 번호 내가 임의로 설정

        pDTO.setDiary_seq(diary_seq);

        // 매퍼한테 보내면서 함수 호출
        diaryMapper.deleteDiaryInfo(pDTO);


        log.info(this.getClass().getName() + " 게시물 삭제 서비스(deleteDiaryInfo) 종료!");

        // 반환 값은 없음(삭제만 하면 끝)
    }


    // 게시물 좋아요수 증가
    @Transactional
    @Override
    public void updateDiaryLikeCnt(DiaryDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + " 좋아요수 증가 서비스(updateLikeCnt)실행!");

        // 매퍼 함수 호출하기
        diaryMapper.updateDiaryLikeCnt(pDTO);

        log.info(this.getClass().getName() + " 좋아요수 증가 서비스(updateLikeCnt)종료!");

        // 반환 값 없음 그냥 쿼리문 실행해서 데이터 베이스 내 데이터 변경하면 끝!
    }


    // 북마크 추가
    @Override
    public void insertBookMark(DiaryDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + " 북마크 추가 서비스(insertBookMark)실행!");

        diaryMapper.insertBookMark(pDTO);

        log.info(this.getClass().getName() + " 북마크 추가 서비스(insertBookMark)종료!");
    }

    // 북마크 여부 조회
    @Override
    public String getBookMarkExists(DiaryDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + " 북마크 여부 조회 서비스(getBookMarkExists) 실행");

        String bookmark_yn = String.valueOf(diaryMapper.getBookMarkExists(pDTO));

        log.info(this.getClass().getName() + " 북마크 여부 조회 서비스(getBookMarkExists) 종료");

        return bookmark_yn;
    }


    // NFT 다이어리 리스트 가져오기
    @Override
    public List<DiaryDTO> getNFTDiaryList(DiaryDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + " NFT 다이어리 리스트 가져오기 서비스(getNFTDiaryList) 실행!");

        List<DiaryDTO> rList = diaryMapper.getNFTDiaryList(pDTO);

        log.info(this.getClass().getName() + " NFT 다이어리 리스트 가져오기 서비스(getNFTDiaryList) 종료!");

        return rList;
    }

    // 보괌함 리스트 가져오기 전 데이터 조회(북마크 테이블)
    @Override
    public List<DiaryDTO> getBookMarkFind(DiaryDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + " 북마크 리스트 가져오기 위해 북마크 테이블 조회 실행 합니다.");

        List<DiaryDTO> rList = diaryMapper.getBookMarkFind(pDTO);

        log.info(this.getClass().getName() + " 북마크 리스트 가져오기 위해 북마크 테이블 조회 종료 합니다.");

        return rList;
    }

    // 보관함 리스트 가져오기
    @Override
    public List<DiaryDTO> getBookMarkList(DiaryDTO sDTO) throws Exception {
        log.info(this.getClass().getName() + " 북마크 리스트 가져오기 위해 ~~~ 실행 합니다.");

        List<DiaryDTO> bList = (diaryMapper.getBookMarkList(sDTO));

        log.info(this.getClass().getName() + " 북마크 리스트 가져오기 위해 ~~~ 종료 합니다.");

        return bList;
    }

    // 검색 페이지 불러오기
    @Override
    public List<DiaryDTO> getSearchFeed() throws Exception {
        log.info(this.getClass().getName() + " 검색 페이지 가져오기 위한 서비스(getSearchFeed) 실행합니다! ");

        log.info(this.getClass().getName() + " 검색 페이지 가져오기 위한 서비스(getSearchFeed) 종료합니다! ");

        return diaryMapper.getSearchFeed();
    }

    // 검색 페이지 결과 인기순으로 정렬
    @Override
    public List<DiaryDTO> sortByLikeCnt() throws Exception {
        log.info(this.getClass().getName() + " 검색 페이지 인기순으로 정렬 서비스(sortByLikeCnt) 실행합니다!");

        log.info(this.getClass().getName() + " 검색 페이지 인기순으로 정렬 서비스(sortByLikeCnt) 종료합니다!");

        return diaryMapper.sortByLikeCnt();
    }

    // 검색 페이지 결과 최신순으로 정렬
    @Override
    public List<DiaryDTO> sortByDatetime() throws Exception {
        log.info(this.getClass().getName() + " 검색 페이지 최신순으로 정렬 서비스(sortByDatetime) 실행!");

        log.info(this.getClass().getName() + " 검색 페이지 최신순으로 정렬 서비스(sortByDatetime) 종료!");

        return diaryMapper.sortByDatetime();
    }

    // 검색 페이지 결과 조회수순으로 정렬
    @Override
    public List<DiaryDTO> sortByReadCnt() throws Exception {
        log.info(this.getClass().getName() + " 검색 페이지 조회수순으로 정렬 서비스(sortByReadCnt) 실행!");

        log.info(this.getClass().getName() + " 검색 페이지 조회수순으로 정렬 서비스(sortByReadCnt) 종료!");

        return diaryMapper.sortByReadCnt();
    }

    @Override
    public int getMyDiaryCnt(DiaryDTO pDTO) throws Exception {
        log.info(this.getClass().getName() + ".getMyDiaryCnt");
        DiaryDTO rDTO = diaryMapper.getMyDiaryCnt(pDTO);
        return rDTO.getDiary_cnt();
    }

    @Override
    public UserInfoDTO getProfilePath(UserInfoDTO pDTO) throws Exception {
        return diaryMapper.getProfilePath(pDTO);
    }
}
