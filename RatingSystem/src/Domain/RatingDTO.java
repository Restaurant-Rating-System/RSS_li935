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

    public void reset() {
        id = null;
        businessName = null;
        mainMenu = null;
        rating = null;
        price = null;
        createAt = null;
        updateAt = null;
    }

    public boolean isFilled() {
        return
                isColumnFilled(businessName) &&
                isColumnFilled(mainMenu) &&
                isColumnFilled(rating) &&
                isColumnFilled(price) &&
                isColumnFilled(createAt) &&
                isColumnFilled(updateAt) &&
                Long.toString(id).isEmpty();
    }

    private boolean isColumnFilled(String column) {
        return column.isEmpty();
    }
}
