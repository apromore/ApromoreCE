/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.filestore.servlet;

import org.apache.commons.lang.StringUtils;
import org.apromore.filestore.client.FileStoreService;
import org.apromore.model.MembershipType;
import org.apromore.model.UserType;
import org.apromore.filestore.common.WebAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * HACK to get new user registrations working outside of ZK.
 *
 * Couldn't get zk, spring and bootstrap css to work nicely together so doing this hack.
 * Dont' repeat this hack, spend the time the implement the correct bootstrap, spring and zk integration
 * when ZK 7 is finished.
 *
 * @author Raffaele Conforti
 * @since 1.0
 */
@Component("newUserRegistration")
public class NewUserRegistrationHttpServletRequestHandler extends BaseServletRequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewUserRegistrationHttpServletRequestHandler.class);

    @Autowired
    private FileStoreService manager;

    /* (non-Javadoc)
     * @see HttpRequestHandler#handleRequest(HttpServletRequest, HttpServletResponse)
     */
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserType userType;
        String url = LOGIN_PAGE;
        Set<String> messages = new HashSet<>();
        HttpSession session = request.getSession(false);

        try {
            if (isUserRequestOk(request, messages)) {
                userType = constructUserType(request);
                System.out.println(manager.toString());
                manager.writeUser(userType);
                clearAuthenticationAttributes(request);
                url += MESSAGE_EXTENSION;
            } else {
                url += ERROR_EXTENSION;
                if (session != null && !messages.isEmpty()) {
                    request.getSession().setAttribute(WebAttributes.REGISTRATION_EXCEPTION, messages);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error with registering a new user!", e);
            messages.add("Error with registering a new user! Please try again.");
            url += ERROR_EXTENSION;
        }

        sendRedirect(request, response, url);
    }


    /* Construct the usertype for the manager from the user entered data in the request. */
    private UserType constructUserType(HttpServletRequest request) {
        UserType user = new UserType();
        user.setFirstName(request.getParameter(FIRSTNAME));
        user.setLastName(request.getParameter(SURNAME));
        user.setUsername(request.getParameter(USERNAME));

        MembershipType membership = new MembershipType();
        membership.setEmail(request.getParameter(EMAIL));
        membership.setPassword(request.getParameter(PASSWORD));
        membership.setPasswordQuestion(request.getParameter(SECURITY_QUESTION));
        membership.setPasswordAnswer(request.getParameter(SECURITY_ANSWER));
        membership.setFailedLogins(0);
        membership.setFailedAnswers(0);
        user.setMembership(membership);

        return user;
    }

    /* Check that all the data is correct and present so we can proceed with user registration. */
    private boolean isUserRequestOk(HttpServletRequest request, Set<String> messages) {
        boolean ok = true;
        if (StringUtils.isEmpty(request.getParameter(USERNAME))) {
            ok = false;
            messages.add("Username can not be empty!");
        }
        if (StringUtils.isEmpty(request.getParameter(FIRSTNAME))) {
            ok = false;
            messages.add("First Name can not be empty!");
        }
        if (StringUtils.isEmpty(request.getParameter(SURNAME))) {
            ok = false;
            messages.add("Surname can not be empty!");
        }
        if (StringUtils.isEmpty(request.getParameter(EMAIL))) {
            ok = false;
            messages.add("Email can not be empty!");
        }
        if (StringUtils.isEmpty(request.getParameter(PASSWORD))) {
            ok = false;
            messages.add("Password can not be empty!");
        }
        if (StringUtils.isEmpty(request.getParameter(CONFIRM_PASSWORD))) {
            ok = false;
            messages.add("Confirm Password can not be empty!");
        }
        if (ok && !request.getParameter(PASSWORD).equals(request.getParameter(CONFIRM_PASSWORD))) {
            ok = false;
            messages.add("Password and Confirm Password Don't match!");
        }
        return ok;
    }

}