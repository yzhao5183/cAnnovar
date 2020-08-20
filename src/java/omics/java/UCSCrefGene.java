/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.omics.java;

import java.util.HashMap;

/**
 *
 * @author Max He, PhD; Yiqing Zhao, MS
 */
public class UCSCrefGene {

    private final String id;
    private final int chrom;

    private final boolean strand;
    private final int txStart;
    private final int txEnd;
    private final int cdsStart;
    private final int cdsEnd;
    private final int exonCount;
    private final int[] exonStarts;
    private final int[] exonEnds;
    private final String name;

    private String cytoband;
    private String mrna;

    private int mrnaStartOffset;
    private int mrnaEndOffset;

    private String flippedMrna;
    private String cdna;
    private String protein1;
    private String protein3;

    private String[] exons;

    private static HashMap<String, String> codon1;
    private static HashMap<String, String> codon3;
    private static HashMap<String, String> codon1m;
    private static HashMap<String, String> codon3m;

    public UCSCrefGene() {

        id = null;
        chrom = -1;

        strand = false;
        txStart = -1;
        txEnd = -1;
        cdsStart = -1;
        cdsEnd = -1;
        exonCount = -1;
        exonStarts = null;
        exonEnds = null;
        name = null;

        //initCodons();
        protein1 = null;
        protein3 = null;

    }

    public UCSCrefGene(String _line) {

        String[] fields = _line.trim().split("\t");

        id = fields[1].trim() + "\t" + Cytoband.getIntChrom(fields[2].trim()) + "\t" + Integer.parseInt(fields[4].trim());

        chrom = Cytoband.getIntChrom(fields[2].trim());

        strand = fields[3].trim().equals("+");

        txStart = Integer.parseInt(fields[4].trim());
        txEnd = Integer.parseInt(fields[5].trim());
        cdsStart = Integer.parseInt(fields[6].trim());
        cdsEnd = Integer.parseInt(fields[7].trim());
        exonCount = Integer.parseInt(fields[8].trim());

        String[] stringArray = removeTrailingChar(fields[9].trim()).trim().split(",");
        exonStarts = new int[stringArray.length];
        for (int i = 0; i < exonStarts.length; i++) {
            exonStarts[i] = Integer.parseInt(stringArray[i].trim());
        }

        stringArray = removeTrailingChar(fields[10].trim()).trim().split(",");
        exonEnds = new int[stringArray.length];
        for (int i = 0; i < exonEnds.length; i++) {
            exonEnds[i] = Integer.parseInt(stringArray[i].trim());
        }

        name = fields[12].trim();

        //initCodons();
        protein1 = null;
        protein3 = null;

    }

    public void inputCytoBand(String _cytoband) {
        cytoband = _cytoband;

    }

    public void inputMrna(String _mrna) {
        mrna = _mrna.toUpperCase();
        processMrna();
    }

