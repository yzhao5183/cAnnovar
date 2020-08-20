/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.omics.java;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;

/**
 *
 * @author Max He, PhD; Yiqing Zhao, MS
 */
public class Cytoband {

    private static String fileName;

    private static HashMap<Integer, HashMap<String, String>> cytobands;

    public Cytoband() {

    }

    /**
     *
     * @param _fileName
     * @throws java.io.IOException
     */
    public Cytoband(String _fileName) throws IOException {

        fileName = _fileName;

        cytobands = new HashMap<>();

        for (int i = 1; i <= 26; i++) {

            HashMap<String, String> band = new HashMap<>();
            
            //cytoband structure: <chromosome number,<start_stop, name of cytogenetic band>>
            cytobands.put(i, band);

        }
        
        //get band: <start_stop, name of cytogenetic band>
        processFile();

    }

    /**
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void processFile() throws FileNotFoundException, IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String sCurrentLine;

            String currentChrom = "-1";

            while ((sCurrentLine = br.readLine()) != null) {

                String[] parts = sCurrentLine.trim().split("\t");
                
                // get start position and end position
                String start_stop = parts[1] + "_" + parts[2];

                // get chromosome number
                String Chrom = parts[0];
                if (parts[0].startsWith("chr")) {
                    Chrom = parts[0].substring(3);
                }
                               
                switch (Chrom.trim()) {
                    case "X":
                        Chrom = "23";
                        break;
                    case "XY":
                        Chrom = "24";
                        break;
                    case "Y":
                        Chrom = "25";
                        break;
                    case "M": // mitochrondria chromosome
                        Chrom = "26";
                        break;
                    case "MT": // mitochrondria chromosome
                        Chrom = "26";
                        break;
                }

                HashMap<String, String> band = cytobands.get(Integer.parseInt(Chrom));

                //band=<start_stop, name of cytogenetic band e.g. p36.33>
                band.put(start_stop, parts[3]);

                if (!currentChrom.equals(Chrom)) {

                    currentChrom = Chrom;
                }

                if (Integer.parseInt(Chrom) == -1) {
                    System.out.println(Chrom + "\t" + start_stop + "\t" + parts[3]);
                    System.exit(213);
                }

            }

            br.close();
        }

        //getCytoBand(1, -1);
    }

    /**
     *
     * @param _chrom
     * @param _startPos
     * @return
     */
    public String getCytoBand(int _chrom, int _startPos) {

        String bandToReturn = "-1";

        HashMap<String, String> band = new HashMap<>();

        if (_chrom >= 1 && _chrom <= 25 && _chrom != 24) {
            band = cytobands.get(_chrom);
        } else if (_chrom == 24 || _chrom == 26) {
            return "mtXY";
        } else if (_chrom == 0) {
            return "0";
        }

        if (band == null) {
            System.out.println("1. No bands for chrom: " + _chrom);
            return bandToReturn;
        }

        if (band.isEmpty()) {
            System.out.println("2. No bands for chrom: " + _chrom);
            return bandToReturn;
        }

        ArrayList<String> notFound = new ArrayList<>();

        for (String positions : band.keySet()) {

            String[] pos = positions.split("_");

            //System.out.println(pos[0] +"\t"+pos[1]);
            if (_startPos >= Double.parseDouble(pos[0]) && _startPos <= Double.parseDouble(pos[1])) {
                //System.out.println(band.get(positions));

                return band.get(positions);
            } else {
                notFound.add(_chrom + "\t" + pos[0] + "\t" + _startPos + "\t" + pos[1]);
            }
        }

        for (String str : notFound) {
            System.out.println(str);
        }

        System.out.println("3. No bands for chrom: " + _chrom + "with POS " + _startPos);
        System.exit(_startPos);
        return bandToReturn;

    }

	/**
     *
     * @param _chrom
     * @return
     */
    public static int getIntChrom(String _chrom) {

        String Chr = _chrom;
        if (_chrom.startsWith("chr")) {
            Chr = _chrom.substring(3);
        }

        // e.g. chr1_gl000191_random 
        if(Chr.contains("_")){
        	Chr = "0";
        }
        // chr1-23+X+Y+M+MT
        switch (Chr) {
            case "X":
                Chr = "23";
                break;
            case "XY":
                Chr = "24";
                break;
            case "Y":
                Chr = "25";
                break;
            case "M":
                Chr = "26";
                break;
            case "MT":
                Chr = "26";
                break;	

        }

        int chrom = Integer.parseInt(Chr);

        return chrom;

    }

    /**
     *
     * @return
     */
    public static HashMap getCytoBands() {
        return cytobands;
    }

}
