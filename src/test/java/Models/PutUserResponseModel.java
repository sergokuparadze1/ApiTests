package Models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PutUserResponseModel {
    public String name;
    public String job;
    public String updatedAt;

}