    public void processMrna() {
    	
	        exons = new String[exonCount];
	
	        int exonLength;
	
	        int exonStart = 0;
	
	//        for (int i = 0; i < exonCount; i++) {
	//
	//            exonLength = exonEnds[i] - exonStarts[i];
	//
	//            if ((exonLength + exonStart) > mrna.length()) {
	//
	//                System.out.println("Invalid refGene file");
	//
	//                System.exit(213);
	//
	//            } else {
	//                exons[i] = mrna.substring(exonStart, exonLength + exonStart);
	//            }
	//
	//            exonStart += exonLength;
	//        }
	        
	        if (id.startsWith("NM_")) {
	
	            int start = cdsStart - txStart;
	
	            int cdsStartInExon = inExon(cdsStart, cdsStart);
	
	            if (cdsStartInExon != -1) {
	            	//Get CDS start offset
	                start = getCDSStartOffset(cdsStart, cdsStart);
	
	            }
	
	            int end = mrna.length() - (txEnd - cdsEnd);
	
	            int cdsEndInExon = inExon(cdsEnd, cdsEnd);
	
	            if (cdsEndInExon != exonCount - 1) {
	            	//Get CDS end offset
	                end = mrna.length() - getEndOffset(cdsEnd, cdsEnd);
	
	            }
	
	            if (!strand) {
	
	                flippedMrna = flipStrand(mrna);
	
	                int newStart = mrna.length() - end;
	
	                int newEnd = mrna.length() - start;
	                end = newEnd;
	
	                start = newStart;
	            }
	
	            if (end <= start || end < 0 || end > mrna.length()) {
	
	                System.out.println("\nend " + end);
	                System.out.println("start " + start);
	                System.out.println("mrna.length() " + mrna.length());
	
	                System.out.println("startOffset" + getCDSStartOffset(cdsStart, cdsStart));
	
	                System.out.println("incorect end position");
	                System.exit(213);
	            }
	
	            if (start >= end || start < 0) {
	
	                System.out.println("\nend " + end);
	                System.out.println("start " + start);
	                System.out.println("mrna.length() " + mrna.length());
	
	                System.out.println("endOffset " + getEndOffset(cdsEnd, cdsEnd));
	                System.out.println("startOffset " + getCDSStartOffset(cdsStart, cdsStart));
	                System.out.println("Incorect start position");
	
	                System.exit(213);
	            }
	
	            cdna = mrna.substring(start, end);
	
	            mrnaStartOffset = start;
	
	            mrnaEndOffset = end;
	
	            translateDNA(cdna);
		
	            if (protein1.startsWith("*") || !protein1.endsWith("*")) {
	
	//                System.out.println("\n" + id + "\t" + name);
	//                System.out.println("txStart "+txStart);
	//                System.out.println("cdsStart "+cdsStart);
	//                System.out.println("cdsEnd "+cdsEnd);
	//                System.out.println("txEnd "+txEnd);
	//                System.out.println("mrna.length() "+ mrna.length());
	//                //System.out.println((cdsEnd-cdsStart) + "\tmod3 "+((cdsEnd-cdsStart) % 3));
	//                System.out.println("cdsEndInExon "+ cdsEndInExon);
	//                System.out.println("EndOffset "+ EndOffset);
	//            
	//                
	//                System.out.println("mrna.length()-(txEnd-cdsEnd) " + (mrna.length() - (txEnd - cdsEnd)));
	//            
	//                System.out.println("start End " + start + "\t" + end);
	//            
	//            
	//                //System.out.println("cdna length " + cdna.length() + " mod3 " + (cdna.length() % 3));
	//                
	//                System.out.println("mrna");
	//                System.out.println(mrna);
	//                
	//                System.out.println("cdna");
	//                System.out.println(cdna);
	//                
	//                System.out.println("protein1");
	//                System.out.println(protein1);
	//                
	//                System.exit(0);
	                protein1 = null;
	                protein3 = null;
	
	            } else if (removeTrailingChar(protein1).contains("*")) {
	
	                protein1 = null;
	                protein3 = null;
	
	            }
	
	        }
	
	    }

	public String translateDNA(String _cdna) {

        String proteinSeq1 = "";
        String proteinSeq3 = "";

        char[] cdna1 = _cdna.toCharArray();

        for (int i = 0; i < cdna1.length; i += 3) {
            if (i + 3 <= cdna1.length) {
                String codon = Character.toString(cdna1[i]) + Character.toString(cdna1[i + 1]) + Character.toString(cdna1[i + 2]);
                proteinSeq1 += Annotation.codon1.get(codon);
                proteinSeq3 += Annotation.codon3.get(codon);
            }
        }

        if (protein1 == null) {
            protein1 = proteinSeq1;
            protein3 = proteinSeq3;

        }

        return proteinSeq1;

    }

    private int getCDSStartOffset(int _start, int _end) {

        int cdsStartInExon = inExon(_start, _end);

        int cdsStartOffset = 0;

        for (int i = 0; i < cdsStartInExon; i++) {
            cdsStartOffset += (exonEnds[i] - exonStarts[i]);
        }

        cdsStartOffset += (cdsStart - exonStarts[cdsStartInExon]);

        return cdsStartOffset;
    }

