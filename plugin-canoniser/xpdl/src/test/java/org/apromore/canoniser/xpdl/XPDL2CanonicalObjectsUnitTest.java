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

package org.apromore.canoniser.xpdl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.xpdl.internal.XPDL2Canonical;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.junit.Test;
import org.wfmc._2009.xpdl2.PackageType;
import org.xml.sax.SAXException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests the conversion from XPDL to Canonical Format.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class XPDL2CanonicalObjectsUnitTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testObjectConversionWorks() throws JAXBException, SAXException, CanoniserException {
        JAXBContext jc = JAXBContext.newInstance(XPDL22Canoniser.XPDL2_CONTEXT);
        Unmarshaller u = jc.createUnmarshaller();
        InputStream data = new ByteArrayInputStream(XPDL_DATA.getBytes());
        JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(data);
        PackageType PKType = rootElement.getValue();

        XPDL2Canonical x2c = new XPDL2Canonical(PKType);
        assertThat(x2c, notNullValue());

        CanonicalProcessType cpt = x2c.getCpf();
        //TODO produce valid CPF
        CPFSchema.marshalCanonicalFormat(System.out, cpt, false);
    }

    /* The model that has objects we want to convert. */
    public static final String XPDL_DATA = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
            "<Package xmlns=\"http://www.wfmc.org/2009/XPDL2.2\" xmlns:ns2=\"http://www.wfmc.org/2002/XPDL1.0\" Id=\"ec5783be-d860-48dc-918d-74d5f2412162\" Name=\"F3 International Departure Passport Control\" OnlyOneProcess=\"false\">" +
            " <PackageHeader>" +
            "  <XPDLVersion>2.2</XPDLVersion>" +
            "  <Vendor>BizAgi Process Modeler.</Vendor>" +
            "  <Created>2010-02-16T20:32:14.9813338+10:00</Created>" +
            "  <ModificationDate/>" +
            "  <Description>FRA3 International Departure Passport Control</Description>" +
            "  <Documentation/>" +
            " </PackageHeader>" +
            " <RedefinableHeader>" +
            "  <Author>fauvet</Author>" +
            "  <Version>0.1</Version>" +
            "  <Countrykey>CO</Countrykey>" +
            " </RedefinableHeader>" +
            " <ExternalPackages/>" +
            " <Participants/>" +
            " <Pools>" +
            "  <Pool Id=\"3e50b0f2-ab96-4e8b-baf8-661473698ea0\" Process=\"dbcb5bf2-d661-49d7-9a41-502ff9b29c45\" BoundaryVisible=\"false\">" +
            "   <Lanes/>" +
            "   <NodeGraphicsInfos>" +
            "    <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"350.0\" Width=\"700.0\" BorderColor=\"-16777216\" FillColor=\"-1\">" +
            "     <Coordinates XCoordinate=\"0.0\" YCoordinate=\"0.0\"/>" +
            "    </NodeGraphicsInfo>" +
            "   </NodeGraphicsInfos>" +
            "  </Pool>" +
            "  <Pool Id=\"2d0cac57-0b94-4055-a5a7-77262512d686\" Name=\"Passenger\" Process=\"b147c0cb-2be1-4246-8a6e-f45be5c695af\" BoundaryVisible=\"true\">" +
            "   <Lanes/>" +
            "   <NodeGraphicsInfos>" +
            "    <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"477.0\" Width=\"1428.0\" BorderColor=\"-16777216\" FillColor=\"-1\">" +
            "     <Coordinates XCoordinate=\"30.0\" YCoordinate=\"7.0\"/>" +
            "    </NodeGraphicsInfo>" +
            "   </NodeGraphicsInfos>" +
            "  </Pool>" +
            "  <Pool Id=\"30785776-6795-48c8-975e-936a55a601d1\" Name=\"Government Agent\" Process=\"b68f49d5-1e4f-4873-9087-399a0da09a74\" BoundaryVisible=\"true\">" +
            "   <Lanes/>" +
            "   <NodeGraphicsInfos>" +
            "    <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"232.0\" Width=\"1429.0\" BorderColor=\"-16777216\" FillColor=\"-1\">" +
            "     <Coordinates XCoordinate=\"24.0\" YCoordinate=\"523.0\"/>" +
            "    </NodeGraphicsInfo>" +
            "   </NodeGraphicsInfos>" +
            "  </Pool>" +
            " </Pools>" +
            " <MessageFlows>" +
            "  <MessageFlow Id=\"751a5357-6027-4ee0-a9cf-3893992ac702\" Name=\"Travel Documents\" Source=\"20b34f24-2bda-4ac2-ae7c-fc7cc713556f\" Target=\"a5559df9-2311-4fa7-b566-819d69266bf4\">" +
            "   <ConnectorGraphicsInfos>" +
            "    <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "     <Coordinates XCoordinate=\"982.0\" YCoordinate=\"333.0\"/>" +
            "     <Coordinates XCoordinate=\"982.0\" YCoordinate=\"501.0\"/>" +
            "     <Coordinates XCoordinate=\"940.0\" YCoordinate=\"501.0\"/>" +
            "     <Coordinates XCoordinate=\"940.0\" YCoordinate=\"574.0\"/>" +
            "    </ConnectorGraphicsInfo>" +
            "   </ConnectorGraphicsInfos>" +
            "   <ExtendedAttributes/>" +
            "  </MessageFlow>" +
            "  <MessageFlow Id=\"3aef0b59-f79e-4da4-a9b6-ede82b1fa6d7\" Name=\"Travel documents\" Source=\"62410e1b-dcfd-4c53-b6b2-fa2801f42c00\" Target=\"1e64aeb7-b850-4b42-a58c-37cc04da2b7c\">" +
            "   <ConnectorGraphicsInfos>" +
            "    <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" ToPort=\"2\" FromPort=\"1\">" +
            "     <Coordinates XCoordinate=\"1289.0\" YCoordinate=\"559.0\"/>" +
            "     <Coordinates XCoordinate=\"1289.0\" YCoordinate=\"496.0\"/>" +
            "     <Coordinates XCoordinate=\"1147.0\" YCoordinate=\"496.0\"/>" +
            "     <Coordinates XCoordinate=\"1147.0\" YCoordinate=\"332.0\"/>" +
            "    </ConnectorGraphicsInfo>" +
            "   </ConnectorGraphicsInfos>" +
            "   <ExtendedAttributes/>" +
            "  </MessageFlow>" +
            " </MessageFlows>" +
            " <Associations>" +
            "  <Association Id=\"33b608aa-7ff5-47d8-9776-67fec1a07fbb\" Source=\"6162890f-8023-4ac6-a509-dd506d5b2945\" Target=\"3a469c9d-c39b-41f0-95d1-594e72e06367\" Name=\"A1\">" +
            "   <ConnectorGraphicsInfos>" +
            "    <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" FromPort=\"4\">" +
            "     <Coordinates XCoordinate=\"458.0\" YCoordinate=\"48.0\"/>" +
            "     <Coordinates XCoordinate=\"476.0\" YCoordinate=\"48.0\"/>" +
            "     <Coordinates XCoordinate=\"476.0\" YCoordinate=\"108.0\"/>" +
            "    </ConnectorGraphicsInfo>" +
            "   </ConnectorGraphicsInfos>" +
            "   <ExtendedAttributes/>" +
            "  </Association>" +
            "  <Association Id=\"4b1cb74f-e263-4e06-908d-098377a414d8\" Source=\"ab08d48d-6b8d-453c-b079-3496a1342399\" Target=\"6475d36f-e503-4877-84ad-81a3ba5aadd3\" Name=\"A2\">" +
            "   <ConnectorGraphicsInfos>" +
            "    <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "     <Coordinates XCoordinate=\"594.0\" YCoordinate=\"106.0\"/>" +
            "     <Coordinates XCoordinate=\"594.0\" YCoordinate=\"91.5\"/>" +
            "     <Coordinates XCoordinate=\"553.0\" YCoordinate=\"91.5\"/>" +
            "     <Coordinates XCoordinate=\"553.0\" YCoordinate=\"78.0\"/>" +
            "    </ConnectorGraphicsInfo>" +
            "   </ConnectorGraphicsInfos>" +
            "   <ExtendedAttributes/>" +
            "  </Association>" +
            "  <Association Id=\"e4fc466a-03fe-4b1d-a996-929a81a9010a\" Source=\"428c8b95-2813-485e-8920-d965aa4dc1bd\" Target=\"20b34f24-2bda-4ac2-ae7c-fc7cc713556f\" Name=\"A3\">" +
            "   <ConnectorGraphicsInfos>" +
            "    <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "     <Coordinates XCoordinate=\"987.0\" YCoordinate=\"238.0\"/>" +
            "     <Coordinates XCoordinate=\"987.0\" YCoordinate=\"257.0\"/>" +
            "     <Coordinates XCoordinate=\"982.0\" YCoordinate=\"257.0\"/>" +
            "     <Coordinates XCoordinate=\"982.0\" YCoordinate=\"271.0\"/>" +
            "    </ConnectorGraphicsInfo>" +
            "   </ConnectorGraphicsInfos>" +
            "   <ExtendedAttributes/>" +
            "  </Association>" +
            "  <Association Id=\"b7dfac4e-ec15-4eb0-bd97-f80ce87b177b\" Source=\"098ac9d7-51c2-45d0-a53a-1b3b382d0e4c\" Target=\"20b34f24-2bda-4ac2-ae7c-fc7cc713556f\" Name=\"A4\">" +
            "   <ConnectorGraphicsInfos>" +
            "    <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" ToPort=\"12\" FromPort=\"3\">" +
            "     <Coordinates XCoordinate=\"1037.0\" YCoordinate=\"223.0\"/>" +
            "     <Coordinates XCoordinate=\"1016.0\" YCoordinate=\"223.0\"/>" +
            "     <Coordinates XCoordinate=\"1016.0\" YCoordinate=\"271.0\"/>" +
            "    </ConnectorGraphicsInfo>" +
            "   </ConnectorGraphicsInfos>" +
            "   <ExtendedAttributes/>" +
            "  </Association>" +
            "  <Association Id=\"26fc4b3d-8482-4e5a-a05a-3c484022186a\" Source=\"3a469c9d-c39b-41f0-95d1-594e72e06367\" Target=\"6475d36f-e503-4877-84ad-81a3ba5aadd3\" Name=\"A5\">" +
            "   <ConnectorGraphicsInfos>" +
            "    <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" ToPort=\"2\" FromPort=\"12\">" +
            "     <Coordinates XCoordinate=\"514.0\" YCoordinate=\"108.0\"/>" +
            "     <Coordinates XCoordinate=\"514.0\" YCoordinate=\"91.5\"/>" +
            "     <Coordinates XCoordinate=\"553.0\" YCoordinate=\"91.5\"/>" +
            "     <Coordinates XCoordinate=\"553.0\" YCoordinate=\"78.0\"/>" +
            "    </ConnectorGraphicsInfo>" +
            "   </ConnectorGraphicsInfos>" +
            "   <ExtendedAttributes/>" +
            "  </Association>" +
            "  <Association Id=\"187788ac-385d-48bb-9074-fe4c869f0905\" Source=\"ff94771f-103b-4584-95f3-c0c13405d750\" Target=\"20b34f24-2bda-4ac2-ae7c-fc7cc713556f\" Name=\"A6\">" +
            "   <ConnectorGraphicsInfos>" +
            "    <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" ToPort=\"10\" FromPort=\"2\">" +
            "     <Coordinates XCoordinate=\"924.0\" YCoordinate=\"241.0\"/>" +
            "     <Coordinates XCoordinate=\"924.0\" YCoordinate=\"257.0\"/>" +
            "     <Coordinates XCoordinate=\"964.0\" YCoordinate=\"257.0\"/>" +
            "     <Coordinates XCoordinate=\"964.0\" YCoordinate=\"271.0\"/>" +
            "    </ConnectorGraphicsInfo>" +
            "   </ConnectorGraphicsInfos>" +
            "   <ExtendedAttributes/>" +
            "  </Association>" +
            "  <Association Id=\"d68c4874-d67a-4e26-a5ba-66b622b67808\" Source=\"7c8869c7-c2f0-4420-9ee0-b304213d0258\" Target=\"1e64aeb7-b850-4b42-a58c-37cc04da2b7c\" Name=\"A7\">" +
            "   <ConnectorGraphicsInfos>" +
            "    <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" ToPort=\"11\" FromPort=\"2\">" +
            "     <Coordinates XCoordinate=\"1192.0\" YCoordinate=\"233.0\"/>" +
            "     <Coordinates XCoordinate=\"1192.0\" YCoordinate=\"252.5\"/>" +
            "     <Coordinates XCoordinate=\"1164.0\" YCoordinate=\"252.5\"/>" +
            "     <Coordinates XCoordinate=\"1164.0\" YCoordinate=\"272.0\"/>" +
            "    </ConnectorGraphicsInfo>" +
            "   </ConnectorGraphicsInfos>" +
            "   <ExtendedAttributes/>" +
            "  </Association>" +
            "  <Association Id=\"6707e0c5-a0d1-4d1f-a41c-b1616195e63f\" Source=\"1266365e-9893-473e-9f4c-9a3a276d5b08\" Target=\"1e64aeb7-b850-4b42-a58c-37cc04da2b7c\" Name=\"A8\">" +
            "   <ConnectorGraphicsInfos>" +
            "    <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" FromPort=\"2\">" +
            "     <Coordinates XCoordinate=\"1127.0\" YCoordinate=\"233.0\"/>" +
            "     <Coordinates XCoordinate=\"1127.0\" YCoordinate=\"257.5\"/>" +
            "     <Coordinates XCoordinate=\"1147.0\" YCoordinate=\"257.5\"/>" +
            "     <Coordinates XCoordinate=\"1147.0\" YCoordinate=\"272.0\"/>" +
            "    </ConnectorGraphicsInfo>" +
            "   </ConnectorGraphicsInfos>" +
            "   <ExtendedAttributes/>" +
            "  </Association>" +
            "  <Association Id=\"4ad592b8-9b25-4179-8197-53278f4eef2d\" Source=\"805902c6-e634-48a9-8f9a-f695de8f4acf\" Target=\"306e6834-2a11-49c5-adf0-441b87b5524d\" Name=\"A9\">" +
            "   <ConnectorGraphicsInfos>" +
            "    <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "     <Coordinates XCoordinate=\"1166.0\" YCoordinate=\"619.0\"/>" +
            "     <Coordinates XCoordinate=\"1166.0\" YCoordinate=\"680.0\"/>" +
            "     <Coordinates XCoordinate=\"1246.0\" YCoordinate=\"680.0\"/>" +
            "    </ConnectorGraphicsInfo>" +
            "   </ConnectorGraphicsInfos>" +
            "   <ExtendedAttributes/>" +
            "  </Association>" +
            "  <Association Id=\"28547534-ef2c-464b-ab44-8a15c3af0ed0\" Source=\"037d0ec9-ce2a-413a-b3e2-bad12c935eda\" Target=\"9dc4f7c7-899d-4333-8aa0-505174b889b4\" Name=\"A10\">" +
            "   <ConnectorGraphicsInfos>" +
            "    <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" ToPort=\"5\">" +
            "     <Coordinates XCoordinate=\"985.0\" YCoordinate=\"652.0\"/>" +
            "     <Coordinates XCoordinate=\"985.0\" YCoordinate=\"633.0\"/>" +
            "     <Coordinates XCoordinate=\"1013.0\" YCoordinate=\"633.0\"/>" +
            "     <Coordinates XCoordinate=\"1013.0\" YCoordinate=\"619.0\"/>" +
            "    </ConnectorGraphicsInfo>" +
            "   </ConnectorGraphicsInfos>" +
            "   <ExtendedAttributes/>" +
            "  </Association>" +
            "  <Association Id=\"2333d277-cef9-4b31-a68c-1c99d26b235c\" Source=\"d97de8e9-434c-478d-96a7-cf7205135b56\" Target=\"9dc4f7c7-899d-4333-8aa0-505174b889b4\" Name=\"A11\">" +
            "   <ConnectorGraphicsInfos>" +
            "    <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "     <Coordinates XCoordinate=\"1038.0\" YCoordinate=\"645.0\"/>" +
            "     <Coordinates XCoordinate=\"1038.0\" YCoordinate=\"634.0\"/>" +
            "     <Coordinates XCoordinate=\"1048.0\" YCoordinate=\"634.0\"/>" +
            "     <Coordinates XCoordinate=\"1048.0\" YCoordinate=\"619.0\"/>" +
            "    </ConnectorGraphicsInfo>" +
            "   </ConnectorGraphicsInfos>" +
            "   <ExtendedAttributes/>" +
            "  </Association>" +
            "  <Association Id=\"a7859bb4-d60c-4a7c-a537-585c35f74bb6\" Source=\"ef42d8d3-4f84-4921-b877-5ea3556a886d\" Target=\"9dc4f7c7-899d-4333-8aa0-505174b889b4\" Name=\"A12\">" +
            "   <ConnectorGraphicsInfos>" +
            "    <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" ToPort=\"7\" FromPort=\"1\">" +
            "     <Coordinates XCoordinate=\"1092.0\" YCoordinate=\"654.0\"/>" +
            "     <Coordinates XCoordinate=\"1092.0\" YCoordinate=\"633.0\"/>" +
            "     <Coordinates XCoordinate=\"1065.0\" YCoordinate=\"633.0\"/>" +
            "     <Coordinates XCoordinate=\"1065.0\" YCoordinate=\"619.0\"/>" +
            "    </ConnectorGraphicsInfo>" +
            "   </ConnectorGraphicsInfos>" +
            "   <ExtendedAttributes/>" +
            "  </Association>" +
            "  <Association Id=\"18f47699-fc4e-4781-b85b-eac4fa6473a0\" Source=\"306e6834-2a11-49c5-adf0-441b87b5524d\" Target=\"62410e1b-dcfd-4c53-b6b2-fa2801f42c00\" Name=\"A13\">" +
            "   <ConnectorGraphicsInfos>" +
            "    <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "     <Coordinates XCoordinate=\"1266.0\" YCoordinate=\"655.0\"/>" +
            "     <Coordinates XCoordinate=\"1266.0\" YCoordinate=\"637.0\"/>" +
            "     <Coordinates XCoordinate=\"1289.0\" YCoordinate=\"637.0\"/>" +
            "     <Coordinates XCoordinate=\"1289.0\" YCoordinate=\"619.0\"/>" +
            "    </ConnectorGraphicsInfo>" +
            "   </ConnectorGraphicsInfos>" +
            "   <ExtendedAttributes/>" +
            "  </Association>" +
            "  <Association Id=\"c5283b6f-9cb2-4b87-8e41-e96a23d8767c\" Source=\"4d5a4717-6d22-47bf-887d-1460c59d4485\" Target=\"62410e1b-dcfd-4c53-b6b2-fa2801f42c00\" Name=\"A14\">" +
            "   <ConnectorGraphicsInfos>" +
            "    <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" ToPort=\"7\">" +
            "     <Coordinates XCoordinate=\"1336.0\" YCoordinate=\"663.0\"/>" +
            "     <Coordinates XCoordinate=\"1336.0\" YCoordinate=\"641.0\"/>" +
            "     <Coordinates XCoordinate=\"1306.0\" YCoordinate=\"641.0\"/>" +
            "     <Coordinates XCoordinate=\"1306.0\" YCoordinate=\"619.0\"/>" +
            "    </ConnectorGraphicsInfo>" +
            "   </ConnectorGraphicsInfos>" +
            "   <ExtendedAttributes/>" +
            "  </Association>" +
            " </Associations>" +
            " <Artifacts>" +
            "  <Artifact Id=\"6475d36f-e503-4877-84ad-81a3ba5aadd3\" ArtifactType=\"Annotation\" TextAnnotation=\"Iris identification system\" >" +
            "   <NodeGraphicsInfos>" +
            "    <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"54.0\" Width=\"93.0\" BorderColor=\"-2763307\" FillColor=\"-2763307\">" +
            "     <Coordinates XCoordinate=\"507.0\" YCoordinate=\"24.0\"/>" +
            "    </NodeGraphicsInfo>" +
            "   </NodeGraphicsInfos>" +
            "   <Documentation/>" +
            "  </Artifact>" +
            "  <Artifact Id=\"6162890f-8023-4ac6-a509-dd506d5b2945\" ArtifactType=\"DataObject\">" +
            "   <DataObject Id=\"6162890f-8023-4ac6-a509-dd506d5b2945\" Name=\"Passport\" State=\"\">" +
            "    <RequiredForStartSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</RequiredForStartSpecified>" +
            "    <ProducedAtCompletionSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</ProducedAtCompletionSpecified>" +
            "   </DataObject>" +
            "   <NodeGraphicsInfos>" +
            "    <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"50.0\" Width=\"40.0\" BorderColor=\"-10066330\" FillColor=\"-986896\">" +
            "     <Coordinates XCoordinate=\"418.0\" YCoordinate=\"23.0\"/>" +
            "    </NodeGraphicsInfo>" +
            "   </NodeGraphicsInfos>" +
            "   <Documentation/>" +
            "  </Artifact>" +
            "  <Artifact Id=\"428c8b95-2813-485e-8920-d965aa4dc1bd\" ArtifactType=\"DataObject\">" +
            "   <DataObject Id=\"428c8b95-2813-485e-8920-d965aa4dc1bd\" Name=\"Passport\" State=\"\">" +
            "    <RequiredForStartSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</RequiredForStartSpecified>" +
            "    <ProducedAtCompletionSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</ProducedAtCompletionSpecified>" +
            "   </DataObject>" +
            "   <NodeGraphicsInfos>" +
            "    <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"50.0\" Width=\"40.0\" BorderColor=\"-10066330\" FillColor=\"-986896\">" +
            "     <Coordinates XCoordinate=\"967.0\" YCoordinate=\"188.0\"/>" +
            "    </NodeGraphicsInfo>" +
            "   </NodeGraphicsInfos>" +
            "   <Documentation/>" +
            "  </Artifact>" +
            "  <Artifact Id=\"098ac9d7-51c2-45d0-a53a-1b3b382d0e4c\" ArtifactType=\"DataObject\">" +
            "   <DataObject Id=\"098ac9d7-51c2-45d0-a53a-1b3b382d0e4c\" Name=\"Boarding pass\" State=\"\">" +
            "    <RequiredForStartSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</RequiredForStartSpecified>" +
            "    <ProducedAtCompletionSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</ProducedAtCompletionSpecified>" +
            "   </DataObject>" +
            "   <NodeGraphicsInfos>" +
            "    <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"50.0\" Width=\"40.0\" BorderColor=\"-10066330\" FillColor=\"-986896\">" +
            "     <Coordinates XCoordinate=\"1037.0\" YCoordinate=\"198.0\"/>" +
            "    </NodeGraphicsInfo>" +
            "   </NodeGraphicsInfos>" +
            "   <Documentation/>" +
            "  </Artifact>" +
            "  <Artifact Id=\"ff94771f-103b-4584-95f3-c0c13405d750\" ArtifactType=\"DataObject\">" +
            "   <DataObject Id=\"ff94771f-103b-4584-95f3-c0c13405d750\" Name=\"Exit form\" State=\"\">" +
            "    <RequiredForStartSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</RequiredForStartSpecified>" +
            "    <ProducedAtCompletionSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</ProducedAtCompletionSpecified>" +
            "   </DataObject>" +
            "   <NodeGraphicsInfos>" +
            "    <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"50.0\" Width=\"40.0\" BorderColor=\"-10066330\" FillColor=\"-986896\">" +
            "     <Coordinates XCoordinate=\"904.0\" YCoordinate=\"191.0\"/>" +
            "    </NodeGraphicsInfo>" +
            "   </NodeGraphicsInfos>" +
            "   <Documentation/>" +
            "  </Artifact>" +
            "  <Artifact Id=\"7c8869c7-c2f0-4420-9ee0-b304213d0258\" ArtifactType=\"DataObject\">" +
            "   <DataObject Id=\"7c8869c7-c2f0-4420-9ee0-b304213d0258\" Name=\"Boarding pass\" State=\"\">" +
            "    <RequiredForStartSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</RequiredForStartSpecified>" +
            "    <ProducedAtCompletionSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</ProducedAtCompletionSpecified>" +
            "   </DataObject>" +
            "   <NodeGraphicsInfos>" +
            "    <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"50.0\" Width=\"40.0\" BorderColor=\"-10066330\" FillColor=\"-986896\">" +
            "     <Coordinates XCoordinate=\"1172.0\" YCoordinate=\"183.0\"/>" +
            "    </NodeGraphicsInfo>" +
            "   </NodeGraphicsInfos>" +
            "   <Documentation/>" +
            "  </Artifact>" +
            "  <Artifact Id=\"1266365e-9893-473e-9f4c-9a3a276d5b08\" ArtifactType=\"DataObject\">" +
            "   <DataObject Id=\"1266365e-9893-473e-9f4c-9a3a276d5b08\" Name=\"Passport\" State=\"\">" +
            "    <RequiredForStartSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</RequiredForStartSpecified>" +
            "    <ProducedAtCompletionSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</ProducedAtCompletionSpecified>" +
            "   </DataObject>" +
            "   <NodeGraphicsInfos>" +
            "    <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"50.0\" Width=\"40.0\" BorderColor=\"-10066330\" FillColor=\"-986896\">" +
            "     <Coordinates XCoordinate=\"1107.0\" YCoordinate=\"183.0\"/>" +
            "    </NodeGraphicsInfo>" +
            "   </NodeGraphicsInfos>" +
            "   <Documentation/>" +
            "  </Artifact>" +
            "  <Artifact Id=\"306e6834-2a11-49c5-adf0-441b87b5524d\" ArtifactType=\"DataObject\">" +
            "   <DataObject Id=\"306e6834-2a11-49c5-adf0-441b87b5524d\" Name=\"Passport\" State=\"\">" +
            "    <RequiredForStartSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</RequiredForStartSpecified>" +
            "    <ProducedAtCompletionSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</ProducedAtCompletionSpecified>" +
            "   </DataObject>" +
            "   <NodeGraphicsInfos>" +
            "    <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"50.0\" Width=\"40.0\" BorderColor=\"-10066330\" FillColor=\"-986896\">" +
            "     <Coordinates XCoordinate=\"1246.0\" YCoordinate=\"655.0\"/>" +
            "    </NodeGraphicsInfo>" +
            "   </NodeGraphicsInfos>" +
            "   <Documentation/>" +
            "  </Artifact>" +
            "  <Artifact Id=\"ef42d8d3-4f84-4921-b877-5ea3556a886d\" ArtifactType=\"DataObject\">" +
            "   <DataObject Id=\"ef42d8d3-4f84-4921-b877-5ea3556a886d\" Name=\"Boarding pass\" State=\"\">" +
            "    <RequiredForStartSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</RequiredForStartSpecified>" +
            "    <ProducedAtCompletionSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</ProducedAtCompletionSpecified>" +
            "   </DataObject>" +
            "   <NodeGraphicsInfos>" +
            "    <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"50.0\" Width=\"40.0\" BorderColor=\"-10066330\" FillColor=\"-986896\">" +
            "     <Coordinates XCoordinate=\"1072.0\" YCoordinate=\"654.0\"/>" +
            "    </NodeGraphicsInfo>" +
            "   </NodeGraphicsInfos>" +
            "   <Documentation/>" +
            "  </Artifact>" +
            "  <Artifact Id=\"d97de8e9-434c-478d-96a7-cf7205135b56\" ArtifactType=\"DataObject\">" +
            "   <DataObject Id=\"d97de8e9-434c-478d-96a7-cf7205135b56\" Name=\"Passport\" State=\"\">" +
            "    <RequiredForStartSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</RequiredForStartSpecified>" +
            "    <ProducedAtCompletionSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</ProducedAtCompletionSpecified>" +
            "   </DataObject>" +
            "   <NodeGraphicsInfos>" +
            "    <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"50.0\" Width=\"40.0\" BorderColor=\"-10066330\" FillColor=\"-986896\">" +
            "     <Coordinates XCoordinate=\"1018.0\" YCoordinate=\"645.0\"/>" +
            "    </NodeGraphicsInfo>" +
            "   </NodeGraphicsInfos>" +
            "   <Documentation/>" +
            "  </Artifact>" +
            "  <Artifact Id=\"037d0ec9-ce2a-413a-b3e2-bad12c935eda\" ArtifactType=\"DataObject\">" +
            "   <DataObject Id=\"037d0ec9-ce2a-413a-b3e2-bad12c935eda\" Name=\"Exit form\" State=\"\">" +
            "    <RequiredForStartSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</RequiredForStartSpecified>" +
            "    <ProducedAtCompletionSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</ProducedAtCompletionSpecified>" +
            "   </DataObject>" +
            "   <NodeGraphicsInfos>" +
            "    <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"50.0\" Width=\"40.0\" BorderColor=\"-10066330\" FillColor=\"-986896\">" +
            "     <Coordinates XCoordinate=\"965.0\" YCoordinate=\"652.0\"/>" +
            "    </NodeGraphicsInfo>" +
            "   </NodeGraphicsInfos>" +
            "   <Documentation/>" +
            "  </Artifact>" +
            "  <Artifact Id=\"4d5a4717-6d22-47bf-887d-1460c59d4485\" ArtifactType=\"DataObject\">" +
            "   <DataObject Id=\"4d5a4717-6d22-47bf-887d-1460c59d4485\" Name=\"Boarding pass\" State=\"\">" +
            "    <RequiredForStartSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</RequiredForStartSpecified>" +
            "    <ProducedAtCompletionSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</ProducedAtCompletionSpecified>" +
            "   </DataObject>" +
            "   <NodeGraphicsInfos>" +
            "    <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"50.0\" Width=\"40.0\" BorderColor=\"-10066330\" FillColor=\"-986896\">" +
            "     <Coordinates XCoordinate=\"1316.0\" YCoordinate=\"663.0\"/>" +
            "    </NodeGraphicsInfo>" +
            "   </NodeGraphicsInfos>" +
            "   <Documentation/>" +
            "  </Artifact>" +
            " </Artifacts>" +
            " <WorkflowProcesses>" +
            "  <WorkflowProcess Id=\"dbcb5bf2-d661-49d7-9a41-502ff9b29c45\" Name=\"Main Process\">" +
            "   <ProcessHeader>" +
            "    <Created>2010-02-16T20:32:14.9813338+10:00</Created>" +
            "    <Description/>" +
            "   </ProcessHeader>" +
            "   <RedefinableHeader>" +
            "    <Author>BizAgi Process Modeler.</Author>" +
            "    <Version>1.0</Version>" +
            "    <Countrykey>CO</Countrykey>" +
            "   </RedefinableHeader>" +
            "   <ActivitySets/>" +
            "   <Activities/>" +
            "   <Transitions/>" +
            "   <ExtendedAttributes/>" +
            "  </WorkflowProcess>" +
            "  <WorkflowProcess Id=\"b147c0cb-2be1-4246-8a6e-f45be5c695af\" Name=\"Passenger\">" +
            "   <ProcessHeader>" +
            "    <Created>2010-02-16T20:32:14.9813338+10:00</Created>" +
            "    <Description/>" +
            "   </ProcessHeader>" +
            "   <RedefinableHeader>" +
            "    <Author>BizAgi Process Modeler.</Author>" +
            "    <Version>1.0</Version>" +
            "    <Countrykey>CO</Countrykey>" +
            "   </RedefinableHeader>" +
            "   <ActivitySets/>" +
            "   <Activities>" +
            "    <Activity Id=\"9ffb16b9-3250-4ee7-9efd-c41efc7411f6\" Name=\"N0\">" +
            "     <Description/>" +
            "     <Route MarkerVisible=\"true\"/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"40.0\" Width=\"40.0\" BorderColor=\"-5855715\" FillColor=\"-52\">" +
            "       <Coordinates XCoordinate=\"1217.0\" YCoordinate=\"170.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"ab08d48d-6b8d-453c-b079-3496a1342399\" Name=\"N1\">" +
            "     <Description/>" +
            "     <Implementation>" +
            "      <Task/>" +
            "     </Implementation>" +
            "     <Performers/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"65.0\" Width=\"90.0\" BorderColor=\"-16553830\" FillColor=\"-1249281\">" +
            "       <Coordinates XCoordinate=\"549.0\" YCoordinate=\"106.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"3a469c9d-c39b-41f0-95d1-594e72e06367\" Name=\"N2\">" +
            "     <Description/>" +
            "     <Implementation>" +
            "      <Task/>" +
            "     </Implementation>" +
            "     <Performers/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <InputSets>" +
            "      <InputSet>" +
            "       <Input ArtifactId=\"6162890f-8023-4ac6-a509-dd506d5b2945\"/>" +
            "      </InputSet>" +
            "     </InputSets>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"61.0\" Width=\"99.0\" BorderColor=\"-16553830\" FillColor=\"-1249281\">" +
            "       <Coordinates XCoordinate=\"427.0\" YCoordinate=\"108.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"39b181a8-56eb-471c-83b0-f65b7651b972\" Name=\"N3\">" +
            "     <Description/>" +
            "     <Implementation>" +
            "      <Task/>" +
            "     </Implementation>" +
            "     <Performers/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"60.0\" Width=\"90.0\" BorderColor=\"-16553830\" FillColor=\"-1249281\">" +
            "       <Coordinates XCoordinate=\"719.0\" YCoordinate=\"108.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"56afd409-340e-4c97-b066-e2550459f540\" Name=\"N4\">" +
            "     <Description/>" +
            "     <Route MarkerVisible=\"true\"/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"40.0\" Width=\"40.0\" BorderColor=\"-5855715\" FillColor=\"-52\">" +
            "       <Coordinates XCoordinate=\"191.0\" YCoordinate=\"178.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"d7c2e37d-bbd3-4f31-977f-d19a1e83028d\" Name=\"N5\">" +
            "     <Description/>" +
            "     <Implementation>" +
            "      <Task/>" +
            "     </Implementation>" +
            "     <Performers/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"61.0\" Width=\"90.0\" BorderColor=\"-16553830\" FillColor=\"-1249281\">" +
            "       <Coordinates XCoordinate=\"305.0\" YCoordinate=\"108.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"20b34f24-2bda-4ac2-ae7c-fc7cc713556f\" Name=\"N6\">" +
            "     <Description/>" +
            "     <Implementation>" +
            "      <Task/>" +
            "     </Implementation>" +
            "     <Performers/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <InputSets>" +
            "      <InputSet>" +
            "       <Input ArtifactId=\"428c8b95-2813-485e-8920-d965aa4dc1bd\"/>" +
            "       <Input ArtifactId=\"098ac9d7-51c2-45d0-a53a-1b3b382d0e4c\"/>" +
            "       <Input ArtifactId=\"ff94771f-103b-4584-95f3-c0c13405d750\"/>" +
            "      </InputSet>" +
            "     </InputSets>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"62.0\" Width=\"90.0\" BorderColor=\"-16553830\" FillColor=\"-1249281\">" +
            "       <Coordinates XCoordinate=\"937.0\" YCoordinate=\"271.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"34b6c72b-ac63-4081-9808-d41ff58abf09\" Name=\"N7\">" +
            "     <Description/>" +
            "     <Implementation>" +
            "      <Task/>" +
            "     </Implementation>" +
            "     <Performers/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"60.0\" Width=\"90.0\" BorderColor=\"-16553830\" FillColor=\"-1249281\">" +
            "       <Coordinates XCoordinate=\"424.0\" YCoordinate=\"272.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"1e64aeb7-b850-4b42-a58c-37cc04da2b7c\" Name=\"N8\">" +
            "     <Description/>" +
            "     <Implementation>" +
            "      <Task/>" +
            "     </Implementation>" +
            "     <Performers/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <InputSets>" +
            "      <InputSet>" +
            "       <Input ArtifactId=\"7c8869c7-c2f0-4420-9ee0-b304213d0258\"/>" +
            "       <Input ArtifactId=\"1266365e-9893-473e-9f4c-9a3a276d5b08\"/>" +
            "      </InputSet>" +
            "     </InputSets>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"60.0\" Width=\"90.0\" BorderColor=\"-16553830\" FillColor=\"-1249281\">" +
            "       <Coordinates XCoordinate=\"1102.0\" YCoordinate=\"272.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"25071d92-c3a6-4b54-beb4-1147f7f97cab\" Name=\"N9\">" +
            "     <Description/>" +
            "     <Implementation>" +
            "      <Task/>" +
            "     </Implementation>" +
            "     <Performers/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"60.0\" Width=\"90.0\" BorderColor=\"-16553830\" FillColor=\"-1249281\">" +
            "       <Coordinates XCoordinate=\"303.0\" YCoordinate=\"272.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"a6be9295-2829-47f4-aa8a-31d721d03f20\" Name=\"N10\">" +
            "     <Description/>" +
            "     <Event>" +
            "      <IntermediateEvent Trigger=\"Link\">" +
            "       <TriggerResultLink Name=\"Proceed_x0020_to_x0020_boarding\"/>" +
            "      </IntermediateEvent>" +
            "     </Event>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"30.0\" Width=\"30.0\" BorderColor=\"-6909623\" FillColor=\"-66833\">" +
            "       <Coordinates XCoordinate=\"1321.0\" YCoordinate=\"174.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"bf0c9511-c200-4788-9535-34854fc8c799\" Name=\"N11\">" +
            "     <Description/>" +
            "     <Event>" +
            "      <IntermediateEvent Trigger=\"Timer\">" +
            "       <TriggerTimer>" +
            "        <ItemElementName xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">TimeCycle</ItemElementName>" +
            "       </TriggerTimer>" +
            "      </IntermediateEvent>" +
            "     </Event>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"30.0\" Width=\"30.0\" BorderColor=\"-6909623\" FillColor=\"-66833\">" +
            "       <Coordinates XCoordinate=\"667.0\" YCoordinate=\"123.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"98c301c5-0061-4ad6-ae71-932f475afc47\" Name=\"N12\">" +
            "     <Description/>" +
            "     <Event>" +
            "      <IntermediateEvent Trigger=\"Link\">" +
            "       <TriggerResultLink Name=\"Proceed_x0020_from_x0020_Security_x0020_Check\"/>" +
            "      </IntermediateEvent>" +
            "     </Event>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"30.0\" Width=\"30.0\" BorderColor=\"-6909623\" FillColor=\"-66833\">" +
            "       <Coordinates XCoordinate=\"116.0\" YCoordinate=\"184.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"01f71b50-91de-442d-845b-f88a568752b7\" Name=\"N13\">" +
            "     <Description/>" +
            "     <Implementation>" +
            "      <Task/>" +
            "     </Implementation>" +
            "     <Performers/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"60.0\" Width=\"90.0\" BorderColor=\"-6432178\" FillColor=\"-6432178\">" +
            "       <Coordinates XCoordinate=\"636.0\" YCoordinate=\"355.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"10944bde-0484-43e7-9013-5df8eef93c39\" Name=\"N14\">" +
            "     <Description/>" +
            "     <Route MarkerVisible=\"true\"/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"40.0\" Width=\"40.0\" BorderColor=\"-5855715\" FillColor=\"-52\">" +
            "       <Coordinates XCoordinate=\"564.0\" YCoordinate=\"282.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"b1f82b6d-7e0b-4dec-a28d-f7a42b286800\" Name=\"N15\">" +
            "     <Description/>" +
            "     <Route MarkerVisible=\"true\"/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"40.0\" Width=\"40.0\" BorderColor=\"-5855715\" FillColor=\"-52\">" +
            "       <Coordinates XCoordinate=\"849.0\" YCoordinate=\"282.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"a8f37f1d-6953-4eeb-8a6e-1f447e519a21\" Name=\"N16\">" +
            "     <Description/>" +
            "     <Implementation>" +
            "      <Task/>" +
            "     </Implementation>" +
            "     <Performers/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"60.0\" Width=\"90.0\" BorderColor=\"-6432178\" FillColor=\"-6432178\">" +
            "       <Coordinates XCoordinate=\"747.0\" YCoordinate=\"355.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "   </Activities>" +
            "   <Transitions>" +
            "    <Transition Id=\"2f3a14af-0eb7-45d0-ba1e-a16b98de35d3\" From=\"98c301c5-0061-4ad6-ae71-932f475afc47\" To=\"56afd409-340e-4c97-b066-e2550459f540\" Name=\"T1\">" +
            "     <Condition/>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "       <Coordinates XCoordinate=\"146.0\" YCoordinate=\"199.0\"/>" +
            "       <Coordinates XCoordinate=\"168.5\" YCoordinate=\"199.0\"/>" +
            "       <Coordinates XCoordinate=\"168.5\" YCoordinate=\"198.0\"/>" +
            "       <Coordinates XCoordinate=\"191.0\" YCoordinate=\"198.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"1cae608a-2eb9-4a57-b7b7-f6e38c435b3a\" From=\"39b181a8-56eb-471c-83b0-f65b7651b972\" To=\"9ffb16b9-3250-4ee7-9efd-c41efc7411f6\" Name=\"T2\">" +
            "     <Condition/>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" ToPort=\"1\">" +
            "       <Coordinates XCoordinate=\"809.0\" YCoordinate=\"138.0\"/>" +
            "       <Coordinates XCoordinate=\"1237.0\" YCoordinate=\"138.0\"/>" +
            "       <Coordinates XCoordinate=\"1237.0\" YCoordinate=\"170.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"24947102-1b90-4c53-9305-cb0b6412e93b\" From=\"56afd409-340e-4c97-b066-e2550459f540\" To=\"d7c2e37d-bbd3-4f31-977f-d19a1e83028d\" Name=\"T3\">" +
            "     <Condition Type=\"CONDITION\">" +
            "      <Expression/>" +
            "     </Condition>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" FromPort=\"1\">" +
            "       <Coordinates XCoordinate=\"211.0\" YCoordinate=\"178.0\"/>" +
            "       <Coordinates XCoordinate=\"211.0\" YCoordinate=\"138.0\"/>" +
            "       <Coordinates XCoordinate=\"305.0\" YCoordinate=\"138.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"42b83fb1-14d4-4cc8-90e1-a16e8bb1a0fb\" From=\"3a469c9d-c39b-41f0-95d1-594e72e06367\" To=\"ab08d48d-6b8d-453c-b079-3496a1342399\" Name=\"T4\">" +
            "     <Condition/>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "       <Coordinates XCoordinate=\"526.0\" YCoordinate=\"138.0\"/>" +
            "       <Coordinates XCoordinate=\"549.0\" YCoordinate=\"138.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"cc184f36-851e-4785-88f6-37dfde7360fa\" From=\"d7c2e37d-bbd3-4f31-977f-d19a1e83028d\" To=\"3a469c9d-c39b-41f0-95d1-594e72e06367\" Name=\"T5\">" +
            "     <Condition/>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "       <Coordinates XCoordinate=\"395.0\" YCoordinate=\"138.0\"/>" +
            "       <Coordinates XCoordinate=\"427.0\" YCoordinate=\"138.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"8aadfc40-ebba-4c0f-8820-4db370d4b751\" From=\"bf0c9511-c200-4788-9535-34854fc8c799\" To=\"39b181a8-56eb-471c-83b0-f65b7651b972\" Name=\"T6\">" +
            "     <Condition/>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "       <Coordinates XCoordinate=\"697.0\" YCoordinate=\"138.0\"/>" +
            "       <Coordinates XCoordinate=\"719.0\" YCoordinate=\"138.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"31921dc2-cc6d-49c8-8ddd-92697babef0f\" From=\"20b34f24-2bda-4ac2-ae7c-fc7cc713556f\" To=\"1e64aeb7-b850-4b42-a58c-37cc04da2b7c\" Name=\"T7\">" +
            "     <Condition/>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "       <Coordinates XCoordinate=\"1027.0\" YCoordinate=\"302.0\"/>" +
            "       <Coordinates XCoordinate=\"1102.0\" YCoordinate=\"302.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"0c6a38b3-ee74-4113-804f-a8bb26a034d2\" From=\"25071d92-c3a6-4b54-beb4-1147f7f97cab\" To=\"34b6c72b-ac63-4081-9808-d41ff58abf09\" Name=\"T8\">" +
            "     <Condition/>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "       <Coordinates XCoordinate=\"393.0\" YCoordinate=\"302.0\"/>" +
            "       <Coordinates XCoordinate=\"424.0\" YCoordinate=\"302.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"9e97f588-04ce-425e-a957-2cffdf2fd74a\" From=\"1e64aeb7-b850-4b42-a58c-37cc04da2b7c\" To=\"9ffb16b9-3250-4ee7-9efd-c41efc7411f6\" Name=\"T9\">" +
            "     <Condition/>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" ToPort=\"2\">" +
            "       <Coordinates XCoordinate=\"1192.0\" YCoordinate=\"302.0\"/>" +
            "       <Coordinates XCoordinate=\"1237.0\" YCoordinate=\"302.0\"/>" +
            "       <Coordinates XCoordinate=\"1237.0\" YCoordinate=\"210.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"2cf1c883-614e-47f3-9a97-83a0c6d7f71c\" From=\"56afd409-340e-4c97-b066-e2550459f540\" To=\"25071d92-c3a6-4b54-beb4-1147f7f97cab\" Name=\"T10\">" +
            "     <Condition Type=\"CONDITION\">" +
            "      <Expression/>" +
            "     </Condition>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" FromPort=\"2\">" +
            "       <Coordinates XCoordinate=\"211.0\" YCoordinate=\"218.0\"/>" +
            "       <Coordinates XCoordinate=\"211.0\" YCoordinate=\"302.0\"/>" +
            "       <Coordinates XCoordinate=\"303.0\" YCoordinate=\"302.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"d9f5a23d-2065-4c05-ab1e-a11e49791568\" From=\"ab08d48d-6b8d-453c-b079-3496a1342399\" To=\"bf0c9511-c200-4788-9535-34854fc8c799\" Name=\"T11\">" +
            "     <Condition/>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "       <Coordinates XCoordinate=\"639.0\" YCoordinate=\"138.0\"/>" +
            "       <Coordinates XCoordinate=\"667.0\" YCoordinate=\"138.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"5a875bd6-939f-460f-af0e-b5e82a82d513\" From=\"9ffb16b9-3250-4ee7-9efd-c41efc7411f6\" To=\"a6be9295-2829-47f4-aa8a-31d721d03f20\" Name=\"T12\">" +
            "     <Condition Type=\"CONDITION\">" +
            "      <Expression/>" +
            "     </Condition>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "       <Coordinates XCoordinate=\"1257.0\" YCoordinate=\"190.0\"/>" +
            "       <Coordinates XCoordinate=\"1289.0\" YCoordinate=\"190.0\"/>" +
            "       <Coordinates XCoordinate=\"1289.0\" YCoordinate=\"189.0\"/>" +
            "       <Coordinates XCoordinate=\"1321.0\" YCoordinate=\"189.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"15412912-3a69-449a-8b7f-e1ffc97cd87a\" From=\"10944bde-0484-43e7-9013-5df8eef93c39\" To=\"01f71b50-91de-442d-845b-f88a568752b7\" Name=\"T13\">" +
            "     <Condition Type=\"CONDITION\">" +
            "      <Expression/>" +
            "     </Condition>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" FromPort=\"2\">" +
            "       <Coordinates XCoordinate=\"584.0\" YCoordinate=\"322.0\"/>" +
            "       <Coordinates XCoordinate=\"584.0\" YCoordinate=\"385.0\"/>" +
            "       <Coordinates XCoordinate=\"636.0\" YCoordinate=\"385.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"619af006-51e4-4d9d-b04d-42c067fc0305\" From=\"a8f37f1d-6953-4eeb-8a6e-1f447e519a21\" To=\"b1f82b6d-7e0b-4dec-a28d-f7a42b286800\" Name=\"T14\">" +
            "     <Condition/>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" ToPort=\"2\" FromPort=\"4\">" +
            "       <Coordinates XCoordinate=\"837.0\" YCoordinate=\"385.0\"/>" +
            "       <Coordinates XCoordinate=\"869.0\" YCoordinate=\"385.0\"/>" +
            "       <Coordinates XCoordinate=\"869.0\" YCoordinate=\"322.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"8ef6bdca-771a-4492-b117-838ef3f11654\" From=\"10944bde-0484-43e7-9013-5df8eef93c39\" To=\"b1f82b6d-7e0b-4dec-a28d-f7a42b286800\" Name=\"T15\">" +
            "     <Condition Type=\"CONDITION\">" +
            "      <Expression/>" +
            "     </Condition>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "       <Coordinates XCoordinate=\"604.0\" YCoordinate=\"302.0\"/>" +
            "       <Coordinates XCoordinate=\"849.0\" YCoordinate=\"302.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"6fcd2411-c210-4003-a606-a9e590dcd7f0\" From=\"01f71b50-91de-442d-845b-f88a568752b7\" To=\"a8f37f1d-6953-4eeb-8a6e-1f447e519a21\" Name=\"T16\">" +
            "     <Condition/>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"-6432178\">" +
            "       <Coordinates XCoordinate=\"726.0\" YCoordinate=\"385.0\"/>" +
            "       <Coordinates XCoordinate=\"747.0\" YCoordinate=\"385.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"81f6e9d1-f5ff-4c76-92a9-a10b308c379c\" From=\"b1f82b6d-7e0b-4dec-a28d-f7a42b286800\" To=\"20b34f24-2bda-4ac2-ae7c-fc7cc713556f\" Name=\"T17\">" +
            "     <Condition Type=\"CONDITION\">" +
            "      <Expression/>" +
            "     </Condition>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "       <Coordinates XCoordinate=\"889.0\" YCoordinate=\"302.0\"/>" +
            "       <Coordinates XCoordinate=\"937.0\" YCoordinate=\"302.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"0f003524-95dd-4aed-a863-ed1b115b58a1\" From=\"34b6c72b-ac63-4081-9808-d41ff58abf09\" To=\"10944bde-0484-43e7-9013-5df8eef93c39\" Name=\"T18\">" +
            "     <Condition/>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "       <Coordinates XCoordinate=\"514.0\" YCoordinate=\"302.0\"/>" +
            "       <Coordinates XCoordinate=\"564.0\" YCoordinate=\"302.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "   </Transitions>" +
            "   <ExtendedAttributes/>" +
            "  </WorkflowProcess>" +
            "  <WorkflowProcess Id=\"b68f49d5-1e4f-4873-9087-399a0da09a74\" Name=\"Government Agent\">" +
            "   <ProcessHeader>" +
            "    <Created>2010-02-16T20:32:14.9813338+10:00</Created>" +
            "    <Description/>" +
            "   </ProcessHeader>" +
            "   <RedefinableHeader>" +
            "    <Author>BizAgi Process Modeler.</Author>" +
            "    <Version>1.0</Version>" +
            "    <Countrykey>CO</Countrykey>" +
            "   </RedefinableHeader>" +
            "   <ActivitySets/>" +
            "   <Activities>" +
            "    <Activity Id=\"a5559df9-2311-4fa7-b566-819d69266bf4\" Name=\"N17\">" +
            "     <Description/>" +
            "     <Event>" +
            "      <StartEvent Trigger=\"Message\">" +
            "       <TriggerResultMessage/>" +
            "      </StartEvent>" +
            "     </Event>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"30.0\" Width=\"30.0\" BorderColor=\"-10311914\" FillColor=\"-1638505\">" +
            "       <Coordinates XCoordinate=\"925.0\" YCoordinate=\"574.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"9dc4f7c7-899d-4333-8aa0-505174b889b4\" Name=\"N18\">" +
            "     <Description/>" +
            "     <Implementation>" +
            "      <Task/>" +
            "     </Implementation>" +
            "     <Performers/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <InputSets>" +
            "      <InputSet>" +
            "       <Input ArtifactId=\"037d0ec9-ce2a-413a-b3e2-bad12c935eda\"/>" +
            "       <Input ArtifactId=\"ef42d8d3-4f84-4921-b877-5ea3556a886d\"/>" +
            "       <Input ArtifactId=\"d97de8e9-434c-478d-96a7-cf7205135b56\"/>" +
            "      </InputSet>" +
            "     </InputSets>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"60.0\" Width=\"90.0\" BorderColor=\"-16553830\" FillColor=\"-1249281\">" +
            "       <Coordinates XCoordinate=\"1003.0\" YCoordinate=\"559.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"805902c6-e634-48a9-8f9a-f695de8f4acf\" Name=\"N19\">" +
            "     <Description/>" +
            "     <Implementation>" +
            "      <Task/>" +
            "     </Implementation>" +
            "     <Performers/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <OutputSets>" +
            "      <OutputSet>" +
            "       <Output ArtifactId=\"306e6834-2a11-49c5-adf0-441b87b5524d\"/>" +
            "      </OutputSet>" +
            "     </OutputSets>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"60.0\" Width=\"90.0\" BorderColor=\"-16553830\" FillColor=\"-1249281\">" +
            "       <Coordinates XCoordinate=\"1121.0\" YCoordinate=\"559.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"5e7a1e77-4fb0-4f89-bee5-cfa642a72973\" Name=\"N20\">" +
            "     <Description/>" +
            "     <Event>" +
            "      <EndEvent/>" +
            "     </Event>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"30.0\" Width=\"30.0\" BorderColor=\"-6750208\" FillColor=\"-1135958\">" +
            "       <Coordinates XCoordinate=\"1373.0\" YCoordinate=\"574.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "    <Activity Id=\"62410e1b-dcfd-4c53-b6b2-fa2801f42c00\" Name=\"N21\">" +
            "     <Description/>" +
            "     <Implementation>" +
            "      <Task/>" +
            "     </Implementation>" +
            "     <Performers/>" +
            "     <Documentation/>" +
            "     <ExtendedAttributes/>" +
            "     <InputSets>" +
            "      <InputSet>" +
            "       <Input ArtifactId=\"306e6834-2a11-49c5-adf0-441b87b5524d\"/>" +
            "       <Input ArtifactId=\"4d5a4717-6d22-47bf-887d-1460c59d4485\"/>" +
            "      </InputSet>" +
            "     </InputSets>" +
            "     <NodeGraphicsInfos>" +
            "      <NodeGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" Height=\"60.0\" Width=\"90.0\" BorderColor=\"-16553830\" FillColor=\"-1249281\">" +
            "       <Coordinates XCoordinate=\"1244.0\" YCoordinate=\"559.0\"/>" +
            "      </NodeGraphicsInfo>" +
            "     </NodeGraphicsInfos>" +
            "     <IsForCompensationSpecified xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">false</IsForCompensationSpecified>" +
            "    </Activity>" +
            "   </Activities>" +
            "   <Transitions>" +
            "    <Transition Id=\"486a43df-eae2-409c-b7bc-d3dfd6cd0b49\" From=\"a5559df9-2311-4fa7-b566-819d69266bf4\" To=\"9dc4f7c7-899d-4333-8aa0-505174b889b4\" Name=\"T19\">" +
            "     <Condition/>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "       <Coordinates XCoordinate=\"955.0\" YCoordinate=\"589.0\"/>" +
            "       <Coordinates XCoordinate=\"1003.0\" YCoordinate=\"589.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"30eea2d3-7a5c-40ba-93a1-4ccee79d5070\" From=\"9dc4f7c7-899d-4333-8aa0-505174b889b4\" To=\"805902c6-e634-48a9-8f9a-f695de8f4acf\" Name=\"T20\">" +
            "     <Condition/>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" ToPort=\"3\">" +
            "       <Coordinates XCoordinate=\"1093.0\" YCoordinate=\"589.0\"/>" +
            "       <Coordinates XCoordinate=\"1121.0\" YCoordinate=\"589.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"c24fb532-5aa9-4e46-b9fe-9bf7c01b32bc\" From=\"805902c6-e634-48a9-8f9a-f695de8f4acf\" To=\"62410e1b-dcfd-4c53-b6b2-fa2801f42c00\" Name=\"T21\">" +
            "     <Condition/>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\">" +
            "       <Coordinates XCoordinate=\"1211.0\" YCoordinate=\"589.0\"/>" +
            "       <Coordinates XCoordinate=\"1244.0\" YCoordinate=\"589.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "    <Transition Id=\"e9648e1d-c588-4984-9044-205747c3bc01\" From=\"62410e1b-dcfd-4c53-b6b2-fa2801f42c00\" To=\"5e7a1e77-4fb0-4f89-bee5-cfa642a72973\" Name=\"T22\">" +
            "     <Condition/>" +
            "     <Description/>" +
            "     <ExtendedAttributes/>" +
            "     <ConnectorGraphicsInfos>" +
            "      <ConnectorGraphicsInfo ToolId=\"BizAgi_Process_Modeler\" BorderColor=\"0\" FromPort=\"4\">" +
            "       <Coordinates XCoordinate=\"1334.0\" YCoordinate=\"589.0\"/>" +
            "       <Coordinates XCoordinate=\"1373.0\" YCoordinate=\"589.0\"/>" +
            "      </ConnectorGraphicsInfo>" +
            "     </ConnectorGraphicsInfos>" +
            "    </Transition>" +
            "   </Transitions>" +
            "   <ExtendedAttributes/>" +
            "  </WorkflowProcess>" +
            " </WorkflowProcesses>" +
            " <ExtendedAttributes/>" +
            "</Package>";

}
