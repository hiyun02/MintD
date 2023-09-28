package kopo.poly.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
//@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserInfoDTO {

    private String user_id;
    private String user_name;
    private String user_nick;
    private String user_pwd;
    private String user_email;
    private String profile_path;
    private String user_intro;
    private String user_date;

    private String exists_yn;

    private int authNumber;



}
