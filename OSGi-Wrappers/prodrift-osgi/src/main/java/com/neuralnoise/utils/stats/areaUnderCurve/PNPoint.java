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
// Source File Name:   PNPoint.java

package com.neuralnoise.utils.stats.areaUnderCurve;


public class PNPoint
    implements Comparable
{

    public PNPoint(double d, double d1)
    {
        if(d < 0.0D || d1 < 0.0D)
        {
            pos = 0.0D;
            neg = 0.0D;
            System.err.println((new StringBuilder()).append("ERROR: ").append(d).append(",").append(d1).append(" - Defaulting ").append("PNPoint to 0,0").toString());
        } else
        {
            pos = d;
            neg = d1;
        }
    }

    public double getPos()
    {
        return pos;
    }

    public double getNeg()
    {
        return neg;
    }

    public int compareTo(Object obj)
    {
        if(obj instanceof PNPoint)
        {
            PNPoint pnpoint = (PNPoint)obj;
            if(pos - pnpoint.pos > 0.0D)
                return 1;
            if(pos - pnpoint.pos < 0.0D)
                return -1;
            if(neg - pnpoint.neg > 0.0D)
                return 1;
            return neg - pnpoint.neg >= 0.0D ? 0 : -1;
        } else
        {
            return -1;
        }
    }

    public boolean equals(Object obj)
    {
        if(obj instanceof PNPoint)
        {
            PNPoint pnpoint = (PNPoint)obj;
            if(Math.abs(pos - pnpoint.pos) > 0.001D)
                return false;
            return Math.abs(neg - pnpoint.neg) <= 0.001D;
        } else
        {
            return false;
        }
    }

    public String toString()
    {
        String s = "";
        s = (new StringBuilder()).append(s).append("(").append(pos).append(",").append(neg).append(")").toString();
        return s;
    }

    private double pos;
    private double neg;
}