    private int getStartOffsetForVariant(int _start, int _end) {

        int cdsStartOffset = 0;
        int startOffset = 0;

        int inExon = inExon(_start, _end);
        int cdsStartInExon = inExon(cdsStart, cdsStart);

        if (cdsStartInExon != 0) {
//            for (int i = 0; i < cdsStartInExon; i++) {
//                cdsStartOffset += (exonEnds[i] - exonStarts[i]);
//            }
//
//            cdsStartOffset += (cdsStart - exonStarts[cdsStartInExon]);

            for (int i = 0; i < inExon; i++) {
                startOffset += (exonEnds[i] - exonStarts[i]);
            }

            startOffset += (_start - exonStarts[inExon]);

            return startOffset;

        } else //cdsStartOffset += cdsStart - txStart;
        if (inExon == cdsStartInExon) {
            startOffset = _start - txStart;

//                startOffset = (_start - cdsStart) + cdsStartOffset;
//
////                System.out.println("mrnaStartOffset " + mrnaStartOffset);
//                startOffset += cdsStartOffset;
//                System.out.println("id " + id);
//                System.out.println("name " + name);
//
//                System.out.println("mrna.length() " + mrna.length());
//                System.out.println("cdna.length() " + cdna.length());
//
//                System.out.println("cdna.length() mod3 " + (cdna.length() % 3));
//
//                System.out.println("_start " + _start);
//                System.out.println("_end " + _end);
////        
//                System.out.println("startOffset " + startOffset);
//                System.out.println("cdsStartInExon " + cdsStartInExon);
//
//                System.out.println("cdsStartOffset " + cdsStartOffset);
//                System.out.println("inExon " + inExon);
//                System.out.println("exonCount " + exonCount);
//                System.out.println("exonStarts.length " + exonStarts.length);
//                System.out.println("exonStarts.length " + exonEnds.length);
            return startOffset;
        } else {

            startOffset = 0;

            for (int i = cdsStartInExon; i < inExon; i++) {
                startOffset += (exonEnds[i] - exonStarts[i]);
                //System.out.println("startOffset " + startOffset);
            }

            //System.out.println("startOffset " + startOffset);
            startOffset += (_start - exonStarts[inExon]);

            return startOffset;

        }
//
//        System.out.println("mrnaStartOffset " + mrnaStartOffset);
//
//        //startOffset += cdsStartOffset;
//
//        System.out.println("id " + id);
//        System.out.println("name " + name);
//        System.out.println("mrna.length() " + mrna.length());
//        System.out.println("cdna.length " + cdna.length());
//        System.out.println("exonCount " + exonCount);
//        System.out.println("exonStarts.length " + exonStarts.length);
//        System.out.println("exonEnds.length " + exonEnds.length);
//        System.out.println("cdsStart " + cdsStart);
//
//        System.out.println("_start " + _start);
//        System.out.println("_end " + _end);
//        System.out.println("startOffset " + startOffset);
//
//        System.out.println("cdsStartOffset " + cdsStartOffset);
//        System.out.println("cdsStartInExon " + cdsStartInExon);
//        System.out.println("inExon " + inExon);
//
//        
//
//            for (int i = cdsStartInExon; i < inExon; i++) {
//                startOffset += (exonEnds[i] - exonStarts[i]);
//                System.out.println("startOffset " + startOffset);
//            }
//
//            System.out.println("startOffset " + startOffset);
//
//            startOffset += (_start - exonStarts[inExon]);
//
//        
//
//        for (int i = cdsStartInExon; i < inExon; i++) {
//            startOffset += (exonEnds[i] - exonStarts[i]);
//            System.out.println("startOffset " + startOffset);
//        }
//
//        System.out.println("startOffset " + startOffset);
//
//        startOffset += (_start - exonStarts[inExon]);
//
//        System.out.println("startOffset " + startOffset);
////
//
//        //System.out.println(startOffset);
//        // System.exit(_start);
        //return -1;
    }

    private int getEndOffset(int _start, int _end) {

        int endOffset = 0;

        int inExon = inExon(_start, _end);

        for (int i = exonCount - 1; i > inExon; i--) {
            //if (exons[i] != null) {

            endOffset += (exonEnds[i] - exonStarts[i]);

//            }
        }

        endOffset += (exonEnds[inExon] - cdsEnd);

        return endOffset;
    }

