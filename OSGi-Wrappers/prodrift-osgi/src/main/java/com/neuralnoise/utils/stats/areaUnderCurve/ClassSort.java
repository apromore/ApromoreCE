/*-
 * #%L
 * This file is part of "Apromore Community".
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
// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ClassSort.java

package com.neuralnoise.utils.stats.areaUnderCurve;


public class ClassSort
    implements Comparable
{

    public ClassSort(double d, int i)
    {
        val = d;
        classification = i;
    }

    public int getClassification()
    {
        return classification;
    }

    public double getProb()
    {
        return val;
    }

    public int compareTo(Object obj)
    {
        double d = ((ClassSort)obj).getProb();
        if(val < d)
            return -1;
        if(val > d)
            return 1;
        int i = ((ClassSort)obj).getClassification();
        if(i == classification)
            return 0;
        return classification <= i ? 1 : -1;
    }

    private double val;
    private int classification;
}
