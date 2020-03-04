/*
 *  Copyright (C) 2018 Raffaele Conforti (www.raffaeleconforti.com)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.raffaeleconforti.alphadollar.alphamminer;

import java.util.ArrayList;

public class AlphaPPData {
	RelationMatrix rmRelation = new RelationMatrix();	//relation matrix.
	ArrayList<String> alL1L = new ArrayList<String>();	//alL1L
	ArrayList<String> alT_I = new ArrayList<String>();	//alT_I
	ArrayList<String> alT_O = new ArrayList<String>();	//alT_O
	ArrayList<String> alT_prime = new ArrayList();				//alT_prime
	ArrayList<String> alT_log = new ArrayList();				//alT_log
	int allSize;										//size of all tasks
	int allVisibleSize;									//size of all visible tasks
	L1LPlaces lpL_W;
	ArrayList<InvTask> invTasks = new ArrayList<InvTask>();			//the invisible tasks
	ArrayList<AXYB> invTaskAXYB;							//the medacious relationship found in the alpha#
}
