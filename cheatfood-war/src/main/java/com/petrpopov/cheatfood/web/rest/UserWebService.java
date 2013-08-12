package com.petrpopov.cheatfood.web.rest;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.ErrorType;
import com.petrpopov.cheatfood.model.PasswordForgetToken;
import com.petrpopov.cheatfood.model.UserCreate;
import com.petrpopov.cheatfood.model.UserEntity;
import com.petrpopov.cheatfood.security.CheatPasswordEncoder;
import com.petrpopov.cheatfood.security.CheatRememberMeServices;
import com.petrpopov.cheatfood.security.LoginManager;
import com.petrpopov.cheatfood.service.MailService;
import com.petrpopov.cheatfood.service.PasswordForgetTokenService;
import com.petrpopov.cheatfood.service.UserContextHandler;
import com.petrpopov.cheatfood.service.UserService;
import com.petrpopov.cheatfood.web.other.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * User: petrpopov
 * Date: 16.07.13
 * Time: 15:52
 */

@Controller
@RequestMapping("/api/users/")
public class UserWebService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserContextHandler userContextHandler;

    @Autowired
    private LoginManager loginManager;

    @Autowired
    private CheatRememberMeServices rememberMeServices;

    @Autowired
    private CheatPasswordEncoder cheatPasswordEncoder;

    @Autowired
    private PasswordForgetTokenService tokenService;

    @Autowired
    private MailService mailService;

    @RequestMapping(value = "add", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public MessageResult processSubmit(@Valid @RequestBody UserCreate user, HttpServletRequest request, HttpServletResponse response) {

        MessageResult res = new MessageResult();

        UserEntity entity;
        try {
            entity = userService.createUser(user);
        } catch (CheatException e) {
            res.setError(true);
            res.setMessage("User is already exists !");
            return res;
        }

        Authentication authenticate = loginManager.authenticate(entity.getId(), user.getPassword());
        rememberMeServices.onLoginSuccess(request, response, authenticate);

        res.setResult(entity);
        return res;
    }

    @RequestMapping(value = "login", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public MessageResult processLogin(@Valid @RequestBody UserCreate user, HttpServletRequest request, HttpServletResponse response) {

        MessageResult res = new MessageResult();

        UserEntity userByEmail = userService.getUserByEmail(user.getEmail());
        if( userByEmail == null ) {
            res.setError(true);
            res.setErrorType(ErrorType.no_such_user);
            res.setMessage("There is no such user");
            return res;
        }

        String salt = userByEmail.getSalt();
        String passwordHash = userByEmail.getPasswordHash();

        //only social registration
        if( passwordHash == null ) {
            res.setError(true);
            res.setErrorType(ErrorType.no_password_data);
            res.setMessage("No password data");
            return res;
        }

        if( !cheatPasswordEncoder.isPasswordValid(passwordHash, user.getPassword(), salt) ) {
            res.setError(true);
            res.setErrorType(ErrorType.wrong_password);
            res.setMessage("Wrong password");
            return res;
        }

        try {
            Authentication authenticate = loginManager.authenticate(userByEmail.getId(), user.getPassword());
            rememberMeServices.onLoginSuccess(request, response, authenticate);
        }
        catch (Exception e) {
            res.setError(true);
            res.setErrorType(ErrorType.login_error);
        }

        return res;
    }

    @RequestMapping(value = "forget", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public MessageResult processForgetPassword(@RequestBody String email, HttpServletRequest request) throws MessagingException {

        MessageResult res = new MessageResult();

        PasswordForgetToken tokenForEmail = tokenService.createTokenForEmail(email);

        mailService.sendMail(email, tokenForEmail.getValue(), request);

        return res;
    }

    @RequestMapping(value = "current", method = RequestMethod.GET)
    @ResponseBody
    public UserEntity getCurrentUser() {

        UserEntity entity = userContextHandler.currentContextUser();
        return entity;
    }

}
