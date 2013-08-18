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
import com.petrpopov.cheatfood.web.other.RestorePassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

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
            res.setErrorType(ErrorType.user_already_exists);
            return res;
        }

        try {
            this.authenticate(entity.getId(), user.getPassword(), request, response);
        }
        catch (CheatException e) {
            res.setError(true);
            res.setErrorType(e.getErrorType());
        }

        res.setResult(entity);
        return res;
    }

    @RequestMapping(value = "login", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public MessageResult processLogin(@Valid @RequestBody UserCreate user, HttpServletRequest request, HttpServletResponse response) {

        MessageResult res = new MessageResult();

        try {
            Authentication authenticate = this.authenticate(user.getEmail(), user.getPassword(), request, response);
            return res;
        }
        catch (CheatException e) {
        }

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
            this.authenticate(userByEmail.getId(), user.getPassword(), request, response);
        }
        catch (CheatException e) {
            res.setError(true);
            res.setErrorType(e.getErrorType());
        }

        return res;
    }

    @RequestMapping(value = "forget", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public MessageResult processForgetPassword(@RequestBody String email, HttpServletRequest request) throws MessagingException {

        MessageResult res = new MessageResult();

        if( email == null ) {
            res.setError(true);
            res.setErrorType(ErrorType.email_is_empty);
            return res;
        }

        if( email.isEmpty() ) {
            res.setError(true);
            res.setErrorType(ErrorType.email_is_empty);
            return res;
        }

        PasswordForgetToken tokenForEmail = null;
        try {
            tokenForEmail = tokenService.createTokenForEmail(email);
        } catch (CheatException e) {
            res.setError(true);
            res.setErrorType(ErrorType.no_user_with_such_email);
            return res;
        }

        mailService.sendMail(email, tokenForEmail.getValue(), request);

        return res;
    }

    @RequestMapping(value = "forget/{tokenid}", method = RequestMethod.GET)
    public ModelAndView processForgetRecovery(@PathVariable String tokenid) {

        UserEntity userEntity = userContextHandler.currentContextUser();
        if( userEntity != null ) {
            RedirectView view = new RedirectView("/", true);
            return new ModelAndView(view);
        }

        PasswordForgetToken byToken = tokenService.findByToken(tokenid);
        if( byToken == null ) {
            RedirectView view = new RedirectView("/", true);
            return new ModelAndView(view);
        }

        if( byToken.getValid().equals(Boolean.FALSE) ) {
            RedirectView view = new RedirectView("/", true);
            return new ModelAndView(view);
        }

        ModelAndView modelAndView = new ModelAndView("restore");
        modelAndView.addObject("token", tokenid);
        return modelAndView;
    }

    @RequestMapping(value = "restore", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public MessageResult restorePassword(@Valid @RequestBody RestorePassword restorePassword, HttpServletRequest request, HttpServletResponse response) {

        MessageResult res = new MessageResult();

        if( !restorePassword.getPassword().equals(restorePassword.getPasswordCopy())) {
            res.setError(true);
            res.setErrorType(ErrorType.password_mismatch);
            return res;
        }

        PasswordForgetToken byToken = tokenService.findByToken(restorePassword.getToken());
        if( byToken == null ) {
            res.setError(true);
            res.setErrorType(ErrorType.wrong_token);
            return res;
        }

        if( byToken.getEmail() == null ) {
            res.setError(true);
            res.setErrorType(ErrorType.wrong_token);
            return res;
        }

        if(byToken.getValid().equals(Boolean.FALSE)) {
            res.setError(true);
            res.setErrorType(ErrorType.wrong_token);
            return res;
        }

        UserEntity userByEmail = userService.getUserByEmail(byToken.getEmail());
        if( userByEmail == null ) {
            res.setError(true);
            res.setErrorType(ErrorType.wrong_token);
            return res;
        }

        userService.updatePasswordForUser(userByEmail, restorePassword.getPassword());
        tokenService.invalidateTokensForEmail(userByEmail.getEmail());

        try {
            this.authenticate(userByEmail.getId(), restorePassword.getPassword(), request, response);
        }
        catch (CheatException e) {
            res.setError(true);
            res.setErrorType(e.getErrorType());
        }

        return res;
    }

    @RequestMapping(value = "current", method = RequestMethod.GET)
    @ResponseBody
    public UserEntity getCurrentUser() {

        UserEntity entity = userContextHandler.currentContextUser();
        return entity;
    }


    private Authentication authenticate(String name, String password, HttpServletRequest request, HttpServletResponse response) throws CheatException {

        try {
            Authentication authenticate = loginManager.authenticate(name, password);
            rememberMeServices.onLoginSuccess(request, response, authenticate);
            return authenticate;
        }
        catch (Exception e) {
            throw new CheatException(ErrorType.login_error);
        }
    }
}
