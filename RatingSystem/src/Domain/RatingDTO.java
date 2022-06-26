package Domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RatingDTO {
    private Long id;
    private String businessName;
    private String mainMenu;
    private String rating;
    private String price;
    private String createAt;
    private String updateAt;
}
