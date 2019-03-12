package com.redhat.gps.pathfinder.web.api.security;
/*-
 * #%L
 * Pathfinder
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2018 RedHat
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.redhat.gps.pathfinder.domain.Member;
import com.redhat.gps.pathfinder.repository.MembersRepository;
import com.redhat.gps.pathfinder.service.util.MapBuilder;
import mjson.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.Objects;

@RestController
@Service
public class AuthenticationRestController {
    public enum AuthKeys
    {
        TOKEN, USERNAME, DISPLAYNAME;
    }

    @Value("Authorization")
    private String tokenHeader;

    @Autowired
    MembersRepository membersRepository;
    
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    @Qualifier("jwtUserDetailsService")
    private UserDetailsService userDetailsService;

    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthToken(@RequestBody String authRequest) throws RuntimeException, URISyntaxException, CredentialsException {
        Json jsonReq=Json.read(authRequest);
        String username=jsonReq.at("username").asString();
        String password=jsonReq.at("password").asString();
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        EnumMap<AuthKeys, String> resp = getResponseEntity(username, password);

        return buildResult(resp.get(AuthKeys.TOKEN), resp.get(AuthKeys.USERNAME),resp.get(AuthKeys.DISPLAYNAME));
    }

    public EnumMap<AuthKeys, String> getResponseEntity(String username, String password) throws CredentialsException {
        EnumMap<AuthKeys, String> resp = new EnumMap<>(AuthKeys.class);
        try {
          if (null==authenticationManager) authenticationManager=new CustomAuthenticationProvider(membersRepository);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new CredentialsException("User is disabled!", e);
        } catch (BadCredentialsException e) {
            throw new CredentialsException("Bad credentials!", e);
        }

        // Reload password post-security so we can generate the token
        try{
          final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
          final String token = jwtTokenUtil.generateToken(userDetails);

          Member member=membersRepository.findOne(username);
          resp.put(AuthKeys.TOKEN,token);
          resp.put(AuthKeys.USERNAME,member.getUsername());
          resp.put(AuthKeys.DISPLAYNAME,member.getDisplayName());

          return resp;
        }catch(UsernameNotFoundException e){
          throw new CredentialsException("No user with the name "+username);
        }
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String authToken = request.getHeader(tokenHeader);
        final String token = authToken.substring(7);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        // just to check the user still exists associated with the token
        final UserDetails userDetails = (UserDetails)userDetailsService.loadUserByUsername(username);
        
        Member member=membersRepository.findOne(username);
        
        String refreshedToken = jwtTokenUtil.refreshToken(token);
        
        return buildResult(refreshedToken, member.getUsername(),member.getDisplayName());
    }
    
    private ResponseEntity buildResult(String token, String userName, String displayName){
      return ResponseEntity.ok(new MapBuilder<String,String>()
          .put("token", token)
          .put("username", userName)
          .put("displayName", displayName)
          .build());
    }

    public ResponseEntity<String> handleAuthenticationException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

}