    private int getEndOffsetForVariant(int _start, int _end) {

        int endOffset = 0;
        int cdsEndOffset = 0;

        int inExon = inExon(_start, _end);
        int cdsEndInExon = inExon(cdsEnd, cdsEnd);

        if (cdsEndInExon != exonCount - 1) {

            for (int i = exonCount - 1; i > inExon; i--) {

                cdsEndOffset += (exonEnds[i] - exonStarts[i]);

            }

            cdsEndOffset += (exonEnds[cdsEndInExon] - cdsEnd);

        } else {

            if (inExon == cdsEndInExon) {
                endOffset = (cdsEnd - _end) + mrnaEndOffset;// cdsEndOffset-
                return endOffset;
            }

            endOffset += (cdsEnd - exonEnds[exonCount - 1]);

        }

        for (int i = inExon; i < cdsEndInExon; i++) {
            endOffset += (exonEnds[i] - exonStarts[i]);
        }

        System.out.println("cdsEnd " + cdsEnd);
        //System.out.println("exonEnds "+exonEnds);
        System.out.println("exonEnds[exonCount - 1] " + exonEnds[exonCount - 1]);
        System.out.println("endOffset " + endOffset);

//        for (int i = exonCount - 1; i > inExon; i--) {
//
//            endOffset += (exonEnds[i] - exonStarts[i]);
//
//            System.out.println("endOffset " + endOffset);
//
//        }
        return (endOffset);
    }

    private static String flipStrand(String _str) {
        String flippedcdna;
        char[] flipcdna = _str.toCharArray();
        flippedcdna = "";
        for (int i = flipcdna.length - 1; i >= 0; i--) {
            if (flipcdna[i] == 'C') {
                flippedcdna += "G";
            } else if (flipcdna[i] == 'G') {
                flippedcdna += "C";
            } else if (flipcdna[i] == 'T') {
                flippedcdna += "A";
            } else if (flipcdna[i] == 'A') {
                flippedcdna += "T";
            }
        }
        return flippedcdna;
    }

    public static String removeTrailingChar(String _str) {
        if (_str.length() > 0)// && _str.charAt(_str.length()-1)==",")
        {
            _str = _str.substring(0, _str.length() - 1);
        }
        return _str;
    }

    public boolean betweenTX(int _variantStart, int _variantStop) {
    	return _variantStart > (txStart) && _variantStop <= (txEnd);
    }

    public boolean betweenCDS(int _variantStart, int _variantStop) {
        return _variantStart >= cdsStart && _variantStop <= cdsEnd;
    }
    
    public boolean nonCoding(int _variantStart, int _variantStop) {
        return _variantStop < cdsStart || _variantStart > cdsEnd;
    }

    public boolean isUpStream(int _variantStart, int _variantStop) {
        //System.out.println(name);
    	return _variantStop <= txStart && _variantStart > (txStart - 999);
    }

    public boolean isDownStream(int _variantStart, int _variantStop) {
    	return _variantStart >= txEnd && _variantStop < (txEnd + 1000);
    }

    public boolean isSplice(int _variantStart, int _variantStop) {
	    //boolean isSplice = false;
	
	    if (_variantStop < exonStarts[0]) {
	        return false;
	    }
	
	    for (int i = 0; i < exonCount; i++) {
	
	    	// Annovar result come from the first if condition, wierd...
	    	if (_variantStart <= (exonStarts[i]) && _variantStop > (exonStarts[i] - 2)) {

	            return true;
	        }
	
	    	if (_variantStart > (exonEnds[i]) && _variantStop <= (exonEnds[i] + 2)) {	
	            return true;
	        }
	
	    }
	
	    return false;
	}

	public boolean isExon(int _variantStart) {

	        for (int i = 0; i < exonCount; i++) {

	        	if (_variantStart >= (exonStarts[i]) && _variantStart <= (exonEnds[i])) {
	                return true;
	            }
	        }
	        return false;
	}

	public int inExon(int _variantStart, int _variantStop) {
        int exonNumber = -1;
        if (_variantStop < exonStarts[0]) {
            return exonNumber;
        }
        for (int i = 0; i < exonCount; i++) {
            //if (_variantStart >= (exonStarts[i] - (_variantStop - _variantStart)) && _variantStop <= (exonEnds[i] + (_variantStop - _variantStart))) {
            if (_variantStart >= (exonStarts[i]) && _variantStop <= (exonEnds[i])) {
                return i;
            } else {
//                System.out.println(_variantStart +"\t"+ (exonStarts[i]));
//                System.out.println(_variantStop +"\t"+ (exonEnds[i]));
            }
        }
        return exonNumber;
    }
	
