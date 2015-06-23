package hm.orz.chaos114.oauthsamples.valueobject;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(suppressConstructorProperties = true)
public class PhotoObject {
    private String id;
    private String imageUrl;
    private Date createdTime;
}
