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

/**
 *
 */
package com.raffaeleconforti.foreignkeydiscovery.functionaldependencies;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;


/**
 * The class TANEjava provides key features to extract functional dependencies of any table of a given
 * database. The following code is sufficient to obtain functional dependencies for a
 * given table. Note that the DatabaseConnection object must be associated with a table by using the <i>setTable</i>
 * method.<p>
 * <code>
 * DatabaseConnection con = new DatabaseConnection();<br>
 * try{<br>
 * con.connect(DatabaseConnection.DBvendors.Oracle10gSeries,"localhost","1521","XE", USER, PASSWORD);<br>
 * con.setTable("employees");<br>
 * TANEjava tane = new TANEjava(con);<br>
 * tane.getFD();<br>
 * </code>
 *
 * @author Tobias
 *         Modified by Tanel Teinemaa
 */

public class TANEjava {

    int foundFDs;
    //Tane standard output file
    //private String TaneResultFile = "TanejavaOutput.xml";
    //BufferedWriter output = null;
    //----------------DEBUG MODE------------------------
    boolean debug = false;
    /* To store Partitions on disk */
    String tempFolder = System.getProperty("java.io.tmpdir");
    //private boolean isConnected;//the current table to be investigated
    private int stoplevel;
    //table information
    private Data data;
    private int table_columns;
    private int table_rows;
    // --------------Information the algorithm uses------------------------>
    private UnifiedMap<BitSet, CandidateInfo> last_level = null;    //level l-1
    private UnifiedMap<BitSet, CandidateInfo> current_level = null;    //level l
    private UnifiedMap<BitSet, ArrayList<BitSet>> prefix_blocks = null;
    private UnifiedMap<BitSet, BitSet> foundFD = null;
    private BitSet foundValues = null;
    private UnifiedSet<BitSet> candidateKeys = null;
    /* Variables for calculating with partitions */
    private int tuple_tbl[];
    private int set_tbl[];
    private int set_visit[];
    private int newtuple_tbl[];
    private int noofsets = 0;


    public TANEjava(Data dt) {

        data = dt;

        //update member variables

        table_columns = data.columnTitles.length;
        table_rows = data.table.size();
        //default stoplevel
        stoplevel = table_columns;

        //inialize tables to calculate partitions effiently
        int nooftuples = table_rows + 1;
        noofsets = nooftuples;

        tuple_tbl = new int[nooftuples];
        set_tbl = new int[noofsets];
        set_visit = new int[noofsets];
        newtuple_tbl = new int[noofsets];
    }

    /**
     * Computes the minimal cover for a set of functional dependencies.
     *
     * @param FD_SET -a set of functional dependencies
     * @return
     */
    public static ComparableSet<FunctionalDependency> minimalCover(ComparableSet<FunctionalDependency> FD_SET) {
        ComparableSet<FunctionalDependency> cover = new ComparableSet<FunctionalDependency>();

        // Applying decomposition rule
        for (FunctionalDependency fd : FD_SET) {
            for (String Y : fd.getY()) {
                FunctionalDependency fd_new = new FunctionalDependency();
                fd_new.addX(fd.getX());
                fd_new.addY(Y);
                cover.add(fd_new);
            }
        }
        // make the LHS of cover minimal
        ComparableSet<FunctionalDependency> newFDs = new ComparableSet<FunctionalDependency>();

        Iterator<FunctionalDependency> it = cover.iterator();
        while (it.hasNext()) {
            FunctionalDependency fd = it.next();
            if (fd.getX().size() > 1) {
                for (String B : fd.getX()) {
                    if (fd.getY()
                            .isSubset(closure(cover, fd.getX().without(B)))) {
                        FunctionalDependency replacedFD = new FunctionalDependency();
                        replacedFD.addX(fd.getX().without(B));
                        replacedFD.addY(fd.getY());
                        newFDs.add(replacedFD);
                        it.remove();
                    }
                }
            }
        }
        cover.addAll(newFDs);
//        newFDs = null; // clean up for Garbage Collector
        // System.out.println("Fc nach Linksreduktion: "+cover);
        // remove redundant FDs
        it = cover.iterator(); // inializes the iterator new
        while (it.hasNext()) {
            FunctionalDependency fd = it.next();
            if (fd.getY().isSubset(closure(cover.without(fd), fd.getX())))
                it.remove();
        }

        return cover;
    }

