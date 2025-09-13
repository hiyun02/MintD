package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class HashtagDiaryDTO {
    private String hashtag_diary_seq;
    private String diary_seq;
    private String hashtag_id;
}
