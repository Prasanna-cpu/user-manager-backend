package net.springboot.backend.DTO;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import net.springboot.backend.Entity.OurUsers;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestResponseDTO {
    private Integer statuscode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String password;
    private String email;
    private String city;
    private String role;
    private OurUsers ourUsers;
    private List<OurUsers> ourUsersList;
}
