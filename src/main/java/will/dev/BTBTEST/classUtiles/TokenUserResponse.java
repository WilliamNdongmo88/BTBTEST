package will.dev.BTBTEST.classUtiles;

import lombok.Data;
import will.dev.BTBTEST.dto.UserDto;

@Data
public class TokenUserResponse {
    private String token;
    private String refresh;
    private UserDto user;

    public TokenUserResponse(String token, String refresh, UserDto userdto) {
        this.token = token;
        this.refresh = refresh;
        this.user = userdto;
    }
}
