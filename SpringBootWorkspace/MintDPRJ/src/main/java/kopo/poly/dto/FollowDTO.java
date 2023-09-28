package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FollowDTO {
    private String follow_seq;
    private String follow_id;
    private String following_id;
    private String following_count;
    private String follower_count;
}
