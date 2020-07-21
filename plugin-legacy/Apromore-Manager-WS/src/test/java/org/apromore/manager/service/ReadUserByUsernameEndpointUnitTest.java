/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.manager.service;

import org.apromore.dao.model.User;
import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.model.ObjectFactory;
import org.apromore.model.ReadUserByUsernameInputMsgType;
import org.apromore.model.ReadUserByUsernameOutputMsgType;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBElement;
import java.util.Date;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test the Read User method on the Manager Portal Endpoint WebService.
 */
public class ReadUserByUsernameEndpointUnitTest extends AbstractEndpointUnitTest {

    @Test
    public void testInvokeReadUser() throws Exception {
        ReadUserByUsernameInputMsgType msg = new ReadUserByUsernameInputMsgType();
        msg.setUsername("someone");
        JAXBElement<ReadUserByUsernameInputMsgType> request = new ObjectFactory().createReadUserByUsernameRequest(msg);

        User user = new User();
        user.setLastActivityDate(new Date());
        expect(secSrv.getUserByName(msg.getUsername())).andReturn(user);

        replay(secSrv);

        JAXBElement<ReadUserByUsernameOutputMsgType> response = endpoint.readUserByUsername(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getUser());
        Assert.assertEquals("Result Code Doesn't Match", 0, response.getValue().getResult().getCode().intValue());
        Assert.assertEquals("UserType shouldn't contain anything", null, response.getValue().getUser().getFirstName());

        verify(secSrv);
    }

    @Test
    public void testInvokeReadUserNotFound() throws Exception {
        ReadUserByUsernameInputMsgType msg = new ReadUserByUsernameInputMsgType();
        msg.setUsername("someone");
        JAXBElement<ReadUserByUsernameInputMsgType> request = new ObjectFactory().createReadUserByUsernameRequest(msg);

        User user = null;
        expect(secSrv.getUserByName(msg.getUsername())).andReturn(user);

        replay(secSrv);

        JAXBElement<ReadUserByUsernameOutputMsgType> response = endpoint.readUserByUsername(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertEquals("Result Code Doesn't Match", -1, response.getValue().getResult().getCode().intValue());

        verify(secSrv);
    }
}