    /**
     * This method finds the minimal dependencies with the left-hand side in L<sub>l-1</sub>
     */

    /**
     * Computes the closure of an attribute set X under F where F is a set of
     * functional dependencies. For all subsets A in the closure we have
     * <p/>
     * <center>X --> A</center> Therefore X --> 'closure' also holds due to the
     * decomposition rule
     *
     * @param FD_SET
     * @param ATTR_SET
     * @return ComparableSet<String> - the closure of an attribute according to
     * F
     */
    public static ComparableSet<String> closure(ComparableSet<FunctionalDependency> FD_SET, ComparableSet<String> ATTR_SET) {
        ComparableSet<String> closure = new ComparableSet<String>();
        ComparableSet<String> oldClosure = new ComparableSet<String>();

        closure.addAll(ATTR_SET);
        do {
            // oldClosure.clear();
            oldClosure.addAll(closure);
            for (FunctionalDependency fd : FD_SET) {
                if (fd.getX().isSubset(closure))
                    closure.addAll(fd.getY());

            }
        } while (!closure.equals(oldClosure));
        return closure;
    }

    /**
     * This is the TANE main algorithm and returns all minimal non-trivial
     * functional dependencies. By default the output is written in TanejavaOutput.xml. You can change the filename by using
     * calling setTaneResultFile().<p>
     * Before any use of this method, a table must
     * be specified by using the <i>setTable</i>  method.<p>
     * <code>
     * DatabaseConnection con = new DatabaseConnection();<br>
     * try{<br>
     * con.connect(DatabaseConnection.DBvendors.Oracle10gSeries,"localhost","1521","XE", USER, PASSWORD);<br>
     * con.setTable("employees");<br>
     * TANEjava tane = new TANEjava(con);<br>
     * tane.getFD();<br>
     * </code>
     *
     * @throws Exception
     * @throws OutOfMemoryError
     * @throws ArrayStoreException
     */
    public void getFD() throws OutOfMemoryError {

        try {

			/*if(debug){
                System.out.println("Event class \"" + data.title + "\"");


				System.out.print("{");
				boolean first = true;
				for (int i = 1; i <= data.columnTitles.length; i++) {
					if(!first) System.out.print(", ");
					else first=false;
					System.out.print(data.columnTitles[i-1]);
					// operate on index i here
				}
				System.out.println("}");


				System.out.println("Minimal dependencies:");

			}*/

            long start = System.currentTimeMillis(); // start timing

			/*the levels are hold into a hashtable for constant access time O(1)
             * Only two level are needed
			 */
            last_level = new UnifiedMap<BitSet, CandidateInfo>();
            current_level = new UnifiedMap<BitSet, CandidateInfo>();
            foundFD = new UnifiedMap<BitSet, BitSet>();


            // ----------Create Level 0 ----------------------------
            BitSet empty = new BitSet();
            CandidateInfo c = new CandidateInfo();
            c.setRHS_BitRange(1, table_columns + 1);    // C(empty)=R
            last_level.put(empty, c);

            // ------------create Level 1--------------------->
            for (int i = 1; i <= table_columns; i++) {
                BitSet candidate = new BitSet();
                candidate.set(i); // initalize BitSet

                CandidateInfo candidateInfo = new CandidateInfo(); // Create CandiateInfo
                candidateInfo.setRHS_BitRange(1, table_columns + 1);

                //Create Strtipped Parition only if we useTANEsql is set to false
                // query database for creating stripped Partitions
                //We need to sort the tuples by column[0] --> Tuples might be stored unstructered on disk
                String col = data.columnTitles[i - 1];

                // create StrippedPartition
                StrippedPartition sp = new StrippedPartition(data, col, table_rows);

                candidateInfo.setStrippedPartition(sp);

                current_level.put(candidate, candidateInfo);
            }


            //Write XML file header
            //output = new BufferedWriter(new FileWriter(TaneResultFile));
            //output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            //output.write("<?xml-stylesheet type=\"text/xsl\" href=\"transform.xsl\"?>\n");
            //output.write("<configuration>\n");
            //output.write("\t<dbConfig>\n");
            //output.write("\t\t<dbUrl>"+ "</dbUrl>\n");
            //output.write("\t\t<username>" + "</username>\n");
            //output.write("\t\t<driverClassName>" + "</driverClassName>\n");
            //String driverFileName = "YOUR DRIVER FILE NAME";


            //output.write("\t\t<driverFileName>" + driverFileName + "</driverFileName>\n");
            //output.write("\t</dbConfig>\n");
            //output.write("\t<schema name=\""+"\">\n");
            //output.write("\t\t<tableInfo>\n");
            //output.write("\t\t\t<table name=\""+"\">\n");
            //output.write("\t\t\t\t<fds>\n");


            // -----------------------------MAIN_LOOP------------------>
            int l = 1;
            while (!current_level.isEmpty() && l <= stoplevel) {
                computeDependencies(l);        //find all minimal dependencies in previous level

                prune();                    //prunning the search space


                //delete last_level
                for (CandidateInfo candidateInfo : last_level.values()) {
                    candidateInfo.clear();
                }
                last_level.clear();    //last level is no longer of interest
                last_level = null;
                System.gc();    //run garbage collector

                //calculate the next level from the current level
                generateNextLevel(l);
                l++;
            }

            //Convert milliseconds to hh:mm:ss
//            sec = sec % 60;    //neue sekunden
            //int hours = min/60;
//            min = min % 60;


            //Write file tail
            //output.write("\t\t\t\t</fds>\n");
            //output.write("\t\t\t</table>\n");
            //output.write("\t\t</tableInfo>\n");
            //output.write("\t</schema>\n");
            //output.write("\t<time>\n");
            //output.write("\t\t<hours>"+hours+"</hours>\n");
            //output.write("\t\t<minutes>"+min+"</minutes>\n");
            //output.write("\t\t<seconds>"+sec+"</seconds>\n");
            //output.write("\t\t<milliseconds>"+time+"</milliseconds>\n");
            //output.write("\t</time>\n");
            //output.write("</configuration>\n");

            foundFDs = 0;  //reset FDs to 0
            //close output file
            //output.close();
        } finally {
            //close files and delete
            //if(output != null){
            //	output.close();
            //	output = null;
            //}

            //Destroy partition files


            System.gc();    //run GC
        }


    }

