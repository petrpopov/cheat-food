package com.petrpopov.cheatfood.web.rest;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.data.*;
import com.petrpopov.cheatfood.model.entity.PasswordForgetToken;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import com.petrpopov.cheatfood.model.entity.UserLogin;
import com.petrpopov.cheatfood.security.CheatPasswordEncoder;
import com.petrpopov.cheatfood.service.PasswordForgetTokenService;
import com.petrpopov.cheatfood.service.UserContextHandler;
import com.petrpopov.cheatfood.service.UserService;
import com.petrpopov.cheatfood.web.other.CookieRequest;
import com.petrpopov.cheatfood.web.other.UrlService;
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
    private PasswordForgetTokenService passwordForgetTokenService;

    @Autowired
    private UrlService urlService;

    @RequestMapping(value = "add", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public MessageResult processAdd(@Valid @RequestBody UserCreate user,
                                       HttpServletRequest request, HttpServletResponse response) throws CheatException {

        MessageResult res = new MessageResult();

        UserEntity entity = userService.createUser(user);
        this.authenticate(entity.getId(), user.getPassword(), true, request, response);

        res.setResult(entity);
        return res;
    }

    @RequestMapping(value = "update", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public MessageResult processUpdate(@Valid @RequestBody UserUpdate user, HttpServletRequest request,
                                       @CookieValue(required = true, value = "CHEATFOOD") CookieRequest cookie)
            throws CheatException, MessagingException {

        MessageResult result = userService.updateUser(user, urlService.getGlobalUrl(request));

        return result;
    }

    @RequestMapping(value = "changeemail/{tokenid}", method = RequestMethod.GET)
    public ModelAndView processChangeEmail(@PathVariable String tokenid,
                                           @CookieValue(required = true, value = "CHEATFOOD") CookieRequest cookie) throws CheatException {

        UserEntity userEntity = userContextHandler.currentContextUser();
        if( userEntity == null ) {
            RedirectView view = new RedirectView("/", true);
            return new ModelAndView(view);
        }

        try {
            userService.updateEmailForUser(userEntity, tokenid);
        }
        catch (CheatException e) {
            RedirectView view = new RedirectView("/", true);
            return new ModelAndView(view);
        }

        RedirectView view = new RedirectView("/", true);
        return new ModelAndView(view);
    }


    @RequestMapping(value = "login", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public MessageResult processLogin(@Valid @RequestBody UserLogin user,
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

        userService.forgetPasswordForUser(email, urlService.getGlobalUrl(request));

        return res;
    }

    @RequestMapping(value = "forget/{tokenid}", method = RequestMethod.GET)
    public ModelAndView processForgetRecovery(@PathVariable String tokenid) {

        UserEntity userEntity = userContextHandler.currentContextUser();
        if( userEntity != null ) {
            RedirectView view = new RedirectView("/", true);
            return new ModelAndView(view);
        }

        PasswordForgetToken byToken = passwordForgetTokenService.findByTokenValue(tokenid);
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

        PasswordForgetToken byToken = passwordForgetTokenService.findByTokenValue(restorePassword.getToken());
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
        passwordForgetTokenService.invalidateTokensForEmail(userByEmail.getEmail());

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
