/*-
 * #%L
 * This file is part of "Apromore Community".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package au.ltl.main;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import au.ltl.domain.Constraint;
import au.ltl.utils.ModelAbstractions;


public class Main {

	public static void main(String[] args) throws Exception {

		String modelName="Apromore-OSGI-Bundles/ltl-conf-check-osgi/data"+File.separator+"model1.bpmn";
		byte[] modelArray=getFileAsArray(modelName);
		ModelAbstractions model = new ModelAbstractions(modelArray);
		File XmlFileDeclareRules=new File("Apromore-OSGI-Bundles/ltl-conf-check-osgi/data"+File.separator+"declare10.xml");;
		LinkedList<Constraint> LTLConstraintList=null;
		int addActionCost=1;
		int deleteActionCost=1;

		ModelChecker checker = new ModelChecker(model,new FileInputStream(XmlFileDeclareRules), LTLConstraintList, addActionCost, deleteActionCost);
		HashMap<String, List<RuleVisualization>> results = checker.checkNet();

//		System.out.println(new Gson().toJson(results));
		System.out.println("Check completed");
	}

	public static byte[] getFileAsArray(String fileName) {
		FileInputStream fileInputStream = null;
		File file = new File(fileName);

		try {
			byte[] bFile = new byte[(int) file.length()];

			// convert file into array of bytes
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bFile);
			fileInputStream.close();

			return bFile;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	private static void st(Object x){
		System.out.println(x.toString());
	}
}