    /**
     * @param level
     * @throws Exception
     * @throws OutOfMemoryError
     */
    private void computeDependencies(int level) throws OutOfMemoryError {
        //------------COMPUTE THE INITALIZE RHS CANDIDATES------>
        //System.out.println("Entering generate COMPUTE in Level " +level);

        for (Map.Entry<BitSet, CandidateInfo> entry : current_level.entrySet()) {
            BitSet rhs = entry.getValue().getRHS();
            BitSet Xclone = (BitSet) entry.getKey().clone();
            //iterate through all bits in the attribute set
            for (int l = entry.getKey().nextSetBit(0); l >= 0; l = entry.getKey().nextSetBit(l + 1)) {
                Xclone.clear(l);
                BitSet CxwithouA = last_level.get(Xclone).getRHS();
                rhs.and(CxwithouA);
                Xclone.set(l);
            }
            CandidateInfo c = entry.getValue();
            c.setRHS(rhs);
            current_level.put(entry.getKey(), c);
        }
        //--------------------COMPUTE DEPENDENCIES--------------------------->
        //for each AttributeSet do
        if (level > 1) {
            //BitSet that represents the Relationscheme


            for (Map.Entry<BitSet, CandidateInfo> entry : current_level.entrySet()) {
                //For each A in X intersec C(X) do
                BitSet C = entry.getValue().getRHS();
                BitSet intersection = (BitSet) entry.getKey().clone();


                intersection.and(C);    //intersection X n C(X)

                //create a copy the attributeSet to avoid a ConcurrentModificationError
                BitSet X = (BitSet) entry.getKey().clone();


                //go through all A in XnC(X)
                for (int l = intersection.nextSetBit(0); l >= 0; l = intersection.nextSetBit(l + 1)) {
                    X.clear(l);
                    boolean fd_holds = false;
                    //if the memory version of Tane-java ist used, Stripped Partitions are used

                    StrippedPartition spXwithoutA = last_level.get(X).getStrippedPartition();
                    StrippedPartition spX = entry.getValue().getStrippedPartition();
                    //check if FD holds or not
                    if (spXwithoutA.error() == spX.error()) {
                        fd_holds = true;
                    }

                    if (fd_holds) {
                        foundFDs++;
                        //console output
                        /*if(debug){
                            System.out.print("{");
							boolean first = true;
							for (int i = X.nextSetBit(0); i >= 0; i = X.nextSetBit(i+1)) {
								if(!first) System.out.print(", ");
								else first=false;
								System.out.print(data.columnTitles[i-1]);
								// operate on index i here
							}
							System.out.println("} --> " + data.columnTitles[l-1]);
						}*/

                        if (!foundFD.containsKey(X)) foundFD.put((BitSet) X.clone(), new BitSet());
                        foundFD.get(X).set(l);

                        //write dependecy in XML file
                        //output.write("\t\t\t\t\t\t<fd>\n");
                        //output.write("\t\t\t\t\t\t\t<lhs>\n");
                        //for (int j = X.nextSetBit(0); j >= 0; j = X.nextSetBit(j+1)) {
                        //	output.write("\t\t\t\t\t\t\t\t<attribute>"+"</attribute>\n");
                        //}
                        //output.write("\t\t\t\t\t\t\t</lhs>\n");
                        //output.write("\t\t\t\t\t\t\t<rhs>\n");
                        //output.write("\t\t\t\t\t\t\t\t<attribute>"+"</attribute>\n");
                        //output.write("\t\t\t\t\t\t\t</rhs>\n");
                        //output.write("\t\t\t\t\t\t</fd>\n");

                        //Remove A from C(X)

                        C.clear(l);


                        //Remove all B in R\X from C(X)
                        BitSet R = new BitSet();
                        R.set(1, table_columns + 1);


                        //R equals R\X
                        R.xor(entry.getKey());    //xor --> symmetische Differenz entspricht R\X, da X Teilmenge aus R
                        C.xor(R);            //delete R\X from C(X)


                        //Remove all A in X, if there's a B in X such that X\{A,B}--> B holds
                        for (int k = R.nextSetBit(0); k >= 0; k = R.nextSetBit(k + 1)) {
                            BitSet XB = (BitSet) entry.getKey().clone();
                            XB.set(k); //set bit k in XB
                            XB.clear(l);    //remove bit l from XB
                            if (current_level.containsKey(XB)) {
                                current_level.get(XB).getRHS().clear(l);
                            }

                        }

                        //add l to attributset again
                        X.set(l);
                    }
                    X.set(l);
                }
            }
        }
    }