	public boolean in3utr(int _variantStart, int _variantStop) {
		return _variantStart >= cdsEnd && _variantStop <= (txEnd);
    }

    public boolean in5utr(int _variantStart, int _variantStop) {
    	return _variantStart > txStart && _variantStop <= (cdsStart);
    }

    public int getChrom() {
        return chrom;
    }

    public boolean getStrand() {
        return strand;

    }

    public int getTXstart() {
        return txStart;

    }

    public int getTXend() {
        return txEnd;

    }

    public int getCDSstart() {
        return cdsStart;

    }

    public int getCDSend() {
        return cdsEnd;

    }
    
    public String getID() {
        return id;
    }

    public int getExonCount() {
        return exonCount;
    }
  
    public int[] getExonStarts() {
        return exonStarts;
    }
    
    public int[] getExonEnds() {
        return exonEnds;
    }
    
    public String getName() {
        return name;
    }

    public String getMrna() {
        return mrna;
    }
    
    public String getflippedMrna() {
        return flippedMrna;
    }

    public int getmrnaStartOffset() {
	    return mrnaStartOffset;
	}

	public int getmrnaEndOffset() {
	    return mrnaEndOffset;
	}

	public int getcdsStart() {
        return cdsStart;
    }

    public int getcdscdsEnd() {
        return cdsEnd;
    }

    public String getProtein1() {
        return protein1;
    }

    public String getProtein3() {
        return protein3;
    }

