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

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;

import org.apromore.manager.ManagerPortalEndpoint;
import org.apromore.model.ObjectFactory;
import org.apromore.model.ReadDomainsInputMsgType;
import org.apromore.model.ReadDomainsOutputMsgType;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the Read User method on the Manager Portal Endpoint WebService.
 */
public class ReadDomainsEndpointUnitTest extends AbstractEndpointUnitTest {

    @Test
    public void testInvokeReadDomains() throws Exception {
        ReadDomainsInputMsgType msg = new ReadDomainsInputMsgType();
        msg.setEmpty("");
        JAXBElement<ReadDomainsInputMsgType> request = new ObjectFactory().createReadDomainsRequest(msg);

        List<String> domains = new ArrayList<String>();
        expect(domSrv.findAllDomains()).andReturn(domains);

        replay(domSrv);

        JAXBElement<ReadDomainsOutputMsgType> response = endpoint.readDomains(request);
        Assert.assertNotNull(response.getValue().getResult());
        Assert.assertNotNull(response.getValue().getDomains());
        Assert.assertEquals("Result Code Doesn't Match", response.getValue().getResult().getCode().intValue(), 0);
        Assert.assertEquals("UserNames should be empty", response.getValue().getDomains().getDomain().size(), 0);

        verify(domSrv);
    }

}