    /**
     * This method prunes the search space. Goal is to reduce the computation on each
     * level by using results from the previous levels.
     */
    private void prune() throws OutOfMemoryError {
        prefix_blocks = new UnifiedMap<BitSet, ArrayList<BitSet>>();
        ArrayList<BitSet> BitsToRemove = new ArrayList<BitSet>();
        boolean RHS_is_empty;
        boolean X_is_superkey;


        // check if C(X) is empty
        for (Map.Entry<BitSet, CandidateInfo> entry : current_level.entrySet()) {
            RHS_is_empty = false;
            X_is_superkey = false;

            if (entry.getValue().getRHS().isEmpty()) {

//                RHS_is_empty = true;
                BitsToRemove.add(entry.getKey());
                continue; // go to loop header
            }
            // check if X is a (super)key Code adapted from original TANE source

            if (entry.getValue().getStrippedPartition().error() == 0.0)
                X_is_superkey = true;

            // do pruning
            if (X_is_superkey) {
                BitSet R = new BitSet();
                R.set(1, table_columns + 1); // R
                R.xor(entry.getKey()); // R\X
                R.and(entry.getValue().getRHS()); // R\X intersection
                // C(X)
                foundFD.put((BitSet) entry.getKey().clone(), (BitSet) R.clone());

                for (int l = R.nextSetBit(0); l >= 0; l = R.nextSetBit(l + 1)) {
                    /*if(debug){
                        System.out.print("{");
						boolean first = true;
						for (int i = attr.nextSetBit(0); i >= 0; i = attr.nextSetBit(i+1)) {
							if(!first) System.out.print(", ");
							else first=false;
							System.out.print(data.columnTitles[i-1]);
							// operate on index i here
						}
						System.out.println("} --> " + data.columnTitles[l-1] + " (key)");
					}*/


                    //output.write("\t\t\t\t\t\t<fd>\n");
                    //output.write("\t\t\t\t\t\t\t<lhs>\n");
                    //for (int j = attr.nextSetBit(0); j >= 0; j = attr.nextSetBit(j + 1)) {
                    //	output.write("\t\t\t\t\t\t\t\t<attribute>"+ "</attribute>\n");
                    //}
                    //output.write("\t\t\t\t\t\t\t</lhs>\n");
                    //output.write("\t\t\t\t\t\t\t<rhs>\n");
                    //output.write("\t\t\t\t\t\t\t\t<attribute>" +"</attribute>\n");
                    //output.write("\t\t\t\t\t\t\t</rhs>\n");
                    //output.write("\t\t\t\t\t\t\t<comment>(Super)Key</comment>\n");
                    //output.write("\t\t\t\t\t\t</fd>\n");
                    foundFDs++;
                    BitsToRemove.add(entry.getKey());
                }
            }
            if (!RHS_is_empty && !X_is_superkey) {    //both must be false, otherise design error
                // generate prefix block if the attribute set has not
                // removed from level
                ArrayList<BitSet> set;
                if ((set = prefix_blocks.get(getPrefix(entry.getKey()))) == null) {
                    set = new ArrayList<BitSet>();
                }
                set.add(entry.getKey());
                prefix_blocks.put(getPrefix(entry.getKey()), set);

            }

        }
        for (BitSet X : BitsToRemove)
            current_level.remove(X);
        BitsToRemove.clear();
//        BitsToRemove = null;

    }

