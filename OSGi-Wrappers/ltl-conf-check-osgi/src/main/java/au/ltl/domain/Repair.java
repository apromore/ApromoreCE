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
package au.ltl.domain;

public class Repair {
	
	String repairType; // can be either "del" or "add"
	String labelToAdd; //Meaningful  only if the actionType is equal to "add"
	
	public Repair(String repairType, String labelToAdd) {
		super();
		this.repairType = repairType;
		this.labelToAdd = labelToAdd;
	}
	
	public String getRepairType() {
		return repairType;
	}
	public void setRepairType(String repairType) {
		this.repairType = repairType;
	}
	public String getLabelToAdd() {
		return labelToAdd;
	}
	public void setLabelToAdd(String labelToAdd) {
		this.labelToAdd = labelToAdd;
	}

	@Override
	public String toString() {
		return "Repair [repairType=" + repairType + ", labelToAdd=" + labelToAdd + "]";
	}
	
	
}