    public String getExonicFunc_refGene(int _variantStart, int _variantStop, String _ref, String _alt) {

//        if (protein1 == null) {
//            return ".";
//        }
        if (_ref.length() != _alt.length()) {

            if (!_ref.equals(".") && !_alt.equals(".")) {

                char[] refArray = _ref.toCharArray();
                char[] altArray = _alt.toCharArray();

                String newRef = "";
                String newAlt = "";

                int numMatchChar = 0;

                if (_ref.length() > _alt.length()) {

                    for (int i = 0; i < altArray.length; i++) {
                        if (refArray[i] == altArray[i]) {
                            numMatchChar++;
                        } else {
                            break;
                        }
                    }

                } else {
                    for (int i = 0; i < refArray.length; i++) {
                        if (refArray[i] == altArray[i]) {
                            numMatchChar++;
                        } else {
                            break;
                        }
                    }
                }
                if (numMatchChar > 0) {

                    for (int i = numMatchChar; i < altArray.length; i++) {
                        newAlt += altArray[i];
                    }
                    for (int i = numMatchChar; i < refArray.length; i++) {
                        newRef += refArray[i];
                    }
                } else {
                    newRef = _ref;
                    newAlt = _alt;
                }

                if ((newRef.length() + newAlt.length()) % 3 != 0) {
                    if (_alt.length() > _ref.length()) {
                        return "frameshift insertion";
                    } else {
                        return "frameshift deletion";
                    }
                }
            } else if (!_ref.equals(".") && _alt.equals(".")) {
                if (_ref.length() % 3 != 0) {
                    return "frameshift deletion";
                } else {
                    return "nonframeshift deletion";
                }
            } else if (_ref.equals(".") && !_alt.equals(".")) {
                if (_alt.length() % 3 != 0) {
                    //System.out.println("HERE?!?!");
                    return "frameshift insertion";
                } else {
                    //System.out.println("WHY IS IT HERE?!?!");
                    return "nonframeshift insertion";
                }
            }
        } else if (_alt.length() == _ref.length()) {

            //System.out.println("HERE?!!");
            if (protein1 == null) {
                return "nonframeshift substitution";
            } else {
                //System.out.println(protein1);
            }

            if (!_ref.equals(".") && _alt.equals(".")) {
                if (_ref.length() % 3 != 0) {
                    return "frameshift deletion";
                } else {
                    return "nonframeshift deletion";
                }
            } else if (_ref.equals(".") && !_alt.equals(".")) {
                if (_alt.length() % 3 != 0) {
                    //System.out.println("HERE?!?!");
                    return "frameshift insertion";
                } else {
                    //System.out.println("WHY IS IT HERE?!?!");
                    return "nonframeshift insertion";
                }
            }

            char[] mrnaArray = mrna.toCharArray();

            if (!strand) {
                mrnaArray = flippedMrna.toCharArray();
            }

            char[] refArray = _ref.toCharArray();

            char[] altArray = _alt.toCharArray();

            int startOffset = getStartOffsetForVariant(_variantStart, _variantStop);

            if (startOffset < 0 || startOffset > mrna.length()) {
                System.out.println("\nBad StartOffset");

                int inExon = inExon(_variantStart, _variantStop);
                int cdsStartInExon = inExon(cdsStart, cdsStart);

                System.out.println("id " + id);
                System.out.println("startOffset " + startOffset);
                System.out.println("txStart " + txStart);
                System.out.println("txEnd " + txEnd);
                System.out.println("cdsStart " + cdsStart);
                System.out.println("cdsEnd " + cdsEnd);
                System.out.println("mrna.length() " + mrna.length());

                System.out.println("cdsStartInExon " + cdsStartInExon);

                System.out.println("mrnaStartOffset " + mrnaStartOffset);
                System.out.println("inExon " + inExon);
                System.out.println("exonCount " + exonCount);
                System.out.println(_variantStart + "\t" + _variantStop + "\t" + _ref + "\t" + _alt);

                System.out.println();
                System.exit(213);
            }

//            if (startOffset % 3 == 1) {
//                int protein = (((startOffset - 1) / 3));
//                System.out.println("original amino acids ");
//                System.out.println(protein1.substring((protein - 1), (protein)) + "\t" + protein1.substring(protein, (protein + 1)) + "\t" + protein1.substring((protein + 1), (protein + 2)));
//            } else if (startOffset % 3 == 2) {
//                int protein = (((startOffset - 2) / 3));
//                System.out.println("original amino acid ");
//                System.out.println(protein1.substring((protein - 1), (protein)) + "\t" + protein1.substring(protein, (protein + 1)) + "\t" + protein1.substring((protein + 1), (protein + 2)));
//            } else if (startOffset % 3 == 0) {
//                int protein = (((startOffset - 3) / 3));
//                System.out.println("original amino acid ");
//                System.out.println(protein1.substring((protein - 1), (protein)) + "\t" + protein1.substring(protein, (protein + 1)) + "\t" + protein1.substring((protein + 1), (protein + 2)));
//            }
            for (int i = startOffset; i < (startOffset + _alt.length()); i++) {

                if (mrnaArray == null || mrna == null) {

                    System.out.println("\nnull mrna");
                    System.out.println("id " + id);
                    System.out.println("startOffset " + startOffset);
                    System.out.println("i " + i);
                    System.out.println("refArray[i - startOffset] " + refArray[i - startOffset]);
                    //System.out.println("mrnaArray.length " + mrnaArray.length);
                    //System.out.println(mrnaArray[i - 3] + "\t" + mrnaArray[i - 2] + "\t" + mrnaArray[i - 1] + "\t" + mrnaArray[i] + "\t" + mrnaArray[i + 1]);
                    System.out.println("mrna.length() " + mrna.length());
                    System.out.println(_variantStart + "\t" + _variantStop + "\t" + _ref + "\t" + _alt);
                    System.out.println();
                    //System.exit(213);
                    return ".";

                } else if ((i - 1) < 0 || (i - startOffset) < 0) {

                    System.out.println("\ninvalid startOffset");
                    System.out.println("id " + id);
                    System.out.println("startOffset " + startOffset);
                    System.out.println("i " + i);
                    System.out.println("refArray[i - startOffset] " + refArray[i - startOffset]);
                    System.out.println("mrnaArray.length " + mrnaArray.length);
                    //System.out.println(mrnaArray[i - 3] + "\t" + mrnaArray[i - 2] + "\t" + mrnaArray[i - 1] + "\t" + mrnaArray[i] + "\t" + mrnaArray[i + 1]);
                    System.out.println("mrna.length() " + mrna.length());
                    System.out.println(_variantStart + "\t" + _variantStop + "\t" + _ref + "\t" + _alt);
                    System.out.println();
                    //System.exit(213);
                    return ".";

                } else if (mrnaArray[i - 1] != refArray[i - startOffset]) {
                    System.out.println("\nRef misMatch Match");
                    System.out.println("id " + id);
                    System.out.println("startOffset " + startOffset);
                    System.out.println("i " + i);
                    System.out.println("refArray[i - startOffset] " + refArray[i - startOffset]);
                    System.out.println("mrnaArray[i] " + mrnaArray[i - 1]);
                    System.out.println(mrnaArray[i - 3] + "\t" + mrnaArray[i - 2] + "\t" + mrnaArray[i - 1] + "\t" + mrnaArray[i] + "\t" + mrnaArray[i + 1]);
                    System.out.println("mrna.length() " + mrna.length());
                    System.out.println(_variantStart + "\t" + _variantStop + "\t" + _ref + "\t" + _alt);
                    System.out.println();
                    //System.exit(213);
                    return ".";

                } else {
//                    System.out.println(mrnaArray[i - 3] + "\t" + mrnaArray[i - 2] + "\t" + mrnaArray[i - 1] + "\t" + mrnaArray[i] + "\t" + mrnaArray[i + 1] + "\t" + mrnaArray[i + 2] + "\t" + mrnaArray[i + 3]);
//
//                    System.out.println("mrnaArray[i + 1] " + mrnaArray[i + 1]);
//                    System.out.println("altArray[i - startOffset " + (altArray[i - startOffset]));

                    mrnaArray[i - 1] = altArray[i - startOffset];
                }
            }

            String newMrna = new String(mrnaArray);

            if (!strand) {
                newMrna = flipStrand(newMrna);
            }

            if (newMrna.equals(mrna)) {
                System.out.println("\nNO CHANGE in mRNA!");
                int inExon = inExon(_variantStart, _variantStop);
                int cdsStartInExon = inExon(cdsStart, cdsStart);

                System.out.println("id " + id);
                System.out.println("startOffset " + startOffset);
                System.out.println("txStart " + txStart);
                System.out.println("txEnd " + txEnd);
                System.out.println("cdsStart " + cdsStart);
                System.out.println("cdsEnd " + cdsEnd);
                System.out.println("mrna.length() " + mrna.length());

                System.out.println("cdsStartInExon " + cdsStartInExon);

                System.out.println("mrnaStartOffset " + mrnaStartOffset);
                System.out.println("inExon " + inExon);
                System.out.println("exonCount " + exonCount);
                System.out.println(_variantStart + "\t" + _variantStop + "\t" + _ref + "\t" + _alt);

                System.out.println();

                return ".";

                //System.exit(213);
            }

            String newProtein = translateDNA(newMrna.substring(mrnaStartOffset, mrnaEndOffset));

//            if (startOffset % 3 == 1) {
//                int protein = (((startOffset - 1) / 3));
//                System.out.println(newProtein.substring((protein - 1), (protein)) + "\t" + newProtein.substring(protein, (protein + 1)) + "\t" + newProtein.substring((protein + 1), (protein + 2)));
//            } else if (startOffset % 3 == 2) {
//                int protein = (((startOffset - 2) / 3));
//                System.out.println(newProtein.substring((protein - 1), (protein)) + "\t" + newProtein.substring(protein, (protein + 1)) + "\t" + newProtein.substring((protein + 1), (protein + 2)));
//            } else if (startOffset % 3 == 0) {
//                int protein = (((startOffset - 3) / 3));
//                System.out.println(newProtein.substring((protein - 1), (protein)) + "\t" + newProtein.substring(protein, (protein + 1)) + "\t" + newProtein.substring((protein + 1), (protein + 2)));
//            }
            //System.out.println("Here?!?!");
            if (newProtein.equals(protein1)) {
                if (_alt.length() == 1) {
                    return "synonymous SNV";
                }
                return "synonymous block substitution";

            } else if (!newProtein.endsWith("*")) {
                return "stoploss";
            } else if (removeTrailingChar(newProtein).contains("*")) {
                return "stopgain";
            } else if (_alt.length() == 1) {
                return "nonsynonymous SNV";
            } else {
                return "nonsynonymous block substitution";
            }
        }
        return ".";
    }

    @Override
    public String toString() {
        return id;
    }

}