    /**
     * Generates the next level.
     *
     * @return
     * @throws Exception
     */
    private void generateNextLevel(int level) throws OutOfMemoryError {
        //Calculate size of number of candiates in next Level
        int numberOfNextLevelCandidates = current_level.size() * (table_columns - level) / (level + 1);
        StrippedPartition candidateI;
        StrippedPartition candidateK;


        last_level = current_level;
        //current_level.clear();

        current_level = new UnifiedMap<BitSet, CandidateInfo>(numberOfNextLevelCandidates);

        //System.out.println("Entering generate Nextgeneration");

        //go through each PrefixBlock
        for (ArrayList<BitSet> prefix_block : prefix_blocks.values()) {

            for (int i = 0; i < prefix_block.size(); i++) {
                //load partition
                candidateI = last_level.get(prefix_block.get(i)).getStrippedPartition();
                loadPartitionTable(candidateI);

                for (int k = i + 1; k < prefix_block.size(); k++) {

                    //Create LHS candidate for next level
                    BitSet nextCandidate = new BitSet();    //empty bitset
                    nextCandidate.or(prefix_block.get(i));            //or --> equals the union of two bitsets
                    nextCandidate.or(prefix_block.get(k));
                    //System.out.println("ATTSET I: " +prefix_block.get(i));
                    //System.out.println("ATTSET K: " +prefix_block.get(k));

                    //Create Partition
                    candidateK = last_level.get(prefix_block.get(k)).getStrippedPartition();
                    //load partition

                    //check if all subsets of length l of the new candidate are in the current level
                    //iterate over each bit, remove it, check if subset, add bit again
                    boolean candidateIsValid = true;
                    for (int l = nextCandidate.nextSetBit(0); l >= 0; l = nextCandidate.nextSetBit(l + 1)) {
                        //delete Attribute and check if candidate is in currentLevel
                        nextCandidate.clear(l);
                        if (!last_level.containsKey(nextCandidate)) {
                            candidateIsValid = false;
                            nextCandidate.set(l);
                            break;
                        }
                        //add delted attribute to the nextCandidate again
                        nextCandidate.set(l);
                    }
                    //if candiate is valid then calaculate stripped partitions
                    if (candidateIsValid) {
                        //create new CandidateInfo
                        CandidateInfo info = new CandidateInfo();
                        info.setRHS_BitRange(1, table_columns + 1);

                        //calculate StrippedPartition
                        StrippedPartition sp_new = strippedProduct(candidateK);

                        info.setStrippedPartition(sp_new);

                        current_level.put(nextCandidate, info);
                    }
                }
                unloadPartitionTable(candidateI);

            }
        }
        prefix_blocks.clear();        //delete prefix_blocks
        prefix_blocks = null;

    }

