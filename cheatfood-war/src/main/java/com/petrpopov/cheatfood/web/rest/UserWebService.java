package com.petrpopov.cheatfood.web.rest;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.data.ErrorType;
import com.petrpopov.cheatfood.model.data.MessageResult;
import com.petrpopov.cheatfood.model.data.RestorePassword;
import com.petrpopov.cheatfood.model.data.UserCreate;
import com.petrpopov.cheatfood.model.entity.PasswordForgetToken;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import com.petrpopov.cheatfood.security.CheatPasswordEncoder;
import com.petrpopov.cheatfood.service.MailService;
import com.petrpopov.cheatfood.service.PasswordForgetTokenService;
import com.petrpopov.cheatfood.service.UserContextHandler;
import com.petrpopov.cheatfood.service.UserService;
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
public class UserWebService extends BaseWebService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserContextHandler userContextHandler;

    @Autowired
    private CheatPasswordEncoder cheatPasswordEncoder;

    @Autowired
    private PasswordForgetTokenService tokenService;

    @Autowired
    private MailService mailService;

    @RequestMapping(value = "add", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public MessageResult processSubmit(@Valid @RequestBody UserCreate user,
                                       HttpServletRequest request, HttpServletResponse response) throws CheatException {

        MessageResult res = new MessageResult();

        UserEntity entity = userService.createUser(user);
        this.authenticate(entity.getId(), user.getPassword(), true, request, response);

        res.setResult(entity);
        return res;
    }

    @RequestMapping(value = "login", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public MessageResult processLogin(@Valid @RequestBody UserCreate user,
                                      HttpServletRequest request, HttpServletResponse response) throws CheatException {

        MessageResult res = new MessageResult();

        //built-in admin  - do not need to check existense, just authenticate it
        try {
            Authentication authenticate = this.authenticate(user.getEmail(), user.getPassword(), request, response);
            if( authenticate != null )
                return res;
        } catch (Exception e) {};


        UserEntity userByEmail = userService.getUserByEmail(user.getEmail());
        if( userByEmail == null ) {
            res.setError(true);
            res.setErrorType(ErrorType.no_such_user);
            return res;
        }

        String salt = userByEmail.getSalt();
        String passwordHash = userByEmail.getPasswordHash();

        //only social registration
        if( passwordHash == null ) {
            res.setError(true);
            res.setErrorType(ErrorType.no_password_data);
            return res;
        }

        if( !cheatPasswordEncoder.isPasswordValid(passwordHash, user.getPassword(), salt) ) {
            res.setError(true);
            res.setErrorType(ErrorType.wrong_password);
            return res;
        }

        this.authenticate(userByEmail.getId(), user.getPassword(), request, response);

        return res;
    }

    @RequestMapping(value = "forget", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public MessageResult processForgetPassword(@RequestBody String email, HttpServletRequest request)
            throws CheatException, MessagingException {

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

        PasswordForgetToken tokenForEmail = tokenService.createTokenForEmail(email);

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
    public MessageResult restorePassword(@Valid @RequestBody RestorePassword restorePassword,
                                         HttpServletRequest request, HttpServletResponse response) throws CheatException {

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

        this.authenticate(userByEmail.getId(), restorePassword.getPassword(), request, response);

        return res;
    }

    @RequestMapping(value = "current", method = RequestMethod.GET)
    @ResponseBody
    public UserEntity getCurrentUser() {

        UserEntity entity = userContextHandler.currentContextUser();
        return entity;
    }
}
