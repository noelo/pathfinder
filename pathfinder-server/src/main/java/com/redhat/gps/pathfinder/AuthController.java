package com.redhat.gps.pathfinder;

/*-
 * #%L
 * com.redhat.gps.pathfinder.pathfinder-server
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2018 - 2019 RedHat Inc
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

import com.redhat.gps.pathfinder.web.api.security.AuthenticationRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumMap;


@Controller
public class AuthController {

    @Autowired
    private AuthenticationRestController authController;

    private final Logger log = LoggerFactory.getLogger(AuthController.class);

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public Response logout(@Context HttpServletRequest request, @Context HttpServletResponse response) throws URISyntaxException, IOException {
        log.info("Get /logout");
        request.getSession().invalidate();
        return Response.status(302).location(new URI("../index.jsp")).build();
    }

    private String maskPasswords(String input) {
        return input.replaceAll("password=.+&", "password=****&");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@Context HttpServletRequest request, @Context HttpServletResponse response) throws URISyntaxException, IOException {
        String in_username,in_password;
        log.info("Post /login");

        in_username = request.getParameter("username");
        in_password = request.getParameter("password");
        log.info("login():: username=" + in_username);

        try {
            EnumMap<AuthenticationRestController.AuthKeys, String> authResp;
            authResp = authController.getResponseEntity(in_username, in_password);
            String jwtToken = authResp.get(AuthenticationRestController.AuthKeys.TOKEN);
            String userName = authResp.get(AuthenticationRestController.AuthKeys.USERNAME);
            String displayName = authResp.get(AuthenticationRestController.AuthKeys.DISPLAYNAME);

            request.getSession().setAttribute("x-access-token", jwtToken);
            request.getSession().setAttribute("x-username", userName);
            request.getSession().setAttribute("x-displayName", displayName);

            log.info("login()::Success Redirecting to manageCustomers");

//           return Response.status(302).location(new URI("manageCustomers")).header("x-access-token", jwtToken).build();
            response.addHeader("x-access-token", jwtToken);

           return "manageCustomers";
        }catch(Exception ex){
            log.info("login()::Failed Redirecting to index.jsp?error");
            return "redirect:index.jsp";

//            return Response.status(302).location(new URI("index.jsp?error=" + URLEncoder.encode("Username and/or password is unknown or incorrect", "UTF-8"))).build();
        }

//        io.restassured.response.Response loginResp = given()
//                .body("{\"username\":\"" + keyValues.get("username") + "\",\"password\":\"" + keyValues.get("password") + "\"}")
//                .post(getProperty("PATHFINDER_SERVER") + "/auth");
//        if (loginResp.statusCode() != 200) {
//            log.error("Controller:login():: ERROR1 loginResp(code=" + loginResp.statusCode() + ").asString() = " + loginResp.asString());
//            log.error("Controller:login():: 3 OUT/ERROR loginResp(code=" + loginResp.statusCode() + ").asString() = " + loginResp.asString());
//            String error = "Username and/or password is unknown or incorrect"; // would grab the text from server side but spring wraps some debug info in there so until we can strip that we cant give details of failure
//            return Response.status(302).location(new URI("../index.jsp?error=" + URLEncoder.encode(error, "UTF-8"))).build();
//        }
//        log.info("Controller:login():: 2 loginResp(code=" + loginResp.statusCode() + ").asString() = " + loginResp.asString());
//        mjson.Json jsonResp = mjson.Json.read(loginResp.asString());
//        String jwtToken = jsonResp.at("token").asString();
//        String username = jsonResp.at("username").asString();
//        String displayName = jsonResp.at("displayName").asString();

    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Response logout(@Context HttpServletRequest request, @Context HttpServletResponse response, @Context HttpSession session) throws URISyntaxException {
        session.removeAttribute("username");
        session.invalidate();
        // TODO: and invalidate it on the server end too!
        return Response.status(302).location(new URI("/index.jsp")).build();
    }


}