    /**
     * Return the prefix of an attribute set.
     * Removes the highes bit of the BitSet and return a new BitSet
     *
     * @param bitset -A BitSet object
     * @return BitSet -the new coded BitSet with the highest bit removed.
     */
    private BitSet getPrefix(BitSet bitset) {
        BitSet prefix = (BitSet) bitset.clone();
        //delete highest bit
        prefix.clear(prefix.length() - 1);

        return prefix;
    }

    /**
     * This class is needed before the product of two partitions are build.
     * More precisely, it inializes the table T[] of Algorithm 'StrippedPartition'
     * in "TANE - An efficient Algorithm for discovering functional dependencies".
     *
     * @param p - the stripped partition whose table T[] has to be inializes.
     */
    private void loadPartitionTable(StrippedPartition p) {
        int i = 0, setno = 1;
        //p.print();
        //System.out.println("len");
        int[] elements = p.getElements();
        while (i < elements.length) {
            while (true) {
                if (Bits.testBit(elements[i], 31)) break;

                tuple_tbl[elements[i++] - 1] = setno;
            }
            tuple_tbl[Bits.clearBit(elements[i++], 31) - 1] = setno;
            setno++;
        }
        noofsets = setno - 1;
        set_tbl[noofsets + 1] = 1;
    }

    /**
     * Sets the table T[] to all 0;
     *
     * @param p
     */
    private void unloadPartitionTable(StrippedPartition p) {
        int i;
        int elements[] = p.getElements();

        for (i = 0; i < elements.length; i++) {
            tuple_tbl[Bits.clearBit(elements[i], 31) - 1] = 0;
        }
        set_tbl[noofsets + 1] = 0;
    }

