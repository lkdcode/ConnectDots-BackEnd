package site.connectdots.connectdotsprj.musicboard.dto.response;

import lombok.*;
import site.connectdots.connectdotsprj.musicboard.entity.Music;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MusicListResponseDTO {

    private Long musicBoardIdx;
    private String musicBoardTrack;
    private String musicBoardTrackImg;
    private String musicBoardTitle;
    private String musicBoardTitleImg;
    private String musicBoardSinger;
    private  Long musicBoardViewCount;

    public MusicListResponseDTO(Music music){
        this.musicBoardIdx = music.getMusicBoardIdx();
        this.musicBoardTrack=music.getMusicBoardTrack();
        this.musicBoardTrackImg=music.getMusicBoardTrackImg();
        this.musicBoardTitle=music.getMusicBoardTitle();
        this.musicBoardTitleImg= music.getMusicBoardTitleImg();
        this.musicBoardSinger=music.getMusicBoardSinger();
        this.musicBoardViewCount=music.getMusicBoardViewCount();
    }

}
