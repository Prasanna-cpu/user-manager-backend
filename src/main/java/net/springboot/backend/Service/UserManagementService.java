package net.springboot.backend.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.springboot.backend.DTO.RequestResponseDTO;
import net.springboot.backend.Entity.OurUsers;
import net.springboot.backend.Repository.UsersRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class UserManagementService {

    private final AuthenticationManager authenticationManager;
    private final UsersRepository repository;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public RequestResponseDTO register(RequestResponseDTO registrationRequest){
        RequestResponseDTO resp=new RequestResponseDTO();
        try{
            OurUsers users=new OurUsers();
            users.setEmail(registrationRequest.getEmail());
            users.setName(registrationRequest.getName());
            users.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            users.setRole(registrationRequest.getRole());
            users.setCity(registrationRequest.getCity());

            OurUsers result=repository.save(users);
            if(result.getId()>0){
                resp.setOurUsers(result);
                resp.setMessage("User created successfully");
                resp.setStatuscode(200);
            }


        }
        catch(Exception e){
            resp.setStatuscode(500);
            resp.setError(e.getMessage());

        }
        return resp;

    }

    public RequestResponseDTO login(RequestResponseDTO loginRequest) {

        RequestResponseDTO resp = new RequestResponseDTO();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));

            var user=repository.findByEmail(loginRequest.getEmail()).orElseThrow(()->new UsernameNotFoundException("User Not Found"));
            var jwt=jwtUtils.generateToken(user);
            var refreshToken=jwtUtils.generateRefreshToken(new HashMap<>(),user);
            resp.setStatuscode(200);
            resp.setToken(jwt);
            resp.setRefreshToken(refreshToken);
            resp.setOurUsers(user);
            resp.setRole(user.getRole());
            resp.setMessage("Login successful");
            resp.setExpirationTime("24Hrs");
            resp.setMessage("Sucessfully logged in");

        }
        catch(UsernameNotFoundException e){
            resp.setStatuscode(404);
            resp.setError(e.getMessage());
        }
        catch(Exception e){
            resp.setStatuscode(500);
            resp.setError(e.getMessage());
        }

        return resp;

    }
    public RequestResponseDTO refreshToken(RequestResponseDTO refreshTokenRequest) {
        RequestResponseDTO response = new RequestResponseDTO();
        try {
            String ourEmail=jwtUtils.extractUsername(refreshTokenRequest.getToken());
            OurUsers users=repository.findByEmail(ourEmail).orElseThrow(()->new UsernameNotFoundException("User Not Found"));
            if(jwtUtils.ValidateToken(refreshTokenRequest.getToken(),users)){
                var jwt = jwtUtils.generateToken(users);
                response.setStatuscode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
            else{
                response.setStatuscode(401);
                response.setError("Invalid token");
            }

        }
        catch (Exception e) {
            response.setStatuscode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    public RequestResponseDTO getAllUsers(){
        RequestResponseDTO response=new RequestResponseDTO();
        try{
            List<OurUsers> result=repository.findAll();
            if(!result.isEmpty()){
                response.setStatuscode(200);
                response.setOurUsersList(result);
                response.setMessage("Users fetched successfully");
            }
            else{
                response.setStatuscode(404);
                response.setMessage("No users found");
            }

        }
        catch(Exception e){
            response.setStatuscode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    public RequestResponseDTO getUserById(Integer id){
        RequestResponseDTO response=new RequestResponseDTO();

        try{
            OurUsers result=repository.findById(id).orElse(null);
            if(result!=null){
                response.setStatuscode(200);
                response.setOurUsers(result);
                response.setMessage("User fetched successfully");
            }
            else{
                response.setStatuscode(404);
                response.setMessage("User not found");
                throw new UsernameNotFoundException("User not found");
            }
        }
        catch(Exception e){
            response.setStatuscode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    public RequestResponseDTO updateUser(Integer id,OurUsers updatedUser){
        RequestResponseDTO response=new RequestResponseDTO();

        try{
            OurUsers target=repository.findById(id).orElse(null);
            if(target!=null){
                target.setName(updatedUser.getName());
                target.setEmail(updatedUser.getEmail());
                target.setCity(updatedUser.getCity());
                target.setRole(updatedUser.getRole());
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    // Encode the password and update it
                    target.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                OurUsers savedUser = repository.save(target);
                repository.save(savedUser);
                response.setStatuscode(200);
                response.setMessage("User updated successfully");
                response.setOurUsers(target);
            }
            else{
                response.setStatuscode(404);
                response.setMessage("User not found");
                throw new UsernameNotFoundException("User not found");
            }
        }
        catch(Exception e){
            response.setStatuscode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    public RequestResponseDTO deleteUser(Integer id) {
        RequestResponseDTO response = new RequestResponseDTO();
        try {
            OurUsers target = repository.findById(id).orElse(null);
            if (target != null) {
                repository.delete(target);
                response.setStatuscode(200);
                response.setMessage("User deleted successfully");
            } else {
                response.setStatuscode(404);
                response.setMessage("User not found");
                throw new UsernameNotFoundException("User not found");
            }
        } catch (Exception e) {
            response.setStatuscode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    public RequestResponseDTO getMyInfo(String email) {
        RequestResponseDTO response = new RequestResponseDTO();
        try {
            OurUsers target = repository.findByEmail(email).orElse(null);
            if (target != null){
                response.setStatuscode(200);
                response.setOurUsers(target);
                response.setMessage("User fetched successfully");
            } else {
                response.setStatuscode(404);
                response.setMessage("User not found");
                throw new UsernameNotFoundException("User not found");
            }
        } catch (Exception e) {
            response.setStatuscode(500);
            response.setError(e.getMessage());
        }
        return response;
    }





}