    /**
     * Computes a new partition P<sub>z</sub> = P<sub>x</sub> * P<sub>y</sub>.<p>
     * The partition P<sub>x</sub> has to be loaded by the method loadPartitionTable()
     * <code>
     * loadPartitionTable(X)<br>
     * StrippedPartition Z = strippedProduct(Y)<br>
     * unloadPartitionTable(X)
     * </code>
     *
     * @param p
     * @return
     */
    private StrippedPartition strippedProduct(StrippedPartition p) {
        int i = 0, j = 0, k, base_index = 1, setindex;
        int element;
        int newElements = 0;
        int newNoofSets = 0;

        int elements[] = p.getElements();

        while (i < elements.length) {
            setindex = 0;
            while (!Bits.testBit(elements[i], 31)) {
                if (set_tbl[tuple_tbl[elements[i++] - 1]]++ == 0) {
                    set_visit[setindex++] = tuple_tbl[elements[i - 1] - 1];
                }
            }
            elements[i] = Bits.clearBit(elements[i], 31);

            if (set_tbl[tuple_tbl[elements[i++] - 1]]++ == 0) {
                set_visit[setindex++] = tuple_tbl[elements[i - 1] - 1];
            }

            set_tbl[0] = 0;
            for (k = 0; k < setindex; k++) {
                if (set_visit[k] == 0) continue;
                if (set_tbl[set_visit[k]] == 1) {
                    set_tbl[set_visit[k]] = 0;
                    set_visit[k] = 0;
                    continue;
                }
                base_index += set_tbl[set_visit[k]];
                set_tbl[set_visit[k]] = base_index - set_tbl[set_visit[k]];
            }

            for (; j < i; j++) {
                if (set_tbl[tuple_tbl[elements[j] - 1]] == 0) {
                    continue;
                }
                element = set_tbl[tuple_tbl[elements[j] - 1]]++;
                newtuple_tbl[element - 1] = elements[j];
                newElements++;    //ArraySize of the new partition
            }
            elements[i - 1] = Bits.setBit(elements[i - 1], 31);

            for (k = 0; k < setindex; k++) {
                if (set_visit[k] == 0) continue;
                newtuple_tbl[set_tbl[set_visit[k]] - 2] = Bits.setBit(newtuple_tbl[set_tbl[set_visit[k]] - 2], 31);
                newNoofSets++;
                set_tbl[set_visit[k]] = 0;
            }
        }
        int newPartition[] = new int[newElements];
        for (i = 0; i < newElements; i++) {
            newPartition[i] = newtuple_tbl[i];
        }
        return new StrippedPartition(newPartition, newElements, newNoofSets);
    }

    /**
     * Shows discovered functional dependencies on the console, if
     * debug <code>debug</code> is true.
     *
     * @param debug
     */
    public void setConsoleOutput(boolean debug) {
        this.debug = debug;
    }

    protected void finalize() {

        //close files and delete
        //if(output != null){
        //	output.close();
        //	output = null;
        //}

    }

    /**
     * Sets the temporary folder for swapping stripped partitions. By defualt, the
     * temp folder is your operating system's defualt temporary folder.
     *
     * @param tempFolder - the new temporary folder
     */
    public void setTempFolder(String tempFolder) {
        this.tempFolder = tempFolder;
    }


    //Function calculates keys by brute-force search. Warning, exponential complexity.

    public void getKeys() {
        foundValues = new BitSet();
        for (BitSet value : foundFD.values()) {
            foundValues.or(value);
        }


        candidateKeys = new UnifiedSet<BitSet>();

        generateKeys(1, new BitSet(table_columns + 1));

        data.keys = new ArrayList<UnifiedSet<String>>();
        for (BitSet key : candidateKeys) {
            boolean first = true;
            UnifiedSet<String> stringKey = new UnifiedSet<String>();
            for (int i = key.nextSetBit(0); i >= 0; i = key.nextSetBit(i + 1)) {
                if (first) first = false;
                stringKey.add(data.columnTitles[i - 1]);
            }
            data.keys.add(stringKey);
        }

    }


    private void generateKeys(int level, BitSet key) {
        if (level > table_columns) {
            checkKey((BitSet) key.clone());
            return;
        }
        if (foundValues.get(level)) generateKeys(level + 1, key);
        key.set(level, true);
        generateKeys(level + 1, key);
        key.set(level, false);
    }


    private void checkKey(BitSet key) {
        BitSet signature = (BitSet) key.clone();
        for (Map.Entry<BitSet, BitSet> entry : foundFD.entrySet()) {
            BitSet cloneKey = (BitSet) key.clone();
            cloneKey.or(entry.getKey());
            if (cloneKey.equals(key)) {
                signature.or(entry.getValue());
            }
        }

        if (signature.cardinality() != table_columns) return;


        Iterator<BitSet> iter = candidateKeys.iterator();
        //boolean isCandidate=true;
        while (iter.hasNext()) {
            BitSet currentKey = iter.next();
            BitSet cloneKey = (BitSet) key.clone();
            cloneKey.or(currentKey);
            if (cloneKey.equals(key)) return;
            if (cloneKey.equals(currentKey)) iter.remove();
        }

        candidateKeys.add(key);

    }

}
